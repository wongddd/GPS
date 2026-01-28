package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.progress.materialprogressbar.MaterialProgressBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceSysMsgBean;
import com.yyt.trackcar.bean.HealthReportBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.HealthModel;
import com.yyt.trackcar.dbflow.HealthRecordModel;
import com.yyt.trackcar.dbflow.HealthRecordModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DBUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BloodOxygenFragment
 * @ author:        QING
 * @ createTime:    6/12/21 14:44
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "BloodOxygen", params = {CWConstant.TITLE, CWConstant.CONTENT})
public class BloodOxygenFragment extends BaseFragment {
    @BindView(R.id.tvBloodOxygenNum)
    TextView mTvBloodOxygenNum;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.startBtn)
    Button mStartBtn;
    @BindView(R.id.mpbBloodOxygen)
    MaterialProgressBar mMpbBloodOxygen;
    @BindView(R.id.bcBloodOxygen)
    BarChart mBcBloodOxygen;
    @AutoWired
    String title;
    @AutoWired
    String content; // 内容
    private long mTestTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blood_oxygen;
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
        titleBar.setBackgroundResource(R.color.health_blood_oxygen);
        titleBar.setTitle(R.string.blood_oxygen);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_note) {
            @Override
            public void performAction(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE, 2);
                openNewPageForResult(HealthRecordFragment.class, bundle,
                        CWConstant.REQUEST_HEALTH_RECORD);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        initCharts();
//        initChartsData();
        initBloodOxygen();
        HealthModel healthModel = null;
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            healthModel = DBUtils.getDeviceHealth(deviceModel.getImei());
        }
        Date date;
        if (healthModel == null || healthModel.getBlood_oxygen_system_time() == 0) {
            mTvBloodOxygenNum.setText("--%");
            mMpbBloodOxygen.setProgress(0);
            date = new Date();
        } else {
            date = TimeUtils.formatUTC(TimeUtils.formatUTC(healthModel.getBlood_oxygen_system_time(), null), null);
            mTvBloodOxygenNum.setText(String.format("%s%%", healthModel.getBlood_oxygen()));
            int num = healthModel.getBlood_oxygen();
            int bloodOxygenProgess = (num - 80) * 5;
            if (bloodOxygenProgess < 0)
                bloodOxygenProgess = 0;
            else if (bloodOxygenProgess > 100)
                bloodOxygenProgess = 100;
            mMpbBloodOxygen.setProgress(bloodOxygenProgess);
        }
        String dateString = TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd");
        String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                date.getDay() - 1);
        mTvDate.setText(String.format("%s %s", dateString, dayString));
        getSevenBloodOxygen();
        mTestTime = SettingSPUtils.getInstance().getLong(CWConstant.HEART_RATE_TEST, 0);
        long testTime = 40000 + mTestTime - System.currentTimeMillis();
        if (testTime > 0) {
            mStartBtn.setText(String.format("%s(%s)", getString(R.string.health_testing),
                    Math.round(testTime / 1000.0f)));
            mStartBtn.setEnabled(false);
            mHandler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    private void initCharts() {
        mBcBloodOxygen.getDescription().setEnabled(false);
        mBcBloodOxygen.setMaxVisibleValueCount(7);
        mBcBloodOxygen.setPinchZoom(false);
        mBcBloodOxygen.setDrawGridBackground(false);
        mBcBloodOxygen.setScaleEnabled(false);
        mBcBloodOxygen.setTouchEnabled(true);
        mBcBloodOxygen.setDrawBorders(false);
        mBcBloodOxygen.setHighlightPerTapEnabled(false);

        mBcBloodOxygen.getAxisRight().setEnabled(false);
        mBcBloodOxygen.getAxisLeft().setEnabled(false);
        mBcBloodOxygen.getAxisLeft().setAxisMinimum(0);
        mBcBloodOxygen.getAxisLeft().setAxisMaximum(100);
        mBcBloodOxygen.getXAxis().setEnabled(true);
        mBcBloodOxygen.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBcBloodOxygen.getXAxis().setDrawGridLines(false);
        mBcBloodOxygen.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity,
                R.color.health_blood_oxygen));
        mBcBloodOxygen.getXAxis().setAxisLineWidth(2);

        XAxis xAxis = mBcBloodOxygen.getXAxis();//获取设置X轴
        List<String> dateList = new ArrayList<>();
        dateList.add("");
        dateList.add("");
        dateList.add("");
        dateList.add("");
        dateList.add("");
        dateList.add("");
        dateList.add("");
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

        Legend l = mBcBloodOxygen.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.EMPTY);
        l.setFormSize(9f);
        l.setFormLineWidth(16f);
        l.setTextSize(12f);
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
        d.setHighlightEnabled(false);
        d.setHighLightAlpha(1);

        d.setGradientColor(ContextCompat.getColor(mActivity, R.color.health_blood_oxygen),
                ContextCompat.getColor(mActivity, R.color.health_blood_oxygen_second));

        BarData data = new BarData(d);
        mBcBloodOxygen.setData(data);
        mBcBloodOxygen.setHighlightFullBarEnabled(false);
        mBcBloodOxygen.invalidate();
    }

    private void initBloodOxygen() {
        DeviceModel deviceModel = getDevice();
        List<HealthRecordModel> modelList = new ArrayList<>();
        if (deviceModel != null && !TextUtils.isEmpty(deviceModel.getImei())) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(HealthRecordModel_Table.type.eq(2))
                            .and(HealthRecordModel_Table.imei.eq(deviceModel.getImei())));
            List<HealthRecordModel> list = SQLite.select().from(HealthRecordModel.class)
                    .where(operatorGroup)
                    .orderBy(HealthRecordModel_Table.updateTime, false)
                    .limit(7)
                    .queryList();
            modelList.addAll(list);
        }
        if (modelList.size() == 0) {
            mTvBloodOxygenNum.setText("--%");
            mMpbBloodOxygen.setProgress(0);
            Date date = new Date();
            String dateString = TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd");
            String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                    date.getDay() - 1);
            mTvDate.setText(String.format("%s %s", dateString, dayString));
        } else {
            Collections.reverse(modelList);
            HealthRecordModel model = modelList.get(modelList.size() - 1);
            mTvBloodOxygenNum.setText(String.format("%s%%", model.getMsg()));
            int num = 0;
            try {
                num = Integer.parseInt(model.getMsg());
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            int bloodOxygenProgess = (num - 80) * 5;
            if (bloodOxygenProgess < 0)
                bloodOxygenProgess = 0;
            else if (bloodOxygenProgess > 100)
                bloodOxygenProgess = 100;
            mMpbBloodOxygen.setProgress(bloodOxygenProgess);
            Date date = TimeUtils.formatUTC(TimeUtils.formatUTCC(model.getUpdateTime()
                    , null), null);
            String dateString = TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd");
            String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                    date.getDay() - 1);
            mTvDate.setText(String.format("%s %s", dateString, dayString));
        }
        XAxis xAxis = mBcBloodOxygen.getXAxis();//获取设置X轴
        List<String> dateList = new ArrayList<>();
        ArrayList<BarEntry> values = new ArrayList<>();
        if (modelList.size() < 7) {
            for (int i = 0; i < 7 - modelList.size(); i++) {
                dateList.add("");
                values.add(new BarEntry(i + 1, 0));
            }
        }
        for (HealthRecordModel recordModel : modelList) {
            dateList.add(String.format("%s%%", recordModel.getMsg()));
            int num = 0;
            try {
                num = Integer.parseInt(recordModel.getMsg());
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            int bloodOxygenProgess = (num - 80) * 5;
            if (bloodOxygenProgess < 0)
                bloodOxygenProgess = 0;
            else if (bloodOxygenProgess > 100)
                bloodOxygenProgess = 100;
            values.add(new BarEntry(dateList.size(), bloodOxygenProgess));
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

        BarDataSet d = new BarDataSet(values, "");
        d.setDrawValues(false);
        d.setHighlightEnabled(false);
        d.setHighLightAlpha(1);

        d.setGradientColor(ContextCompat.getColor(mActivity, R.color.health_blood_oxygen),
                ContextCompat.getColor(mActivity, R.color.health_blood_oxygen_second));

        BarData data = new BarData(d);
        mBcBloodOxygen.setData(data);
        mBcBloodOxygen.setHighlightFullBarEnabled(false);
        mBcBloodOxygen.invalidate();
    }

    /**
     * 健康管理状态指令下发 想主动获取
     */
    private void healthSet(int type) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            mTestTime = System.currentTimeMillis();
            CWRequestUtils.getInstance().healthSet(getContext(), getIp(), userModel.getToken()
                    , deviceModel.getImei(), type, mHandler);
        }
    }

    private void getSevenBloodOxygen() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getSevenBloodOxygen(getContext(), userModel.getToken()
                    , deviceModel.getD_id(), deviceModel.getImei(), mHandler);
    }

    @SingleClick
    @OnClick({R.id.startBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn: // 开始测试
                if (System.currentTimeMillis() - mTestTime > 40 * 1000)
                    healthSet(7);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_HEALTH_RECORD && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                HealthRecordModel model =
                        mGson.fromJson(bundle.getString(CWConstant.MODEL, ""),
                                HealthRecordModel.class);
                mTvBloodOxygenNum.setText(String.format("%s%%", model.getMsg()));
                int num = 0;
                try {
                    num = Integer.parseInt(model.getMsg());
                } catch (NumberFormatException e) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace();
                }
                int bloodOxygenProgess = (num - 80) * 5;
                if (bloodOxygenProgess < 0)
                    bloodOxygenProgess = 0;
                else if (bloodOxygenProgess > 100)
                    bloodOxygenProgess = 100;
                mMpbBloodOxygen.setProgress(bloodOxygenProgess);
                Date date = TimeUtils.formatUTC(TimeUtils.formatUTCC(model.getUpdateTime()
                        , null), null);
                String dateString = TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd");
                String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                        date.getDay() - 1);
                mTvDate.setText(String.format("%s %s", dateString, dayString));
            }
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
                    case 1:
                        if(mActivity != null && !mActivity.isFinishing()) {
                            long testTime = 40000 + mTestTime - System.currentTimeMillis();
                            if (testTime < 0) {
                                mStartBtn.setText(R.string.blood_oxygen_start_test);
                                mStartBtn.setEnabled(true);
                            } else {
                                mStartBtn.setText(String.format("%s(%s)", getString(R.string.health_testing),
                                        Math.round(testTime / 1000.0f)));
                                mHandler.sendEmptyMessageDelayed(1, 1000);
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_HEALTH_SET: // 健康管理状态指令下发 想主动获取
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.send_success_prompt);
                                SettingSPUtils.getInstance().putLong(CWConstant.HEART_RATE_TEST, mTestTime);
                                mStartBtn.setText(String.format("%s(%s)", getString(R.string.health_testing), 40));
                                mStartBtn.setEnabled(false);
                                mHandler.sendEmptyMessageDelayed(1, 1000);
                                return false;
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        mTestTime = 0;
                        break;
                    case CWConstant.REQUEST_URL_GET_SEVEN_BLOOD_OXYGEN: // 最近七次的血氧
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.NOT_RESULT) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject
                                                ()), RequestBean.class);
                                if (resultBean.getList() != null && resultBean.getList().size() > 0) {
                                    HealthReportBean lastBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getList().get(0)),
                                                    HealthReportBean.class);
                                    HealthModel healthModel =
                                            DBUtils.getDeviceHealth(requestBean.getImei());
                                    if (healthModel != null) {
                                        healthModel.setBlood_oxygen(lastBean.getMsg());
                                        healthModel.setBlood_oxygen_upload_time(lastBean.getTime());
                                        healthModel.setBlood_oxygen_system_time(lastBean.getUploadTime());
                                        healthModel.save();
                                    }
                                    for (Object obj : resultBean.getList()) {
                                        HealthReportBean bean = mGson.fromJson(mGson.toJson(obj),
                                                HealthReportBean.class);
                                        HealthRecordModel model = new HealthRecordModel();
                                        model.setImei(requestBean.getImei());
                                        model.setMsg(String.valueOf(bean.getMsg()));
                                        model.setType(2);
                                        model.setTime(bean.getTime());
                                        model.setUpdateTime(bean.getUploadTime());
                                        model.save();
                                    }
                                    initBloodOxygen();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMessage(DeviceSysMsgBean event) {
        if (CWConstant.LAGENIO_01 == event.getType()) {
            mTestTime = 0;
            mStartBtn.setText(R.string.blood_oxygen_start_test);
            mStartBtn.setEnabled(true);
            SettingSPUtils.getInstance().putLong(CWConstant.HEART_RATE_TEST, 0);
            if(TextUtils.isEmpty(event.getMsg()) || "0,0,0,0,0".equals(event.getMsg()))
                XToastUtils.toast(R.string.msg_health_data_test_fail);
            else {
                String[] array = event.getMsg().split(",");
                if (array.length > 1 && !TextUtils.isEmpty(array[1]) && !"0".equals(array[1])) {
//                mStartBtn.setText(R.string.blood_oxygen_start_test);
//                mStartBtn.setEnabled(true);
                    getSevenBloodOxygen();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
