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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.progress.materialprogressbar.MaterialProgressBar;
import com.xuexiang.xui.widget.tabbar.TabSegment;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.StepModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.MainFragmentAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.ui.widget.ChartMarkerView;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DBUtils;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      StepFragment
 * @ author:        QING
 * @ createTime:    6/12/21 15:07
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "Step", params = {CWConstant.TITLE, CWConstant.CONTENT})
public class StepFragment extends BaseFragment {
    @BindView(R.id.tabSegment)
    TabSegment mTabSegment;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tvStepNum)
    TextView mTvStepNum;
    @BindView(R.id.tvStepUnit)
    TextView mTvStepUnit;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.tvStepMetre)
    TextView mTvStepMetre;
    @BindView(R.id.tvStepCalorie)
    TextView mTvStepCalorie;
    @BindView(R.id.tvStepTime)
    TextView mTvStepTime;
    @BindView(R.id.tvStepTimeUnit)
    TextView mTvStepTimeUnit;
    @BindView(R.id.tvSportNum)
    TextView mTvSportNum;
    @BindView(R.id.tvSportWeekNum)
    TextView mTvSportWeekNum;
    @BindView(R.id.bcSport)
    BarChart mBcSport;
    @BindView(R.id.mpbFirst)
    MaterialProgressBar mMpbFirst;
    @BindView(R.id.mpbSecond)
    MaterialProgressBar mMpbSecond;
    @BindView(R.id.mpbThird)
    MaterialProgressBar mMpbThird;
    @BindView(R.id.mpbForth)
    MaterialProgressBar mMpbForth;
    @BindView(R.id.mpbFifth)
    MaterialProgressBar mMpbFifth;
    @BindView(R.id.mpbSixth)
    MaterialProgressBar mMpbSixth;
    @BindView(R.id.mpbSeventh)
    MaterialProgressBar mMpbSeventh;
    @BindView(R.id.tvFirst)
    TextView mTvFirst;
    @BindView(R.id.tvSecond)
    TextView mTvSecond;
    @BindView(R.id.tvThird)
    TextView mTvThird;
    @BindView(R.id.tvForth)
    TextView mTvForth;
    @BindView(R.id.tvFifth)
    TextView mTvFifth;
    @BindView(R.id.tvSixth)
    TextView mTvSixth;
    @BindView(R.id.tvSeventh)
    TextView mTvSeventh;
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
        return R.layout.fragment_step;
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
        titleBar.setBackgroundResource(R.color.health_step);
        titleBar.setTitle(R.string.step_num_title);
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
                                else if (selectTime > 0) {
                                    refreshDate(day.getCalendar().getTime());
                                    initStepInfo("");
                                }
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
        Date date = new Date();
        mTvStepUnit.setText(getString(R.string.step_unit, ""));
        String dateString = TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy/MM/dd");
        String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                date.getDay() - 1);
        mTvDate.setText(String.format("%s %s", dateString, dayString));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.report_day)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.report_week)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.report_month)));
        mItemList.add(new StepDayFragment());
        mItemList.add(new StepWeekFragment());
        mItemList.add(new StepMonthFragment());
        mAdapter = new MainFragmentAdapter(getChildFragmentManager(), mItemList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0, false);
        mTabSegment.setupWithViewPager(mViewPager, false);
        mTabSegment.setIndicatorWidthAdjustContent(false);
        mTvStepTimeUnit.setText(getString(R.string.minutes, ""));
        mTvSportNum.setText("0");
        mTvSportWeekNum.setText("0");
        initCharts();
        initChartsData();
        refreshDate(new Date());
        DeviceModel deviceModel = getDevice();
        if (deviceModel == null)
            initStepInfo("");
        else {
            DeviceSettingsModel settingsModel = DBUtils.getDeviceSettings(getUserModel(),
                    deviceModel.getImei());
            if (settingsModel == null || TextUtils.isEmpty(settingsModel.getDevicestep())
                    || "0".equals(settingsModel.getDevicestep()))
                initStepInfo("");
            else
                initStepInfo(settingsModel.getDevicestep());
        }
    }

    /**
     * 初始化图表
     */
    private void initCharts() {
        mBcSport.getDescription().setEnabled(false);
        mBcSport.setMaxVisibleValueCount(25);
        mBcSport.setPinchZoom(false);
        mBcSport.setDrawGridBackground(false);
        mBcSport.setScaleEnabled(false);
        mBcSport.setTouchEnabled(true);
        mBcSport.setDrawBorders(false);
        mBcSport.setHighlightPerTapEnabled(true);

        mBcSport.getAxisRight().setEnabled(false);
        mBcSport.getAxisLeft().setEnabled(false);
        mBcSport.getAxisLeft().setAxisMinimum(0);
        mBcSport.getAxisLeft().setAxisMaximum(120);
        mBcSport.getXAxis().setEnabled(true);
        mBcSport.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBcSport.getXAxis().setDrawGridLines(false);
        mBcSport.getXAxis().setLabelCount(7);
        mBcSport.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity,
                R.color.health_step));

        ChartMarkerView mv = new ChartMarkerView(mActivity);
        mBcSport.setMarker(mv);

        XAxis xAxis = mBcSport.getXAxis();//获取设置X轴
        ValueFormatter valueFormatter = new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value) - 1;
                switch (index) {
                    case 0:
                        return getString(R.string.report_sun);
                    case 1:
                        return getString(R.string.report_mon);
                    case 2:
                        return getString(R.string.report_tue);
                    case 3:
                        return getString(R.string.report_wed);
                    case 4:
                        return getString(R.string.report_thu);
                    case 5:
                        return getString(R.string.report_fri);
                    case 6:
                        return getString(R.string.report_sat);
                    default:
                        return "";
                }
            }
        };
        xAxis.setValueFormatter(valueFormatter);

        Legend l = mBcSport.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.EMPTY);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    /**
     * 初始化图表数据
     */
    private void initChartsData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(1, 0));
        values.add(new BarEntry(2, 0));
        values.add(new BarEntry(3, 0));
        values.add(new BarEntry(4, 0));
        values.add(new BarEntry(5, 0));
        values.add(new BarEntry(6, 0));
        values.add(new BarEntry(7, 0));

        BarDataSet d = new BarDataSet(values, "");
        d.setDrawValues(false);

        d.setGradientColor(ContextCompat.getColor(mActivity, R.color.health_step),
                ContextCompat.getColor(mActivity, R.color.health_step_second));

        BarData data = new BarData(d);
        mBcSport.setData(data);
        mBcSport.invalidate();
    }

    private void initStepInfo(List list) {
        List<StepModel> modelList = new ArrayList<>();
        if (list != null) {
            for (Object obj : list) {
                StepModel model = mGson.fromJson(mGson.toJson(obj), StepModel.class);
                model.setDate(TimeUtils.formatUTCC(model.getCreatetime(),
                        "yyyy-MM-dd"));
                modelList.add(model);
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        calendar.add(Calendar.DATE, -6);
        long startTime = calendar.getTime().getTime();
        int targetStep = 80;
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getStep())
                && !"0".equals(settingsModel.getStep())) {
            try {
                targetStep = Integer.parseInt(settingsModel.getStep()) / 100;
                if (targetStep <= 0)
                    targetStep = 80;
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }
        for (int i = 1; i <= 7; i++) {
            String dateTime = TimeUtils.formatUTC(startTime +
                    (long) (i - 1) * TimeUtils.DAY * 1000, "yyyy-MM-dd");
            for (StepModel model : modelList) {
                if (TextUtils.equals(dateTime, model.getDate())) {
                    switch (i) {
                        case 1:
                            mMpbFirst.setProgress((int) model.getStep() / targetStep);
                            break;
                        case 2:
                            mMpbSecond.setProgress((int) model.getStep() / targetStep);
                            break;
                        case 3:
                            mMpbThird.setProgress((int) model.getStep() / targetStep);
                            break;
                        case 4:
                            mMpbForth.setProgress((int) model.getStep() / targetStep);
                            break;
                        case 5:
                            mMpbFifth.setProgress((int) model.getStep() / targetStep);
                            break;
                        case 6:
                            mMpbSixth.setProgress((int) model.getStep() / targetStep);
                            break;
                        case 7:
                            initStepInfo(String.valueOf(model.getStep()));
                            mMpbSeventh.setProgress((int) model.getStep() / targetStep);
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }
    }

    private void initStepInfo(String step) {
        if (TextUtils.isEmpty(step) || "0".equals(step)) {
            mTvStepNum.setText("--");
            mTvStepMetre.setText("--");
            mTvStepCalorie.setText("--");
            mTvStepTime.setText("--");
        } else {
            mTvStepNum.setText(step);
            DeviceInfoModel infoModel = getDeviceInfo();
            long deviceStep = 0;
            double height = 1.75;
            int weight = 60;
            try {
                deviceStep = Long.parseLong(step);
                if (infoModel != null) {
                    height = Integer.parseInt(infoModel.getHeight()) / 100f;
                    weight = Integer.parseInt(infoModel.getWeight());
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            double stepRatio;
            if (infoModel != null && infoModel.getSex() == 1)
                stepRatio = 0.413;
            else
                stepRatio = 0.415;
            double metre = height * stepRatio * deviceStep;
            double calorie = weight * metre * 1.036 * 0.001;
            mTvStepMetre.setText(String.valueOf((int) metre));
            mTvStepCalorie.setText(String.valueOf((int) calorie));
            mTvStepTime.setText(String.valueOf((int) Math.floor(deviceStep / 120f)));
        }
    }

    private void refreshDate(Date date) {
        mMpbFirst.setProgress(0);
        mMpbSecond.setProgress(0);
        mMpbThird.setProgress(0);
        mMpbForth.setProgress(0);
        mMpbFifth.setProgress(0);
        mMpbSixth.setProgress(0);
        mMpbSeventh.setProgress(0);
        long time = date.getTime();
        mTvFirst.setText(TimeUtils.formatUTC(time - 6 * TimeUtils.DAY * 1000, "MM/dd"));
        mTvSecond.setText(TimeUtils.formatUTC(time - 5 * TimeUtils.DAY * 1000, "MM/dd"));
        mTvThird.setText(TimeUtils.formatUTC(time - 4 * TimeUtils.DAY * 1000, "MM/dd"));
        mTvForth.setText(TimeUtils.formatUTC(time - 3 * TimeUtils.DAY * 1000, "MM/dd"));
        mTvFifth.setText(TimeUtils.formatUTC(time - 2 * TimeUtils.DAY * 1000, "MM/dd"));
        mTvSixth.setText(TimeUtils.formatUTC(time - TimeUtils.DAY * 1000, "MM/dd"));
        mTvSeventh.setText(TimeUtils.formatUTC(time, "MM/dd"));
        String dateString = TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd");
        String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                date.getDay() - 1);
        mTvDate.setText(String.format("%s %s", dateString, dayString));
        String dateTime = TimeUtils.formatUTC(date.getTime(),
                "yyyy-MM-dd");
        StepDayFragment fragment = (StepDayFragment) mItemList.get(0);
        fragment.refreshDate(dateTime);
        StepWeekFragment weekFragment =
                (StepWeekFragment) mItemList.get(1);
        weekFragment.refreshDate(date, null);
        StepMonthFragment monthFragment =
                (StepMonthFragment) mItemList.get(2);
        monthFragment.refreshDate(date, null);
        getStepList(date);
    }

    /**
     * 获取最近七天的步数
     */
    private void getStepList(Date date) {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            mDate = date;
            String startWeekTime = TimeUtils.formatUTC(date.getTime() -
                    (long) date.getDay() * TimeUtils.DAY * 1000, "yyyy-MM-dd%2000:00:00");
            String startMonthTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-01%2000:00:00");
            String endTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd%2023:59:59");
            CWRequestUtils.getInstance().getStepList(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), deviceModel.getImei(),
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
                    case CWConstant.REQUEST_URL_GET_STEP_LIST: // 获取最近七天的步数
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
                                    initStepInfo(resultBean.getList());
                                    StepWeekFragment fragment =
                                            (StepWeekFragment) mItemList.get(1);
                                    fragment.refreshDate(mDate, resultBean.getList());
                                    StepMonthFragment monthFragment =
                                            (StepMonthFragment) mItemList.get(2);
                                    monthFragment.refreshDate(mDate, resultBean.getList());
                                }
                            }
                            //                                requestBean =
//                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject
//                                                ()), AAATrackRequestBeanOldEdition.class);
//                                if (resultBean.getList() != null && resultBean.getList().size()
//                                > 0) {
//                                    HealthReportBean lastBean =
//                                            mGson.fromJson(mGson.toJson(resultBean.getList()
//                                            .get(0)),
//                                                    HealthReportBean.class);
//                                    HealthModel healthModel =
//                                            DBUtils.getDeviceHealth(requestBean.getImei());
//                                    if (healthModel != null) {
//                                        healthModel.setBlood_oxygen(lastBean.getMsg());
//                                        healthModel.setBlood_oxygen_upload_time(lastBean
//                                        .getTime());
//                                        healthModel.setBlood_oxygen_system_time(lastBean
//                                        .getUploadTime());
//                                        healthModel.save();
//                                    }
//                                    for (Object obj : resultBean.getList()) {
//                                        HealthReportBean bean = mGson.fromJson(mGson.toJson(obj),
//                                                HealthReportBean.class);
//                                        HealthRecordModel model = new HealthRecordModel();
//                                        model.setImei(requestBean.getImei());
//                                        model.setMsg(String.valueOf(bean.getMsg()));
//                                        model.setType(2);
//                                        model.setTime(bean.getTime());
//                                        model.setUpdateTime(bean.getUploadTime());
//                                        model.save();
//                                    }
//                                    initBloodOxygen();
//                                }
//                            }
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
