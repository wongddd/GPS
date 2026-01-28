package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.dialog.OnDaysSelectionListener;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.progress.materialprogressbar.MaterialProgressBar;
import com.xuexiang.xui.widget.tabbar.TabSegment;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.HealthReportBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.HealthHourModel;
import com.yyt.trackcar.dbflow.HealthHourModel_Table;
import com.yyt.trackcar.dbflow.HealthModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.MainFragmentAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DBUtils;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      HeartRateAvgFragment
 * @ author:        QING
 * @ createTime:    6/2/21 17:49
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "HeartRateAvg", params = {CWConstant.TITLE, CWConstant.CONTENT})
public class HeartRateAvgFragment extends BaseFragment {
    @BindView(R.id.tabSegment)
    TabSegment mTabSegment;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tvHeartRateNum)
    TextView mTvHeartRateNum;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.tvTime)
    TextView mTvTime;
    @BindView(R.id.tvTimeTypeFirst)
    TextView mTvTimeTypeFirst;
    @BindView(R.id.tvTimeTypeSecond)
    TextView mTvTimeTypeSecond;
    @BindView(R.id.tvTimeTypeThird)
    TextView mTvTimeTypeThird;
    @BindView(R.id.tvTimeTypeForth)
    TextView mTvTimeTypeForth;
    @BindView(R.id.tvTimeTypeFifth)
    TextView mTvTimeTypeFifth;
    @BindView(R.id.mpbTypeFirst)
    MaterialProgressBar mMpbTypeFirst;
    @BindView(R.id.mpbTypeSecond)
    MaterialProgressBar mMpbTypeSecond;
    @BindView(R.id.mpbTypeThird)
    MaterialProgressBar mMpbTypeThird;
    @BindView(R.id.mpbTypeForth)
    MaterialProgressBar mMpbTypeForth;
    @BindView(R.id.mpbTypeFifth)
    MaterialProgressBar mMpbTypeFifth;
    MainFragmentAdapter mAdapter;
    private List<Fragment> mItemList = new ArrayList<>();
    private CalendarDialog mCalendarDialog;
    @AutoWired
    String title;
    @AutoWired
    String content; // 内容
    private Date mDate;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_heart_rate_avg;
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
        titleBar.setBackgroundResource(R.color.red);
        titleBar.setTitle(R.string.heart_rate_day);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_note) {
            @Override
            public void performAction(View view) {
                if (mCalendarDialog == null) {
                    mCalendarDialog = new CalendarDialog(mActivity, new OnDaysSelectionListener() {
                        @Override
                        public void onDaysSelected(List<Day> selectedDays) {
                            for (Day day : selectedDays) {
                                long selectTime =
                                        System.currentTimeMillis() - day.getCalendar().getTime().getTime();
                                if (selectTime > (long) TimeUtils.MONTH * 1000 * 3)
                                    XToastUtils.toast(R.string.query_too_min_day_prompt);
                                else if (selectTime > 0)
                                    refreshDate(day.getCalendar().getTime());
                                break;
                            }
                        }
                    });
                    mCalendarDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            mCalendarDialog.setSelectionType(SelectionType.SINGLE);
                            mCalendarDialog.setSelectedDayBackgroundColor(ContextCompat.getColor(mActivity, R.color.red));
                        }
                    });
                }
                if (!mCalendarDialog.isShowing())
                    mCalendarDialog.show();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        HealthModel healthModel = null;
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            healthModel = DBUtils.getDeviceHealth(deviceModel.getImei());
        }
        Date date = new Date();
        if (healthModel == null || healthModel.getHeart_rate_system_time() == 0)
            mTvHeartRateNum.setText("--");
        else
            mTvHeartRateNum.setText(String.valueOf(healthModel.getErsi_heart_rate()));

        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.report_day)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.report_week)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.report_month)));
        mItemList.add(new HeartRateDayFragment());
        mItemList.add(new HeartRateWeekFragment());
        mItemList.add(new HeartRateMonthFragment());
        mAdapter = new MainFragmentAdapter(getChildFragmentManager(), mItemList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0, false);
        mTabSegment.setupWithViewPager(mViewPager, false);
        mTabSegment.setIndicatorWidthAdjustContent(false);
