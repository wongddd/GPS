package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAAUserModel;

import java.util.List;

public class AgentUserAdapter extends BaseQuickAdapter<AAAUserModel, BaseViewHolder> {
    public AgentUserAdapter(@Nullable List<AAAUserModel> data) {
        super(R.layout.adapter_search_device_by_imei,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AAAUserModel item) {
        helper.setText(R.id.tv_device_name,String.valueOf(item.getUserId()))
                .setText(R.id.tv_device_imei,item.getUserName());
        helper.addOnClickListener(R.id.fl_edit);
    }
}
