package com.yyt.trackcar.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      AlarmListAdapter
 * @ author:        QING
 * @ createTime:    7/1/21 20:52
 * @ describe:      TODO
 */
public class AlarmListAdapter extends BGARecyclerViewAdapter<BaseItemBean> {

    public AlarmListAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_alarm_list);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, BaseItemBean model) {
        helper.setText(R.id.tvFirst, String.format("%s:%s",
                mContext.getString(R.string.report_time), model.getTitle()));
        helper.setText(R.id.tvSecond, String.format("%s:%s",
                mContext.getString(R.string.alarm_report_content), model.getContent()));
    }

    @Override
    public void setData(List<BaseItemBean> data) {
        mData = data;
        notifyDataSetChangedWrapper();
    }
}
