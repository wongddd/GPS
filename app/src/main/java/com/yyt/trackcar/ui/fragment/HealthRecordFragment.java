package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.HealthReportBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.HealthRecordModel;
import com.yyt.trackcar.dbflow.HealthRecordModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.HealthRecordAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      HealthRecordFragment
 * @ author:        QING
 * @ createTime:    6/18/21 16:07
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "HealthRecord", params = {CWConstant.TYPE})
public class HealthRecordFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout; // RefreshLayout
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private HealthRecordAdapter mAdapter; // 适配器
    private List<BaseItemBean> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    int type; // 类型 0心率 1血压 2.血氧 3体温

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitleColor(ContextCompat.getColor(mActivity, R.color.white));
        titleBar.setLeftImageResource(R.drawable.ic_back_white);
        switch (type) {
            case 1:
                titleBar.setBackgroundResource(R.color.health_blood_pressure);
                titleBar.setTitle(R.string.blood_pressure);
                break;
            case 2:
                titleBar.setBackgroundResource(R.color.health_blood_oxygen);
                titleBar.setTitle(R.string.blood_oxygen);
                break;
            case 3:
                titleBar.setBackgroundResource(R.color.health_temperature);
                titleBar.setTitle(R.string.temperature);
                break;
            default:
                titleBar.setBackgroundResource(R.color.health_heart_rate);
                titleBar.setTitle(R.string.heart_rate);
                break;
        }
        return titleBar;
    }

    @Override
    protected void initViews() {
        mRefreshLayout.setBackgroundResource(R.color.colorPrimary);
        mRefreshLayout.setEnableRefresh(false);
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderAndFooterView();
        getItemList(null);
//        switch (type) {
//            case 1:
//
//                break;
//            case 2:
//                getSevenBloodOxygen();
//                break;
//            case 3:
//
//                break;
//            default:
//                getSevenHeartRate();
//                break;
//        }
    }

    @Override
    protected void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mItemList.size() == 0) {
                    mRefreshLayout.finishLoadMore();
                    mRefreshLayout.setEnableLoadMore(false);
                } else {
                    getItemList(mItemList.get(mItemList.size() - 1));
                    mRefreshLayout.finishLoadMore(500);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            }
        });
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
//        BaseItemBean itemBean;
//        switch (type) {
//            case 1:
//                itemBean = new BaseItemBean("111/69", getString(R.string.blood_pressure_unit));
//                itemBean.setTitleColor(R.color.health_blood_pressure);
//                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//                mItemList.add(itemBean);
//                itemBean = new BaseItemBean("120/75", getString(R.string.blood_pressure_unit));
//                itemBean.setTitleColor(R.color.health_blood_pressure);
//                mItemList.add(itemBean);
//                itemBean = new BaseItemBean("140/88", getString(R.string.blood_pressure_unit));
//                itemBean.setTitleColor(R.color.health_blood_pressure);
//                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//                mItemList.add(itemBean);
//                break;
//            case 2:
//                itemBean = new BaseItemBean("99","%");
//                itemBean.setTitleColor(R.color.health_blood_oxygen);
//                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//                mItemList.add(itemBean);
//                itemBean = new BaseItemBean("68","%");
//                itemBean.setTitleColor(R.color.health_blood_oxygen);
//                mItemList.add(itemBean);
//                itemBean = new BaseItemBean("88","%");
//                itemBean.setTitleColor(R.color.health_blood_oxygen);
//                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//                mItemList.add(itemBean);
//                break;
//            case 3:
//                itemBean = new BaseItemBean("37.0", getString(R.string.temperature_unit));
//                itemBean.setTitleColor(R.color.health_temperature);
//                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//                mItemList.add(itemBean);
//                itemBean = new BaseItemBean("36.8", getString(R.string.temperature_unit));
//                itemBean.setTitleColor(R.color.health_temperature);
//                mItemList.add(itemBean);
//                itemBean = new BaseItemBean("36.6", getString(R.string.temperature_unit));
//                itemBean.setTitleColor(R.color.health_temperature);
//                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//                mItemList.add(itemBean);
//                break;
//            default:
//                itemBean = new BaseItemBean(1, "99", getString(R.string.heart_rate_unit));
//                itemBean.setTitleColor(R.color.health_heart_rate);
//                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//                mItemList.add(itemBean);
//                itemBean = new BaseItemBean(1, "68", getString(R.string.heart_rate_unit));
//                itemBean.setTitleColor(R.color.health_heart_rate);
//                mItemList.add(itemBean);
//                itemBean = new BaseItemBean(1, "88", getString(R.string.heart_rate_unit));
//                itemBean.setTitleColor(R.color.health_heart_rate);
//                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//                mItemList.add(itemBean);
//                break;
//        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new HealthRecordAdapter(mItemList);
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
     * 初始化头脚布局
     */
    private void initHeaderAndFooterView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView, false);
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView, false);
        mAdapter.addHeaderView(headerView);
        mAdapter.addFooterView(footerView);
    }

    private void getItemList(BaseItemBean itemBean) {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            OperatorGroup operatorGroup;
            if (itemBean == null || itemBean.getObject() == null) {
                mItemList.clear();
                operatorGroup =
                        OperatorGroup.clause(OperatorGroup.clause()
                                .and(HealthRecordModel_Table.type.eq(type))
                                .and(HealthRecordModel_Table.imei.eq(deviceModel.getImei())));
            } else {
                HealthRecordModel healthRecordModel = (HealthRecordModel) itemBean.getObject();
                operatorGroup =
                        OperatorGroup.clause(OperatorGroup.clause()
                                .and(HealthRecordModel_Table.type.eq(type))
                                .and(HealthRecordModel_Table.imei.eq(deviceModel.getImei()))
                                .and(HealthRecordModel_Table.updateTime.lessThan(healthRecordModel.getUpdateTime())));
            }
            List<HealthRecordModel> list = SQLite.select().from(HealthRecordModel.class)
                    .where(operatorGroup)
                    .orderBy(HealthRecordModel_Table.updateTime, false)
                    .limit(20)
                    .queryList();
            if (list.size() > 0) {
                if (mItemList.size() > 0)
                    mItemList.get(mItemList.size() - 1).setBgDrawable(0);
                BaseItemBean item;
                for (HealthRecordModel model : list) {
                    switch (type) {
                        case 1:

                            break;
                        case 2:
                            item = new BaseItemBean(model.getMsg(), "%");
                            item.setGroup(TimeUtils.formatUTCC(model.getUpdateTime(), "yyyy" +
                                    "/MM/dd\nHH:mm"));
                            item.setTitleColor(R.color.health_blood_oxygen);
                            item.setObject(model);
                            mItemList.add(item);
                            break;
                        default:
                            item = new BaseItemBean(model.getMsg(),
                                    getString(R.string.heart_rate_unit));
                            item.setGroup(TimeUtils.formatUTCC(model.getUpdateTime(), "yyyy" +
                                    "/MM/dd\nHH:mm"));
                            item.setTitleColor(R.color.health_heart_rate);
                            item.setObject(model);
                            mItemList.add(item);
                            break;
                    }
                }
                if (mItemList.size() == 1)
                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                else if (mItemList.size() > 2) {
                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                    mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                }
            }
            mAdapter.notifyDataSetChanged();
            if (list.size() < 20)
                mRefreshLayout.setEnableLoadMore(false);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(CWConstant.MODEL, mGson.toJson(mItemList.get(position).getObject()));
            intent.putExtras(bundle);
            setFragmentResult(Activity.RESULT_OK, intent);
            popToBack();
        }
    }

    private void getSevenHeartRate() {
//        AAAUserModel userModel = getUserModel();
//        AAADeviceModel deviceModel = getDevice();
//        if (userModel != null && deviceModel != null)
//            CWRequestUtils.getInstance().getSevenHeartRate(getContext(), userModel.getToken()
//                    , deviceModel.getD_id(), mHandler);
    }

    private void getSevenBloodOxygen() {
//        AAAUserModel userModel = getUserModel();
//        AAADeviceModel deviceModel = getDevice();
//        if (userModel != null && deviceModel != null)
//            CWRequestUtils.getInstance().getSevenBloodOxygen(getContext(), userModel.getToken()
//                    , deviceModel.getD_id(), mHandler);
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                RequestBean requestBean;
                UserModel userModel;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_SEVEN_HEART_RATE: // 最近七次的心率
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                mItemList.clear();
                                if (resultBean.getList() != null) {
                                    BaseItemBean itemBean;
                                    for (Object obj : resultBean.getList()) {
                                        HealthReportBean model = mGson.fromJson(mGson.toJson(obj),
                                                HealthReportBean.class);
                                        itemBean = new BaseItemBean(1,
                                                String.valueOf(model.getMsg()),
                                                getString(R.string.heart_rate_unit));
                                        itemBean.setGroup(TimeUtils.getCallDateDescriptionByNow(mActivity,
                                                TimeUtils.formatUTCC(model.getUploadTime(), "yyyy" +
                                                        "/MM/dd HH:mm:ss")));
                                        itemBean.setTitleColor(R.color.health_heart_rate);
                                        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
                                        mItemList.add(itemBean);
                                    }
                                }
                                if (mItemList.size() == 1)
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                                else if (mItemList.size() > 2) {
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                                    mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                }
                                mAdapter.notifyDataSetChanged();
//                                requestBean =
//                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject
//                                        ()), AAATrackRequestBeanOldEdition.class);
//                                userModel = getUserModel();
//                                if (userModel != null) {
//                                    for (AAADeviceModel model : getDeviceList()) {
//                                        if (model.getD_id() == requestBean.getD_id()) {
//                                            break;
//                                        }
//                                    }
//                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_SEVEN_BLOOD_OXYGEN: // 最近七次的血氧
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                if (resultBean.getList() != null) {
                                    BaseItemBean itemBean;
                                    for (Object obj : resultBean.getList()) {
                                        HealthReportBean model = mGson.fromJson(mGson.toJson(obj),
                                                HealthReportBean.class);
                                        itemBean =
                                                new BaseItemBean(String.valueOf(model.getMsg()),
                                                        "%");
                                        itemBean.setTitleColor(R.color.health_blood_oxygen);
                                        itemBean.setGroup(TimeUtils.getCallDateDescriptionByNow(mActivity,
                                                TimeUtils.formatUTCC(model.getUploadTime(), "yyyy" +
                                                        "/MM/dd HH:mm:ss")));
                                        itemBean.setTitleColor(R.color.health_heart_rate);
                                        itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
                                        mItemList.add(itemBean);
                                    }
                                }
                                if (mItemList.size() == 1)
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                                else if (mItemList.size() > 2) {
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                                    mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                }
                                mAdapter.notifyDataSetChanged();
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
