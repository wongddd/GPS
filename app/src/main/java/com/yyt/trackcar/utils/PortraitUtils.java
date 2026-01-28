package com.yyt.trackcar.utils;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.PortraitModel_Table;

import java.util.HashMap;
import java.util.Map;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      PortraitUtils
 * @ author:        QING
 * @ createTime:    2020/5/9 15:12
 * @ describe:      TODO 头像工具类
 */
public class PortraitUtils {
    private static PortraitUtils mPortraitUtils = null;
    private Map<String, PortraitModel> mPortraitMap = new HashMap<>();

    private PortraitUtils() {
    }

    public static PortraitUtils getInstance() {
        synchronized (PortraitUtils.class) {
            if (mPortraitUtils == null) {
                mPortraitUtils = new PortraitUtils();
            }
        }
        return mPortraitUtils;
    }

    /**
     * 获取头像信息
     *
     * @param deviceModel 设备对象
     * @param userId      用户id
     * @return 返回头像信息
     */
    public PortraitModel getPortrait(DeviceModel deviceModel, String userId) {
        if (deviceModel != null && userId != null) {
            PortraitModel portraitModel = mPortraitMap.get(String.format("%s%s",
                    deviceModel.getImei(), userId));
            if (portraitModel == null) {
                OperatorGroup operatorGroup =
                        OperatorGroup.clause(OperatorGroup.clause()
                                .and(PortraitModel_Table.imei.eq(deviceModel.getImei()))
                                .and(PortraitModel_Table.userId.eq(userId)));
                portraitModel = SQLite.select().from(PortraitModel.class)
                        .where(operatorGroup)
                        .querySingle();
                if(portraitModel != null){
                    updatePortrait(portraitModel);
                }
                return portraitModel;
            } else
                return portraitModel;
        } else
            return null;
    }

    /**
     * 更新头像信息对象
     *
     * @param model 头像信息对象
     */
    public void updatePortrait(PortraitModel model) {
        mPortraitMap.put(String.format("%s%s",
                model.getImei(), model.getUserId()), model);
    }
}
