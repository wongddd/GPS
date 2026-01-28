package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.List;

public class PigeonRingAdapter extends BaseQuickAdapter<AAADeviceModel, BaseViewHolder> {
    public PigeonRingAdapter(@Nullable @org.jetbrains.annotations.Nullable List<AAADeviceModel> data) {
        super(R.layout.aaa_item_pigeon_rings,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AAADeviceModel item) {
        helper.setText(R.id.tv_number,item.getDeviceImei());

        helper.addOnClickListener(R.id.ll_operation);
    }
}
