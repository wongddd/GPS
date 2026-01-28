package com.yyt.trackcar.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.display.DensityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.activity.LoginActivity;
import com.yyt.trackcar.ui.adapter.MoreAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DataUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      MoreFragment
 * @ author:        QING
 * @ createTime:    2020-03-02 18:16
 * @ describe:      TODO 更多页面
 */
@Page(name = "AAAMore", anim = CoreAnim.none)
public class AAAMoreFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar; // titleBar
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private MoreAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private XUISimplePopup mMenuPopup; // 弹出菜单

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = mTitleBar;
        titleBar.setTitle(R.string.my);
        titleBar.setLeftImageResource(0);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.add) {
            @Override
            public void performAction(View view) {
                if (mMenuPopup == null)
                    initMenuPopup();
                mMenuPopup.showDown(view);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        initMenuPopup();
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
    }

    /**
     * 初始化弹出菜单
     */
    private void initMenuPopup() {
        mMenuPopup = new XUISimplePopup(mActivity, DataServer.getModeMenuData(mActivity))
                .create(DensityUtils.dip2px(216), DensityUtils.dip2px(240),
                        new XUISimplePopup.OnPopupItemClickListener() {
                            @Override
                            public void onItemClick(XUISimpleAdapter adapter, AdapterItem item,
                                                    int position) {
                                Bundle bundle;
                                switch (position) {
                                    case 0: // 切换设备
                                        bundle = new Bundle();
                                        bundle.putInt(CWConstant.TYPE, 20);
                                        bundle.putString(CWConstant.TITLE,
                                                getString(R.string.switch_device_new));
                                        openNewPage(CustomSelectorFragment.class, bundle);
                                        break;
                                    case 1: // 手表二维码
                                        bundle = new Bundle();
                                        bundle.putString(CWConstant.TITLE,
                                                getString(R.string.device_qrcode_new));
                                        openNewPage(QRCodeFragment.class, bundle);
                                        break;
                                    case 2: // 添加设备
                                        bundle = new Bundle();
                                        bundle.putInt(CWConstant.TYPE, 1);
                                        openNewPage(InputImeiFragment.class, bundle);
                                        break;
                                    case 3: // 解绑设备
                                        unBindDevice();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getModeData(mActivity, mItemList);
//        if (!DataUtils.isAgent()) {
//            for (int i = 0; i < mItemList.size(); i++) {
//                if (mItemList.get(i).t != null
//                        && mItemList.get(i).t.getTitle().equals(getString(R.string
//                        .agent_sale_device_management))) {
//                    mItemList.remove(i);
//                    mItemList.remove(i);
//                    break;
//                }
//            }
//        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new MoreAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化脚布局
     */
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_more, mRecyclerView,
                false);
        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
        TextView tvContent = headerView.findViewById(R.id.tvContent);
        TextView tvCreateTime = headerView.findViewById(R.id.tvCreateTime);
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null) {
            tvTitle.setText(String.format("%s:%s", getString(R.string.user_id),
                    userModel.getUserId()));
            tvContent.setText(SettingSPUtils.getInstance().getString(CWConstant.USERNAME_LOGIN,
                    ""));
            long createTime = DataUtils.getCreateTime();
            if (createTime != 0) {
                tvCreateTime.setVisibility(View.VISIBLE);
                tvCreateTime.setText(getString(R.string.create_time,
                        TimeUtils.formatUTC(DataUtils.getCreateTime(), "yyyy/MM/dd")));
            }
        }
        mAdapter.addHeaderView(headerView);
    }

    /**
     * 解绑设备
     */
    private void unBindDevice() {
        AAADeviceModel deviceModel = getTrackDeviceModel();
        mMaterialDialog =
                DialogUtils.customMaterialDialog(getContext(),
                        mMaterialDialog,
                        getString(R.string.unbind),
                        String.format("%s%s",
                                deviceModel.getDeviceName() + "(" + deviceModel.getDeviceImei() + ")", getString(R.string.unbind_prompt)),
                        getString(R.string.confirm),
                        getString(R.string.cancel), null,
                        CWConstant.DIALOG_UNBIND, mHandler);
    }

    /**
     * 解绑
     */
    private void deleteDevice() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
//        UserModel userModel = getUserModel();
//        DeviceModel deviceModel = getDevice();
//        if (userModel != null && deviceModel != null) {
//            CWRequestUtils.getInstance().deleteDevice(getContext(), userModel.getToken(),
//                    deviceModel.getImei(), deviceModel.getU_id(), mHandler);
//        }
        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (userModel != null && deviceModel != null) {
            CarGpsRequestUtils.deleteDevice(deviceModel.getDeviceImei(), userModel, mHandler);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                Bundle bundle;
                switch (itemBean.getType()) {
                    case CWConstant.MORE_UPDATE_FIRMWARE: // 手表固件升级
                        openNewPage(UpdateFirmwareFragment.class);
                        break;
                    case CWConstant.MORE_BIND_MEMBER: // 绑定成员
                        openNewPage(BindMemberFragment.class);
                        break;
                    case CWConstant.MORE_ADDRESS_BOOK: // 宝贝通讯录
                        openNewPage(DeviceAddressBookFragment.class);
                        break;
                    case CWConstant.MORE_SMS_COLLECTION: // 代收短信
                        openNewPage(SmsCollectionFragment.class);
                        break;
                    case CWConstant.MORE_WATCH_BILL: // 手表话费
                        openNewPage(WatchBillFragment.class);
                        break;
                    case CWConstant.MORE_BAN_CLASSES: // 上课禁用
                        openNewPage(BanClassesFragment.class);
                        break;
                    case CWConstant.MORE_REFUSE_STRANGERS: // 拒接陌生人
                        openNewPage(RefuseStrangersFragment.class);
                        break;
                    case CWConstant.MORE_SCHOOL_GUARDIAN: // 上学守护
                        openNewPage(SchoolGuardianFragment.class);
                        break;
                    case CWConstant.MORE_TIME_SWITCH: // 定时开关机
                        openNewPage(TimeSwitchFragment.class);
                        break;
                    case CWConstant.MORE_AUTOMATIC_CONNECTION: // 自动接通
                        openNewPage(AutomaticConnectionFragment.class);
                        break;
                    case CWConstant.MORE_RESERVED_ELECTRIC: // 预留电量
                        openNewPage(ReservedElectricFragment.class);
                        break;
                    case CWConstant.MORE_DEVICE_WIFI: // 手表WIFI
                        openNewPage(DeviceWifiFragment.class);
                        break;
                    case CWConstant.MORE_DEIVCE_SETTINGS: // 手表设置
                        openNewPage(DeviceSettingsFragment.class);
                        break;
                    case CWConstant.MORE_APP_SETTINGS: // App设置
                        openNewPage(AppSettingsFragment.class);
                        break;
                    case CWConstant.MORE_BIND_AND_UNBIND: // 绑定与解绑
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 1);
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        openNewPage(QRCodeFragment.class, bundle);
                        break;
                    case CWConstant.MORE_CALL_RECORD: // 通话记录
                        openNewPage(CallRecordFragment.class);
                        break;
                    case CWConstant.MORE_FENCE: // 电子围栏
                        openNewPage(FenceFragment.class);
                        break;
                    case CWConstant.MORE_ALARM_CLOCK: // 闹钟设置
                        openNewPage(AlarmClockFragment.class);
                        break;
                    case CWConstant.MORE_REMOTE_PHOTO_TAKE: // 远程拍照
                        openNewPage(RemotePhotoTakeFragment.class);
                        break;
                    case CWConstant.MORE_FIND_WATCH: // 查找手表
                        mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                                mMaterialDialog, getString(R.string.prompt),
                                getString(R.string.instruct_send_prompt, itemBean.getTitle()),
                                getString(R.string.confirm), getString(R.string.cancel), null,
                                itemBean.getType(), mHandler);
                        break;
                    case CWConstant.MORE_VOICE_MONITOR: // 语音监护
                        mMaterialDialog = DialogUtils.customInputMaterialDialog(getContext(),
                                mMaterialDialog, getString(R.string.prompt), null,
                                getString(R.string.voice_monitor_hint), null,
                                InputType.TYPE_CLASS_NUMBER
                                , 20, 1, getString(R.string.confirm), getString(R.string.cancel),
                                CWConstant.DIALOG_VOICE_MONITOR, mHandler);
                        break;
                    case CWConstant.MORE_SEDENTARY_REMINDER: // 久坐提醒
                        openNewPage(HealthSettingsFragment.class);
                        break;
                    case CWConstant.MORE_FALL_OFF: // 脱落提醒
                        openNewPage(FallOffFragment.class);
                        break;
                    case CWConstant.AGENT_DEVICE: // 经销设备管理
                        openNewPage(SearchAgentUserFragment.class);
                        break;
                    case CWConstant.LOGIN_OUT: // 退出登录
                        mMaterialDialog =
                                DialogUtils.customMaterialDialog(getContext(),
                                        mMaterialDialog,
                                        getString(R.string.login_out),
                                        getString(R.string.login_out_prompt_new),
                                        getString(R.string.confirm),
                                        getString(R.string.cancel), null,
                                        CWConstant.DIALOG_LOGIN_OUT, mHandler);
                        break;

                    case CWConstant.PRECAUTIONS: // 注意事项
                        openNewPage(PrecautionsFragment.class);
                        break;
                    case CWConstant.ONLINE_UPDATE:
                        Uri uri = Uri.parse("market://details?id=com.yyt.trackcar");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.android.vending");//应用市场包名
                        if (intent.resolveActivity(mActivity.getPackageManager()) == null)
                            XToastUtils.toast("Google Play Store not installed");
                        else
                            mActivity.startActivity(intent);
                        break;
                    case CWConstant.MORE_BLUE_TOOTH: // 蓝牙连接
                        openNewPage(BlueToothFragment.class);
                        break;
                    case CWConstant.MORE_MESSAGE_CENTER: // 消息中心
                        openNewPage(MessageCenterFragment.class);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                RequestBean requestBean;
                UserModel userModel;
                DeviceModel deviceModel;
                switch (msg.what) {
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 弹窗点击确认按钮的处理
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_UNBIND: // 解除绑定
                                showDialog();
                                deleteDevice();
                                break;
                            case CWConstant.DIALOG_LOGIN_OUT:
                                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                                MainApplication.getInstance().setTrackDeviceModel(null);
                                MainApplication.getInstance().setTrackUserModel(null);
                                MainApplication.getInstance().getTrackDeviceList().clear();
                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                ActivityUtils.startActivity(LoginActivity.class);
                                break;
                            default:
                                break;
                        }
                        break;
                    case TConstant.REQUEST_UNBIND_DEVICE: // 解绑设备
                        if (msg.obj != null) {
                            try {
                                AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                                if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                    showDialog();
                                    CarGpsRequestUtils.getDeviceList(getTrackUserModel(), null,
                                            mHandler);
                                } else {
                                    showMessage(ErrorCode.getResId(response.getCode()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_DEVICE_LIST: // 获取设备列表
                        dismisDialog();
                        if (msg.obj != null) {
                            try {
                                AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                                if (response.getCode() == TConstant.RESPONSE_SUCCESS
                                        || response.getCode() == TConstant.RESPONSE_SUCCESS_NEW) {
                                    List<AAADeviceModel> deviceList = new ArrayList<>();
                                    List list = (ArrayList) response.getData();
                                    for (int i = 0; i < list.size(); i++) {
                                        deviceList.add(mGson.fromJson(mGson.toJson(list.get(i)),
                                                AAADeviceModel.class));
                                    }
                                    showMessage(R.string.unbind_device_success_tips);
                                    if (deviceList.size() == 0) {
                                        showMessage(R.string.user_no_bound_device_tips);
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                        mActivity.finish();
                                    } else {
                                        MainApplication.getInstance().setTrackDeviceList(deviceList);
                                        MainApplication.getInstance().setTrackDeviceModel(deviceList.get(0));
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
//                                        EventBus.getDefault().post(new PostMessage(CWConstant
//                                        .POST_MESSAGE_CHANGE_DEVICE_TYPE));
                                    }
                                } else
                                    showMessage(ErrorCode.getResId(response.getCode()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            return false;
        }
    });

}
