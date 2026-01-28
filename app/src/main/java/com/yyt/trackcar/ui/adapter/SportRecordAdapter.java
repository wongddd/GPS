package com.yyt.trackcar.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.TrackSumBean;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      SportRecordAdapter
 * @ author:        QING
 * @ createTime:    6/28/21 21:06
 * @ describe:      TODO
 */
public class SportRecordAdapter extends BaseEmptyAdapter<TrackSumBean> {

    public SportRecordAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_sport_record);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, TrackSumBean model) {
        if (mData.size() == 0) {
        } else {
            helper.setText(R.id.tvFirst, String.format("%s:%s",
                    mContext.getString(R.string.report_data), model.getSquadDate()));
            helper.setText(R.id.tvSecond, String.format("%s:%s",
                    mContext.getString(R.string.ride_human), model.getRide()));
            helper.setText(R.id.tvThird, String.format("%s:%s",
                    mContext.getString(R.string.ride_pas), model.getERide()));
            helper.setText(R.id.tvFourth, String.format("%s:%s",
                    mContext.getString(R.string.ride_odometer), model.getRideMileage()));
            helper.setText(R.id.tvFifth, String.format("%s:%s",
                    mContext.getString(R.string.ride_calorie), model.getCalorie()));
        }
    }

    @Override
    public void setData(List<TrackSumBean> data) {
        mData = data;
        notifyDataSetChangedWrapper();
    }
}