//        mTvTime.setText(getString(R.string.sleep_hour, "0"));
//        mTvTimeTypeFirst.setText(getString(R.string.sleep_hour, "0"));
//        mTvTimeTypeSecond.setText(getString(R.string.sleep_hour, "0"));
//        mTvTimeTypeThird.setText(getString(R.string.sleep_hour, "0"));
//        mTvTimeTypeForth.setText(getString(R.string.sleep_hour, "0"));
//        mTvTimeTypeFifth.setText(getString(R.string.sleep_hour, "0"));
        refreshDate(date);
    }

    private void initTimeType(List list) {
        int typeFirst = 0;
        int typeSecond = 0;
        int typeThird = 0;
        int typeFourth = 0;
        int typeFifth = 0;
        if (list != null) {
            for (Object obj : list) {
                HealthReportBean model = mGson.fromJson(mGson.toJson(obj), HealthReportBean.class);
                if (model.getMsg() < 119)
                    typeFirst += 1;
                else if (model.getMsg() < 139)
                    typeSecond += 1;
                else if (model.getMsg() < 159)
                    typeThird += 1;
                else if (model.getMsg() < 179)
                    typeFourth += 1;
                else
                    typeFifth += 1;
            }
        }
        int sumCount = typeFirst + typeSecond + typeThird + typeFourth + typeFifth;
        mTvTime.setText(getString(R.string.sleep_hour, String.valueOf(sumCount)));
        mTvTimeTypeFirst.setText(getString(R.string.sleep_hour, String.valueOf(typeFirst)));
        mTvTimeTypeSecond.setText(getString(R.string.sleep_hour, String.valueOf(typeSecond)));
        mTvTimeTypeThird.setText(getString(R.string.sleep_hour, String.valueOf(typeThird)));
        mTvTimeTypeForth.setText(getString(R.string.sleep_hour, String.valueOf(typeFourth)));
        mTvTimeTypeFifth.setText(getString(R.string.sleep_hour, String.valueOf(typeFifth)));
        if (sumCount == 0) {
            mMpbTypeFirst.setProgress(0);
            mMpbTypeSecond.setProgress(0);
            mMpbTypeThird.setProgress(0);
            mMpbTypeForth.setProgress(0);
            mMpbTypeFifth.setProgress(0);
        } else {
            mMpbTypeFirst.setProgress(typeFirst * 100 / sumCount);
            mMpbTypeSecond.setProgress(typeSecond * 100 / sumCount);
            mMpbTypeThird.setProgress(typeThird * 100 / sumCount);
            mMpbTypeForth.setProgress(typeFourth * 100 / sumCount);
            mMpbTypeFifth.setProgress(typeFifth * 100 / sumCount);
        }
    }

    private void refreshDate(Date date) {
        mTvDate.setText(String.format("%s,%s", TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd"),
                getString(R.string.heart_rate_average)));
        String dateString = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd");
        mDate = date;
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(HealthHourModel_Table.type.eq(0))
                            .and(HealthHourModel_Table.imei.eq(deviceModel.getImei()))
                            .and(HealthHourModel_Table.date.eq(dateString)));
            List<HealthHourModel> list = SQLite.select().from(HealthHourModel.class)
                    .where(operatorGroup)
                    .orderBy(HealthHourModel_Table.time, true)
                    .queryList();
            if (list.size() == 0)
                getHeartRateDayList(dateString);
            List<HealthReportBean> modelList = new ArrayList<>();
            for (HealthHourModel model : list) {
                HealthReportBean bean = new HealthReportBean();
                int num = 0;
                try {
                    num = Integer.parseInt(model.getMsg());
                } catch (NumberFormatException e) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace();
                }
                bean.setMsg(num);
                bean.setTime(model.getTime());
                modelList.add(bean);
            }
            initTimeType(modelList);
            HeartRateDayFragment fragment = (HeartRateDayFragment) mItemList.get(0);
            fragment.refreshDate(modelList);
        } else {
            initTimeType(null);
            HeartRateDayFragment fragment = (HeartRateDayFragment) mItemList.get(0);
            fragment.refreshDate(null);
        }
        HeartRateWeekFragment weekFragment =
                (HeartRateWeekFragment) mItemList.get(1);
        weekFragment.refreshDate(date, null);
        HeartRateMonthFragment monthFragment =
                (HeartRateMonthFragment) mItemList.get(2);
        monthFragment.refreshDate(date, null);
