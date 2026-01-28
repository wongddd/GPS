package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.HealthReportBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.ui.widget.ChartMarkerView;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      HeartRateWeekFragment
 * @ author:        QING
 * @ createTime:    5/29/21 17:35
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "HeartRateWeek", anim = CoreAnim.none)
public class HeartRateWeekFragment extends BaseFragment {
    @BindView(R.id.tvHeartRateNum)
    TextView mTvHeartRateNum;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.bcHeartRate)
    BarChart mBcHeartRate;
    private Date mDate;
    private List mItemList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_heart_rate_week;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initViews() {
        mTvHeartRateNum.setText("--");
        mTvDate.setText(TimeUtils.formatUTC(System.currentTimeMillis(), String.format("MM%sdd%s,%s",
                getString(R.string.picker_month), getString(R.string.picker_day),
                getString(R.string.heart_rate_average))));
        initCharts();
//        initChartsData();
        initHeartRateWeek(mDate, mItemList);
//        getHeartRateWeekList();
    }

    /**
     * 初始化图表
     */
    private void initCharts() {
        mBcHeartRate.getDescription().setEnabled(false);
        mBcHeartRate.setMaxVisibleValueCount(7);
        mBcHeartRate.setPinchZoom(false);
        mBcHeartRate.setDrawGridBackground(false);
        mBcHeartRate.setScaleEnabled(false);
        mBcHeartRate.setTouchEnabled(true);
        mBcHeartRate.setDrawBorders(false);
        mBcHeartRate.setHighlightPerTapEnabled(true);

        mBcHeartRate.getAxisRight().setEnabled(false);
        mBcHeartRate.getAxisLeft().setEnabled(false);
        mBcHeartRate.getAxisLeft().setAxisMinimum(0);
        mBcHeartRate.getAxisLeft().setAxisMaximum(120);
        mBcHeartRate.getXAxis().setEnabled(true);
        mBcHeartRate.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBcHeartRate.getXAxis().setDrawGridLines(false);
        mBcHeartRate.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        mBcHeartRate.getXAxis().setLabelCount(7);
        mBcHeartRate.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity, R.color.white));

        ChartMarkerView mv = new ChartMarkerView(mActivity, R.drawable.bg_white_round, R.color.blue);
        mBcHeartRate.setMarker(mv);

        XAxis xAxis = mBcHeartRate.getXAxis();//获取设置X轴
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

        Legend l = mBcHeartRate.getLegend();
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

        int color = ContextCompat.getColor(mActivity, R.color.white);
        d.setColor(color);

        BarData data = new BarData(d);
        mBcHeartRate.setData(data);
        mBcHeartRate.invalidate();
    }

    private void initHeartRateWeek(Date date, List list) {
        mDate = date;
        mItemList = list;
        if (mBcHeartRate == null)
            return;
        if (date == null)
            date = new Date();
        List<HealthReportBean> modelList = new ArrayList<>();
        if (list != null) {
            for (Object obj : list) {
                HealthReportBean model = mGson.fromJson(mGson.toJson(obj),
                        HealthReportBean.class);
                modelList.add(model);
            }
        }
        ArrayList<BarEntry> values = new ArrayList<>();
        long startTime = date.getTime() - (long) date.getDay() * TimeUtils.DAY * 1000;
        for (int i = 1; i <= 7; i++) {
            boolean isFind = false;
            String dateTime = TimeUtils.formatUTC(startTime +
                    (long) (i - 1) * TimeUtils.DAY * 1000, "yyyy-MM-dd");
            for (HealthReportBean model : modelList) {
                if (TextUtils.equals(dateTime, model.getTime())) {
                    isFind = true;
                    values.add(new BarEntry(i, model.getMsg()));
                    break;
                }
            }
            if (!isFind)
                values.add(new BarEntry(i, 0));
        }

        BarDataSet d = new BarDataSet(values, "");
        d.setDrawValues(false);

        int color = ContextCompat.getColor(mActivity, R.color.white);
        d.setColor(color);

        BarData data = new BarData(d);
        mBcHeartRate.setData(data);
        mBcHeartRate.invalidate();
    }

    public void refreshDate(Date date, List list) {
        initHeartRateWeek(date, list);
    }

    private void getHeartRateWeekList() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            Date date = new Date();
            String startWeekTime = TimeUtils.formatUTC(date.getTime() -
                    (long) date.getDay() * TimeUtils.DAY * 1000, "yyyy-MM-dd%2000:00:00");
            String endTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd%20HH:mm:ss");
            CWRequestUtils.getInstance().getHeartRateWeekList(getContext(), userModel.getToken()
                    , deviceModel.getD_id(), startWeekTime, endTime, mHandler);
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
                UserModel userModel;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_HEART_RATE_WEEK_LIST: // 心率周平均记录
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
//                                initHeartRateWeek(resultBean.getList());
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
