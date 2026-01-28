package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BLEItemModel;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      BlueToothAdapter
 * @ author:        QING
 * @ createTime:    2023/7/21 16:20
 * @ describe:      TODO 蓝牙选项适配器
 */
public class BlueToothItemAdapter extends BaseQuickAdapter<BLEItemModel, BaseViewHolder> {

    public BlueToothItemAdapter(@Nullable List<BLEItemModel> data) {
        super(R.layout.item_blue_tooth, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BLEItemModel item) {
        helper.setText(R.id.tvTitle, item.getImei());
        helper.setText(R.id.tvContent, item.getMacAddress());
        StringBuilder statusString = new StringBuilder();
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusString.append(String.format(mContext.getString(R.string.blue_tooth_upload_status), item.getStatus())).append("\n");
        }
        if (!TextUtils.isEmpty(item.getConnectTime())) {
            statusString.append(String.format(mContext.getString(R.string.blue_tooth_connect_time), item.getConnectTime())).append("\n");
        }
        if (!TextUtils.isEmpty(item.getDisconnectTime())) {
            statusString.append(String.format(mContext.getString(R.string.blue_tooth_disconnect_time), item.getDisconnectTime())).append("\n");
        }
        if (item.getStatusType() >= 5) {
            statusString.append(String.format(mContext.getString(R.string.blue_tooth_upload_record), item.getUploadCount(), item.getLocationCount())).append("\n");
        }
        helper.setText(R.id.tvStatus, statusString.toString());
    }
}
