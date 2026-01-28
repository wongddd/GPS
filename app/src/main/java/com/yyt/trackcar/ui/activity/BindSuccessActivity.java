package com.yyt.trackcar.ui.activity;

import android.os.Bundle;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserModel_Table;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.ui.fragment.BindSuccessFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.SettingSPUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      BindSuccessActivity
 * @ author:        QING
 * @ createTime:    2020-02-28 17:35
 * @ describe:      TODO 绑定成功页面
 */
public class BindSuccessActivity extends BaseActivity {
    private boolean isFinish; // 是否结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        isFinish = false;
//        initUserModel();
//        if(isFinish)
//            return;
        openPage(BindSuccessFragment.class, getIntent().getExtras());
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * 初始化用户信息
     */
    private void initUserModel(){
        if(getUserModel() == null) {
            long uID = SettingSPUtils.getInstance().getLong(CWConstant.U_ID, -1);
            if (uID >= 0) {
                UserModel userModel =
                        SQLite.select().from(UserModel.class)
                                .where(UserModel_Table.u_id.eq(uID))
                                .querySingle();
                if (userModel == null)
                    ActivityUtils.startActivity(LoginActivity.class);
                else {
                    List<DeviceModel> deviceList = SQLite.select().from(DeviceModel.class)
                            .where(DeviceModel_Table.u_id.eq(uID))
                            .queryList();
                    String selectImei = userModel.getSelectImei();
                    if (selectImei == null)
                        selectImei = "";
                    for (DeviceModel deviceModel : deviceList) {
                        if (selectImei.equals(deviceModel.getImei()))
                            MainApplication.getInstance().setDeviceModel(deviceModel);
                    }
                    if (MainApplication.getInstance().getDeviceModel() == null && deviceList.size() > 0) {
                        MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                        userModel.setSelectImei(MainApplication.getInstance().getDeviceModel().getImei());
                        userModel.save();
                    }
                    MainApplication.getInstance().setUserModel(userModel);
                    MainApplication.getInstance().setDeviceList(deviceList);
                }
            } else {
                isFinish = true;
                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                ActivityUtils.startActivity(LoginActivity.class);
                finish();
            }
        }
    }

    @Override
    protected void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_FINISH == event.getType())
            finish();
    }

}
