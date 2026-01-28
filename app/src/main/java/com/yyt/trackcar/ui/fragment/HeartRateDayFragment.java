package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.HealthReportBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.HealthHourModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.ui.widget.ChartMarkerView;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      HeartRateDayFragment
 * @ author:        QING
 * @ createTime:    5/29/21 15:38
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "HeartRateDay", anim = CoreAnim.none)
public class HeartRateDayFragment extends BaseFragment {
    @BindView(R.id.tvHeartRateNum)
    TextView mTvHeartRateNum;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.lbHeartRate)
    LineChart mLcHeartRate;
    private String mDate;
    private List mItemList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_heart_rate_day;
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
        initHeartRateDay(mItemList);
//        initChartsData();
//        refreshDate(TimeUtils.formatUTC(System.currentTimeMillis(),
//                "yyyy-MM-dd"));
    }

    /**
     * 初始化图表
     */
    private void initCharts() {
        mLcHeartRate.getDescription().setEnabled(false);
        mLcHeartRate.setMaxVisibleValueCount(25);
        mLcHeartRate.setPinchZoom(false);
        mLcHeartRate.setDrawGridBackground(false);
        mLcHeartRate.setScaleEnabled(false);
        mLcHeartRate.setTouchEnabled(true);
        mLcHeartRate.setDrawBorders(false);
        mLcHeartRate.setHighlightPerTapEnabled(true);

        mLcHeartRate.getAxisRight().setEnabled(false);
        mLcHeartRate.getAxisLeft().setEnabled(false);
        mLcHeartRate.getAxisLeft().setAxisMinimum(0);
        mLcHeartRate.getAxisLeft().setAxisMaximum(120);
        mLcHeartRate.getXAxis().setEnabled(true);
        mLcHeartRate.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLcHeartRate.getXAxis().setDrawGridLines(false);
        mLcHeartRate.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        mLcHeartRate.getXAxis().setLabelCount(25);
        mLcHeartRate.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity, R.color.white));

        ChartMarkerView mv = new ChartMarkerView(mActivity, R.drawable.bg_white_round, R.color.blue);
        mLcHeartRate.setMarker(mv);

        XAxis xAxis = mLcHeartRate.getXAxis();//获取设置X轴
        ValueFormatter valueFormatter = new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value) - 1;
                switch (index) {
                    case 0:
                    case 24:
                        return "00:00";
                    case 6:
                        return "06:00";
                    case 12:
                        return "12:00";
                    case 18:
                        return "18:00";
                    default:
                        return "";
                }
            }
        };
        xAxis.setValueFormatter(valueFormatter);

        Legend l = mLcHeartRate.getLegend();
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
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, 0));
        values.add(new Entry(2, 0));
        values.add(new Entry(3, 0));
        values.add(new Entry(4, 0));
        values.add(new Entry(5, 0));
        values.add(new Entry(6, 0));
        values.add(new Entry(7, 0));
        values.add(new Entry(8, 0));
        values.add(new Entry(9, 0));
        values.add(new Entry(10, 0));
        values.add(new Entry(11, 0));
        values.add(new Entry(12, 0));
        values.add(new Entry(13, 0));
        values.add(new Entry(14, 0));
        values.add(new Entry(15, 0));
        values.add(new Entry(16, 0));
        values.add(new Entry(17, 0));
        values.add(new Entry(18, 0));
        values.add(new Entry(19, 0));
        values.add(new Entry(20, 0));
        values.add(new Entry(21, 0));
        values.add(new Entry(22, 0));
        values.add(new Entry(23, 0));
        values.add(new Entry(24, 0));
        values.add(new Entry(25, 0));

        LineDataSet d = new LineDataSet(values, "");
        d.setCubicIntensity(0.2f);
        d.setDrawCircles(false);
        d.setDrawFilled(true);
        d.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d.setDrawValues(false);
        d.setLineWidth(1.8f);
        d.setCircleRadius(4f);
        d.setCircleColor(Color.WHITE);
        d.setHighLightColor(ContextCompat.getColor(mActivity, R.color.white));
        d.setColor(Color.WHITE);
        d.setFillColor(Color.WHITE);
        d.setFillAlpha(100);
        d.setDrawHorizontalHighlightIndicator(false);
        d.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return mLcHeartRate.getAxisLeft().getAxisMinimum();
            }
        });

