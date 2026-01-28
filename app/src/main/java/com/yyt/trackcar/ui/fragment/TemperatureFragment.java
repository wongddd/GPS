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
 * @ fileName:      TemperatureFragment
 * @ author:        QING
 * @ createTime:    6/18/21 16:04
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "Temperature")
public class TemperatureFragment extends BaseFragment {
    @BindView(R.id.tvTemperatureNum)
    TextView mTvTemperatureNum;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.bcTemperature)
    BarChart mBcTemperature;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_temperature;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitleColor(ContextCompat.getColor(mActivity, R.color.white));
        titleBar.setLeftImageResource(R.drawable.ic_back_white);
        titleBar.setBackgroundResource(R.color.health_temperature);
        titleBar.setTitle(R.string.temperature);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_note) {
            @Override
            public void performAction(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE, 3);
                openNewPageForResult(HealthRecordFragment.class, bundle,
                        CWConstant.REQUEST_HEALTH_RECORD);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        Date date = new Date();
        mTvTemperatureNum.setText(String.format("%s%s", "36.6",
                getString(R.string.temperature_unit)));
        String dateString = TimeUtils.formatUTC(System.currentTimeMillis(),"yyyy/MM/dd");
        String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                date.getDay() - 1);
        mTvDate.setText(String.format("%s %s", dateString, dayString));
        initCharts();
        initChartsData();
    }

    private void initCharts() {
        mBcTemperature.getDescription().setEnabled(false);
        mBcTemperature.setMaxVisibleValueCount(7);
        mBcTemperature.setPinchZoom(false);
        mBcTemperature.setDrawGridBackground(false);
        mBcTemperature.setScaleEnabled(false);
        mBcTemperature.setTouchEnabled(true);
        mBcTemperature.setDrawBorders(false);
        mBcTemperature.setHighlightPerTapEnabled(false);

        mBcTemperature.getAxisRight().setEnabled(false);
        mBcTemperature.getAxisLeft().setEnabled(false);
        mBcTemperature.getAxisLeft().setAxisMinimum(36);
        mBcTemperature.getAxisLeft().setAxisMaximum(38);
        mBcTemperature.getXAxis().setEnabled(true);
        mBcTemperature.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBcTemperature.getXAxis().setDrawGridLines(false);
        mBcTemperature.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity,
                R.color.health_temperature));
        mBcTemperature.getXAxis().setAxisLineWidth(2);

        XAxis xAxis = mBcTemperature.getXAxis();//获取设置X轴
        List<String> dateList = new ArrayList<>();
        dateList.add("");
        dateList.add("");
        dateList.add("36.6");
        dateList.add("37.0");
        dateList.add("");
        dateList.add("36.8");
        dateList.add("36.9");
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

        Legend l = mBcTemperature.getLegend();
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
        values.add(new BarEntry(3, 36.6f));
        values.add(new BarEntry(4, 37.0f));
        values.add(new BarEntry(5, 0));
        values.add(new BarEntry(6, 36.8f));
        values.add(new BarEntry(7, 36.9f));

        BarDataSet d = new BarDataSet(values, "");
        d.setDrawValues(false);
        d.setHighlightEnabled(false);
        d.setHighLightAlpha(1);

        d.setGradientColor(ContextCompat.getColor(mActivity, R.color.health_temperature),
                ContextCompat.getColor(mActivity, R.color.health_temperature_second));

        BarData data = new BarData(d);
        mBcTemperature.setData(data);
        mBcTemperature.setHighlightFullBarEnabled(false);
        mBcTemperature.invalidate();
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
