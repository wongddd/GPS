package com.yyt.trackcar.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAATrackModel;
import com.yyt.trackcar.utils.AAAStringUtils;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      HistoryRecordAdapter
 * @ author:        QING
 * @ createTime:    6/28/21 19:19
 * @ describe:      TODO
 */
public class HistoryRecordAdapter extends BGARecyclerViewAdapter<AAATrackModel> {

    public HistoryRecordAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_history_record);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, AAATrackModel model) {
        String gpsTime;
        if (!TextUtils.isEmpty(model.getGpsTime()))
            gpsTime = model.getGpsTime();
        else if (TextUtils.isEmpty(model.getLogTime()))
            gpsTime = "";
        else
            gpsTime = model.getLogTime();
        helper.setText(R.id.tvFirst, String.format("%s:%s",
                mContext.getString(R.string.report_time), gpsTime));
        helper.setText(R.id.tvSecond, String.format("%s:%s,%s",
                mContext.getString(R.string.history_record_latlng), model.getLng(),
                model.getLat()));
        helper.setText(R.id.tvThird, String.format("%s:%s",
                mContext.getString(R.string.history_record_direct),
                AAAStringUtils.directionDescription(mContext, model.getHeading())));
        helper.setText(R.id.tvFourth, String.format("%s:%s",
                mContext.getString(R.string.history_record_speed),
                AAAStringUtils.getSpeed(model.getSpeed())));
        helper.setText(R.id.tvFifth, String.format("%s:%s|%s",
                mContext.getString(R.string.history_record_fuel), model.getAd1() == null ? 0 : model.getAd1(),
                model.getAd2() == null ? 0 : model.getAd2()));
        helper.setText(R.id.tvSixth, String.format("%s:%s",
                mContext.getString(R.string.history_record_mileage), model.getOdometer() == null ? 0 : model.getOdometer()));
        helper.setText(R.id.tvSeventh, String.format("%s:%s",
                mContext.getString(R.string.history_record_temp), model.getTemperature() == null ? 0 : model.getTemperature()));
        helper.setText(R.id.tvEighth, String.format("%s:%s",
                mContext.getString(R.string.history_record_alarm), model.getAlarm() == null ? "" : model.getAlarm()));
        String onOff;
        if (model.getAccStatus() != null && model.getAccStatus() == 1)
            onOff = mContext.getString(R.string.on);
        else
            onOff = mContext.getString(R.string.off);
        helper.setText(R.id.tvNinth, String.format("%s:%s",
                mContext.getString(R.string.history_record_status),
                mContext.getString(R.string.device_acc, onOff)));
    }


    public void setData(List<AAATrackModel> data) {
        mData = data;
        notifyDataSetChangedWrapper();
    }
}
