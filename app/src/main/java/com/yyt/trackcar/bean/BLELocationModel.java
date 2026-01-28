package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      BLELocationModel
 * @ author:        QING
 * @ createTime:    2023/4/28 17:49
 * @ describe:      TODO 蓝牙定位对象
 */
public class BLELocationModel {

//    private String rid; // 赛场ID
//    private String device; // 设备编号
    private String time; // 定位时间
    private Integer pl; // 电量
    private String ld; // 经度|纬度
    private Integer ed; // 海拔高度
    private int ldt; // 坐标类型 使用 0、1 和 2 来进行区分，0 表示空 数据，1 表示 GPS，2 表示 LBS
//    private Integer satellitesNum; // 卫星数
//    private Integer heading; // 对地真航向


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getPl() {
        return pl;
    }

    public void setPl(Integer pl) {
        this.pl = pl;
    }

    public String getLd() {
        return ld;
    }

    public void setLd(String ld) {
        this.ld = ld;
    }

    public Integer getEd() {
        return ed;
    }

    public void setEd(Integer ed) {
        this.ed = ed;
    }

    public int getLdt() {
        return ldt;
    }

    public void setLdt(int ldt) {
        this.ldt = ldt;
    }
}
