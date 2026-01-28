package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.AAAUserModel_Table;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.activity.MainActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DBUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BindSuccessFragment
 * @ author:        QING
 * @ createTime:    2020-02-28 17:07
 * @ describe:      TODO 绑定成功页面
 */
@Page(name = "BindSuccess", params = {CWConstant.TYPE, CWConstant.IMEI})
public class BindSuccessFragment extends BaseFragment {
    @BindView(R.id.ivIcon)
    ImageView mIvIcon; // 图标
    @BindView(R.id.tvTitle)
    TextView mTvTitle; // 标题文本
    @BindView(R.id.tvTitlePrompt)
    TextView mTvTitlePrompt; // 标题提示文本
    @BindView(R.id.confirmBtn)
    Button mConfirmBtn; // 确认按钮
    @AutoWired
    String imei; // 设备imei
    @AutoWired
    int type; // 0.从未绑定界面 1.首页绑定新设备 2.首页绑定新设备
    private boolean mIsEntry; // 是否点击进入首页
    private boolean mIsFinish; // 是否结束

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_result;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(null).setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
//        mIvIcon.setImageResource(R.mipmap.ic_launcher);
        mTvTitle.setText(R.string.bind_success);
        mTvTitlePrompt.setText(R.string.bind_success_agent_prompt);
        mConfirmBtn.setText(R.string.entry_main_page);
        mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                mMaterialDialog, getString(R.string.prompt),
                getString(R.string.bind_success_agent_prompt),
                getString(R.string.confirm));
    }

    /**
     * 查找设备列表
     */
    private void queryDevicesList() {
        if (!NetworkUtils.isNetworkAvailable()) {
            XToastUtils.toast(getContext(), R.string.network_error_prompt);
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        if (userModel != null) {
            showDialog();
            CarGpsRequestUtils.getDeviceList(userModel, null, mHandler);
        }
    }

    @SingleClick
    @OnClick({R.id.confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmBtn: // 确认按钮
                if (!mIsFinish) {
                    mIsEntry = true;
                    queryDevicesList();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 消息处理
     */
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                AAABaseResponseBean responseBean;
                AAARequestBean requestBean;
                switch (msg.what) {
                    case TConstant.REQUEST_URL_GET_DEVICE_LIST: { // 获取设备列表
                        dismisDialog();
                        if (!mIsFinish) {
                            if (msg.obj == null) {
                                if (mIsEntry) {
                                    XToastUtils.toast(getContext(),
                                            R.string.request_unkonow_prompt);
                                }
                            } else {
                                responseBean = (AAABaseResponseBean) msg.obj;
                                if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                    requestBean = mGson.fromJson(responseBean.getRequestObject(),
                                            AAARequestBean.class);
                                    AAAUserModel userModel = null;
                                    if (requestBean.getUserId() != null) {
                                        userModel = SQLite.select().from(AAAUserModel.class)
                                                .where(AAAUserModel_Table.userId.eq(requestBean.getUserId()))
                                                .querySingle();
                                    }
                                    List<AAADeviceModel> deviceList = new ArrayList<>();
                                    if (responseBean.getData() != null) {
                                        deviceList.addAll(mGson.fromJson(mGson.toJson(responseBean.getData()),
                                                new TypeToken<List<AAADeviceModel>>() {
                                                }.getType()));
                                    }
                                    boolean isChangeDevice = DBUtils.saveDeviceList(userModel, deviceList);
                                    if(isChangeDevice){
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                    }
                                    if (mIsEntry) {
                                        mIsFinish = true;
                                        if (getTrackDeviceList().size() > 0) {
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                            ActivityUtils.startActivity(MainActivity.class);
                                        } else {
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                            ActivityUtils.startActivity(BindDeviceActivity.class);
                                        }
                                    }
                                } else if (mIsEntry) {
                                    XToastUtils.toast(getContext(),
                                            ErrorCode.getResId(responseBean.getCode()));
                                }
                            }
                        }
                    }
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

}
