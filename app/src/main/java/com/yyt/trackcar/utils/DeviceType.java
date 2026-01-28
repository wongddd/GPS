package com.yyt.trackcar.utils;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.utils
 * @ fileName:      DeviceType
 * @ author:        QING
 * @ createTime:    2023/6/9 15:40
 * @ describe:      TODO 设备类型
 */
public enum DeviceType {
    /**
     * 车辆
     */
    VEHICLE(0x01),
    /**
     * 鸽子
     */
    PIGEON(0x02),
    /**
     * 学生卡
     */
    STUDENT(0x03),
    /**
     * 行车记录仪
     */
    RECORD(0x04),
    /**
     * 宠物
     */
    PET(0x05);

    private final int value;//自定义属性

    DeviceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
