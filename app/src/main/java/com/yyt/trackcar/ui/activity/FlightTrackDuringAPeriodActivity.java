package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.ListResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FlightTrackDuringAPeriodActivity extends BaseActivity implements View.OnClickListener {
    private TabLayout mTabLayout; // 顶部tab
    private Button mStartDateBtn, mEndDateBtn, mSearchBtn; // 开始时间，结束时间,搜索按钮
    private List<BaseItemBean> mTabItemList = new ArrayList<>(); // tab标题
    private TimePickerView mTimePickerView; // 时间选择器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar(R.string.flight_training_trajectory, R.drawable.ic_back_white, mNavigationOnClickListener);
        initViews();
        initDatas();
        initListeners();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history;
    }

    protected void initViews() {
        mTabLayout = findViewById(R.id.history_tablayout);
        mStartDateBtn = findViewById(R.id.history_start_date_btn);
        mEndDateBtn = findViewById(R.id.history_end_date_btn);
        mSearchBtn = findViewById(R.id.history_search_btn);
        mTabLayout.setVisibility(View.GONE);
        mStartDateBtn.setEnabled(true);
        mEndDateBtn.setEnabled(true);
    }


    protected void initDatas() {
        mStartDateBtn.setText(TimeUtils.formatUTC(getIntent().getLongExtra(TConstant.START_TIME,0), "yyyy/MM/dd HH:mm:ss"));
        mEndDateBtn.setText(TimeUtils.formatUTC(getIntent().getLongExtra(TConstant.END_TIME,0), "yyyy/MM/dd HH:mm:ss"));
    }


    protected void initListeners() {
        mStartDateBtn.setOnClickListener(this);
        mEndDateBtn.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);
    }

    /**
     * 搜索
     */
    public void onSearch(View v) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = MainApplication.getInstance().getTrackDeviceModel();
        if (deviceModel != null) {
            showDialog();
            mLoadingDialog.setMessage(getString(R.string.requesting_tips));
            String startTime = TimeUtils.formatUTC(TimeUtils.formatUTC
                            (mStartDateBtn.getText().toString(), "yyyy/MM/dd HH:mm:ss").getTime(),
                    "yyyy-MM-dd HH:mm:00");
            String endTime = TimeUtils.formatUTC(TimeUtils.formatUTC
                            (mEndDateBtn.getText().toString(), "yyyy/MM/dd HH:mm:ss").getTime(),
                    "yyyy-MM-dd HH:mm:59");
            CarGpsRequestUtils.getHistoryLocation(userModel, deviceModel.getDeviceImei(),
                    startTime, endTime, 0, 1, mHandler);
        }
    }

    /**
     * 显示时间选择器
     *
     * @param type  类型
     * @param title 标题
     * @param str   时间
     */
    private void showTimePickView(final int type, String title, String str) {
        if (mTimePickerView == null || !mTimePickerView.isShowing()) {
            Calendar selectedDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            Date date = TimeUtils.formatUTC(str, "yyyy/MM/dd HH:mm:ss");
            Date nowDate = new Date();
            startDate.set(2018, 0, 1, 0, 0);
            endDate.set(nowDate.getYear() + 1900, 11, 31, 23, 59);
            selectedDate.set(date.getYear() + 1900, date.getMonth(), date.getDate(), date
                    .getHours(), date.getMinutes(), date.getSeconds());
            mTimePickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {//选中事件回调
                    if (type == 0) {
                        mStartDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
                                "HH:mm:ss"));
                        Date endTime = TimeUtils.formatUTC(mEndDateBtn.getText().toString(),
                                "yyyy/MM/dd HH:mm:ss");
//                        if (endTime.getTime() < date.getTime() || !AAATimeUtils.inSameDay(endTime,
//                                date))
//                            mEndDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
//                                    "23:59"));
                    } else {
                        mEndDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
                                "HH:mm:ss"));
                        Date startTime = TimeUtils.formatUTC(mStartDateBtn.getText().toString(),
                                "yyyy/MM/dd HH:mm:ss");
//                        if (startTime.getTime() > date.getTime() || !AAATimeUtils.inSameDay(startTime,
//                                date))
//                            mStartDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd" +
//                                    " 00:00"));
                    }
                }
            }).setType(true, true, true, true, true, true)// 默认全部显示
                    .setCancelText(getString(R.string.cancel))//取消按钮文字
                    .setSubmitText(getString(R.string.confirm))//确认按钮文字
                    .setContentTextSize(15) //滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleText(title)//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    .setTitleBgColor(getResources().getColor(R.color.white))//标题背景颜色 Night mode
                    .setBgColor(getResources().getColor(R.color.layout_background))//滚轮背景颜色 Night
                    // mode
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel(getString(R.string.year), getString(R.string.mouth), getString(R
                                    .string.day), getString(R.string.hour_new), getString(R.string
                                    .minute_new),
                            getString(R.string.second_new))//默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build();
            mTimePickerView.show();
        }
    }

    /**
     * 标题栏Navigation点击监听器
     */
    private final View.OnClickListener mNavigationOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_start_date_btn: // 开始时间
                showTimePickView(0, getString(R.string.history_start_date), mStartDateBtn.getText
                        ().toString());
                break;
            case R.id.history_end_date_btn: // 结束时间
                showTimePickView(1, getString(R.string.history_end_date), mEndDateBtn.getText()
                        .toString());
                break;
            case R.id.history_search_btn: // 搜索
                onSearch(v);
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                AAABaseResponseBean responseBean;
                AAARequestBean requestBean;
                switch (msg.what) {
                    case TConstant.REQUEST_URL_GET_HISTORY_LOCATION:
                        dismisDialog();
                        if (msg.obj == null)
                            showMessage(R.string.request_unkonow_prompt);
                        else {
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
                                    bundle.putString(TConstant.TITLE,getString(R.string.flight_training_trajectory));
                                    bundle.putString(TConstant.IMEI_NO,
                                            requestBean.getDeviceImei());
                                    bundle.putString(TConstant.START_TIME,
                                            requestBean.getStartTime());
                                    bundle.putString(TConstant.END_TIME, requestBean.getEndTime());
                                    bundle.putInt(TConstant.DEVICE_TYPE,2);
                                    bundle.putInt("needScreenshot",1);
                                    if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 1)
                                        startActivity(bundle, HistoryGMapActivity.class);
                                    else
                                        startActivity(bundle, HistoryAMapActivity.class);
                                }
                            } else
                                showMessage(R.string.tip_request_error);
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
}
