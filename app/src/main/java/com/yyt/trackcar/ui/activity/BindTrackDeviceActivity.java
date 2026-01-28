package com.yyt.trackcar.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.AAAUserModel_Table;
import com.yyt.trackcar.ui.base.BaseActivity;
import com.yyt.trackcar.ui.fragment.BindDeviceFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.SettingSPUtils;

import org.jetbrains.annotations.NotNull;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.activity
 * @ fileName:      BindDeviceActivity
 * @ author:        QING
 * @ createTime:    2020-02-26 17:15
 * @ describe:      TODO 绑定手表页面
 */
public class BindTrackDeviceActivity extends BaseActivity {
    private boolean isFinish; // 是否结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFinish = false;
        initTrackUserModel();
        if (isFinish)
            return;

        AAAUserModel userModel = MainApplication.getInstance().getTrackUserModel();
        
        openPage(BindDeviceFragment.class, getIntent().getExtras());
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
    private void initTrackUserModel() {
        if (MainApplication.getInstance().getTrackUserModel() == null) {
            long uID = SettingSPUtils.getInstance().getLong(CWConstant.U_ID, -1);
            if (uID >= 0) {
                AAAUserModel userModel =
                        SQLite.select().from(AAAUserModel.class)
                                .where(AAAUserModel_Table.userId.eq(uID))
                                .querySingle();
                if (userModel == null)
                    ActivityUtils.startActivity(LoginActivity.class);
            }
        }
    }



    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                RequestBean requestBean;
                AAAUserModel userModel;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_TOKEN: // 获取融云Token
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == 200) {
                                userModel = MainApplication.getInstance().getTrackUserModel();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (requestBean.getU_id() == userModel.getUserId()) {
                                    RequestBean bean =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()), RequestBean.class);
                                    userModel.save();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            return false;
        }
    });

    @Override
    protected void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_FINISH == event.getType())
            finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
