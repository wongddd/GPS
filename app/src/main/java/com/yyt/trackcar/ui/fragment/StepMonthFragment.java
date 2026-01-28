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
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.StepModel;
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
 * @ fileName:      StepMonthFragment
 * @ author:        QING
 * @ createTime:    6/18/21 17:16
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "StepMonth", anim = CoreAnim.none)
public class StepMonthFragment extends BaseFragment {
    @BindView(R.id.tvStepNum)
    TextView mTvStepNum;
    @BindView(R.id.tvStepUnit)
    TextView mTvStepUnit;
    @BindView(R.id.tvDate)
    TextView mTvDate;
    @BindView(R.id.bcStep)
    BarChart mBcStep;
    private Date mDate;
    private List mItemList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_month;
    }

    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initViews() {
        Date date = new Date();
        mTvStepNum.setText("0");
        mTvStepUnit.setText(getString(R.string.step_unit, ""));
        String dateString = TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy/MM/dd");
        String dayString = TimeUtils.getWeek(getContext(), date.getDay() == 0 ? 6 :
                date.getDay() - 1);
        mTvDate.setText(String.format("%s %s", dateString, dayString));
        initCharts();
        initStepMonth(mDate, mItemList);
    }

    /**
     * 初始化图表
     */
    private void initCharts() {
        mBcStep.getDescription().setEnabled(false);
        mBcStep.setMaxVisibleValueCount(31);
        mBcStep.setPinchZoom(false);
        mBcStep.setDrawGridBackground(false);
        mBcStep.setScaleEnabled(false);
        mBcStep.setTouchEnabled(true);
        mBcStep.setDrawBorders(false);
        mBcStep.setHighlightPerTapEnabled(true);

        mBcStep.getAxisRight().setEnabled(false);
        mBcStep.getAxisLeft().setEnabled(false);
        mBcStep.getAxisLeft().setAxisMinimum(0);
        mBcStep.getAxisLeft().setAxisMaximum(8000);
        mBcStep.getXAxis().setEnabled(true);
        mBcStep.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBcStep.getXAxis().setDrawGridLines(false);
        mBcStep.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        mBcStep.getXAxis().setAxisLineColor(ContextCompat.getColor(mActivity, R.color.white));

        ChartMarkerView mv = new ChartMarkerView(mActivity);
        mBcStep.setMarker(mv);

        Date date;
        if (mDate == null)
            date = new Date();
        else
            date = mDate;
        int days = TimeUtils.getDays(date.getYear() + 1900, date.getMonth() + 1);
        mBcStep.getXAxis().setLabelCount(days);
        mBcStep.getXAxis().setAxisMaximum(days);
        XAxis xAxis = mBcStep.getXAxis();//获取设置X轴
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

        Legend l = mBcStep.getLegend();
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
        for (int i = 1; i <= mBcStep.getXAxis().getLabelCount(); i++) {
            values.add(new BarEntry(i, 0));
        }

        BarDataSet d = new BarDataSet(values, "");
        d.setDrawValues(false);

        int color = ContextCompat.getColor(mActivity, R.color.white);
        d.setColor(color);

        BarData data = new BarData(d);
        mBcStep.setData(data);
        mBcStep.invalidate();
    }

    private void initStepMonth(Date date, List list) {
        mDate = date;
        mItemList = list;
        if (mBcStep == null)
            return;
        if (date == null)
            date = new Date();
        List<StepModel> modelList = new ArrayList<>();
        if (list != null) {
            for (Object obj : list) {
                StepModel model = mGson.fromJson(mGson.toJson(obj), StepModel.class);
                model.setDate(TimeUtils.formatUTCC(model.getCreatetime(),
                        "yyyy-MM-dd"));
                modelList.add(model);
            }
        }
        ArrayList<BarEntry> values = new ArrayList<>();
        Date nowDate = date;
        int days = TimeUtils.getDays(nowDate.getYear() + 1900, nowDate.getMonth() + 1);
        mBcStep.getXAxis().setLabelCount(days);
        mBcStep.getXAxis().setAxisMaximum(days);
        XAxis xAxis = mBcStep.getXAxis();//获取设置X轴
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
        long maxSize = 100;
        xAxis.setValueFormatter(valueFormatter);
        String startMonthTime = TimeUtils.formatUTC(nowDate.getTime(), "yyyy-MM-01 00:00:00");
        long startTime = TimeUtils.formatUTC(startMonthTime, null).getTime();
        for (int i = 1; i <= nowDate.getMonth() + 1; i++) {
            boolean isFind = false;
            String dateTime = TimeUtils.formatUTC(startTime +
                    (long) (i - 1) * TimeUtils.DAY * 1000, "yyyy-MM-dd");
            for (StepModel model : modelList) {
                if (TextUtils.equals(dateTime, model.getDate())) {
                    isFind = true;
                    if (model.getStep() > maxSize)
                        maxSize = model.getStep();
                    values.add(new BarEntry(i, model.getStep()));
                    break;
                }
            }
            if (!isFind)
                values.add(new BarEntry(i, 0));
        }

        long axisMax = (long) Math.ceil(maxSize / 100.0f) * 100;
        mBcStep.getAxisLeft().setAxisMaximum(axisMax);

        BarDataSet d = new BarDataSet(values, "");
        d.setDrawValues(false);

        int color = ContextCompat.getColor(mActivity, R.color.white);
        d.setColor(color);

        BarData data = new BarData(d);
        mBcStep.setData(data);
        mBcStep.invalidate();
    }

    public void refreshDate(Date date, List list) {
        initStepMonth(date, list);
    }

    /**
     * 获取最近七天的步数
     */
    private void getStepList() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            Date date = new Date();
            String startMonthTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-01%2000:00:00");
            String endTime = TimeUtils.formatUTC(date.getTime(), "yyyy-MM-dd%20HH:mm:ss");
            CWRequestUtils.getInstance().getStepList(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), deviceModel.getImei(), startMonthTime, endTime,
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_STEP_LIST: // 获取最近七天的步数
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
//                                initStepMonth(resultBean.getList());
//                                AAADeviceModel deviceModel = getDevice();
//                                requestBean =
//                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject
//                                        ()), AAATrackRequestBeanOldEdition.class);
//                                if (deviceModel != null && deviceModel.getD_id() == requestBean
//                                .getD_id()) {
//                                    if (resultBean.getList() != null && resultBean.getList()
//                                    .size() > 0) {
//                                        for (Object obj : resultBean.getList()) {
//                                            StepModel model = mGson.fromJson(mGson.toJson(obj),
//                                                    StepModel.class);
//                                            model.setImei(deviceModel.getImei());
//                                            model.setDate(TimeUtils.formatUTCC(model
//                                            .getCreatetime(),
//                                                    "yyyy-MM-dd"));
//                                            model.save();
//                                        }
//                                        initChartsData();
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
