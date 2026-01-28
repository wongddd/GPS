package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.activity.DeviceModelSettingActivity;
import com.yyt.trackcar.ui.activity.DeviceSettingActivity;
import com.yyt.trackcar.ui.activity.ElectronicActivity;
import com.yyt.trackcar.ui.activity.HistoryActivity;
import com.yyt.trackcar.ui.activity.RemoteControlActivity;
import com.yyt.trackcar.ui.activity.ReportActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.Constant;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TransformImageAppearance;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主页
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "HomeOld", anim = CoreAnim.none)
public class HomeOldFragment extends BaseFragment {
    /**
     * 实时追踪
     */
    @BindView(R.id.first) //实时追踪
            RelativeLayout first;
    /**
     * 轨迹回放
     */
    @BindView(R.id.second) //轨迹回放
            RelativeLayout second;
    /**
     * 电子围栏
     */
    @BindView(R.id.third) //电子围栏
            RelativeLayout third;
    /**
     * 远程控制
     */
    @BindView(R.id.fourth) //远程控制
            RelativeLayout fourth;
    /**
     * 旅程报表
     */
    @BindView(R.id.fifth)  //旅程报表
            RelativeLayout fifth;
    /**
     * 报警信息
     */
    @BindView(R.id.sixth)  //报警信息
            RelativeLayout sixth;
    /**
     * 参数设置
     */
    @BindView(R.id.seventh) //设备参数
            RelativeLayout seventh;
    /**
     * 训飞
     */
    @BindView(R.id.ninth)  //训飞
            RelativeLayout ninth;
    /**
     * 搜索设备
     */
    @BindView(R.id.tenth) //搜索设备
            RelativeLayout tenth;
    /**
     * 预设参数
     */
    @BindView(R.id.twelve) // 预设参数
            RelativeLayout twelve;

    /**
     * 下级经销商管理
     */
    @BindView(R.id.eleventh)
    RelativeLayout eleventh;
    @BindView(R.id.ll_column_first)
    LinearLayout llColumnFirst;
    @BindView(R.id.ll_column_second)
    LinearLayout llColumnSecond;
    @BindView(R.id.ll_column_third)
    LinearLayout llColumnThird;

    @BindView(R.id.iv_headPortrait)
    ImageView deviceHeadPortrait;
    @BindView(R.id.tv_device_name)
    TextView deviceNickname;
    @BindView(R.id.tv_imei)
    TextView deviceImei;
    @BindView(R.id.tv_information)
    TextView deviceInformation;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInfo;
    @BindView(R.id.iv_arrow_right)
    ImageView ivArrowRight;


