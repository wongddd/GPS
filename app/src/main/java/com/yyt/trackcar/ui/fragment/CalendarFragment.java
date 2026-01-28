package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;

import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.base.BaseFragment;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      CalendarFragment
 * @ author:        QING
 * @ createTime:    6/8/21 22:11
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "Calendar")
public class CalendarFragment extends BaseFragment {
    @BindView(R.id.calendarView)
    CalendarView mCalendarView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_calendar;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitleColor(ContextCompat.getColor(mActivity, R.color.white));
        titleBar.setLeftImageResource(R.drawable.ic_back_white);
        titleBar.setBackgroundResource(R.color.red);
        return titleBar;
    }

    @Override
    protected void initViews() {
        mCalendarView.setSelectionType(SelectionType.SINGLE);
//        //Set days you want to connect
//        Calendar calendar = Calendar.getInstance();
//        Set<Long> days = new TreeSet<>();
//        days.add(calendar.getTimeInMillis());
//        days.add(calendar.getTimeInMillis() - 24 * 60 * 60 * 1000);
//        days.add(calendar.getTimeInMillis() - 5 * 24 * 60 * 60 * 1000);
//        //Define colors
//        int textColor = Color.parseColor("#ff0000");
//        int selectedTextColor = Color.parseColor("#ff00ff");
//        int disabledTextColor = Color.parseColor("#ffff00");
//        ConnectedDays connectedDays = new ConnectedDays(days, textColor, selectedTextColor, disabledTextColor);
//        //Connect days to calendar
//        mCalendarView.addConnectedDays(connectedDays);
//        mCalendarView.update();
    }

}
