package com.yyt.trackcar.ui.adapter;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceRaceconfigplan;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FlightTrainingAdapter extends BaseQuickAdapter<DeviceRaceconfigplan, BaseViewHolder> {
    public FlightTrainingAdapter(@Nullable List<DeviceRaceconfigplan> data) {
        super(R.layout.adapter_flight_training, data);
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void convert(BaseViewHolder helper, DeviceRaceconfigplan item) {
        helper.setText(R.id.tv_device_imei, mContext.getString(R.string.device_imei, item.getDeviceImei()))
                .setText(R.id.tv_end_time, String.format("%s:%s", mContext.getString(R.string.end_time)
                        , TextUtils.isEmpty(item.getCstEnddatetime()) ? "" : item.getCstEnddatetime()))
                .setText(R.id.tv_count, String.format("%s:%s", mContext.getString(R.string.point_count), item.getPositionCount() == null ? 0 : item.getPositionCount()))
                .setText(R.id.tv_valid_time, String.format("%s:%s", mContext.getString(R.string.valid_end_time)
                        , transDateToString(item.getCstValidenddatetime())))
                .setText(R.id.tv_valid_count, String.format("%s:%s", mContext.getString(R.string.valid_point_count), item.getPositionValidcount() == null ? 0 : item.getPositionValidcount()))
                .setText(R.id.tv_total_positioning_time, String.format("%s:%s", mContext.getString(R.string.total_positioning_time), item.getTotalpositioningTime() == null ? "" : item.getTotalpositioningTime()))
                .setText(R.id.tv_distribute_time, String.format("%s:%s", mContext.getString(R.string.configuration_data_modification_time), transDateToString(item.getCst())))
                .setText(R.id.first_location_time, String.format("%s:%s", mContext.getString(R.string.first_location_time), transDateToString(item.getFirstGpstime())))
                .setText(R.id.tv_device_name, String.format("%s:%s", mContext.getString(R.string.nickname), item.getDeviceName()))
                .setText(R.id.tv_total_standby_time, String.format("%s:%s", mContext.getString(R.string.total_standby_time), item.getTotalStandbyTime() == null ? "" : item.getTotalStandbyTime()))
                .setText(R.id.tv_last_log_time, String.format("%s:%s", mContext.getString(R.string.last_log_time), transDateToString(item.getLogTime())))
                .setText(R.id.tv_schedule_start_time, String.format("%s:%s", mContext.getString(R.string.scheduled_start_time), transDateToString(item.getRsut())))
                .setText(R.id.tv_start_standby_time, String.format("%s:%s", mContext.getString(R.string.standby_time), item.getStandbyDatetime() == null ? "" : transDateToString(item.getStandbyDatetime())));

        helper.addOnClickListener(R.id.iv_edit)
                .addOnClickListener(R.id.iv_information)
                .addOnClickListener(R.id.node_delete);

    }

    private String transDateToString(Long date) {
        if (date == null) {
            return "";
        } else {
            return sdf.format(date);
        }
    }
}
