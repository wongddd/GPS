package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.dialog.OnDaysSelectionListener;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.tabbar.TabSegment;
import com.yyt.trackcar.R;
import com.yyt.trackcar.ui.adapter.MainFragmentAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      SleepFragment
 * @ author:        QING
 * @ createTime:    6/12/21 14:58
 * @ describe:      TODO
 */
@SuppressLint("NonConstantResourceId")
@Page(name = "Sleep")
public class SleepFragment extends BaseFragment {
    @BindView(R.id.tabSegment)
    TabSegment mTabSegment;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    MainFragmentAdapter mAdapter;
    private List<Fragment> mItemList = new ArrayList<>();
    private CalendarDialog mCalendarDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sleep;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitleColor(ContextCompat.getColor(mActivity, R.color.white));
        titleBar.setLeftImageResource(R.drawable.ic_back_white);
        titleBar.setBackgroundResource(R.color.health_sleep);
        titleBar.setTitle(R.string.sleep_title);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_note) {
            @Override
            public void performAction(View view) {
                if (mCalendarDialog == null) {
                    mCalendarDialog = new CalendarDialog(mActivity, new OnDaysSelectionListener() {
                        @Override
                        public void onDaysSelected(List<Day> selectedDays) {

                        }
                    });
                    mCalendarDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            mCalendarDialog.setSelectionType(SelectionType.SINGLE);
                            mCalendarDialog.setSelectedDayBackgroundColor(ContextCompat.getColor(mActivity, R.color.red));
                        }
                    });
                }
                if (!mCalendarDialog.isShowing())
                    mCalendarDialog.show();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.picker_day)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.week_unit)));
        mTabSegment.addTab(new TabSegment.Tab(getString(R.string.picker_month)));
        mItemList.add(new HeartRateDayFragment());
        mItemList.add(new HeartRateWeekFragment());
        mItemList.add(new HeartRateMonthFragment());
        mAdapter = new MainFragmentAdapter(getChildFragmentManager(), mItemList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0, false);
        mTabSegment.setupWithViewPager(mViewPager, false);
        mTabSegment.setIndicatorWidthAdjustContent(false);
    }

}
