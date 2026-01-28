package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.dbflow
 * @ fileName:      VerificationCodeModel
 * @ author:        QING
 * @ createTime:    2020/5/9 16:13
 * @ describe:      TODO 验证码数据库
 */
@Table(database = AppDataBase.class)
public class VerificationCodeModel extends BaseModel {

    @Column
    @PrimaryKey
    private String username; // 用户名

    @Column
    private String verificationCode; // 验证码

    @Column
    private long createtime; // 创建时间

    @Column
    private int count; // 发送数

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
