package com.yyt.trackcar.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.GpsPigeonRaceBean;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

@Page(name = "createPigeonCompetitionFragment", anim = CoreAnim.none)
public class CreateAndModifyPigeonCompetitionFragment extends BaseFragment {

    @BindView(R.id.et_competition_name)
    EditText tvCompetitionName;
    @BindView(R.id.btn_select_time)
    Button btnSelectedTime;
    @BindView(R.id.btn_create_competition)
    Button btnCreate;
    @BindView(R.id.et_start_latitude)
    EditText etStartLat;
    @BindView(R.id.et_start_longitude)
    EditText etStartLng;
    @BindView(R.id.et_end_latitude)
    EditText etEndLat;
    @BindView(R.id.et_end_longitude)
    EditText etEndLng;

    private TimePickerView mTimePickerView;
    private Context mContext;
    private GpsPigeonRaceBean gpsPigeonRaceBean;
    private final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 0.创建比赛(default) 1.修改参数
     */
    private int type = 0;
    private TitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_create_pigeon_competition;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        titleBar = super.initTitle();
//        if (type == 1)
//            titleBar.setTitle("修改比赛信息");
//        else
//            titleBar.setTitle(getString(R.string.race_information));
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.race_information)));
        return titleBar;
    }

    @Override
    protected void initViews() {
        mContext = getContext();
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt(TConstant.TYPE);
            if (type == 1) {
//                titleBar.setTitle("修改比赛信息");
                gpsPigeonRaceBean = bundle.getParcelable(TConstant.PARCELABLE);
                btnCreate.setText(R.string.update);
                tvCompetitionName.setText(gpsPigeonRaceBean.getPigeonRaceName());
                btnSelectedTime.setText(TimeUtils.formatUTC(gpsPigeonRaceBean.getPigeonRaceDate(), DATE_FORMAT));
                etStartLat.setText(String.valueOf(gpsPigeonRaceBean.getStarLat() == null ? "" : gpsPigeonRaceBean.getStarLat()));
                etStartLng.setText(String.valueOf(gpsPigeonRaceBean.getStarLon() == null ? "" : gpsPigeonRaceBean.getStarLon()));
                etEndLat.setText(String.valueOf(gpsPigeonRaceBean.getEndLat() == null ? "" : gpsPigeonRaceBean.getEndLat()));
                etEndLng.setText(String.valueOf(gpsPigeonRaceBean.getEndLon() == null ? "" : gpsPigeonRaceBean.getEndLon()));
            }
        }
        if (type == 0) {
            gpsPigeonRaceBean = new GpsPigeonRaceBean();
            gpsPigeonRaceBean.setPigeonRaceDate(new Date().getTime());
            btnSelectedTime.setText(TimeUtils.formatUTC(new Date().getTime(), DATE_FORMAT));
        }
    }

    @Override
    protected void initListeners() {
        btnSelectedTime.setOnClickListener(view -> {
            if (gpsPigeonRaceBean == null) gpsPigeonRaceBean = new GpsPigeonRaceBean();
            showTimePickView(gpsPigeonRaceBean.getPigeonRaceDate());
        });
        btnCreate.setOnClickListener(view -> {
            String str = tvCompetitionName.getText().toString().trim();
            if (TextUtils.isEmpty(str)) {
                showMessage(String.format("%s%s", getString(R.string.competition_name), getString(R.string.cannot_empty_prompt)));
                tvCompetitionName.requestFocus();
                return;
            }
            gpsPigeonRaceBean.setPigeonRaceName(str);
            Double value = convertEnteredContentToDouble(etStartLng);
            if (value != null) {
                gpsPigeonRaceBean.setStarLon(value);
            }
            value = convertEnteredContentToDouble(etStartLat);
            if (value != null) {
                gpsPigeonRaceBean.setStarLat(value);
            }
            value = convertEnteredContentToDouble(etEndLng);
            if (value != null) {
                gpsPigeonRaceBean.setEndLon(value);
            }
            value = convertEnteredContentToDouble(etEndLat);
            if (value != null) {
                gpsPigeonRaceBean.setEndLat(value);
            }
            showDialog();
            if (type == 0)
                CarGpsRequestUtils.createPigeonCompetition(getTrackUserModel(), gpsPigeonRaceBean, mHandler);
            else if (type == 1)
                CarGpsRequestUtils.updateCompetitionInfo(getTrackUserModel(), gpsPigeonRaceBean, mHandler);
        });
    }

    private Double convertEnteredContentToDouble(EditText view) {
        String valueStr = view.getText().toString().trim();
        if (TextUtils.isEmpty(valueStr)) {
            return null;
        } else {
            return Double.parseDouble(valueStr);
        }
    }

    private void showTimePickView(Long time) {
        if (mTimePickerView == null || !mTimePickerView.isShowing()) {
            Calendar selectedDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            Date date = new Date();
            date.setTime(time);
            startDate.set(date.getYear() + 1900, date.getMonth(), date.getDate(), 0, 0);
            endDate.set(date.getYear() + 1901, 11, 31, 0, 0);
            selectedDate.set(date.getYear() + 1900, date.getMonth(), date.getDate(), 0, 0, 0);
            mTimePickerView = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {//选中事件回调
                    btnSelectedTime.setText(TimeUtils.formatUTC(date.getTime(), DATE_FORMAT));
                    gpsPigeonRaceBean.setPigeonRaceDate(date.getTime());
                }
            }).setType(true, true, true, false, false, false)// 默认全部显示
                    .setCancelText(getString(R.string.cancel))//取消按钮文字
                    .setSubmitText(getString(R.string.confirm))//确认按钮文字
                    .setContentTextSize(15) //滚轮文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleText("")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    .setCancelColor(Color.BLUE)//取消按钮文字颜色
                    .setTitleBgColor(getResources().getColor(R.color.white))//标题背景颜色 Night mode
                    .setBgColor(getResources().getColor(R.color.layout_background))//滚轮背景颜色 Night
                    // mode
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel(getString(R.string.year), getString(R.string.mouth), getString(R
                                    .string.day), getString(R.string.hour_new), getString(R.string
                                    .minute_new),
                            getString(R.string.second_new))//默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build();
            mTimePickerView.show();
        }
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            try {
                AAABaseResponseBean response;
                switch (msg.what) {
                    case TConstant.REQUEST_CREATE_PIGEON_COMPETITION:
                        dismisDialog();
                        if (msg.obj == null) {
                            showMessage(R.string.request_error_prompt);
                            return false;
                        }
                        response = (AAABaseResponseBean) msg.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            showMessage(String.format("%s%s", R.string.create_pigeon_competition, getString(R.string.succeed)));
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("newCompetitionCreated", gpsPigeonRaceBean);
                            intent.putExtras(bundle);
                            setFragmentResult(1, intent);
                            popToBack();
                        } else {
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                        break;
                    case TConstant.REQUEST_UPDATE_COMPETITION_INFO:
                        dismisDialog();
                        if (msg.obj == null) {
                            showMessage(R.string.request_error_prompt);
                            return false;
                        }
                        response = (AAABaseResponseBean) msg.obj;
                    if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                        showMessage(R.string.update_succeed_prompt);
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("newCompetitionCreated", gpsPigeonRaceBean);
                        intent.putExtras(bundle);
                        setFragmentResult(1, intent);
                        popToBack();
                    } else {
                        showMessage(ErrorCode.getResId(response.getCode()));
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

}
