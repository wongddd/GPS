package com.yyt.trackcar.ui.adapter;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.utils.DensityUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.utils.DeviceType;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.StringUtils;
import com.yyt.trackcar.utils.TextColorSizeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      CardDeviceAdapter
 * @ author:        QING
 * @ createTime:    2023/7/4 15:52
 * @ describe:      TODO 设备卡片适配器
 */
public class CardDeviceAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {

    public CardDeviceAdapter(List<BaseItemBean> data) {
        super(R.layout.item_card_device, data);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void convert(BaseViewHolder helper, BaseItemBean item) {
        if (item.getObject() == null) {
            helper.setVisible(R.id.xllInfo, false);
            helper.getView(R.id.xllAdd).setVisibility(View.VISIBLE);
            helper.addOnClickListener(R.id.xllAdd);
        } else {
            AAADeviceModel deviceModel = (AAADeviceModel) item.getObject();
            helper.setVisible(R.id.xllInfo, true);
            helper.getView(R.id.xllAdd).setVisibility(View.GONE);
            ImageView ivPortrait = helper.getView(R.id.ivPortrait);
            ImageLoadUtils.loadPortraitImage(mContext, deviceModel.getHeadPic(), ivPortrait);
            String imei = StringUtils.getNotNullText(deviceModel.getDeviceImei());
            String name = deviceModel.getDeviceName() == null ? imei : deviceModel.getDeviceName();
            String locationMode;
            if (deviceModel.getLocationType() == null) {
                locationMode = "";
            } else if (deviceModel.getLocationType() == 1) {
                locationMode = String.format("GPS  %s:%s",
                        mContext.getString(R.string.satellite_num),
                        deviceModel.getSatellite() == null ? 0 : deviceModel.getSatellite());
            } else if (deviceModel.getLocationType() == 2) {
                locationMode = mContext.getString(R.string.base_station);
            } else if (deviceModel.getLocationType() == 3) {
                locationMode = mContext.getString(R.string.base_station_nb);
            } else if (deviceModel.getLocationType() == 4) {
                locationMode = "WIFI";
            } else {
                locationMode = "";
            }
            if (DeviceType.PIGEON.getValue() == deviceModel.getDeviceType()) {
                helper.setText(R.id.tvType, String.format("%s:%d",
                        mContext.getString(R.string.device_type),
                        deviceModel.getDeviceType()));
            } else {
                String online;
                float vol = 0;
                if (deviceModel.isOnlineStatus()) {
                    online = mContext.getString(R.string.online);
                } else {
                    online = mContext.getString(R.string.offline);
                }
                if (!TextUtils.isEmpty(deviceModel.getLastDeviceVol())) {
                    try {
                        vol = Float.parseFloat(deviceModel.getLastDeviceVol());
                        if (vol < 0)
                            vol = 0;
                        else if (vol > 100)
                            vol = 100;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
                String onlineText = String.format(" %s %s:%s%% ", online,
                        mContext.getString(R.string.device_power), vol);
                list.add(new TextColorSizeHelper.SpanInfo(onlineText,
                        DensityUtils.sp2px(12),
                        ContextCompat.getColor(mContext, R.color.white),
                        ContextCompat.getColor(mContext, R.color.red),
                        DensityUtils.dp2px(2), true));
                helper.setText(R.id.tvType, TextColorSizeHelper.getTextSpan(mContext,
                        String.format("%s:%d %s",
                                mContext.getString(R.string.device_type),
                                deviceModel.getDeviceType()
                                , onlineText), list));
            }
            helper.setText(R.id.tvImei, String.format("IMEI:%s", imei))
                    .setText(R.id.tvName, String.format("%s:%s",
                            mContext.getString(R.string.nickname), name))
                    .setText(R.id.tvLocationTime, String.format("%s:%s",
                            mContext.getString(R.string.location_time),
                            StringUtils.getNotNullText(deviceModel.getLastGpsTime())))
                    .setText(R.id.tvLogTime, String.format("%s:%s",
                            mContext.getString(R.string.log_time),
                            StringUtils.getNotNullText(deviceModel.getLastLocationTime())))
                    .setText(R.id.tvLatlng, String.format("%s:%.6f,%.6f",
                            mContext.getString(R.string.history_record_latlng),
                            deviceModel.getLastLongitude() == null ? 0 :
                                    deviceModel.getLastLongitude(),
                            deviceModel.getLastLatitude() == null ? 0 :
                                    deviceModel.getLastLatitude()))
                    .setText(R.id.tvLocationType, String.format("%s:%s",
                            mContext.getString(R.string.locate_mode), locationMode));
            if (DeviceType.PET.getValue() == deviceModel.getDeviceType()) {
                helper.getView(R.id.locationBtn).setVisibility(View.VISIBLE);
                helper.getView(R.id.messageBtn).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.locationBtn).setVisibility(View.GONE);
                helper.getView(R.id.messageBtn).setVisibility(View.GONE);
            }
            helper.addOnClickListener(R.id.trackBtn, R.id.locationBtn, R.id.messageBtn, R.id.detailsBtn);
        }
    }

}
