package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.DeviceRaceconfigplan;
import com.yyt.trackcar.bean.ListResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.ConfigurationParameterActivity;
import com.yyt.trackcar.ui.activity.FlightTrackDuringAPeriodActivity;
import com.yyt.trackcar.ui.activity.HistoryAMapNewActivity;
import com.yyt.trackcar.ui.activity.HistoryGMapNewActivity;
import com.yyt.trackcar.ui.adapter.FlightTrainingAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

@Page(name = "FlightTrainingFragment", anim = CoreAnim.none)
public class FlightTrainingFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.noDataPrompt)
    TextView noDataPrompt;

    private final String strPattern = "yyyy-MM-dd HH:mm:ss";
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strPattern);

    private AAAUserModel mUserModel;
    private AAADeviceModel mDeviceModel;
    private FlightTrainingAdapter adapter;
    private final List<DeviceRaceconfigplan> mItemList = new ArrayList<>();
    private TimePickerView mTimePickerView;
    private Context mContext;
    private int objectIndex = 0;
    private Date resultDate;
    private int pageIndex = 1;
    private final int PAGE_SIZE = 10;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected void initViews() {
        mContext = getContext();
        initDatas();
        initAdapter();
        initRecyclerView();
        initRefreshLayout();
        showDialog();
        CarGpsRequestUtils.getFlightTrainingPlanList(mUserModel, mDeviceModel.getDeviceImei(),
                pageIndex, PAGE_SIZE, mHandler);
    }

    private void initRefreshLayout() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(10000);
                mItemList.clear();
                pageIndex = 1;
                CarGpsRequestUtils.getFlightTrainingPlanList(mUserModel,
                        mDeviceModel.getDeviceImei(), pageIndex, PAGE_SIZE, mHandler);
            }
        }).setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(10000);
                pageIndex++;
                CarGpsRequestUtils.getFlightTrainingPlanList(mUserModel,
                        mDeviceModel.getDeviceImei(), pageIndex, PAGE_SIZE, mHandler);
            }
        });
    }

    private void initAdapter() {
        adapter = new FlightTrainingAdapter(mItemList);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_edit:
                        showTimePickView(getString(R.string.valid_end_time),
                                mItemList.get(position).getCstValidenddatetime(), position);
                        break;
                    case R.id.node_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.prompt)
                                .setMessage(R.string.whether_delete_flight_training_plan)
                                .setPositiveButton(R.string.confirm,
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        objectIndex = position;
                                        CarGpsRequestUtils.deleteFlightTrainingPlan(mUserModel,
                                                mItemList.get(position).getId(), mHandler);
                                    }
                                }).setNegativeButton(R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                        break;
                    case R.id.iv_information:
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(TConstant.PARCELABLE, mItemList.get(position));
                        startActivity(bundle, ConfigurationParameterActivity.class);
                        break;
                }
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                CarGpsRequestUtils.getHistoryLocation(mUserModel, mDeviceModel.getDeviceImei()
//                        , simpleDateFormat.format(mItemList.get(position).getCst())
//                        , simpleDateFormat.format(mItemList.get(position)
//                        .getCstValidenddatetime())
//                        , 0, 1, mHandler);
                if (position >= 0 && position < mItemList.size()) {
                    DeviceRaceconfigplan model = mItemList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putLong(TConstant.START_TIME, model.getCst() == null ?
                            System.currentTimeMillis() : model.getCst());
                    bundle.putLong(TConstant.END_TIME, model.getCstValidenddatetime() == null ?
                            System.currentTimeMillis() : model.getCstValidenddatetime());
                    startActivity(bundle, FlightTrackDuringAPeriodActivity.class);
                }
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void initDatas() {
        mUserModel = getTrackUserModel();
        mDeviceModel = getTrackDeviceModel();
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(R.string.flight_training);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.flight_training)));
        return titleBar;
    }

    // 判断是否为空，空则显示无数据提示词
    private void judgeItemIsEmpty() {
        if (mItemList.size() == 0) {
            noDataPrompt.setVisibility(View.VISIBLE);
        } else {
            noDataPrompt.setVisibility(View.GONE);
        }
    }

    private void showTimePickView(String title, long dateValue, int position) {
        boolean[] standard = new boolean[]{true, true, true, true, true, true};
        if (mTimePickerView == null || !mTimePickerView.isShowing()) {
            String str = simpleDateFormat.format(dateValue);
            Calendar selectedDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            Date nowDate = new Date();
            Date date = TimeUtils.formatUTC(str, strPattern);
            startDate.set(nowDate.getYear() + 1900, 0, 1, 0, 0, 0);
            endDate.set(nowDate.getYear() + 1900, 11, 31, 23, 59, 59);
            selectedDate.set(date.getYear() + 1900, date.getMonth(), date.getDate()
                    , date.getHours(), date.getMinutes(), date.getSeconds());
            mTimePickerView = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {//选中事件回调
                    showDialog();
                    objectIndex = position;
                    resultDate = date;
                    CarGpsRequestUtils.updateFlightTrainingPlan(mUserModel,
                            mItemList.get(position).getId(), simpleDateFormat.format(date),
                            mHandler);
                }
            }).setType(standard)// 默认全部显示
                    .setCancelText(getString(R.string.cancel))//取消按钮文字
                    .setSubmitText(getString(R.string.confirm))//确认按钮文字
                    .setContentTextSize(15) //滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleText(title)//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTitleColor(Color.BLACK)//标题文字颜色
