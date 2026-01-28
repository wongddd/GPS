package com.yyt.trackcar.bean;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      BLEConfigModel
 * @ author:        QING
 * @ createTime:    2023/4/28 17:35
 * @ describe:      TODO 蓝牙配置对象
 */
public class BLEConfigModel {

    private String rid; // 赛场ID
    private String rsut; // 开始时间
    private String nmst; // 夜间时间
    private String nmet; // 续飞时间
    private Integer lpl; // 低电量
    private Long rsud; // 延时时间
    private Integer forcedStartup; // 强制开机
    private Integer rgli; // 开始定位间隔
    private Integer ngli; // 夜间定位间隔
    private Integer cdui; // 续飞定位间隔
    private Integer lpgli; // 低电量定位间隔

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRsut() {
        return rsut;
    }

    public void setRsut(String rsut) {
        this.rsut = rsut;
    }

    public String getNmst() {
        return nmst;
    }

    public void setNmst(String nmst) {
        this.nmst = nmst;
    }

    public String getNmet() {
        return nmet;
    }

    public void setNmet(String nmet) {
        this.nmet = nmet;
    }

    public Integer getLpl() {
        return lpl;
    }

    public void setLpl(Integer lpl) {
        this.lpl = lpl;
    }

    public Long getRsud() {
        return rsud;
    }

    public void setRsud(Long rsud) {
        this.rsud = rsud;
    }

    public Integer getForcedStartup() {
        return forcedStartup;
    }

    public void setForcedStartup(Integer forcedStartup) {
        this.forcedStartup = forcedStartup;
    }

    public Integer getRgli() {
        return rgli;
    }

    public void setRgli(Integer rgli) {
        this.rgli = rgli;
    }

    public Integer getNgli() {
        return ngli;
    }

    public void setNgli(Integer ngli) {
        this.ngli = ngli;
    }

    public Integer getCdui() {
        return cdui;
    }

    public void setCdui(Integer cdui) {
        this.cdui = cdui;
    }

    public Integer getLpgli() {
        return lpgli;
    }

    public void setLpgli(Integer lpgli) {
        this.lpgli = lpgli;
    }

}
