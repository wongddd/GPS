package com.yyt.trackcar.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      MainFragmentAdapter
 * @ author:        QING
 * @ createTime:    2020/6/2 14:13
 * @ describe:      TODO 主页fragment适配器
 */
public class MainFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mItemList;

    public MainFragmentAdapter(FragmentManager fm, List<Fragment> mItemList) {
        super(fm);
        this.mItemList = mItemList;
    }

    @Override
    public Fragment getItem(int position) {
        return mItemList.get(position);
//        if (PreferencesUtils.getInt(mContext, Constant.MAP_TYPE) == 1 && position == 2)
//            return FragmentFactory.createFragment(6);
//        else
//            return FragmentFactory.createFragment(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//
//    }
}
