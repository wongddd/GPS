package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.SelectionItemBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.adapter.AdapterDeviceList;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
@Page(name = "NewDeviceListFragment", anim = CoreAnim.none)
public class NewDeviceListFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.all_devices)
    TextView allDevices;
    @BindView(R.id.usable_devices)
    TextView usableDevices;
    @BindView(R.id.lost_devices)
    TextView lostDevices;

    private final int CONFIRM_TO_UNBIND = 0x101;
    private final int CONFIRM_HAS_BEEN_LOST = 0x102;
    private final int CONFIRM_RETRIEVED = 0x103;
    private int currentLabelId = R.id.all_devices;
    private AdapterDeviceList mAdapter;
    private final List<SelectionItemBean> mItemList = new ArrayList<>();
    private int currentOperatingPosition = -1; // 当前列表操作对象对应下标(-1表示当前无操作对象)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_new_device_list;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.device_list);
        titleBar.setLeftImageResource(0);
        return titleBar;
    }

    /**
     * 用于传输数据的中转类
     */
    private static class TransportObject {
        private final int position; // 列表操作对象对应下标
        private final String deviceImei;
        private final long deviceId;

        public TransportObject(int position, AAADeviceModel deviceModel) {
            this.position = position;
            this.deviceId = deviceModel.getDeviceId();
            this.deviceImei = deviceModel.getDeviceImei();
        }

        public int getPosition() {
            return position;
        }

        public String getDeviceImei() {
            return deviceImei;
        }

        public long getDeviceId() {
            return deviceId;
        }
    }

    @Override
    protected void initViews() {
        allDevices.setSelected(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AdapterDeviceList(mItemList);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            currentOperatingPosition = position;
            switch (view.getId()) {
                case R.id.tvUnbind: {
                    AAADeviceModel deviceModel = mItemList.get(position).getDeviceModel();
                    TransportObject transportObject = new TransportObject(position, deviceModel);
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext()
                            , mMaterialDialog, getString(R.string.prompt)
                            , String.format("%s%s", deviceModel.getDeviceImei() + "(" + deviceModel.getDeviceName() + ")", getString(R.string.unbind_prompt))
                            , getString(R.string.confirm)
                            , getString(R.string.cancel)
                            , transportObject
                            , CONFIRM_TO_UNBIND, mHandler);
                }
                break;
                case R.id.tvLost: {
                    AAADeviceModel deviceModel = mItemList.get(position).getDeviceModel();
                    TransportObject transportObject = new TransportObject(position, deviceModel);
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext()
                            , mMaterialDialog, getString(R.string.prompt)
                            , String.format("%s%s", deviceModel.getDeviceImei() + "(" + deviceModel.getDeviceName() + ")", getString(R.string.tip_mark_as_lost))
                            , getString(R.string.confirm)
                            , getString(R.string.cancel)
                            , transportObject
                            , CONFIRM_HAS_BEEN_LOST, mHandler);
                }
                break;
                case R.id.tvRetrieve: {
                    AAADeviceModel deviceModel = mItemList.get(position).getDeviceModel();
                    TransportObject transportObject = new TransportObject(position, deviceModel);
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext()
                            , mMaterialDialog, getString(R.string.prompt)
                            , String.format("%s%s", deviceModel.getDeviceImei() + "(" + deviceModel.getDeviceName() + ")", getString(R.string.tip_mark_as_found))
                            , getString(R.string.confirm)
                            , getString(R.string.cancel)
                            , transportObject
                            , CONFIRM_RETRIEVED, mHandler);
                }
                break;
                case R.id.ivCheck:
                    showDialog();
                    allItemsUnselect();
                    mItemList.get(position).setSelected(true);
                    adapter.notifyItemChanged(position);
                    SettingSPUtils.getInstance().putString(TConstant.SELECTED_IMEI, mItemList.get(position).getDeviceModel().getDeviceImei());
                    MainApplication.getInstance().setTrackDeviceModel(mItemList.get(position).getDeviceModel());
                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                dismisDialog();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                default:
                    break;
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 取消所有label选中状态
     */
    private void allLabelUnselect() {
        allDevices.setSelected(false);
        usableDevices.setSelected(false);
        lostDevices.setSelected(false);
    }

    /**
     * 所有设备取消选中状态
     */
    private void allItemsUnselect() {
        for (int i = 0; i < mItemList.size(); i++) {
            if (mItemList.get(i).isSelected()) {
                mItemList.get(i).setSelected(false);
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    @OnClick({R.id.all_devices, R.id.usable_devices, R.id.lost_devices})
    public void onClick(View view) {
        if (currentLabelId == view.getId()) return;
        switch (view.getId()) {
            case R.id.all_devices:
                mAdapter.switchCurrentTag(0);
                currentLabelId = R.id.all_devices;
                allLabelUnselect();
                allDevices.setSelected(true);
                queryDevicesList(currentLabelId);
                break;
            case R.id.usable_devices:
                mAdapter.switchCurrentTag(1);
                currentLabelId = R.id.usable_devices;
                allLabelUnselect();
                usableDevices.setSelected(true);
                queryDevicesList(currentLabelId);
                break;
            case R.id.lost_devices:
                mAdapter.switchCurrentTag(2);
                currentLabelId = R.id.lost_devices;
                allLabelUnselect();
                lostDevices.setSelected(true);
                queryDevicesList(currentLabelId);
                break;
        }
    }

    /**
     * 点击主页底部导航栏时调用
     */
    public void onSwitchToThisPage() {
        queryDevicesList(currentLabelId);
    }

    /**
     * 根据状态请求获取设备列表
     *
     * @param labelId null:全部  1:可用  2:飞丢
     */
    private void queryDevicesList(int labelId) {
        Integer deviceStatus = null;
        if (labelId == R.id.usable_devices) {
            deviceStatus = 1;
        } else if (labelId == R.id.lost_devices) {
            deviceStatus = 2;
        }
        showDialog();
        CarGpsRequestUtils.getDeviceList(getTrackUserModel(), deviceStatus, mHandler);
    }

    private void showNotBoundDeviceAndBackToBindPage() {
        showMessage(R.string.user_no_bound_device_tips);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                ActivityUtils.startActivity(BindDeviceActivity.class);
            }
        }, 1000);
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            try {
                switch (message.what) {
                    case TConstant.REQUEST_URL_GET_DEVICE_LIST: { // 获取设备列表
                        dismisDialog();
                        AAABaseResponseBean responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS && responseBean.getData() != null) {
                            mItemList.clear();
                            List<AAADeviceModel> list = mGson.fromJson(mGson.toJson(responseBean.getData()), new TypeToken<List<AAADeviceModel>>() {
                            }.getType());
                            AAADeviceModel nowDeviceModel = getTrackDeviceModel();
                            for (AAADeviceModel item : list) {
                                if (item.getDeviceId() == nowDeviceModel.getDeviceId()) {
                                    mItemList.add(new SelectionItemBean(item, true));
                                } else {
                                    mItemList.add(new SelectionItemBean(item, false));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            if (mItemList.size() == 0 && currentLabelId == R.id.all_devices) {
                                showNotBoundDeviceAndBackToBindPage();
                            }
                        } else {
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case TConstant.REQUEST_UNBIND_DEVICE: { // 解除绑定
                        dismisDialog();
                        AAABaseResponseBean responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            showMessage(R.string.unbind_succeed_prompt);
                            AAADeviceModel removedDevice = mItemList.get(currentOperatingPosition).getDeviceModel();
                            List<AAADeviceModel> trackDeviceList = getTrackDeviceList();
                            for (int i = 0; i < trackDeviceList.size(); i++) {
                                if (trackDeviceList.get(i).getDeviceImei().equals(removedDevice.getDeviceImei())) {
                                    trackDeviceList.remove(i);
                                    MainApplication.getInstance().setTrackDeviceList(trackDeviceList);
                                    break;
                                }
                            }
                            mItemList.remove(currentOperatingPosition);
                            mAdapter.notifyItemRemoved(currentOperatingPosition);
                            if (mItemList.size() == 0) {
                                showNotBoundDeviceAndBackToBindPage();
                            }
                        } else {
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case TConstant.REQUEST_UPDATE_DEVICE_STATUS: { // 更新设备状态
                        dismisDialog();
                        AAABaseResponseBean responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            showMessage(R.string.update_succeed_prompt);
                            queryDevicesList(currentLabelId);
                        } else {
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                        }
                    }
                    break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // MaterialDialog弹窗点击确认按钮后的回调
                        switch (message.arg1) {
                            case CONFIRM_TO_UNBIND: {
                                if (message.obj != null) {
                                    TransportObject transportObject = mGson.fromJson(mGson.toJson(message.obj), TransportObject.class);
                                    currentOperatingPosition = transportObject.getPosition();
                                    CarGpsRequestUtils.deleteDevice(transportObject.getDeviceImei(), getTrackUserModel(), mHandler);
                                }
                            }
                            break;
                            case CONFIRM_HAS_BEEN_LOST: {
                                if (message.obj != null) {
                                    TransportObject transportObject = mGson.fromJson(mGson.toJson(message.obj), TransportObject.class);
                                    CarGpsRequestUtils.updateDeviceStatus((int) transportObject.getDeviceId(), getTrackUserModel(), 2, mHandler);
                                }
                            }
                            break;
                            case CONFIRM_RETRIEVED: {
                                if (message.obj != null) {
                                    TransportObject transportObject = mGson.fromJson(mGson.toJson(message.obj), TransportObject.class);
                                    CarGpsRequestUtils.updateDeviceStatus((int) transportObject.getDeviceId(), getTrackUserModel(), 1, mHandler);
                                }
                            }
                            break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(PostMessage message) {
        if (CWConstant.POST_MESSAGE_BACK_TO_MAIN == message.getType()) {
            queryDevicesList(currentLabelId);
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
