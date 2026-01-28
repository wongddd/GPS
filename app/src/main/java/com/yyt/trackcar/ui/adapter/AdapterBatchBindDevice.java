package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BatchBindDeviceResponseBean;

import java.util.List;

public class AdapterBatchBindDevice extends BaseQuickAdapter<BatchBindDeviceResponseBean, BaseViewHolder> {
    public AdapterBatchBindDevice(@Nullable List<BatchBindDeviceResponseBean> data) {
        super(R.layout.adapter_batch_bind_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BatchBindDeviceResponseBean item) {
        helper.setText(R.id.tv_imei, item.getDeviceImei());
        TextView view =helper.getView(R.id.tv_status);
        if (item.isBound()) {
            view.setText(R.string.succeed);
            view.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
            view.setText(R.string.failed);
            view.setTextColor(mContext.getResources().getColor(R.color.red));
        }
    }
}
