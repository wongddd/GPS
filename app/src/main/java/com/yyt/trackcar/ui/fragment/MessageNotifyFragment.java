package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserSettingsModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.NotificationUtils;
import com.yyt.trackcar.utils.RequestToastUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      MessageNotifyFragment
 * @ author:        QING
 * @ createTime:    2020/3/11 01:40
 * @ describe:      TODO 消息通知页面
 */
@Page(name = "MessageNotify")
public class MessageNotifyFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.tvNotifySwitch)
    TextView mTvNotifySwitch; // 接受新消息通知是否开启文本
    @BindView(R.id.sbRingtone)
    SwitchButton mSbRingtone; // 手机铃声开关
    @BindView(R.id.sbVibrate)
    SwitchButton mSbVibrate; // 手机振动开关

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message_notify;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.message_notify);
        return titleBar;
    }

    @Override
    protected void initViews() {
        UserSettingsModel settingsModel = getUserSettingsModel();
//        if (settingsModel == null) {
//            mSbRingtone.setChecked(true);
//            mSbVibrate.setChecked(true);
//        } else {
//            mSbRingtone.setChecked(settingsModel.getPhoneVoice() == 1);
//            mSbVibrate.setChecked(settingsModel.getPhoneVibration() == 1);
//        }
//        getNotifyStatus();
    }

    @Override
    protected void initListeners() {
        mSbRingtone.setOnCheckedChangeListener(this);
        mSbVibrate.setOnCheckedChangeListener(this);
    }

    /**
     * 获取用户打开关闭 通知消息
     */
    private void getNotifyStatus() {
        UserModel userModel = getUserModel();
        if (userModel != null)
            CWRequestUtils.getInstance().getNotifyStatus(getContext(), userModel.getToken(),
                    mHandler);
    }

    /**
     * 用户打开关闭 通知消息
     *
     * @param bean 请求对象
     */
    private void setNotifyStatus(UserSettingsModel bean) {
        if(!NetworkUtils.isNetworkAvailable()){
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null)
            CWRequestUtils.getInstance().setNotifyStatus(MainApplication.getContext(),
                    userModel.getToken(), bean.getArriveHome(), bean.getSos(), bean.getLocation(),
                    bean.getAddFriend(), bean.getStep(), bean.getUploadPhoto(),
                    bean.getPhoneLog(), bean.getCost(), bean.getUpgrade(), bean.getFence(),
                    bean.getPhoneVoice(), bean.getPhoneVibration(), mHandler);
    }

    @SingleClick
    @OnClick({R.id.clNotify})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clNotify: // 接受新消息通知
                NotificationUtils.toSetting(mActivity);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        UserSettingsModel settingsModel;
        switch (buttonView.getId()) {
            case R.id.sbRingtone: // 手机铃声
                settingsModel = getUserSettingsModel();
                if (settingsModel != null) {
                    settingsModel.setPhoneVoice(isChecked ? 1 : 0);
                    setNotifyStatus(settingsModel);
                }
                break;
            case R.id.sbVibrate: // 手机震动
                settingsModel = getUserSettingsModel();
                if (settingsModel != null) {
                    settingsModel.setPhoneVibration(isChecked ? 1 : 0);
                    setNotifyStatus(settingsModel);
                }
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
                    case CWConstant.REQUEST_URL_GET_NOTIFY_STATUS: // 获取用户打开关闭 通知消息
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    UserSettingsModel settingsModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()), UserSettingsModel.class);
                                    settingsModel.setU_id(userModel.getU_id());
                                    settingsModel.save();
                                    mSbRingtone.setOnCheckedChangeListener(null);
                                    mSbVibrate.setOnCheckedChangeListener(null);
                                    mSbRingtone.setCheckedImmediately(settingsModel.getPhoneVoice() == 1);
                                    mSbVibrate.setCheckedImmediately(settingsModel.getPhoneVibration() == 1);
//                                    BasicCustomPushNotification notification =
//                                            new BasicCustomPushNotification();
//                                    if (settingsModel.getPhoneVoice() == 1 && settingsModel.getPhoneVibration() == 1)
//                                        notification.setRemindType(BasicCustomPushNotification.REMIND_TYPE_VIBRATE_AND_SOUND);
//                                    else if (settingsModel.getPhoneVoice() == 1 && settingsModel.getPhoneVibration() == 0)
//                                        notification.setRemindType(BasicCustomPushNotification.REMIND_TYPE_SOUND);
//                                    else if (settingsModel.getPhoneVibration() == 1)
//                                        notification.setRemindType(BasicCustomPushNotification.REMIND_TYPE_VIBRATE);
//                                    else
//                                        notification.setRemindType(BasicCustomPushNotification.REMIND_TYPE_SILENT);
//                                    boolean res =
//                                            CustomNotificationBuilder.getInstance().setCustomNotification(1, notification);//注册该通知,并设置ID为1
                                    initListeners();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_NOTIFY_STATUS: // 用户打开关闭 通知消息
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    UserSettingsModel settingsModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), UserSettingsModel.class);
                                    settingsModel.setU_id(userModel.getU_id());
                                    settingsModel.save();
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
    public void onResume() {
        super.onResume();
        mTvNotifySwitch.setText(NotificationUtils.isNotificationEnabled(mActivity) ?
                getString(R.string.is_open) : getString(R.string.is_close));
    }
}
