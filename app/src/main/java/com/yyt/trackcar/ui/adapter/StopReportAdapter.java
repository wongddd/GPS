package com.yyt.trackcar.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.OdometerBean;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.adapter
 * 文件名：   StopReportAdapter
 * 创建者：   QING
 * 创建时间： 2018/5/2 19:31
 * 描述：     TODO 停靠报表适配器
 */

public class StopReportAdapter extends BaseEmptyAdapter<OdometerBean> {
    public StopReportAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.activity_stop_report_item);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, OdometerBean model) {
        if (mData.size() == 0) {
            helper.setBackgroundRes(R.id.ivEmpty, R.mipmap.recharge_normal);
        } else {
            helper.setText(R.id.tvFirst, String.format("%s:%s",
                    mContext.getString(R.string.history_start_date),
                    model.getTripStartDate() == null ? "" : model.getTripStartDate()));
            helper.setText(R.id.tvSecond, String.format("%s:%s",
                    mContext.getString(R.string.history_end_date),
                    model.getTripEndDate() == null ? "" : model.getTripEndDate()));
            helper.setText(R.id.tvThird, String.format("%s:%s",
                    mContext.getString(R.string.history_record_mileage),
                    model.getOdometer() == null ? 0 : model.getOdometer()));
            helper.setText(R.id.tvFourth, String.format("%s:%s",
                    mContext.getString(R.string.trip_report_fuel),
                    model.getOilConsumption() == null ? 0 : model.getOilConsumption()));
            helper.setText(R.id.tvFifth, String.format("%s:%s",
                    mContext.getString(R.string.trip_report_avg_speed),
                    model.getAvgSpeed() == null ? 0 : model.getAvgSpeed()));
            helper.setText(R.id.tvSixth, String.format("%s:%s",
                    mContext.getString(R.string.trip_report_max_speed),
                    model.getMaxSpeed() == null ? 0 : model.getMaxSpeed()));
        }
    }

    @Override
    public void setData(List<OdometerBean> data) {
        mData = data;
        notifyDataSetChangedWrapper();
    }
}
