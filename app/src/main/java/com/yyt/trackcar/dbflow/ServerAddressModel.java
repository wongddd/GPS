package com.yyt.trackcar.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDataBase.class)
public class ServerAddressModel extends BaseModel {
    /**
     *服务器地址
     */
    @Column
    @PrimaryKey
    private String serverAddress;

    public ServerAddressModel() {
    }

    public ServerAddressModel(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
