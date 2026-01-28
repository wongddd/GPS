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
 * @ fileName:      HeartRateMonthFragment
 * @ author:        QING
 * @ createTime:    5/29/21 21:25
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "HeartRateMonth", anim = CoreAnim.none)
public class HeartRateMonthFragment extends BaseFragment {
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
        return R.layout.fragment_heart_rate_month;
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
        initHeartRateMonth(mDate, mItemList);
//        initChartsData();
//        getHeartRateMonthList();
    }

    /**
     * 初始化图表
     */
    private void initCharts() {
        mBcHeartRate.getDescription().setEnabled(false);
        mBcHeartRate.setMaxVisibleValueCount(31);
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
        mBcHeartRate.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity, R.color.white));

        ChartMarkerView mv = new ChartMarkerView(mActivity, R.drawable.bg_white_round, R.color.blue);
        mBcHeartRate.setMarker(mv);

        Date date;
        if (mDate == null)
            date = new Date();
        else
            date = mDate;
        int days = TimeUtils.getDays(date.getYear() + 1900, date.getMonth() + 1);
        mBcHeartRate.getXAxis().setLabelCount(days);
        mBcHeartRate.getXAxis().setAxisMaximum(days);
        XAxis xAxis = mBcHeartRate.getXAxis();//获取设置X轴
        ValueFormatter valueFormatter = new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value) - 1;
                switch (index) {
                    case 0:
                        return String.format("%s.1", date.getMonth() + 1);
                    case 9:
                        return String.format("%s.10", date.getMonth() + 1);
                    case 19:
                        return String.format("%s.20", date.getMonth() + 1);
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                        if (days - 1 == index)
                            return String.format("%s.%s", date.getMonth() + 1, index + 1);
                        else
                            return "";
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
        for (int i = 1; i <= mBcHeartRate.getXAxis().getLabelCount(); i++) {
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

    private void initHeartRateMonth(Date date, List list) {
        mDate = date;
        mItemList = list;
        if (mBcHeartRate == null)
            return;
        if (date == null)
            date = new Date();
        List<HealthReportBean> modelList = new ArrayList<>();
        if (list != null) {
            for (Object obj : list) {
                HealthReportBean model = mGson.fromJson(mGson.toJson(obj), HealthReportBean.class);
                modelList.add(model);
            }
        }
        ArrayList<BarEntry> values = new ArrayList<>();
        Date nowDate = date;
        int days = TimeUtils.getDays(nowDate.getYear() + 1900, nowDate.getMonth() + 1);
        mBcHeartRate.getXAxis().setLabelCount(days);
        mBcHeartRate.getXAxis().setAxisMaximum(days);
        XAxis xAxis = mBcHeartRate.getXAxis();//获取设置X轴
        ValueFormatter valueFormatter = new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value) - 1;
                switch (index) {
                    case 0:
                        return String.format("%s.1", nowDate.getMonth() + 1);
                    case 9:
                        return String.format("%s.10", nowDate.getMonth() + 1);
                    case 19:
                        return String.format("%s.20", nowDate.getMonth() + 1);
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                        if (days - 1 == index)
                            return String.format("%s.%s", nowDate.getMonth() + 1, index + 1);
                        else
                            return "";
                    default:
                        return "";
                }
            }
        };
        xAxis.setValueFormatter(valueFormatter);
        String startMonthTime = TimeUtils.formatUTC(nowDate.getTime(), "yyyy-MM-01 00:00:00");
        long startTime = TimeUtils.formatUTC(startMonthTime, null).getTime();
        for (int i = 1; i <= nowDate.getMonth() + 1; i++) {
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
        initHeartRateMonth(date, list);
    }

    private void getHeartRateMonthList() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            Date date = new Date();
            String startMonthTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-01%2000:00:00");
            String endTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd%20HH:mm:ss");
            CWRequestUtils.getInstance().getHeartRateMonthList(getContext(), userModel.getToken()
                    , deviceModel.getD_id(), deviceModel.getImei(), startMonthTime, endTime,
                    mHandler);
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
                    case CWConstant.REQUEST_URL_GET_HEART_RATE_MONTH_LIST: // 心率月平均记录
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
//                                initHeartRateMonth(resultBean.getList());
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
