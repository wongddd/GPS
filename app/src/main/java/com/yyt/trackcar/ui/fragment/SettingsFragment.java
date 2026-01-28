package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserSettingsModel;
import com.yyt.trackcar.ui.adapter.SettingsAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      SettingsFragment
 * @ author:        QING
 * @ createTime:    2020/3/6 12:21
 * @ describe:      TODO 设置页面
 */
@Page(name = "Settings")
public class SettingsFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private SettingsAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.settings);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        if (!SettingSPUtils.getInstance().getBoolean(CWConstant.DEVICE_SETTINGS, false))
            getNotifyStatus();
        SettingSPUtils.getInstance().putBoolean(CWConstant.DEVICE_SETTINGS, true);
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getSettingsData(mActivity, mItemList);
        UserSettingsModel settingsModel = getUserSettingsModel();
        for (SectionItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null) {
                switch (itemBean.getType()) {
                    case 0: // 到家
                        itemBean.setSelect(settingsModel != null && settingsModel.getArriveHome() == 1);
                        break;
                    case 1: // SOS
                        itemBean.setSelect(settingsModel != null && settingsModel.getSos() == 1);
                        break;
                    case 2: // 定位
                        itemBean.setSelect(settingsModel != null && settingsModel.getLocation() == 1);
                        break;
                    case 3: // 添加朋友
                        itemBean.setSelect(settingsModel != null && settingsModel.getAddFriend() == 1);
                        break;
                    case 4: // 计步
                        itemBean.setSelect(settingsModel != null && settingsModel.getStep() == 1);
                        break;
                    case 5: // 上传图片
                        itemBean.setSelect(settingsModel != null && settingsModel.getUploadPhoto() == 1);
                        break;
                    case 6: // 通话日志
                        itemBean.setSelect(settingsModel != null && settingsModel.getPhoneLog() == 1);
                        break;
                    case 7: // 短信
                        itemBean.setSelect(settingsModel != null && settingsModel.getCost() == 1);
                        break;
                    case 8: // 升级
                        itemBean.setSelect(settingsModel != null && settingsModel.getUpgrade() == 1);
                        break;
                    case 9: // 电子围栏
                        itemBean.setSelect(settingsModel != null && settingsModel.getFence() == 1);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new SettingsAdapter(mItemList, this);
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
     * 获取用户打开关闭 通知消息
     */
    private void getNotifyStatus() {
        UserModel userModel = getUserModel();
        if (userModel != null)
            CWRequestUtils.getInstance().getNotifyStatus(getContext(), userModel.getToken(),
                    mHandler);
    }

    /**
     * 用户打开关闭 通知消息
     *
     * @param bean 请求对象
     */
    private void setNotifyStatus(UserSettingsModel bean) {
        bean.save();
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null)
            CWRequestUtils.getInstance().setNotifyStatus(MainApplication.getContext(),
                    userModel.getToken(), bean.getArriveHome(), bean.getSos(), bean.getLocation(),
                    bean.getAddFriend(), bean.getStep(), bean.getUploadPhoto(),
                    bean.getPhoneLog(), bean.getCost(), bean.getUpgrade(), bean.getFence(),
                    bean.getPhoneVoice(), bean.getPhoneVibration(), mHandler);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        if (position >= 0 && position < mItemList.size()) {
//            BaseItemBean itemBean = mItemList.get(position).t;
//            if (itemBean != null) {
//                switch (itemBean.getType()) {
//                    default:
//                        break;
//                }
//            }
//        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int tag = (int) buttonView.getTag();
        for (SectionItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null && tag == itemBean.getType()) {
                itemBean.setSelect(isChecked);
                break;
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_NOTIFY_STATUS: // 获取用户打开关闭 通知消息
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    UserSettingsModel settingsModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()), UserSettingsModel.class);
                                    settingsModel.setU_id(userModel.getU_id());
                                    settingsModel.save();
                                    mItemList.clear();
                                    initItems();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_NOTIFY_STATUS: // 用户打开关闭 通知消息
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    UserSettingsModel settingsModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), UserSettingsModel.class);
                                    settingsModel.setU_id(userModel.getU_id());
                                    settingsModel.save();
                                }
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

    @Override
    public void onDestroyView() {
        UserSettingsModel settingsModel = getUserSettingsModel();
        if (settingsModel != null) {
            boolean isChange = false;
            for (SectionItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null) {
                    int isOpen = itemBean.isSelect() ? 1 : 0;
                    switch (itemBean.getType()) {
                        case 0: // 到家
                            if (isOpen != settingsModel.getArriveHome())
                                isChange = true;
                            settingsModel.setArriveHome(isOpen);
                            break;
                        case 1: // SOS
                            if (isOpen != settingsModel.getSos())
                                isChange = true;
                            settingsModel.setSos(isOpen);
                            break;
                        case 2: // 定位
                            if (isOpen != settingsModel.getLocation())
                                isChange = true;
                            settingsModel.setLocation(isOpen);
                            break;
                        case 3: // 添加朋友
                            if (isOpen != settingsModel.getAddFriend())
                                isChange = true;
                            settingsModel.setAddFriend(isOpen);
                            break;
                        case 4: // 计步
                            if (isOpen != settingsModel.getStep())
                                isChange = true;
                            settingsModel.setStep(isOpen);
                            break;
                        case 5: // 上传图片
                            if (isOpen != settingsModel.getUploadPhoto())
                                isChange = true;
                            settingsModel.setUploadPhoto(isOpen);
                            break;
                        case 6: // 通话日志
                            if (isOpen != settingsModel.getPhoneLog())
                                isChange = true;
                            settingsModel.setPhoneLog(isOpen);
                            break;
                        case 7: // 短信
                            if (isOpen != settingsModel.getCost())
                                isChange = true;
                            settingsModel.setCost(isOpen);
                            break;
                        case 8: // 升级
                            if (isOpen != settingsModel.getUpgrade())
                                isChange = true;
                            settingsModel.setUpgrade(isOpen);
                            break;
                        case 9: // 电子围栏
                            if (isOpen != settingsModel.getFence())
                                isChange = true;
                            settingsModel.setFence(isOpen);
                            break;
                        default:
                            break;
                    }
                }
            }
            if (isChange)
                setNotifyStatus(settingsModel);
        }
        super.onDestroyView();
    }

}
