package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceSysMsgBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserModel_Table;
import com.yyt.trackcar.ui.activity.LoginActivity;
import com.yyt.trackcar.ui.activity.MainActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.SettingSPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BindDeviceFragment
 * @ author:        QING
 * @ createTime:    2020-02-26 17:17
 * @ describe:      TODO
 */
@Page(name = "BindDevice", anim = CoreAnim.none, params = {CWConstant.TYPE})
public class BindDeviceFragment extends BaseFragment {
    private boolean mIsBind; // 是否已绑定设备
    private boolean mIsLoginout; // 是否退出登录

    @BindView(R.id.tvNotFound)
    XUIAlphaTextView tcNotFound;

    @AutoWired
    String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bind_device;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.bind_device);
        titleBar.setLeftImageResource(0);
        return titleBar;
    }

    @Override
    protected void initViews() {
        tcNotFound.setVisibility(View.GONE);
        if ("1".equals(type))
            getBindDeviceList();
    }

    /**
     * 用户查询绑定设备列表
     */
    private void getBindDeviceList() {
    }

    @SingleClick
    @OnClick({R.id.tvNotFound, R.id.bindBtn, R.id.tvSwitchAccount, R.id.tvRemoveAccount,
    R.id.liveBtn})
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tvNotFound: // 未找到绑定号？
                bundle = new Bundle();
                bundle.putString(CWConstant.TITLE,
                        getString(R.string.not_found_device_bind_imei));
                bundle.putInt(CWConstant.TYPE, 2);
                openNewPage(TextFragment.class, bundle);
                break;
            case R.id.bindBtn: // 已找到绑定号
                bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE, 0);
                openNewPage(InputImeiFragment.class, bundle);
                break;
            case R.id.tvSwitchAccount: // 切换账号
//                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, "");
                mIsLoginout = true;
                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                MainApplication.getInstance().setUserModel(null);
                MainApplication.getInstance().getDeviceList().clear();
                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                ActivityUtils.startActivity(LoginActivity.class);
                break;
            case R.id.tvRemoveAccount: // 注销账号
                break;
            case R.id.liveBtn: //进入直播界面
                bundle = new Bundle();
                bundle.putInt(CWConstant.TYPE,1);
                openNewPage(PigeonRaceBroadcastListFragment.class,bundle);
            break;
            default:
                break;
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
                UserModel userModel;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_BIND_DEVICE_LIST: // 用户查询绑定设备列表
                        if (msg.obj != null && !mIsLoginout) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                RequestBean.class);
                                userModel =
                                        SQLite.select().from(UserModel.class)
                                                .where(UserModel_Table.u_id.eq(requestBean.getU_id()))
                                                .querySingle();
                                if (userModel != null && getDeviceList().size() == 0 && !mIsBind) {
                                    SQLite.delete(DeviceModel.class).where(DeviceModel_Table.u_id.eq(userModel.getU_id())).execute();
                                    String selectImei = userModel.getSelectImei();
                                    if (selectImei == null)
                                        selectImei = "";
                                    List<DeviceModel> deviceList = new ArrayList<>();
                                    if (resultBean.getDeviceList() != null) {
                                        for (Object obj : resultBean.getDeviceList()) {
                                            DeviceModel deviceModel =
                                                    mGson.fromJson(mGson.toJson(obj),
                                                            DeviceModel.class);
                                            saveDeviceIp(requestBean.getU_id(),deviceModel.getImei(),
                                                    deviceModel.getIp());
                                            deviceModel.setU_id(requestBean.getU_id());
                                            deviceModel.save();
                                            deviceList.add(deviceModel);
                                            if (selectImei.equals(deviceModel.getImei()))
                                                MainApplication.getInstance().setDeviceModel(deviceModel);
                                        }
                                        if (getDevice() == null && deviceList.size() > 0) {
                                            MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                            userModel.setSelectImei(getDevice().getImei());
                                            userModel.save();
                                        }
                                    }
                                    MainApplication.getInstance().setUserModel(userModel);
                                    getDeviceList().clear();
                                    getDeviceList().addAll(deviceList);
                                    if (deviceList.size() > 0) {
                                        mIsBind = true;
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(MainActivity.class);
                                    }
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

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onResultSuccess(String event) {
//        if (CWConstant.URL_BIND_DEVICE.equals(event) && !mIsBind) {
//            mIsBind = true;
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostMsgBean(DeviceSysMsgBean event) {
        if (CWConstant.AGREE_BIND == event.getType())
            getBindDeviceList();
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
