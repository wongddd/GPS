package com.yyt.trackcar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.AAADeviceModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      ViewDataUtils
 * @ author:        QING
 * @ createTime:    2023/3/29 18:30
 * @ describe:      TODO 控件数据工具类
 */
public class ViewDataUtils {

    /**
     * 初始化设备信息控件
     *
     * @param context     上下文
     * @param headerView  根布局
     * @param deviceModel 设备对象
     * @return 控件
     */
    @SuppressLint("DefaultLocale")
    public static void initDeviceInfoView(Context context, View headerView,
                                          AAADeviceModel deviceModel) {
        if (deviceModel != null && context != null && headerView != null) {
            ImageView ivPortrait = headerView.findViewById(R.id.ivPortrait);
            TextView tvName = headerView.findViewById(R.id.tvName);
            TextView tvImei = headerView.findViewById(R.id.tvImei);
            TextView tvRingNo = headerView.findViewById(R.id.tvRingNo);
            TextView tvInfo = headerView.findViewById(R.id.tvInfo);

            String imei = StringUtils.getNotNullText(deviceModel.getDeviceImei());
            String name = deviceModel.getDeviceName() == null ? imei : deviceModel.getDeviceName();
            ImageLoadUtils.loadPortraitImage(context, deviceModel.getHeadPic(), ivPortrait);
            StringUtils.setText(tvName, name);
            StringUtils.setText(tvImei, String.format("IMEI: %s", imei));
            if (DataUtils.isPigeonDevice(deviceModel.getDeviceImei())) { // 鸽子设备
                StringUtils.setText(tvRingNo,
                        String.format(context.getString(R.string.device_list_ring_no),
                                StringUtils.getNotNullText(deviceModel.getRingNo())));
                StringUtils.setText(tvInfo, String.format("%s:%s",
                        context.getString(R.string.device_version),
                        StringUtils.getNotNullText(deviceModel.getVersion())));
                tvRingNo.setVisibility(View.VISIBLE);
            } else if (DeviceType.PET.getValue() == deviceModel.getDeviceType()) {
                String online;
                int positionInterval = 0;
                int positionType = 1;
                String positionMode;
                if (deviceModel.isOnlineStatus()) {
                    online = context.getString(R.string.online);
                } else {
                    online = context.getString(R.string.offline);
                }
                float vol = 0;
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
                if (deviceModel.getPositionInterval() != null) {
                    positionInterval = deviceModel.getPositionInterval();
                }
                if (deviceModel.getPositionType() != null) {
                    positionType = deviceModel.getPositionType();
                }
                if (positionType == 2) {
                    positionMode = context.getString(R.string.pet_location_type_second,
                            positionInterval);
                } else if (positionType == 3) {
                    positionMode = context.getString(R.string.pet_location_mode_third);
                } else {
                    positionMode = context.getString(R.string.pet_location_mode_first);
                }
                tvInfo.setText(String.format("%s,%s:%.0f%%,%s", online,
                        context.getString(R.string.device_power), vol, positionMode));
                tvRingNo.setVisibility(View.GONE);
            } else { // 车载设备
                String online;
                if (deviceModel.isOnlineStatus() && deviceModel.getLastMotionStatus() != null &&
                        deviceModel.getLastMotionStatus() == 1)
                    online = context.getString(R.string.device_sport);
                else if (deviceModel.isOnlineStatus())
                    online = context.getString(R.string.device_motionless);
                else
                    online = context.getString(R.string.offline);
                String onOff;
                if (deviceModel.isOnlineStatus() && deviceModel.getEngineStatus() != null
                        && deviceModel.getEngineStatus() == 1)
                    onOff = context.getString(R.string.on);
                else
                    onOff = context.getString(R.string.off);
//                float vol = 0;
//                if (!TextUtils.isEmpty(deviceModel.getLastDeviceVol())) {
//                    try {
//                        vol = Float.parseFloat(deviceModel.getLastDeviceVol());
//                        if (vol < 0)
//                            vol = 0;
//                        else if (vol > 100)
//                            vol = 100;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                tvInfo.setText(String.format("%s ACC %s,%s:%s%%", online, onOff,
//                        getString(R.string.device_power), vol));
                StringUtils.setText(tvInfo, String.format("%s ACC %s", online, onOff));
                tvRingNo.setVisibility(View.GONE);
            }
        }
    }
}
