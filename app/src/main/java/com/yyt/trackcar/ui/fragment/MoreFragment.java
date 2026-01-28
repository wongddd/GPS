package com.yyt.trackcar.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xui.widget.textview.badge.BadgeView;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.display.DensityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.dbflow.AppMsgModel;
import com.yyt.trackcar.dbflow.AppMsgModel_Table;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.activity.MainActivity;
import com.yyt.trackcar.ui.adapter.MoreAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.PortraitUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
@Page(name = "More", anim = CoreAnim.none)
public class MoreFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        View.OnClickListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar; // titleBar
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private MoreAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private BadgeView mBadgeView; // 系统消息提示红点
    private XUISimplePopup mMenuPopup; // 弹出菜单
    private ImageView mIvPortrait; // 头像
    private TextView mTvTitle; // 姓名文本
    private TextView mTvContent; // 设备号文本
    private TextView mTvPoint; // 积分文本
    private TextView mTvStep; // 计步文本
    private MainActivity mMainActivity;
    private BottomSheet mBottomSheet; // 选项弹窗
    private String mDeviceImei = "";
    private long mSendTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = mTitleBar;
        titleBar.setLeftText(R.string.more);
        titleBar.setLeftImageResource(0);
        titleBar.setLeftTextSize(titleBar.getCenterText().getTextSize());
        titleBar.getLeftText().getPaint().setFakeBoldText(true);
        TitleBar.Action nofityAction = new TitleBar.ImageAction(R.drawable.ic_notify) {
            @Override
            public void performAction(View view) {
                mBadgeView.setBadgeNumber(0);
                openNewPage(DeviceMessageFragment.class);
            }
        };
        titleBar.addAction(nofityAction);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_add) {
            @Override
            public void performAction(View view) {
                if (mMenuPopup == null)
                    initMenuPopup();
                mMenuPopup.showDown(view);
            }
        });
        View nofityView = titleBar.getViewByAction(nofityAction);
        if (nofityView != null) {
            mBadgeView = new BadgeView(getContext());
            mBadgeView.bindTarget(nofityView);
            mBadgeView.setBadgeGravity(Gravity.END | Gravity.TOP);
        }
        return titleBar;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    protected void initViews() {
        initMenuPopup();
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        refreshDeviceInfo();
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            String imei = deviceModel.getImei();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DeviceModel model = getDevice();
                    if (model != null && imei.equals(model.getImei()))
                        getWatchInfo();
                }
            }, 3000);
        }
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
                                    case 0: // 切换手表
//                                        Intent intent = new Intent(getActivity(),
//                                        MessageListActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        ActivityUtils.startActivity(intent);
                                        bundle = new Bundle();
                                        bundle.putInt(CWConstant.TYPE, 3);
                                        bundle.putString(CWConstant.TITLE,
                                                getString(R.string.change_device_title));
                                        openNewPage(CustomSelectorFragment.class, bundle);
                                        break;
                                    case 1: // 添加手表
                                        bundle = new Bundle();
                                        bundle.putInt(CWConstant.TYPE, 1);
                                        openNewPage(CameraCaptureFragment.class, bundle);
                                        break;
                                    case 2: // 解绑手表
                                        unBindDevice();
                                        break;
                                    case 3: // 手表二维码
                                        bundle = new Bundle();
                                        bundle.putString(CWConstant.TITLE,
                                                getString(R.string.device_qrcode));
                                        openNewPage(QRCodeFragment.class, bundle);
                                        break;
//                                    case 4: // 切换产品
//                                        bundle = new Bundle();
//                                        bundle.putInt(CWConstant.TYPE, 2);
//                                        openNewPage(SelectDeviceTypeFragment.class, bundle);
//                                        break;
//                                    case 4: // 我的客服
//
//                                        break;
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
        View headerView = getLayoutInflater().inflate(R.layout.header_view_more_old, mRecyclerView,
                false);
        headerView.findViewById(R.id.clInfo).setOnClickListener(this);
        headerView.findViewById(R.id.clPoint).setOnClickListener(this);
        headerView.findViewById(R.id.clStep).setOnClickListener(this);
        mIvPortrait = headerView.findViewById(R.id.ivPortrait);
        mTvTitle = headerView.findViewById(R.id.tvTitle);
        mTvContent = headerView.findViewById(R.id.tvContent);
        mTvPoint = headerView.findViewById(R.id.tvPoint);
        mTvStep = headerView.findViewById(R.id.tvStep);