    private AAADeviceModel mDeviceModel;
    /**
     * 当前选中设备的设备类型
     */
    private int type = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.aaa_fragment_home;
    }

    @Override
    protected void initViews() {
        refreshData();
        initInformationCard();
        restructureVisual();
    }

    private void restructureVisual() {  //切换首页活页卡显示项
        boolean isAgent = SettingSPUtils.getInstance().getInt(TConstant.IS_AGENT, 0) == 1;
        ivArrowRight.setVisibility(View.VISIBLE);
        tenth.setVisibility(View.GONE);
        if (isAgent) {
            eleventh.setVisibility(View.VISIBLE);
        } else {
            eleventh.setVisibility(View.GONE);
        }
        if (type == 2) {
            ninth.setVisibility(View.VISIBLE);
            fourth.setVisibility(View.GONE);
            fifth.setVisibility(View.GONE); //旅程报表
            sixth.setVisibility(View.GONE);
        } else {
            ninth.setVisibility(View.GONE);
            fourth.setVisibility(View.VISIBLE);
            fifth.setVisibility(View.VISIBLE); //旅程报表
            sixth.setVisibility(View.VISIBLE);
        }
    }

    private void refreshData() { // 刷新首页数据
        mDeviceModel = getTrackDeviceModel();
        type = mDeviceModel.getDeviceType();
    }

    private void initInformationCard() { // 更新顶部信息卡内容
        String online;
        int deviceType = mDeviceModel.getDeviceType();
        if (mDeviceModel.isOnlineStatus() && mDeviceModel.getLastMotionStatus() != null &&
                mDeviceModel.getLastMotionStatus() == 1)
            online = getString(R.string.device_sport);
        else if (mDeviceModel.isOnlineStatus())
            online = getString(R.string.device_motionless);
        else
            online = getString(R.string.offline);
        String onOff;
        if (mDeviceModel.isOnlineStatus() && mDeviceModel.getEngineStatus() != null
                && mDeviceModel.getEngineStatus() == 1)
            onOff = getString(R.string.on);
        else
            onOff = getString(R.string.off);
        float vol = 0;
        if (!TextUtils.isEmpty(mDeviceModel.getLastDeviceVol()) && !mDeviceModel.getLastDeviceVol().equals("null")) {
            try {
                vol = Float.parseFloat(mDeviceModel.getLastDeviceVol());
                if (vol < 0)
                    vol = 0;
                else if (vol > 100)
                    vol = 100;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        deviceHeadPortrait.setImageResource(R.mipmap.ic_default_pigeon_marker);
        if (mDeviceModel != null) {
            String headPic = mDeviceModel.getHeadPic();
            if (headPic != null) {
                String[] strings = headPic.split(".com/");
                if (strings.length > 1)
                    Glide.with(mActivity).asBitmap().load(mDeviceModel.getHeadPic()).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource,
                                                    @Nullable Transition<? super Bitmap> transition) {
                            deviceHeadPortrait.setImageBitmap(TransformImageAppearance.transformBitmapToRound(resource));
                        }
                    });
            }
            deviceNickname.setText(mDeviceModel.getDeviceName() == null ?
                    getString(R.string.device) : mDeviceModel.getDeviceName());
            deviceImei.setText(String.format("IMEI: %s", mDeviceModel.getDeviceImei()));
            if (deviceType == 1)
                deviceInformation.setText(String.format("%s ACC %s,%s:%s%%", online, onOff,
                        getString(R.string.device_power), vol));
            else if (deviceType == 2)
                deviceInformation.setText(String.format("%s:%s",
                        getString(R.string.device_version), mDeviceModel.getVersion()));
//            deviceInformation.setVisibility(View.GONE);
//                deviceInformation.setText(String.format("%s %s%s%%", online, getString(R.string
//                .device_power), vol));
        }
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.home);
        titleBar.setLeftImageResource(0);
        return titleBar;
    }

    @OnClick({R.id.first, R.id.second, R.id.third, R.id.fourth
            , R.id.fifth, R.id.sixth, R.id.seventh, R.id.ninth
            , R.id.tenth, R.id.eleventh, R.id.twelve, R.id.rl_device_information})
    public void onClick(View view) {
        Bundle bundle;
        switch (view.getId()) {
            case R.id.first: // 实时跟踪
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else {
                    bundle = new Bundle();
                    bundle.putString(TConstant.TITLE, getString(R.string.home_tracking));
                    bundle.putInt(TConstant.WHERE_FROM, 1);
                    if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 0)
                        openNewPage(MonitorAMapFragment.class, bundle);
//                        PageOption.to(AAATrackingFragment.class)
//                                .setAddToBackStack(false)
//                                .setNewActivity(true)
//                                .open(this);
                    else
                        openNewPage(MonitorGMapFragment.class, bundle);
                }
                break;
            case R.id.second: // 轨迹回放
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else
                    ActivityUtils.startActivity(HistoryActivity.class);
                break;
            case R.id.third: // 电子围栏
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else
                    ActivityUtils.startActivity(ElectronicActivity.class);
                break;
            case R.id.fourth: // 远程控制
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else
                    ActivityUtils.startActivity(RemoteControlActivity.class);
                break;
            case R.id.fifth: // 旅程报表
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else {
                    bundle = new Bundle();
                    bundle.putInt(Constant.TYPE, 1);
                    startActivity(bundle, ReportActivity.class);
                }
                break;
            case R.id.sixth: // 历史记录 or 报警记录
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else {
                    bundle = new Bundle();
//                    bundle.putInt(Constant.TYPE, 4);
                    startActivity(bundle, ReportActivity.class);
//                    startActivity(ReportActivity.class);
                }
                break;
            case R.id.seventh: // 设备参数
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else {
                    bundle = new Bundle();
                    bundle.putInt(TConstant.TYPE, 1);
                    startActivity(bundle, DeviceSettingActivity.class);
                }
                break;
            case R.id.ninth:  //训飞
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else {
                    openNewPage(FlightTrainingFragment.class);
                }
                break;
            case R.id.tenth: //查找设备
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else {
                    openNewPage(SearchDeviceFragment.class);
                }
                break;
            case R.id.eleventh: // 下级经销商管理
                openNewPage(TreeNodeFragment.class);
//                AAAUserModel userModel = getTrackUserModel();
//                bundle = new Bundle();
//                bundle.putString(CWConstant.TITLE, getString(R.string.dealer_manage));
//                bundle.putString(CWConstant.URL, "http://gps88888.com/#/subordinate?id=" +
//                userModel.getUserId() + "&token=" + userModel.getToken());
////                bundle.putString(CWConstant.URL, "http://gps88888.com/#/appMultipleHistory?id="
// + userModel.getUserId() + "&token=" + userModel.getToken());
////                KLog.e(bundle.getString(CWConstant.URL));
//                openNewPage(AAAWebFragment.class, bundle);
//                startActivity(WebActivity.class);
                break;
            case R.id.twelve:
                if (getTrackDeviceList().size() == 0)
                    showMessage(R.string.no_device_tips);
                else {
                    bundle = new Bundle();
                    bundle.putInt(TConstant.TYPE, 1);
                    startActivity(bundle, DeviceModelSettingActivity.class);
                }
                break;
            case R.id.rl_device_information:
                openNewPage(BabyInfoFragment.class);
//                openNewPage(ScanQuickResponseCodeFragment.class);
            default:
                break;
        }
    }

    public void onSwitchToThisPage() { // 底部导航栏切换到首页页面
        this.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_CHANGE_DEVICE == event.getType()) {
            refreshData();
            initInformationCard();
            restructureVisual();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
        initInformationCard();
    }
}
