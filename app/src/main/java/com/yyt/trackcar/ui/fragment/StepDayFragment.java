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
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
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
import com.yyt.trackcar.dbflow.HealthHourModel_Table;
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
 * @ fileName:      StepDayFragment
 * @ author:        QING
 * @ createTime:    6/18/21 17:08
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "StepDay", anim = CoreAnim.none)
public class StepDayFragment extends BaseFragment {
    @BindView(R.id.tvStepNum)
    TextView mTvStepNum;
    @BindView(R.id.tvStepUnit)
    TextView mTvStepUnit;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.lbStep)
    LineChart mLcStep;
    private String mDate;
    private List mItemList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_day;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initViews() {
        Date date = new Date();
        mTvStepNum.setText("--");
        mTvStepUnit.setText(getString(R.string.step_unit, ""));
        String dateString = TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy/MM/dd");
        String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                date.getDay() - 1);
        mTvDate.setText(String.format("%s %s", dateString, dayString));
        initCharts();
        initStepDay(mItemList);
    }

    /**
     * 初始化图表
     */
    private void initCharts() {
        mLcStep.getDescription().setEnabled(false);
        mLcStep.setMaxVisibleValueCount(25);
        mLcStep.setPinchZoom(false);
        mLcStep.setDrawGridBackground(false);
        mLcStep.setScaleEnabled(false);
        mLcStep.setTouchEnabled(true);
        mLcStep.setDrawBorders(false);
        mLcStep.setHighlightPerTapEnabled(true);

        mLcStep.getAxisRight().setEnabled(false);
        mLcStep.getAxisLeft().setEnabled(false);
        mLcStep.getAxisLeft().setAxisMinimum(0);
        mLcStep.getAxisLeft().setAxisMaximum(8000);
        mLcStep.getXAxis().setEnabled(true);
        mLcStep.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLcStep.getXAxis().setDrawGridLines(false);
        mLcStep.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        mLcStep.getXAxis().setLabelCount(25);
        mLcStep.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity, R.color.white));

        ChartMarkerView mv = new ChartMarkerView(mActivity);
        mLcStep.setMarker(mv);

        XAxis xAxis = mLcStep.getXAxis();//获取设置X轴
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
                return mLcStep.getAxisLeft().getAxisMinimum();
            }
        });

        dataSets.add(d);

        LineData data = new LineData(dataSets);
        mLcStep.setData(data);
        mLcStep.invalidate();
    }

    private void initStepDay(List list) {
        mItemList = list;
        if (mLcStep == null)
            return;
        List<HealthReportBean> modelList = new ArrayList<>();
        if (list != null) {
            for (Object obj : list) {
                HealthReportBean model = mGson.fromJson(mGson.toJson(obj), HealthReportBean.class);
                modelList.add(model);
            }
        }
        long maxSize = 100;
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            boolean isFind = false;
            for (HealthReportBean model : modelList) {
                if ((i == 1 && "24".equals(model.getTime())) || String.valueOf(i - 1).equals(model.getTime())) {
                    isFind = true;
                    int num = model.getMsg();
                    if (num > maxSize)
                        maxSize = num;
                    values.add(new Entry(i, num));
                    break;
                }
            }
            if (!isFind)
                values.add(new Entry(i, 0));
        }

        long axisMax = (long) Math.ceil(maxSize / 100.0f) * 100;
        mLcStep.getAxisLeft().setAxisMaximum(axisMax);

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
                return mLcStep.getAxisLeft().getAxisMinimum();
            }
        });

//        int color = ContextCompat.getColor(mActivity, R.color.white);
//        d.setColor(color);
//        d.setCircleColor(color);
        dataSets.add(d);

        LineData data = new LineData(dataSets);
        mLcStep.setData(data);
        mLcStep.invalidate();
    }

    public void refreshDate(String date) {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(HealthHourModel_Table.type.eq(3))
                            .and(HealthHourModel_Table.imei.eq(deviceModel.getImei()))
                            .and(HealthHourModel_Table.date.eq(date)));
            List<HealthHourModel> list = SQLite.select().from(HealthHourModel.class)
                    .where(operatorGroup)
                    .orderBy(HealthHourModel_Table.time, true)
                    .queryList();
            if (list.size() == 0)
                getStepDayList(date);
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
            initStepDay(modelList);
        }
    }

    private void getStepDayList(String date) {
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
            CWRequestUtils.getInstance().getStepDayList(getContext(), userModel.getToken()
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
                    case CWConstant.REQUEST_URL_GET_STEP_DAY_LIST: // 计步日记录  24小时记录
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
                                    initStepDay(resultBean.getList());
                                if (!TextUtils.equals(recordDate, nowDate)) {
                                    for (int i = 1; i <= 24; i++) {
                                        HealthHourModel model = new HealthHourModel();
                                        model.setImei(requestBean.getImei());
                                        model.setDate(recordDate);
                                        model.setTime(String.valueOf(i));
                                        model.setType(3);
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
