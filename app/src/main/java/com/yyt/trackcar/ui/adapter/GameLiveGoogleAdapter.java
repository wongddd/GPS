package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.GoogleMapMovementTrack;

import java.util.List;

public class GameLiveGoogleAdapter extends BaseQuickAdapter<GoogleMapMovementTrack,BaseViewHolder> {
    public GameLiveGoogleAdapter(@Nullable List<GoogleMapMovementTrack> data) {
        super(R.layout.aaa_adapter_game_live,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoogleMapMovementTrack item) {
        helper.setBackgroundColor(R.id.tv_color_board,item.getColor())
                .setText(R.id.tv_device_name,String.format("Nickname: %s",item.getDeviceModel().getDeviceName() == null? mContext.getString(R.string.unnamed) : item.getDeviceModel().getDeviceName() ))
                .setText(R.id.tv_imei,String.format("IMEI: %s",item.getDeviceModel().getDeviceImei()));
    }
}
