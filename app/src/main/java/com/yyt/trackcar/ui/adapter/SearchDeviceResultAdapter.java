package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.List;

public class SearchDeviceResultAdapter extends BaseQuickAdapter<AAADeviceModel, BaseViewHolder> {
    public SearchDeviceResultAdapter(@Nullable List<AAADeviceModel> data) {
        super(R.layout.adapter_search_device_by_imei,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AAADeviceModel item) {
        helper.setText(R.id.tv_device_imei,String.format("%s:%s", mContext.getString(R.string.nickname),item.getDeviceImei()))
                .setText(R.id.tv_device_name,String.format("%s:%s",mContext.getString(R.string.device_imei_new),item.getDeviceName()));
        helper.addOnClickListener(R.id.iv_race_setting)
                .addOnClickListener(R.id.iv_device_setting);
    }
}