//        String name = "设备号  ";
////        String level = " Lv.100 ";
////        String text = String.format("%s%s", name, level);
////        List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
////        list.add(new TextColorSizeHelper.SpanInfo(level,
////                com.xuexiang.xui.utils.DensityUtils.sp2px(15),
////                ContextCompat.getColor(mActivity, R.color.white),
////                ContextCompat.getColor(mActivity, R.color.colorTexNormal),
////                com.xuexiang.xui.utils.DensityUtils.dp2px(2), true));
////        mTvTitle.setText(TextColorSizeHelper.getTextSpan(mActivity, text, list));
////        mTvContent.setText(getString(R.string.device_imei, getDevice().getImei()));
////        mTvPoint.setText("100");
        int imgRes = R.mipmap.ic_default_pigeon_marker;
        mIvPortrait.setImageResource(imgRes);
        mTvContent.setText(getString(R.string.device_imei, ""));
        mTvStep.setText("0");
        mAdapter.addHeaderView(headerView);
    }

    /**
     * 刷新设备信息
     */
    private void refreshDeviceInfo() {
        DeviceSettingsModel stepModel = getDeviceSettings();
        if (mTvStep != null) {
            if (stepModel == null)
                mTvStep.setText("0");
            else
                mTvStep.setText(stepModel.getDevicestep());
        }
        refreshBindMsg();
        refreshDeviceInfo(null);
    }

    /**
     * 刷新绑定成员
     */
    public void refreshBindMsg() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        AppMsgModel appMsgModel = null;
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(AppMsgModel_Table.u_id.eq(userModel.getU_id()))
                            .and(AppMsgModel_Table.imei.eq(deviceModel.getImei()))
                            .and(AppMsgModel_Table.type.eq(27)));
            appMsgModel =
                    SQLite.select().from(AppMsgModel.class).where(operatorGroup).querySingle();
        }
        if (mMainActivity != null) {
            if (appMsgModel == null)
                mMainActivity.getMoreBadge().setBadgeNumber(0);
            else
                mMainActivity.getMoreBadge().setBadgeText("");
        }
        for (SectionItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null && CWConstant.MORE_BIND_MEMBER == itemBean.getType()) {
                itemBean.setSelect(appMsgModel != null);
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 刷新设备信息
     */
    private void refreshDeviceInfo(DeviceInfoModel infoModel) {
        DeviceModel deviceModel = getDevice();
        if (infoModel == null)
            infoModel = getDeviceInfo();
        if (mIvPortrait != null) {
            int imgRes = R.mipmap.ic_default_pigeon_marker;
            ImageLoadUtils.loadPortraitImage(getContext(), infoModel.getHead(),
                    imgRes, mIvPortrait);
        }
        if (mTvTitle != null && mTvContent != null) {
            if (deviceModel == null) {
                if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                    mTvTitle.setText(R.string.baby);
                else
                    mTvTitle.setText(R.string.device_name);
                mTvContent.setText(getString(R.string.device_imei, ""));
            } else {
                if (TextUtils.isEmpty(infoModel.getNickname())) {
                    if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                        mTvTitle.setText(R.string.baby);
                    else
                        mTvTitle.setText(R.string.device_name);
                } else
                    mTvTitle.setText(infoModel.getNickname());
                mTvContent.setText(getString(R.string.device_imei, deviceModel.getImei()));
            }
        }
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (getContext() == null)
            return;
        refreshDeviceInfo();
        getTodayStep();
    }

    /**
     * 解绑设备
     */
    private void unBindDevice() {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null && deviceModel.getStatus() == 1) {
            Context context = getContext();
            if (context != null) {
                mBottomSheet = new BottomSheet.BottomListSheetBuilder(context)
                        .setTitle(R.string.manager_unbind_title)
                        .addItem(getString(R.string.continue_unbind))
                        .addItem(getString(R.string.manager_transfer_prompt))
                        .addItem(getString(R.string.cancel))
                        .setIsCenter(true)
                        .setOnSheetItemClickListener(new BottomSheet
                                .BottomListSheetBuilder.OnSheetItemClickListener() {
                            @Override
                            public void onClick(BottomSheet dialog, View itemView,
                                                int position, String tag) {
                                dialog.dismiss();
                                if (position == 0)
                                    openNewPage(UnbindFragment.class);
                                else if (position == 1)
                                    openNewPage(BindMemberFragment.class);
                            }
                        })
                        .build();
                mBottomSheet.show();
            }
        } else
            mMaterialDialog =
                    DialogUtils.customMaterialDialog(getContext(),
                            mMaterialDialog,
                            getString(R.string.unbind),
                            getString(R.string.unbind_prompt),
                            getString(R.string.confirm),
                            getString(R.string.cancel), null,
                            CWConstant.DIALOG_UNBIND, mHandler);
    }

    /**
     * 查看宝贝资料
     */
    private void getWatchInfo() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getWatchInfo(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), deviceModel.getImei(), mHandler);
    }

    /**
     * 解绑
     */
    private void deleteDevice() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().deleteDevice(getContext(), userModel.getToken(),
                    deviceModel.getImei(), deviceModel.getU_id(), mHandler);
    }

    /**
     * 查找手表
     */
    private void findDevice() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().findDevice(getContext(),
                    getIp(), userModel.getToken(), deviceModel.getImei(),
                    mHandler);
    }

    /**
     * 监听
     */
    private void moniotrDevice(String phone) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().moniotrDevice(getContext(),
                    getIp(), userModel.getToken(), deviceModel.getImei(),
                    phone, mHandler);
    }

    /**
     * 获取手表设置目标步数
     */
    public void getTodayStep() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            if (!deviceModel.getImei().equals(mDeviceImei) || System.currentTimeMillis() - mSendTime > 3 * 60 * 1000) {
                mDeviceImei = deviceModel.getImei();
                mSendTime = System.currentTimeMillis();
                CWRequestUtils.getInstance().getTodayStep(getContext(), getIp(),
                        userModel.getToken(), deviceModel.getD_id(), mHandler);
            }
        }
    }

    /**
     * 获取手表设置目标步数
     */
    public void getStepGoal() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            if (!(deviceModel.getImei().equals(mDeviceImei) && System.currentTimeMillis() - mSendTime < 60 * 1000)) {
                mDeviceImei = deviceModel.getImei();
                mSendTime = System.currentTimeMillis();
                CWRequestUtils.getInstance().getStepGoal(getContext(), getIp(),
                        userModel.getToken(), deviceModel.getD_id(), mHandler);
            }
        }
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clInfo: // 宝贝信息
                openNewPage(PersonalCenterFragment.class);
                break;
            case R.id.clPoint: // 积分

                break;
            case R.id.clStep: // 计步
                openNewPage(StepCounterFragment.class);
                break;
            default:
                break;
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
                    case CWConstant.REQUEST_URL_GET_WATCH_INFO: // 查看宝贝资料
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    deviceModel = getDevice();
                                    DeviceInfoModel infoModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()), DeviceInfoModel.class);
                                    if (deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                        infoModel.setU_id(userModel.getU_id());
                                        infoModel.save();
                                        PortraitModel portraitModel = new PortraitModel();
                                        portraitModel.setImei(deviceModel.getImei());
                                        portraitModel.setUserId(deviceModel.getImei());
                                        if (TextUtils.isEmpty(infoModel.getNickname())) {
                                            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                                                portraitModel.setName(getString(R.string.baby));
                                            else
                                                portraitModel.setName(getString(R.string.device));
                                        } else
                                            portraitModel.setName(infoModel.getNickname());
                                        portraitModel.setUrl(infoModel.getHead());
                                        portraitModel.save();
                                        PortraitUtils.getInstance().updatePortrait(portraitModel);
                                        refreshDeviceInfo(infoModel);
                                    }
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_DELETE_DEVICE: // 解绑
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    List<DeviceModel> deviceList = getDeviceList();
                                    for (int i = 0; i < deviceList.size(); i++) {
                                        deviceModel = deviceList.get(i);
                                        if (deviceModel.getImei().equals(requestBean.getImei())) {
                                            OperatorGroup operatorGroup =
                                                    OperatorGroup.clause(OperatorGroup.clause()
                                                            .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                                            .and(DeviceModel_Table.d_id.eq(deviceModel.getD_id())));
                                            SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                                            deviceList.remove(i);
                                            break;
                                        }
                                    }
                                    if (deviceList.size() == 0) {
                                        MainApplication.getInstance().setDeviceModel(null);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    } else {
                                        MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                        userModel.setSelectImei(getDevice().getImei());
                                        userModel.save();
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                    }
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_STEP_GOAL: // 获取手表设置目标步数
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                DeviceSettingsModel bean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                DeviceSettingsModel.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setStep(bean.getStep());
                                    settingsModel.setDevicestep(bean.getDevicestep());
                                    settingsModel.save();
                                    if (mTvStep != null)
                                        mTvStep.setText(bean.getDevicestep());
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_TODAY_STEP: // 获取手表步数
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                DeviceSettingsModel bean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                DeviceSettingsModel.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setDevicestep(bean.getDevicestep());
                                    settingsModel.save();
                                    if (mTvStep != null)
                                        mTvStep.setText(bean.getDevicestep());
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_MONIOTR_DEVICE: // 语音监护
                    case CWConstant.REQUEST_URL_CAPT_DEVICE: // 远程监拍
                    case CWConstant.REQUEST_URL_FIND_DEVICE: // 查找手表
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getImei().equals(requestBean.getImei())) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    switch (msg.what) {
                                        case CWConstant.REQUEST_URL_MONIOTR_DEVICE: // 语音监护
                                            CWRequestUtils.getInstance().moniotrDevice(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    requestBean.getPhone(), mHandler);
                                            break;
                                        case CWConstant.REQUEST_URL_CAPT_DEVICE: // 远程监拍
                                            CWRequestUtils.getInstance().captDevice(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    requestBean.getCome(), mHandler);
                                            break;
                                        case CWConstant.REQUEST_URL_FIND_DEVICE: // 查找手表
                                            CWRequestUtils.getInstance().findDevice(getContext(),
                                                    resultBean.getLast_online_ip(),
                                                    requestBean.getToken(), requestBean.getImei(),
                                                    mHandler);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS)
                                XToastUtils.toast(R.string.send_success_prompt);
                            else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_UNBIND: // 解除绑定
                                deleteDevice();
                                break;
                            case CWConstant.MORE_REMOTE_PHOTO_TAKE: // 远程拍照
                                if (!NetworkUtils.isNetworkAvailable()) {
                                    RequestToastUtils.toastNetwork();
                                    return false;
                                }
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                if (userModel != null && deviceModel != null)
                                    CWRequestUtils.getInstance().captDevice(getContext(), getIp(),
                                            userModel.getToken(), deviceModel.getImei(), "",
                                            mHandler);
                                break;
                            case CWConstant.MORE_FIND_WATCH: // 查找手表
                                findDevice();
                                break;
                            default:
                                break;
                        }
                        break;
                    case CWConstant.HANDLE_INPUT_ACTION: // 输入回调
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_VOICE_MONITOR: // 语音监护
                                moniotrDevice((String) msg.obj);
                                break;
                            default:
                                break;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_CHANGE_DEVICE == event.getType()) {
            initItems();
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
            refreshDeviceInfo();
            getWatchInfo();
            getTodayStep();
//            AAADeviceModel deviceModel = getDevice();
//            if(deviceModel != null) {
//                String imei = deviceModel.getImei();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        AAADeviceModel model = getDevice();
//                        if(model != null && imei.equals(model.getImei()))
//                            getTodayStep();
//                    }
//                }, 3000);
//            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMainActivity != null) {
            if (mMainActivity.getBottomNavigation() != null && mMainActivity.getBottomNavigation().getSelectedItemId() == R.id.navMore)
                refreshData();
        }
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        DialogUtils.dismiss(mBottomSheet);
        super.onDestroy();
    }

}
