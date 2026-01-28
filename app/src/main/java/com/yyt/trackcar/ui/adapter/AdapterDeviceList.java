package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.SelectionItemBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;

import java.util.List;

public class AdapterDeviceList extends BaseQuickAdapter<SelectionItemBean, BaseViewHolder> {
    private int currentTag = 0;
    public AdapterDeviceList(@Nullable List<SelectionItemBean> data) {
        super(R.layout.adapter_device_list, data);
    }

    public void switchCurrentTag(int currentTag) {
        this.currentTag = currentTag;
    }

    @Override
    protected void convert(BaseViewHolder helper, SelectionItemBean item) {
        AAADeviceModel deviceModel = item.getDeviceModel();
        helper.setText(R.id.tv_device_imei, String.format("%s:%s",mContext.getString(R.string.imei),deviceModel.getDeviceImei()))
                .setText(R.id.tv_device_name, String.format("%s:%s",mContext.getString(R.string.nickname),deviceModel.getDeviceName()))
                .setText(R.id.tv_device_status, deviceModel.getDeviceStatus() == 2 ? String.format("(%s)",mContext.getString(R.string.flight_lost)) : "");
        helper.getView(R.id.tvUnbind).setVisibility(currentTag == 0 ? View.VISIBLE : View.GONE);
        helper.getView(R.id.tvLost).setVisibility(deviceModel.getDeviceStatus() != 2 ? View.VISIBLE : View.GONE);
        helper.getView(R.id.tvRetrieve).setVisibility(deviceModel.getDeviceStatus() == 2 ? View.VISIBLE : View.GONE);
        helper.getView(R.id.ivCheck).setSelected(item.isSelected());
        helper.addOnClickListener(R.id.tvUnbind, R.id.tvLost, R.id.tvRetrieve, R.id.ivCheck);
    }
}
