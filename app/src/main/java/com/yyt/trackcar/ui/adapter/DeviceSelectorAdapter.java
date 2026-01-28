package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.List;

public class DeviceSelectorAdapter extends BaseQuickAdapter<AAADeviceModel, BaseViewHolder> {
    public DeviceSelectorAdapter(@Nullable List<AAADeviceModel> data) {
        super(R.layout.aaa_item_device_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AAADeviceModel item) {
        helper.setText(R.id.tvName, String.format("%s:%s", mContext.getString(R.string.nickname),
                        item.getDeviceName()))
                .setText(R.id.tvImei, String.format(mContext.getString(R.string.device_imei),
                        item.getDeviceImei()));
        helper.getView(R.id.ivSelect).setSelected(item.isSelected());
    }
}
