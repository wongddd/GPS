package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.progress.CircleProgressView;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.StepModel;
import com.yyt.trackcar.dbflow.StepModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.ui.widget.ChartMarkerView;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      StepCounterFragment
 * @ author:        QING
 * @ createTime:    2020/3/20 14:32
 * @ describe:      TODO 计步页面
 */
@Page(name = "StepCounter")
public class StepCounterFragment extends BaseFragment {
    @BindView(R.id.cpvStep)
    CircleProgressView mCpvStep; // 步数进度条
    @BindView(R.id.tvUpdateTime)
    TextView mTvUpdateTime; // 更新时间
    @BindView(R.id.tvStep)
    TextView mTvStep; // 步数
    @BindView(R.id.tvTargetStep)
    TextView mTvTargetStep; // 目标步数
    @BindView(R.id.lcStep)
    LineChart mLcStep; // 步数图表
    private DeviceSettingsModel mStepModel; // 步数对象

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_counter;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.step_counter);
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.settings)) {
            @Override
            public void performAction(View view) {
                Bundle bundle = new Bundle();
                if (mStepModel != null)
                    bundle.putString(CWConstant.CONTENT, mStepModel.getStep());
                openNewPageForResult(StepTargetFragment.class, bundle,
                        CWConstant.REQUEST_STEP_TARGET);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mStepModel = getDeviceSettings();
        initCharts();
        initChartsData();
        initStepInfo();
        getStepGoal();
        getStepList();
    }

    /**
     * 初始化步数信息
     */
    private void initStepInfo() {
        if (mStepModel == null) {
            mCpvStep.setStartProgress(0);
            mCpvStep.setEndProgress(0);
            mCpvStep.startProgressAnimation();
            mTvUpdateTime.setText(getString(R.string.step_update,
                    TimeUtils.formatUTC(System.currentTimeMillis(), "HH:mm")));
            mTvStep.setText("0");
            mTvTargetStep.setText(getString(R.string.step_num_target, "8000"));
        } else {
            long step = 8000;
            long deviceStep = 0;
            try {
                step = Long.parseLong(mStepModel.getStep());
                if (step < 1000)
                    step = 8000;
                deviceStep = Long.parseLong(mStepModel.getDevicestep());
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            mCpvStep.setStartProgress(0);
            float progress = (deviceStep * 1.0f / step) * 100;
            if (progress > 100)
                progress = 100;
            else if (progress < 0)
                progress = 0;
            mCpvStep.setEndProgress(progress);
            mCpvStep.startProgressAnimation();
            mTvUpdateTime.setText(getString(R.string.step_update,
                    TimeUtils.formatUTC(System.currentTimeMillis(), "HH:mm")));
            mTvStep.setText(String.valueOf(deviceStep));
            mTvTargetStep.setText(getString(R.string.step_num_target, String.valueOf(step)));
        }
    }

    /**
     * 初始化图表
     */
    private void initCharts() {
        mLcStep.getDescription().setEnabled(false);
        mLcStep.setMaxVisibleValueCount(7);
        mLcStep.setPinchZoom(false);
        mLcStep.setDrawGridBackground(false);
        mLcStep.setScaleEnabled(false);
        mLcStep.setTouchEnabled(true);
        mLcStep.setDrawBorders(false);
        mLcStep.setHighlightPerTapEnabled(true);

        mLcStep.getAxisRight().setEnabled(false);
        mLcStep.getAxisLeft().setEnabled(true);
        mLcStep.getAxisLeft().setAxisMinimum(0);
        mLcStep.getAxisLeft().setAxisMaximum(8000);
        mLcStep.getXAxis().setEnabled(true);
        mLcStep.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        ChartMarkerView mv = new ChartMarkerView(mActivity);
        mLcStep.setMarker(mv);

        XAxis xAxis = mLcStep.getXAxis();//获取设置X轴
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, 1);
            dateList.add(TimeUtils.formatUTC(calendar.getTime().getTime(), "MM.dd"));
        }
        ValueFormatter valueFormatter = new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value) - 1;
                if (index >= 0 && index < dateList.size()) {
                    return dateList.get(index);
                } else {
                    return "";
                }
            }
        };
        xAxis.setValueFormatter(valueFormatter);

        Legend l = mLcStep.getLegend();
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
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        for (int i = 0; i < 6; i++) {
            calendar.add(Calendar.DATE, 1);
            dateList.add(TimeUtils.formatUTC(calendar.getTime().getTime(), "yyyy-MM-dd"));
        }
        List<StepModel> mList = new ArrayList<>();
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(StepModel_Table.imei.eq(deviceModel.getImei()))
                    .and(StepModel_Table.date.greaterThanOrEq(dateList.get(0)))
                    .and(StepModel_Table.date.lessThanOrEq(dateList.get(dateList.size() - 1))));
            mList = SQLite.select().from(StepModel.class)
                    .where(operatorGroup)
                    .orderBy(StepModel_Table.date, true)
                    .queryList();
        }
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        long maxSize = 8000;
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i++) {
            boolean isAdd = false;
            for (int j = 0; j < mList.size(); j++) {
                StepModel model = mList.get(j);
                if (dateList.get(i).equals(model.getDate())) {
                    values.add(new Entry(i + 1, model.getStep()));
                    isAdd = true;
                    if (model.getStep() > maxSize)
                        maxSize = model.getStep();
                    mList.remove(j);
                    break;
                }

            }
            if (!isAdd)
                values.add(new Entry(i + 1, 0));
        }
        if (mStepModel == null)
            values.add(new Entry(7, 0));
        else {
            long deviceStep = 0;
            try {
                deviceStep = Long.parseLong(mStepModel.getDevicestep());
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            if (deviceStep > maxSize)
                maxSize = deviceStep;
            values.add(new Entry(7, deviceStep));
        }
        long axisMax = (long) Math.ceil(maxSize / 1000.0f) * 1000;
        mLcStep.getAxisLeft().setAxisMaximum(axisMax);

        LineDataSet d = new LineDataSet(values, "");
        d.setLineWidth(2.5f);
        d.setDrawCircles(true);
        d.setMode(LineDataSet.Mode.LINEAR);
        d.setDrawValues(false);

        int color = ContextCompat.getColor(mActivity, R.color.red);
        d.setColor(color);
        d.setCircleColor(color);
        dataSets.add(d);

        LineData data = new LineData(dataSets);
        mLcStep.setData(data);
        mLcStep.invalidate();
    }

    /**
     * 获取手表设置目标步数
     */
    private void getStepGoal() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getStepGoal(getContext(), getIp(), userModel.getToken(),
                    deviceModel.getD_id(), mHandler);
    }

    /**
     * 获取最近七天的步数
     */
    private void getStepList() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(StepModel_Table.imei.eq(deviceModel.getImei())));
            StepModel model = SQLite.select().from(StepModel.class)
                    .where(operatorGroup)
                    .orderBy(StepModel_Table.date, false)
                    .querySingle();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -6);
            String startTime = TimeUtils.formatUTC(calendar.getTime().getTime(), "yyyy-MM-dd");
            calendar.add(Calendar.DATE, 5);
            String endTime = TimeUtils.formatUTC(calendar.getTime().getTime(), "yyyy-MM-dd");
            if (model != null) {
                if (endTime.compareTo(model.getDate()) <= 0)
                    return;
                else {
                    calendar.setTime(TimeUtils.formatUTC(model.getDate(), "yyyy-MM-dd"));
                    calendar.add(Calendar.DATE, 1);
                    String lastTime = TimeUtils.formatUTC(calendar.getTime().getTime(), "yyyy-MM" +
                            "-dd");
                    if (startTime.compareTo(lastTime) < 0)
                        startTime = lastTime;
                }
            }
            CWRequestUtils.getInstance().getStepList(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), deviceModel.getImei(), String.format("%s%%2000:00:00",
                            startTime), String.format(
                            "%s%%2023:59:59", endTime), mHandler);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_STEP_TARGET && resultCode == Activity.RESULT_OK && data != null) {
            UserModel userModel = getUserModel();
            DeviceModel deviceModel = getDevice();
            if (userModel != null && deviceModel != null) {
                mStepModel = getDeviceSettings();
                initStepInfo();
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_STEP_GOAL: // 获取手表设置目标步数
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
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
                                    mStepModel = settingsModel;
                                    initStepInfo();
                                    initChartsData();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_STEP_LIST: // 获取最近七天的步数
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    if (resultBean.getList() != null && resultBean.getList().size() > 0) {
                                        for (Object obj : resultBean.getList()) {
                                            StepModel model = mGson.fromJson(mGson.toJson(obj),
                                                    StepModel.class);
                                            model.setImei(deviceModel.getImei());
                                            model.setDate(TimeUtils.formatUTCC(model.getCreatetime(),
                                                    "yyyy-MM-dd"));
                                            model.save();
                                        }
                                        initChartsData();
                                    }
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
