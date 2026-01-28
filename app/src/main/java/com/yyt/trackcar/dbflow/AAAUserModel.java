package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      AAAUserModel
 * @ author:        QING
 * @ createTime:    6/19/21 15:49
 * @ describe:      TODO
 */
@Table(database = AppDataBase.class)
public class AAAUserModel extends BaseModel {

    /**
     * 用户id
     */
    @Column
    @PrimaryKey
    private long userId;

    /**
     * 用户名称
     */
    @Column
    private String userName;

    /**
     * token
     */
    @Column
    private String token;

    /**
     * 邮箱
     */
    @Column
    private String email;

    /**
     * 手机号码
     */
    @Column
    private String mobile;

    /**
     * 性别
     */
    @Column
    private Integer sex;

    /**
     * 客户名称
     */
    @Column
    private String customerName;

    /**
     * 创建时间
     */
    @Column
    private String createTime;

    /**
     * 更新时间
     */
    @Column
    private String updateTime;

    /**
     * 备注
     */
    @Column
    private String remark;

    @Column
    private String selectDeviceId;

    /**
     * 是否为经销商角色  1、是 其他值否
     */
    private int isAgent;

    /**
     * 经销商角色下的经销设备列表
     */
    private List<AAADeviceModel> gpsDeviceList;

    /**
     * 上级经销商ID
     */
    private long parentId;

    private String password;

    private String roles;

    private long createDate;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public List<AAADeviceModel> getGpsDeviceList() {
        return gpsDeviceList;
    }

    public void setGpsDeviceList(List<AAADeviceModel> gpsDeviceList) {
        this.gpsDeviceList = gpsDeviceList;
    }

    public int getIsAgent() {
        return isAgent;
    }

    public void setIsAgent(int isAgent) {
        this.isAgent = isAgent;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSelectDeviceId() {
        return selectDeviceId;
    }

    public void setSelectDeviceId(String selectDeviceId) {
        this.selectDeviceId = selectDeviceId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
