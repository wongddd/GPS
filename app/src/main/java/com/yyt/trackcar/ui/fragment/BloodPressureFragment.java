package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BloodPressureFragment
 * @ author:        QING
 * @ createTime:    6/18/21 14:21
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "BloodPressure")
public class BloodPressureFragment extends BaseFragment {
    @BindView(R.id.tvSystolicPressureNum)
    TextView mTvSystolicPressureNum;
    @BindView(R.id.tvDiastolicPressureNum)
    TextView mTvDiastolicPressureNum;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.bcBloodPressure)
    BarChart mBcBloodPressure;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blood_pressure;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitleColor(ContextCompat.getColor(mActivity, R.color.white));
        titleBar.setLeftImageResource(R.drawable.ic_back_white);
        titleBar.setBackgroundResource(R.color.health_blood_pressure);
        titleBar.setTitle(R.string.blood_pressure);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_note) {
            @Override
            public void performAction(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE, 1);
                openNewPageForResult(HealthRecordFragment.class, bundle,
                        CWConstant.REQUEST_HEALTH_RECORD);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        Date date = new Date();
        mTvSystolicPressureNum.setText("99");
        mTvDiastolicPressureNum.setText("66");
        String dateString = TimeUtils.formatUTC(System.currentTimeMillis(),"yyyy/MM/dd");
        String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                date.getDay() - 1);
        mTvDate.setText(String.format("%s %s", dateString, dayString));
        initCharts();
        initChartsData();
    }

    private void initCharts() {
        mBcBloodPressure.getDescription().setEnabled(false);
        mBcBloodPressure.setMaxVisibleValueCount(7);
        mBcBloodPressure.setPinchZoom(false);
        mBcBloodPressure.setDrawGridBackground(false);
        mBcBloodPressure.setScaleEnabled(false);
        mBcBloodPressure.setTouchEnabled(true);
        mBcBloodPressure.setDrawBorders(false);
        mBcBloodPressure.setHighlightPerTapEnabled(false);

        mBcBloodPressure.getAxisRight().setEnabled(false);
        mBcBloodPressure.getAxisLeft().setEnabled(false);
        mBcBloodPressure.getAxisLeft().setAxisMinimum(0);
        mBcBloodPressure.getAxisLeft().setAxisMaximum(180);
        mBcBloodPressure.getXAxis().setEnabled(true);
        mBcBloodPressure.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBcBloodPressure.getXAxis().setDrawGridLines(false);
        mBcBloodPressure.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity, R.color.health_blood_pressure));
        mBcBloodPressure.getXAxis().setAxisLineWidth(2);

        XAxis xAxis = mBcBloodPressure.getXAxis();//获取设置X轴
        List<String> dateList = new ArrayList<>();
        dateList.add("");
        dateList.add("");
        dateList.add("96/150");
        dateList.add("86/136");
        dateList.add("");
        dateList.add("66/120");
        dateList.add("76/113");
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

        Legend l = mBcBloodPressure.getLegend();
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
        values.add(new BarEntry(1, new float[]{0, 0}));
        values.add(new BarEntry(2, new float[]{0, 0}));
        values.add(new BarEntry(3, new float[]{96, 150}));
        values.add(new BarEntry(4, new float[]{86, 136}));
        values.add(new BarEntry(5, new float[]{0, 0}));
        values.add(new BarEntry(6, new float[]{66, 120}));
        values.add(new BarEntry(7, new float[]{76, 113}));

        BarDataSet d = new BarDataSet(values, "");
        d.setDrawValues(false);
        d.setHighlightEnabled(false);
        d.setStackLabels(new String[]{"", ""});
        d.setHighLightAlpha(1);

        d.setColors(ContextCompat.getColor(mActivity, R.color.health_blood_pressure),
                ContextCompat.getColor(mActivity, R.color.health_blood_pressure_second));

        BarData data = new BarData(d);
        mBcBloodPressure.setData(data);
        mBcBloodPressure.setHighlightFullBarEnabled(false);
        mBcBloodPressure.setFitBars(true);
        mBcBloodPressure.invalidate();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_HEALTH_RECORD && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
            }
        }
    }

}
