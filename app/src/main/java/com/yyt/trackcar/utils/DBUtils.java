package com.yyt.trackcar.utils;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAADeviceModel_Table;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel_Table;
import com.yyt.trackcar.dbflow.HealthDayModel;
import com.yyt.trackcar.dbflow.HealthDayModel_Table;
import com.yyt.trackcar.dbflow.HealthHourModel;
import com.yyt.trackcar.dbflow.HealthHourModel_Table;
import com.yyt.trackcar.dbflow.HealthModel;
import com.yyt.trackcar.dbflow.HealthModel_Table;
import com.yyt.trackcar.dbflow.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      DBUtils
 * @ author:        QING
 * @ createTime:    7/5/21 11:33
 * @ describe:      TODO
 */
public class DBUtils {

    /**
     * 获取设备设置
     */
    public static DeviceSettingsModel getDeviceSettings(UserModel userModel, String imei) {
        DeviceSettingsModel settingsModel = null;
        if (userModel != null && !TextUtils.isEmpty(imei)) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(DeviceSettingsModel_Table.u_id.eq(userModel.getU_id()))
                    .and(DeviceSettingsModel_Table.imei.eq(imei)));
            settingsModel = SQLite.select().from(DeviceSettingsModel.class)
                    .where(operatorGroup)
                    .querySingle();
            if (settingsModel == null) {
                settingsModel = new DeviceSettingsModel();
                settingsModel.setU_id(userModel.getU_id());
                settingsModel.setImei(imei);
                settingsModel.setLocationMode("0");
                settingsModel.setWifi("");
                settingsModel.setWifiType(0);
                settingsModel.setDisabledInClass("");
                settingsModel.setOther("5,0,0,06:00|22:00|1,20|0");
                settingsModel.setAutomaticAnswer("0");
                settingsModel.setLoss("#0");
                settingsModel.setDial_pad("0");
                settingsModel.setAlarm_clock("");
                settingsModel.setStep("8000");
                settingsModel.setDevicestep("0");
                settingsModel.setWebTraffic("0");
                settingsModel.setWifiStatus("0");
                settingsModel.setPhonebook("");
                settingsModel.save();
            }
        }
        return settingsModel;
    }

    public static HealthModel getDeviceHealth(String imei) {
        HealthModel healthModel = null;
        if (!TextUtils.isEmpty(imei)) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(HealthModel_Table.imei.eq(imei)));
            healthModel = SQLite.select().from(HealthModel.class)
                    .where(operatorGroup)
                    .querySingle();
            if (healthModel == null) {
                healthModel = new HealthModel();
                healthModel.setImei(imei);
                healthModel.setKm("0");
                healthModel.setBody_temperature("0");
                healthModel.setBlood_pressure("0,0");
                healthModel.setHeartRateTest("0");
                healthModel.setFallOff("0");
                healthModel.save();
            }
        }
        return healthModel;
    }

    public static List<HealthHourModel> getDeviceHealthHour(String imei, String date, int type) {
        List<HealthHourModel> list = new ArrayList<>();
        if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(date)) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(HealthHourModel_Table.imei.eq(imei))
                    .and(HealthHourModel_Table.date.eq(date))
                    .and(HealthHourModel_Table.type.eq(type)));
            list.addAll(SQLite.select().from(HealthHourModel.class)
                    .where(operatorGroup)
                    .queryList());
        }
        return list;
    }

    public static List<HealthDayModel> getDeviceHealthDay(String imei, String date, int type) {
        List<HealthDayModel> list = new ArrayList<>();
        if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(date)) {
            OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
                    .and(HealthDayModel_Table.imei.eq(imei))
                    .and(HealthDayModel_Table.time.eq(date))
                    .and(HealthDayModel_Table.type.eq(type)));
            list.addAll(SQLite.select().from(HealthDayModel.class)
                    .where(operatorGroup)
                    .queryList());
        }
        return list;
    }

    /**
     * 保存设备列表
     *
     * @param userModel  用户对象
     * @param deviceList 设备列表
     * @return 是否切换了选中设备
     */
    public static boolean saveDeviceList(AAAUserModel userModel, List<AAADeviceModel> deviceList) {
        if (userModel == null) {
            return false;
        }
        SQLite.delete(AAADeviceModel.class).where(AAADeviceModel_Table.userId.eq(userModel.getUserId())).execute();
        AAADeviceModel deviceModel = null;
        boolean isChangeDevice = false;
        if (deviceList != null && deviceList.size() > 0) {
            for (AAADeviceModel model : deviceList) {
                model.setUserId(userModel.getUserId());
                model.save();
                if (String.valueOf(model.getDeviceId()).equals(userModel.getSelectDeviceId())) {
                    deviceModel = model;
                }
            }
            if (deviceModel == null) {
                deviceModel = deviceList.get(0);
                userModel.setSelectDeviceId(String.valueOf(deviceModel.getDeviceId()));
                userModel.save();
                isChangeDevice = true;
            }
        }
        AAAUserModel currentModel = MainApplication.getInstance().getTrackUserModel();
        if (currentModel != null && userModel.getUserId() == currentModel.getUserId()) {
            MainApplication.getInstance().setTrackDeviceModel(deviceModel);
            MainApplication.getInstance().setTrackDeviceList(deviceList);
            MainApplication.getInstance().setTrackUserModel(userModel);
            return isChangeDevice;
        }
        return false;
    }

}
