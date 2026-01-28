package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.PigeonModel;

import java.util.List;

public class PigeonsAdapter extends BaseQuickAdapter<PigeonModel, BaseViewHolder> {
    public PigeonsAdapter(@Nullable @org.jetbrains.annotations.Nullable List<PigeonModel> data) {
        super(R.layout.aaa_item_pigeons,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PigeonModel item) {
        helper.setText(R.id.tv_number,item.getNumber())
                .setText(R.id.tv_device_name,item.getNickname())
                .setText(R.id.tv_color,item.getColor());

        helper.addOnClickListener(R.id.ll_operation);
    }
}
