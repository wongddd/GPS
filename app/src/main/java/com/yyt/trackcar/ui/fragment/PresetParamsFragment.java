package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseItemBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.adapter.DeviceSettingItemAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      PresetParamsFragment
 * @ author:        QING
 * @ createTime:    2023/3/29 18:00
 * @ describe:      TODO 预约设定
 */
@Page(name = "PresetParams")
public class PresetParamsFragment extends BaseFragment implements View.OnClickListener,
        BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private DeviceSettingItemAdapter mAdapter; // 适配器
    private final List<AAABaseItemBean> mItemList = new ArrayList<>(); // 列表
    private View mHeaderView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.details);
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
     * 初始化列表信息
     */
    private void initItems() {
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new DeviceSettingItemAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
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
        initDeviceInfo();
        mAdapter.addHeaderView(mHeaderView);
    }

    /**
     * 初始化设备信息
     */
    private void initDeviceInfo() {
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (deviceModel != null && mHeaderView != null) {
            ImageView ivPortrait = mHeaderView.findViewById(R.id.ivPortrait);
            TextView tvName = mHeaderView.findViewById(R.id.tvName);
            TextView tvImei = mHeaderView.findViewById(R.id.tvImei);
            TextView tvInfo = mHeaderView.findViewById(R.id.tvInfo);

            String imei = StringUtils.getNotNullText(deviceModel.getDeviceImei());
            String name = deviceModel.getDeviceName() == null ? imei : deviceModel.getDeviceName();
            ImageLoadUtils.loadPortraitImage(getContext(), deviceModel.getHeadPic(), ivPortrait);
            StringUtils.setText(tvName, name);
            StringUtils.setText(tvImei, String.format("IMEI: %s", imei));
            if (deviceModel.getDeviceType() == 1) {
                String online;
                if (deviceModel.isOnlineStatus() && deviceModel.getLastMotionStatus() != null &&
                        deviceModel.getLastMotionStatus() == 1)
                    online = getString(R.string.device_sport);
                else if (deviceModel.isOnlineStatus())
                    online = getString(R.string.device_motionless);
                else
                    online = getString(R.string.offline);
                String onOff;
                if (deviceModel.isOnlineStatus() && deviceModel.getEngineStatus() != null
                        && deviceModel.getEngineStatus() == 1)
                    onOff = getString(R.string.on);
                else
                    onOff = getString(R.string.off);
//                float vol = 0;
//                if (!TextUtils.isEmpty(deviceModel.getLastDeviceVol())) {
//                    try {
//                        vol = Float.parseFloat(deviceModel.getLastDeviceVol());
//                        if (vol < 0)
//                            vol = 0;
//                        else if (vol > 100)
//                            vol = 100;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                tvInfo.setText(String.format("%s ACC %s,%s:%s%%", online, onOff,
//                        getString(R.string.device_power), vol));
                StringUtils.setText(tvInfo, String.format("%s ACC %s", online, onOff));
            } else {
                StringUtils.setText(tvInfo, String.format("%s:%s",
                        getString(R.string.device_version),
                        StringUtils.getNotNullText(deviceModel.getVersion())));
            }
        }
    }

    /**
     * 点击主页底部导航栏时调用
     */
    public void onSwitchToThisPage() {
        initItems();
        initDeviceInfo();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xlLayout: // 设备信息
                openNewPage(BabyInfoFragment.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

}