//                    .setSubmitColor(Color.BLUE)//确定按钮文字颜色
//                    .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    .setTitleBgColor(getResources().getColor(R.color.white))//标题背景颜色 Night mode
                    .setBgColor(getResources().getColor(R.color.layout_background))//滚轮背景颜色 Night
                    // mode
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel(getString(R.string.year), getString(R.string.mouth), getString(R
                            .string.day), getString(R.string.hour_new), getString(R.string
                            .minute_new), getString(R.string.second_new))//默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build();
            mTimePickerView.show();
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (refreshLayout == null)
                return false;
            AAABaseResponseBean responseBean;
            AAARequestBean requestBean;
            try {
                switch (msg.what) {
                    case TConstant.REQUEST_GET_FLIGHT_TRAINING_PLAN:
                        dismisDialog();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                        if (msg.obj == null) {
                            refreshLayout.setEnableLoadMore(false);
                            judgeItemIsEmpty();
                            showMessage(R.string.request_error_prompt);
                        } else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS && responseBean.getData() != null) {
                                List list1 = (ArrayList) responseBean.getData();
                                if (list1.size() == 0) {
                                    judgeItemIsEmpty();
                                    refreshLayout.setEnableLoadMore(false);
                                    return false;
                                }
                                List list2 = (ArrayList) list1.get(0);
                                if (list2.size() == 0) {
                                    judgeItemIsEmpty();
                                    refreshLayout.setEnableLoadMore(false);
                                    return false;
                                }
                                for (int i = 0; i < list2.size(); i++) {
                                    mItemList.add(mGson.fromJson(mGson.toJson(list2.get(i)),
                                            DeviceRaceconfigplan.class));
                                }
                                refreshLayout.setEnableLoadMore(true);
                                judgeItemIsEmpty();
                                adapter.notifyDataSetChanged();
                            } else {
                                judgeItemIsEmpty();
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                            }
                        }
                        break;
                    case TConstant.REQUEST_UPDATE_FLIGHT_TRAINING_VALID_END_TIME:
                        dismisDialog();
                        if (msg.obj == null) {
                            showMessage(R.string.update_failed_prompt);
                        } else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS_NEW
                                    || responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                showMessage(R.string.update_succeed_prompt);
                                mItemList.get(objectIndex).setCstValidenddatetime(resultDate.getTime());
                                adapter.notifyDataSetChanged();
                            } else {
                                showMessage(R.string.update_failed_prompt);
                            }
                        }
                        break;
                    case TConstant.REQUEST_DELETE_FLIGHT_TRAINING_PLAN:
                        dismisDialog();
                        if (msg.obj == null) {
                            showMessage(R.string.request_error_prompt);
                        } else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                showMessage(R.string.delete_succeed_prompt);
                                mItemList.remove(objectIndex);
                                adapter.notifyDataSetChanged();
                            } else {
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                            }
                        }
                        break;
                    case TConstant.REQUEST_URL_GET_HISTORY_LOCATION:
                        dismisDialog();
                        if (msg.obj == null) {
                            showMessage(R.string.request_unkonow_prompt);
                        } else {
                            responseBean = (AAABaseResponseBean) msg.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_NET_ERROR)
                                showMessage(R.string.request_unkonow_prompt);
                            else if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                ListResponseBean listResponseBean =
                                        mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                ListResponseBean.class);
                                requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                        AAARequestBean.class);
                                if (listResponseBean == null || listResponseBean.getList() == null
                                        || listResponseBean.getList().size() == 0
                                        || TextUtils.isEmpty(requestBean.getDeviceImei()))
                                    showMessage(R.string.during_this_period_no_flight_training_tips);
                                else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(TConstant.TITLE,
                                            getString(R.string.flight_training_trajectory));
                                    bundle.putString(TConstant.IMEI_NO,
                                            requestBean.getDeviceImei());
                                    bundle.putString(TConstant.START_TIME,
                                            requestBean.getStartTime());
                                    bundle.putString(TConstant.END_TIME, requestBean.getEndTime());
                                    if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE,
                                            0) == 1)
                                        startActivity(bundle, HistoryGMapNewActivity.class);
                                    else
                                        startActivity(bundle, HistoryAMapNewActivity.class);
                                }
                            } else
                                showMessage(R.string.tip_request_error);
                        }
                        break;
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            return false;
        }
    });
}