//        int color = ContextCompat.getColor(mActivity, R.color.white);
//        d.setColor(color);
//        d.setCircleColor(color);
        dataSets.add(d);

        LineData data = new LineData(dataSets);
        mLcHeartRate.setData(data);
        mLcHeartRate.invalidate();
    }

    private void initHeartRateDay(List list) {
        mItemList = list;
        if (mLcHeartRate == null)
            return;
        List<HealthReportBean> modelList = new ArrayList<>();
        if (list != null) {
            for (Object obj : list) {
                HealthReportBean model = mGson.fromJson(mGson.toJson(obj), HealthReportBean.class);
                modelList.add(model);
            }
        }
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            boolean isFind = false;
            for (HealthReportBean model : modelList) {
                if ((i == 1 && "24".equals(model.getTime())) || String.valueOf(i - 1).equals(model.getTime())) {
                    isFind = true;
                    int num = model.getMsg();
                    values.add(new Entry(i, num));
                    break;
                }
            }
            if (!isFind)
                values.add(new Entry(i, 0));
        }

        LineDataSet d = new LineDataSet(values, "");
        d.setCubicIntensity(0.2f);
        d.setDrawCircles(false);
        d.setDrawFilled(true);
        d.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d.setDrawValues(false);
        d.setLineWidth(1.8f);
        d.setCircleRadius(4f);
        d.setCircleColor(Color.WHITE);
        d.setHighLightColor(ContextCompat.getColor(mActivity, R.color.white));
        d.setColor(Color.WHITE);
        d.setFillColor(Color.WHITE);
        d.setFillAlpha(100);
        d.setDrawHorizontalHighlightIndicator(false);
        d.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet,
                                             LineDataProvider dataProvider) {
                return mLcHeartRate.getAxisLeft().getAxisMinimum();
            }
        });

//        int color = ContextCompat.getColor(mActivity, R.color.white);
//        d.setColor(color);
//        d.setCircleColor(color);
        dataSets.add(d);

        LineData data = new LineData(dataSets);
        mLcHeartRate.setData(data);
        mLcHeartRate.invalidate();
    }

    public void refreshDate(List list) {
        initHeartRateDay(list);
    }

//    public void refreshDate(String date) {
//        AAADeviceModel deviceModel = getDevice();
//        if (deviceModel != null) {
//            OperatorGroup operatorGroup =
//                    OperatorGroup.clause(OperatorGroup.clause()
//                            .and(HealthHourModel_Table.type.eq(0))
//                            .and(HealthHourModel_Table.imei.eq(deviceModel.getImei()))
//                            .and(HealthHourModel_Table.date.eq(date)));
//            List<HealthHourModel> list = SQLite.select().from(HealthHourModel.class)
//                    .where(operatorGroup)
//                    .orderBy(HealthHourModel_Table.time, true)
//                    .queryList();
//            if (list.size() == 0)
//                getHeartRateDayList(date);
//            List<HealthReportBean> modelList = new ArrayList<>();
//            for (HealthHourModel model : list) {
//                HealthReportBean bean = new HealthReportBean();
//                int num = 0;
//                try {
//                    num = Integer.parseInt(model.getMsg());
//                } catch (NumberFormatException e) {
//                    if (BuildConfig.DEBUG)
//                        e.printStackTrace();
//                }
//                bean.setMsg(num);
//                bean.setTime(model.getTime());
//                modelList.add(bean);
//            }
//            initHeartRateDay(modelList);
//        }
//    }

    private void getHeartRateDayList(String date) {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            long nowTime = System.currentTimeMillis();
            mDate = date;
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
                                if (TextUtils.equals(mDate, recordDate))
                                    initHeartRateDay(resultBean.getList());
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