//        AAADeviceModel deviceModel = getDevice();
//        if (deviceModel != null) {
//            OperatorGroup operatorGroup =
//                    OperatorGroup.clause(OperatorGroup.clause()
//                            .and(HealthDayModel_Table.type.eq(0))
//                            .and(HealthDayModel_Table.imei.eq(deviceModel.getImei()))
//                            .and(HealthDayModel_Table.time.eq(dateTime)));
//            HealthDayModel model = SQLite.select().from(HealthDayModel.class)
//                    .where(operatorGroup)
//                    .querySingle();
//            if (model == null || TextUtils.isEmpty(model.getMsg()) || "0".equals(model.getMsg()))
//                mTvHeartRateNum.setText("--");
//            else
//                mTvHeartRateNum.setText(model.getMsg());
//        }
        getHeartRateMonthList(date);
    }

    private void getHeartRateDayList(String date) {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            long nowTime = System.currentTimeMillis();
            String nowDate = TimeUtils.formatUTC(System.currentTimeMillis(),
                    "yyyy-MM-dd");
            String startTime = String.format("%s%%2000:00:00", date);
            String endTime;
            if (TextUtils.equals(date, nowDate))
                endTime = TimeUtils.formatUTC(nowTime, "yyyy-MM-dd%20HH:mm:ss");
            else
                endTime = String.format("%s%%2023:59:59", date);
            CWRequestUtils.getInstance().getHeartRateDayList(getContext(), userModel.getToken()
                    , deviceModel.getD_id(), deviceModel.getImei(), startTime, endTime, mHandler);
        }
    }

    private void getHeartRateMonthList(Date date) {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            String startWeekTime = TimeUtils.formatUTC(date.getTime() -
                    (long) date.getDay() * TimeUtils.DAY * 1000, "yyyy-MM-dd%2000:00:00");
            String startMonthTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-01%2000:00:00");
            String endTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd%2023:59:59");
            CWRequestUtils.getInstance().getHeartRateMonthList(getContext(), userModel.getToken()
                    , deviceModel.getD_id(), deviceModel.getImei(),
                    startWeekTime.compareTo(startMonthTime) < 0 ?
                            startWeekTime : startMonthTime, endTime, mHandler);
        }
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_HEART_RATE_DAY_LIST: // 心率日记录  24小时记录  小时平均心率
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.NOT_RESULT) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject
                                                ()), RequestBean.class);
                                String recordDate = requestBean.getStartTime().substring(0, 10);
                                String nowDate = TimeUtils.formatUTC(System.currentTimeMillis(),
                                        "yyyy-MM-dd");
                                if (TextUtils.equals(TimeUtils.formatUTC(mDate.getTime(),
                                        "yyyy-MM-dd"), recordDate)) {
                                    HeartRateDayFragment fragment =
                                            (HeartRateDayFragment) mItemList.get(0);
                                    fragment.refreshDate(resultBean.getList());
                                    initTimeType(resultBean.getList());
                                }
                                if (!TextUtils.equals(recordDate, nowDate)) {
                                    for (int i = 1; i <= 24; i++) {
                                        HealthHourModel model = new HealthHourModel();
                                        model.setImei(requestBean.getImei());
                                        model.setDate(recordDate);
                                        model.setTime(String.valueOf(i));
                                        model.setType(0);
                                        model.setMsg("0");
                                        if (resultBean.getList() != null && resultBean.getList().size() > 0) {
                                            for (Object obj : resultBean.getList()) {
                                                HealthReportBean bean =
                                                        mGson.fromJson(mGson.toJson(obj),
                                                                HealthReportBean.class);
                                                if (String.valueOf(i).equals(bean.getTime())) {
                                                    model.setMsg(String.valueOf(bean.getMsg()));
                                                    break;
                                                }
                                            }
                                        }
                                        model.save();
                                    }
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_HEART_RATE_MONTH_LIST: // 心率月平均记录
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.NOT_RESULT) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject
                                                ()), RequestBean.class);
                                String recordDate = requestBean.getEndTime().substring(0, 10);
                                String nowDate = TimeUtils.formatUTC(mDate.getTime(),
                                        "yyyy-MM-dd");
                                if (TextUtils.equals(recordDate, nowDate)) {
                                    if (resultBean.getList() == null || resultBean.getList().size() == 0)
                                        mTvHeartRateNum.setText("--");
                                    else {
                                        boolean isFind = false;
                                        for (Object obj : resultBean.getList()) {
                                            HealthReportBean model =
                                                    mGson.fromJson(mGson.toJson(obj),
                                                            HealthReportBean.class);
                                            if (TextUtils.equals(nowDate, model.getTime())) {
                                                mTvHeartRateNum.setText(String.valueOf(model.getMsg()));
                                                isFind = true;
                                                break;
                                            }
                                        }
                                        if (!isFind)
                                            mTvHeartRateNum.setText("--");
                                    }
                                    HeartRateWeekFragment fragment =
                                            (HeartRateWeekFragment) mItemList.get(1);
                                    fragment.refreshDate(mDate, resultBean.getList());
                                    HeartRateMonthFragment monthFragment =
                                            (HeartRateMonthFragment) mItemList.get(2);
                                    monthFragment.refreshDate(mDate, resultBean.getList());
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

}
