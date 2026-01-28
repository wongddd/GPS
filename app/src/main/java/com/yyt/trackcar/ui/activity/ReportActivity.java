package com.yyt.trackcar.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.utils.AAATimeUtils;
import com.yyt.trackcar.utils.Constant;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.activity
 * 文件名：   ReportActivity
 * 创建者：   QING
 * 创建时间： 2018/4/23 18:28
 * 描述：     TODO 报表统计页面
 */
@SuppressLint("NonConstantResourceId")
public class ReportActivity extends BaseActivity implements View.OnClickListener {
    private Button mStartDateBtn, mEndDateBtn, mSearchBtn; // 开始时间，结束时间,搜索按钮
    private TimePickerView mTimePickerView; // 时间选择器
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    protected void initViews() {
        mStartDateBtn = findViewById(R.id.report_start_date_btn);
        mEndDateBtn = findViewById(R.id.report_end_date_btn);
        mSearchBtn = findViewById(R.id.report_search_btn);
    }


    protected void initDatas() {
        mType = super.getIntent().getIntExtra(Constant.TYPE, 0);
//        if (mType == 1)
//            initToolBar(R.string.home_trip_report, R.drawable.ic_back_white, mNavigationOnClickListener);
//        else if (mType == 2)
//            initToolBar(R.string.home_fuel_report, R.drawable.ic_back_white, mNavigationOnClickListener);
//        else if (mType == 3)
//            initToolBar(R.string.home_sports_report, R.drawable.ic_back_white,
//                    mNavigationOnClickListener);
//        else if (mType == 4)
//            initToolBar(R.string.home_alarm_record, R.drawable.ic_back_white,
//                    mNavigationOnClickListener);
//        else
//            initToolBar(R.string.home_summary_record, R.drawable.ic_back_white,
//                    mNavigationOnClickListener);
        if (mType == 1)
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.home_trip_report)), R.drawable.ic_back_white, mNavigationOnClickListener);
        else if (mType == 2)
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                    getString(R.string.home_fuel_report)), R.drawable.ic_back_white, mNavigationOnClickListener);
        else if (mType == 3)
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                            getString(R.string.home_sports_report)), R.drawable.ic_back_white,
                    mNavigationOnClickListener);
        else if (mType == 4)
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                            getString(R.string.home_alarm_record)), R.drawable.ic_back_white,
                    mNavigationOnClickListener);
        else
            initToolBar(String.format("%s%s", getString(R.string.pet_real_time),
                            getString(R.string.home_summary_record)), R.drawable.ic_back_white,
                    mNavigationOnClickListener);
        mStartDateBtn.setText(TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy/MM/dd 00:00"));
        mEndDateBtn.setText(TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy/MM/dd HH:mm"));
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
        AAADeviceModel deviceModel = getTrackDevice();
        if (deviceModel != null) {
            String startTime = TimeUtils.formatUTC(TimeUtils.formatUTC
                            (mStartDateBtn.getText().toString(), "yyyy/MM/dd HH:mm").getTime(),
                    "yyyy-MM-dd HH:mm:00");
            String endTime = TimeUtils.formatUTC(TimeUtils.formatUTC
                            (mEndDateBtn.getText().toString(), "yyyy/MM/dd HH:mm").getTime(),
                    "yyyy-MM-dd HH:mm:59");
            Bundle bundle = new Bundle();
            bundle.putString(TConstant.IMEI_NO, deviceModel.getDeviceImei());
            bundle.putString(TConstant.START_TIME, startTime);
            bundle.putString(TConstant.END_TIME, endTime);
            if (mType == 1) {
                startActivity(bundle, StopReportActivity.class);
            } else if (mType == 2) {
                showMessage(R.string.no_mileage_tips);
            } else if (mType == 3) {
                startActivity(bundle, SportRecordActivity.class);
            }
            else if (mType == 4) {
                startActivity(bundle, AlarmListActivity.class);
            } else {
                startActivity(bundle, HistoryRecordActivity.class);
            }
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
            Date date = TimeUtils.formatUTC(str, "yyyy/MM/dd HH:mm");
            Date nowDate = new Date();
            startDate.set(2018, 0, 1, 0, 0);
            endDate.set(nowDate.getYear() + 1900, 11, 31, 23, 59);
            selectedDate.set(date.getYear() + 1900, date.getMonth(), date.getDate(), date
                    .getHours(), date.getMinutes());
            mTimePickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {//选中事件回调
                    if (type == 0) {
                        mStartDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
                                "HH:mm"));
                        Date endTime = TimeUtils.formatUTC(mEndDateBtn.getText().toString(),
                                "yyyy/MM/dd HH:mm");
                        if (endTime.getTime() < date.getTime() || !AAATimeUtils.inSameDay(endTime,
                                date))
                            mEndDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
                                    "23:59"));
                    } else {
                        mEndDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
                                "HH:mm"));
                        Date startTime = TimeUtils.formatUTC(mStartDateBtn.getText().toString(),
                                "yyyy/MM/dd HH:mm");
                        if (startTime.getTime() > date.getTime() || !AAATimeUtils.inSameDay(startTime,
                                date))
                            mStartDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd" +
                                    " 00:00"));
                    }
                }
            }).setType(new boolean[]{true, true, true, true, true, false})// 默认全部显示
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
                                    .string.day), getString(R.string.hour), getString(R.string
                                    .minute),
                            getString(R.string.second))//默认设置为年月日时分秒
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_start_date_btn: // 开始时间
                showTimePickView(0, getString(R.string.history_start_date), mStartDateBtn.getText
                        ().toString());
                break;
            case R.id.report_end_date_btn: // 结束时间
                showTimePickView(1, getString(R.string.history_end_date), mEndDateBtn.getText()
                        .toString());
                break;
            case R.id.report_search_btn: // 搜索
                onSearch(v);
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                switch (msg.what) {
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
