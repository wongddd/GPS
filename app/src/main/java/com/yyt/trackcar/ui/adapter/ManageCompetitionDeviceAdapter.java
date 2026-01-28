package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.List;

public class ManageCompetitionDeviceAdapter extends BaseQuickAdapter<AAADeviceModel, BaseViewHolder> {
    public ManageCompetitionDeviceAdapter(@Nullable List<AAADeviceModel> data) {
        super(R.layout.adapter_manage_device_of_pigeon_competition,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AAADeviceModel item) {
        helper.setText(R.id.tv_device_imei,item.getDeviceImei());
        helper.addOnClickListener(R.id.fl_delete);
    }
}
