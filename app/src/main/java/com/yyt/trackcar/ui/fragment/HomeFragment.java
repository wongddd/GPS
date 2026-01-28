package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.HomeMultiItemBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.activity.DeviceModelSettingActivity;
import com.yyt.trackcar.ui.activity.DeviceSettingActivity;
import com.yyt.trackcar.ui.activity.ElectronicActivity;
import com.yyt.trackcar.ui.activity.HistoryActivity;
import com.yyt.trackcar.ui.activity.RemoteControlActivity;
import com.yyt.trackcar.ui.activity.ReportActivity;
import com.yyt.trackcar.ui.activity.WebActivity;
import com.yyt.trackcar.ui.adapter.HomeAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.Constant;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.ViewDataUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      HomeNewFragment
 * @ author:        QING
 * @ createTime:    2023/3/24 16:43
 * @ describe:      TODO 首页
 */
@Page(name = "Home", anim = CoreAnim.none)
public class HomeFragment extends BaseFragment implements View.OnClickListener,
        BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar; // titleBar
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private HomeAdapter mAdapter; // 适配器
    private final List<HomeMultiItemBean> mItemList = new ArrayList<>(); // 列表
    private View mHeaderView; // 头布局

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = mTitleBar;
        titleBar.setTitle(R.string.home);
        titleBar.setLeftImageResource(0);
        titleBar.addAction(new TitleBar.TextAction(getString(R.string
                .blue_tooth)) {

            @Override
            public void performAction(View view) {
                openNewPage(BlueToothFragment.class);
            }

        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
    }

    /**
     * 始化列表信息
     */
    private void initItems() {
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (deviceModel != null) {
            DataServer.getHomeItemData(mActivity, deviceModel.getDeviceType(), mItemList);
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new HomeAdapter(mItemList);
        mAdapter.setOnItemChildClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        mHeaderView = getLayoutInflater().inflate(R.layout.header_view_device_info,
                mRecyclerView, false);
        mHeaderView.findViewById(R.id.clBtn).setVisibility(View.VISIBLE);
        mHeaderView.findViewById(R.id.ivArrow).setVisibility(View.VISIBLE);
        mHeaderView.findViewById(R.id.xlLayout).setOnClickListener(this);
        ViewDataUtils.initDeviceInfoView(getContext(), mHeaderView, getTrackDeviceModel());
        mAdapter.addHeaderView(mHeaderView);
    }

    /**
     * 点击主页底部导航栏时调用
     */
    public void onSwitchToThisPage() {
        initItems();
        ViewDataUtils.initDeviceInfoView(getContext(), mHeaderView, getTrackDeviceModel());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        AAADeviceModel deviceModel;
        switch (v.getId()) {
            case R.id.xlLayout: // 设备信息
                deviceModel = getTrackDeviceModel();
                if (deviceModel == null || TextUtils.isEmpty(deviceModel.getDeviceImei())) {
                    showMessage(R.string.no_device_tips);
                } else {
                    bundle = new Bundle();
                    bundle.putString(TConstant.DEVICE_IMEI, deviceModel.getDeviceImei());
                    openNewPage(BabyInfoFragment.class, bundle);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            HomeMultiItemBean model = mItemList.get(position);
            List<BaseItemBean> list = model.getList();
            BaseItemBean itemModel = null;
            if (list != null) {
                if (view.getId() == R.id.xlLayoutFirst && list.size() > 0) {
                    itemModel = list.get(0);
                } else if (view.getId() == R.id.xlLayoutSecond && list.size() > 1) {
                    itemModel = list.get(1);
                } else if (view.getId() == R.id.xlLayoutThird && list.size() > 2) {
                    itemModel = list.get(2);
                }
            }
            if (itemModel != null) {
                Bundle bundle;
                AAADeviceModel deviceModel;
                switch (itemModel.getType()) {
                    case CWConstant.HOME_MAP_CENTER: // 地图中心
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else {
                            bundle = new Bundle();
                            bundle.putString(TConstant.TITLE, getString(R.string.home_tracking));
//                            bundle.putInt(TConstant.WHERE_FROM, 2);
                            if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 0)
                                openNewPage(MonitorAMapFragment.class, bundle);
                            else
                                openNewPage(MonitorGMapFragment.class, bundle);
                        }
                        break;
                    case CWConstant.HOME_TRACK_PLAYBACK: // 轨迹回放
                        startActivity(WebActivity.class);
                        break;
                    case CWConstant.HOME_SAFE_ZONE: // 电子围栏
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else
                            ActivityUtils.startActivity(ElectronicActivity.class);
//                        openNewPage(BlueToothFragment.class);
                        break;
                    case CWConstant.HOME_TRAIN_FLIGHT: // 训飞
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else {
                            openNewPage(FlightTrainingFragment.class);
                        }
                        break;
                    case CWConstant.HOME_PRESET_PARAMS: // 预约设定
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else {
                            bundle = new Bundle();
                            bundle.putInt(TConstant.TYPE, 1);
                            startActivity(bundle, DeviceModelSettingActivity.class);
                        }
                        break;
                    case CWConstant.HOME_TRAJECTORY_ANALYSIS: // 轨迹分析
                        deviceModel = getTrackDeviceModel();
                        if (deviceModel == null)
                            showMessage(R.string.no_device_tips);
                        else {
                            bundle = new Bundle();
                            bundle.putInt(TConstant.TYPE, 1);
                            startActivity(bundle, WebActivity.class);
//                            bundle = new Bundle();
//                            bundle.putString(TConstant.TITLE, getString(R.string
//                            .trajectory_analysis));
//                            bundle.putString(TConstant.IMEI_NO,
//                                    deviceModel.getDeviceImei());
//                            bundle.putInt("needScreenshot", 1);
//                            if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 1)
//                                startActivity(bundle, HistoryGMapActivity.class);
//                            else
//                                startActivity(bundle, HistoryAMapActivity.class);
                        }
                        break;
                    case CWConstant.HOME_SEARCH_DEVICE: // 搜索设备
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else {
                            openNewPage(SearchDeviceFragment.class);
                        }
                        break;
                    case CWConstant.HOME_DISTRIBUTION_MANAGEMENT: // 经销管理
                        openNewPage(TreeNodeFragment.class);
                        break;
                    case CWConstant.HOME_SEND_ORDER: // 发送指令
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else
                            ActivityUtils.startActivity(RemoteControlActivity.class);
                        break;
                    case CWConstant.HOME_TRIP_REPORT: // 旅程报表
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else {
                            bundle = new Bundle();
                            bundle.putInt(Constant.TYPE, 1);
                            startActivity(bundle, ReportActivity.class);
                        }
                        break;
                    case CWConstant.HOME_SUMMARY_RECORD: // 历史记录
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else {
                            bundle = new Bundle();
                            startActivity(bundle, ReportActivity.class);
                        }
                        break;
                    case CWConstant.HOME_DEVICE_SETTING: // 设备参数
                        if (getTrackDeviceList().size() == 0)
                            showMessage(R.string.no_device_tips);
                        else {
                            bundle = new Bundle();
                            bundle.putInt(TConstant.TYPE, 1);
                            startActivity(bundle, DeviceSettingActivity.class);
                        }
                        break;
                    case CWConstant.HOME_SYSTEM_CONFIGURE: // 系统工具
                        bundle = new Bundle();
                        bundle.putInt(TConstant.TYPE, 2);
                        startActivity(bundle, WebActivity.class);
                        break;
                    case CWConstant.HOME_TRACK: // 轨迹回放
                        deviceModel = getTrackDeviceModel();
                        if (deviceModel == null)
                            showMessage(R.string.no_device_tips);
                        else {
                            startActivity(HistoryActivity.class);
                        }
                        break;
                    case CWConstant.HOME_SEND_ORDER_PET: // 远程控制
                        deviceModel = getTrackDeviceModel();
                        if (deviceModel == null)
                            showMessage(R.string.no_device_tips);
                        else {
                            openNewPage(RemoteControlFragment.class);
                        }
                        break;
                    case CWConstant.HOME_SEND_COMMAND_LIST: // 发送指令列表
                        deviceModel = getTrackDeviceModel();
                        if (deviceModel == null)
                            showMessage(R.string.no_device_tips);
                        else {
                            bundle = new Bundle();
                            bundle.putString(TConstant.DEVICE_IMEI, deviceModel.getDeviceImei());
                            openNewPage(MessageCenterFragment.class, bundle);
                        }
                        break;
                    case CWConstant.HOME_BLUE_TOOTH: // 蓝牙传输
                        openNewPage(BlueToothFragment.class);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_CHANGE_DEVICE == event.getType()) {
            initItems();
            ViewDataUtils.initDeviceInfoView(getContext(), mHeaderView, getTrackDeviceModel());
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initItems();
        ViewDataUtils.initDeviceInfoView(getContext(), mHeaderView, getTrackDeviceModel());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
