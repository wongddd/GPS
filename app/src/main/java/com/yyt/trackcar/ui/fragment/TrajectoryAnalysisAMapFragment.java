package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      TrajectoryAnalysisAMapFragment
 * @ author:        QING
 * @ createTime:    2023/3/30 14:37
 * @ describe:      TODO
 */
@Page(name = "TrajectoryAnalysisAMap")
public class TrajectoryAnalysisAMapFragment extends BaseFragment implements View.OnClickListener {
    private Button mStartDateBtn; // 开始时间按钮
    private Button mEndDateBtn; // 结束时间按钮

    private TimePickerView mTimePickerView; // 时间选择器

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.trajectory_analysis);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_calendar) {
            @Override
            public void performAction(View view) {
                selectStartAndEndDateDialog();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
    }

    /**
     * 选择开始结束时间
     */
    @SuppressLint("InflateParams")
    private void selectStartAndEndDateDialog() {
        Context context = getContext();
        if ((mMaterialDialog == null || !mMaterialDialog.isShowing()) && context != null) {
            MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(context);
            try {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/arial" +
                        ".ttf");
                if (typeface != null)
                    materialDialogBuilder.typeface(typeface, typeface);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            materialDialogBuilder.title(R.string.select_time);
            String startDate;
            String endDate;
            if (mStartDateBtn == null || mEndDateBtn == null) {
                startDate = TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy/MM/dd 00:00:00");
                endDate = TimeUtils.formatUTC(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss");
            } else {
                startDate = mStartDateBtn.getText().toString();
                endDate = mEndDateBtn.getText().toString();
            }
            View customView = getLayoutInflater().inflate(R.layout.dialog_start_end_date, null);
            mStartDateBtn = customView.findViewById(R.id.startDateBtn);
            mEndDateBtn = customView.findViewById(R.id.endDateBtn);
            mStartDateBtn.setText(startDate);
            mEndDateBtn.setText(endDate);
            mStartDateBtn.setOnClickListener(this);
            mEndDateBtn.setOnClickListener(this);
            materialDialogBuilder.customView(customView, true);
            materialDialogBuilder.positiveText(R.string.confirm)
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();
                    });
            materialDialogBuilder.negativeText(R.string.cancel);
            mMaterialDialog = materialDialogBuilder.show();
        }
    }

    /**
     * 显示时间选择器
     *
     * @param type       类型
     * @param title      标题
     * @param timeString 时间
     */
    private void showTimePickView(final int type, String title, String timeString) {
        Context context = getContext();
        if (context != null && (mTimePickerView == null || !mTimePickerView.isShowing())) {
            Calendar selectedDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            Date date = TimeUtils.formatUTC(timeString, "yyyy/MM/dd HH:mm:ss");
            startDate.set(2022, 0, 1, 0, 0, 0);
            endDate.setTime(new Date());
            endDate.set(endDate.get(Calendar.YEAR), 11, 31, 23, 59, 59);
            selectedDate.setTime(date);
            mTimePickerView = new TimePickerBuilder(context, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {//选中事件回调
                    if (type == 0) {
                        mStartDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
                                "HH:mm:ss"));
                        Date endTime = TimeUtils.formatUTC(mEndDateBtn.getText().toString(),
                                "yyyy/MM/dd HH:mm:ss");
                        if (endTime.getTime() < date.getTime())
                            mEndDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
                                    "23:59:59"));
                    } else {
                        mEndDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd " +
                                "HH:mm:ss"));
                        Date startTime = TimeUtils.formatUTC(mStartDateBtn.getText().toString(),
                                "yyyy/MM/dd HH:mm:ss");
                        if (startTime.getTime() > date.getTime())
                            mStartDateBtn.setText(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd" +
                                    " 00:00:00"));
                    }
                }
            }).setType(true, true, true, true, true, true)// 默认全部显示
                    .setCancelText(getString(R.string.cancel))//取消按钮文字
                    .setSubmitText(getString(R.string.confirm))//确认按钮文字
                    .setContentTextSize(15) //滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleText(title)//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    // mode
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel(getString(R.string.year), getString(R.string.mouth), getString(R
                                    .string.day), getString(R.string.hour_new), getString(R.string
                                    .minute_new),
                            getString(R.string.second_new))//默认设置为年月日时分秒
                    .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(true)//是否显示为对话框样式
                    .build();
            mTimePickerView.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startDateBtn: // 开始时间
                showTimePickView(0, getString(R.string.history_start_date), mStartDateBtn.getText
                        ().toString());
                break;
            case R.id.endDateBtn: // 结束时间
                showTimePickView(1, getString(R.string.history_end_date), mStartDateBtn.getText
                        ().toString());
                break;
            default:
                break;
        }
    }
}
