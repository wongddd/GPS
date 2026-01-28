package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AMapMovementTrack;

import java.util.List;

public class GameLiveAdapter extends BaseQuickAdapter<AMapMovementTrack,BaseViewHolder> {
    public GameLiveAdapter(@Nullable List<AMapMovementTrack> data) {
        super(R.layout.aaa_adapter_game_live,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AMapMovementTrack item) {
        helper.setBackgroundColor(R.id.tv_color_board,item.getColor())
                .setText(R.id.tv_device_name,String.format("%s: %s", mContext.getString(R.string.nickname),item.getDeviceModel().getDeviceName() == null? mContext.getString(R.string.unnamed) : item.getDeviceModel().getDeviceName() ))
                .setText(R.id.tv_imei,String.format("IMEI: %s",item.getDeviceModel().getDeviceImei()));
    }
}
