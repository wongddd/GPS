package com.yyt.trackcar.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.app.AppUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.ResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.dbflow.AlbumModel;
import com.yyt.trackcar.dbflow.CallRecordModel;
import com.yyt.trackcar.dbflow.DeviceSysMsgModel;
import com.yyt.trackcar.dbflow.SmsModel;
import com.yyt.trackcar.dbflow.StepModel;
import com.yyt.trackcar.dbflow.TrackModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.LoginActivity;
import com.yyt.trackcar.ui.adapter.CustomTextAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DataCleanManager;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.NotificationUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      AppSettingsFragment
 * @ author:        QING
 * @ createTime:    2020/3/10 21:30
 * @ describe:      TODO App设置页面
 */
@Page(name = "AppSettings")
public class AppSettingsFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        View.OnClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomTextAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private final int INPUT_LOCATION_REFRESH_INTERVAL = 723;
    private final int LOG_OFF = 0x320;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.app_settings);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initLanguageAndServer();
        initAdapters();
        initRecyclerViews();
//        initFooterView();
//        initAppVersion();
//        getAppVersion();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getAppSettingsData(mActivity, mItemList);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CustomTextAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.footer_view_btn, mRecyclerView,
                false);
        footerView.findViewById(R.id.rootView).setOnClickListener(this);
        footerView.findViewById(R.id.rootView).setBackgroundResource(R.drawable.btn_custom_item_round_selector);
        TextView tvContent = footerView.findViewById(R.id.tvContent);
        tvContent.setText(R.string.login_out);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 初始化服务器和语言
     */
    private void initLanguageAndServer() {
        for (SectionItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null) {
                switch (itemBean.getType()) {
                    case 5: // 服务器选择
                        if (SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR, 0) == 1)
                            itemBean.setContent(getString(R.string.server_address_west));
                        else
                            itemBean.setContent(getString(R.string.server_address_east));
                        break;
                    case 6: // 语言
                        if ("zh".equals(SettingSPUtils.getInstance().getString(CWConstant.LANGUAGE, "")))
                            itemBean.setContent(getString(R.string.language_type_first));
                        else
                            itemBean.setContent(getString(R.string.language_type_second));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 初始化APP版本信息
     */
    private void initAppVersion() {
        int nowAppVersionCode = AppUtils.getAppVersionCode();
        int newAppVersionCode = SettingSPUtils.getInstance().getInt(CWConstant.APP_VERSION_CODE,
                -1);
        String nowAppVersionName = AppUtils.getAppVersionName();
        String newAppVersionName =
                SettingSPUtils.getInstance().getString(CWConstant.APP_VERSION_NAME, "");
        boolean isCanUpdate =
                nowAppVersionCode < newAppVersionCode || (nowAppVersionCode == newAppVersionCode
                        && !newAppVersionName.equals(nowAppVersionName));
        for (SectionItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null && itemBean.getType() == 3) {
                if (isCanUpdate) {
//                    itemBean.setContent(getString(R.string.app_version_can_update));
                    itemBean.setContent("");
                    itemBean.setSelect(true);
                } else {
//                    itemBean.setContent(getString(R.string.app_is_newest_version));
                    itemBean.setContent("");
                    itemBean.setSelect(false);
                }
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 获取APP最新版本信息
     */
    private void getAppVersion() {
        UserModel userModel = getUserModel();
        if (userModel != null)
            CWRequestUtils.getInstance().getAppVersion(getContext(), userModel.getToken(),
                    mHandler);
    }

    @SingleClick(1000)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rootView: // 退出登录
                mMaterialDialog =
                        DialogUtils.customMaterialDialog(getContext(),
                                mMaterialDialog,
                                getString(R.string.login_out),
                                getString(R.string.login_out_prompt_new),
                                getString(R.string.confirm),
                                getString(R.string.cancel), null,
                                CWConstant.DIALOG_LOGIN_OUT, mHandler);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                Bundle bundle;
                switch (itemBean.getType()) {
                    case 0: // 消息通知
                        openNewPage(MessageNotifyFragment.class);
                        break;
                    case 1: // 修改密码
                        openNewPage(ChangePwdFragment.class);
                        break;
                    case 2: // 清除缓存
                        mMaterialDialog =
                                DialogUtils.customMaterialDialog(getContext(),
                                        mMaterialDialog,
                                        getString(R.string.clear_cache),
                                        getString(R.string.clear_cache_prompt),
                                        getString(R.string.confirm),
                                        getString(R.string.cancel), null,
                                        CWConstant.DIALOG_CLEAR_CACHE, mHandler);
                        break;
                    case 3: // App版本更新
//                        openNewPage(AppUpdateFragment.class);
                        Uri uri = Uri.parse("market://details?id=com.yyt.trackcar");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.android.vending");//应用市场包名
                        if (intent.resolveActivity(mActivity.getPackageManager()) == null)
                            XToastUtils.toast("Google Play Store not installed");
                        else
                            mActivity.startActivity(intent);
                        break;
                    case 4: // 关于
                        openNewPage(AboutFragment.class);
                        break;
                    case 5: // 服务器选择
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 5);
                        bundle.putString(CWConstant.TITLE,
                                getString(R.string.select_server_address));
                        bundle.putString(CWConstant.CONTENT,
                                String.valueOf(SettingSPUtils.getInstance().getInt(CWConstant.SERVER_ADDR,
                                        0)));
                        openNewPageForResult(CustomSelectorFragment.class, bundle,
                                CWConstant.REQUEST_SERVER_ADDR);
                        break;
                    case 6: // 语言
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 4);
                        bundle.putString(CWConstant.TITLE, getString(R.string.select_language));
                        bundle.putString(CWConstant.CONTENT,
                                SettingSPUtils.getInstance().getString(CWConstant.LANGUAGE, ""));
                        openNewPageForResult(CustomSelectorFragment.class, bundle,
                                CWConstant.REQUEST_LANGUAGE);
                        break;
                    case 7: //设置地图定位点刷新间隔
                        mMaterialDialog = DialogUtils.customInputMaterialDialog(
                                getContext(), mMaterialDialog, getString(R.string.set_location_refresh_interval)
                                , null, getString(R.string.set_positioning_refresh_interval_prompt)
                                , null, InputType.TYPE_CLASS_NUMBER, 3, 2
                                , getString(R.string.confirm), getString(R.string.cancel),
                                INPUT_LOCATION_REFRESH_INTERVAL, mHandler);
                        break;
                    case 8:
                        mMaterialDialog = DialogUtils.customMaterialDialog(
                                getContext(), mMaterialDialog, getString(R.string.confirm_account_cancellation)
                                , getString(R.string.hint_account_cancellation)
                                , getString(R.string.confirm)
                                , getString(R.string.cancel), LOG_OFF,mHandler);
                        break;
                    default:
                        break;
                }
            }
        }
    }

//    @Override
//    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
//        super.onFragmentResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            Bundle bundle = data.getExtras();
//            if (bundle != null) {
//                switch (requestCode) {
//                    case CWConstant.REQUEST_LANGUAGE: // 选择语言
//                        SettingSPUtils.getInstance().putInt(CWConstant.LANGUAGE,
//                                bundle.getInt(CWConstant.TYPE));
//                        initLanguageAndServer();
//                        mAdapter.notifyDataSetChanged();
//                        break;
//                    case CWConstant.REQUEST_SERVER_ADDR: // 选择服务器
//                        SettingSPUtils.getInstance().putInt(CWConstant.SERVER_ADDR,
//                                bundle.getInt(CWConstant.TYPE));
//                        initLanguageAndServer();
//                        mAdapter.notifyDataSetChanged();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_APP_VERSION: // 获取APP最新版本信息
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                ResultBean resultModel =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                ResultBean.class);
                                SettingSPUtils.getInstance().putInt(CWConstant.APP_VERSION_CODE,
                                        resultModel.getVersion());
                                SettingSPUtils.getInstance().putString(CWConstant.APP_VERSION_NAME,
                                        resultModel.getVersion_name());
                                SettingSPUtils.getInstance().putString(CWConstant.APP_VERSION_CONTENT,
                                        resultModel.getDescription());
                                initAppVersion();
                            }
                        }
                        break;
                    case TConstant.REQUEST_LOG_OFF: //注销帐号
                        dismisDialog();
                        if (msg.obj != null) {
                            AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                            if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                                MainApplication.getInstance().setTrackDeviceModel(null);
                                MainApplication.getInstance().setTrackUserModel(null);
                                MainApplication.getInstance().getTrackDeviceList().clear();
                                showMessage(R.string.account_cancellation_succeed);
                                new Thread(new Runnable() { // 倒计时两秒后返回登录页
                                    @Override
                                    public void run() {
                                        try{
                                            Thread.sleep(1000);
                                        }catch(InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(LoginActivity.class);
                                    }
                                }).start();
                                break;
                            } else {
                                showMessage(ErrorCode.getResId(response.getCode()));
                            }
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_CLEAR_CACHE: // 清除缓存
                                Context context = getContext();
                                if (context != null)
                                    DataCleanManager.clearAllCache(context);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SQLite.delete(AlbumModel.class).execute();
                                        SQLite.delete(CallRecordModel.class).execute();
                                        SQLite.delete(DeviceSysMsgModel.class).execute();
                                        SQLite.delete(StepModel.class).execute();
                                        SQLite.delete(SmsModel.class).execute();
                                        SQLite.delete(TrackModel.class).execute();
                                        Glide.get(MainApplication.getInstance()).clearDiskCache();
                                        if (mActivity != null) {
                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Glide.get(MainApplication.getInstance()).clearMemory();
                                                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CLEAR_CACHE));
                                                    XToastUtils.toast(R.string.clear_cache_success_prompt);
                                                }
                                            });
                                        }
                                    }
                                }).start();
                                break;
                            case CWConstant.DIALOG_LOGIN_OUT: // 退出登录
//                                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, "");
//                                MainApplication.getInstance().setDeviceModel(null);
//                                MainApplication.getInstance().setUserModel(null);
//                                MainApplication.getInstance().getDeviceList().clear();
                                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                                MainApplication.getInstance().setTrackDeviceModel(null);
                                MainApplication.getInstance().setTrackUserModel(null);
                                MainApplication.getInstance().getTrackDeviceList().clear();
                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                ActivityUtils.startActivity(LoginActivity.class);
                                break;
                            case LOG_OFF: // 注销帐号
                                CarGpsRequestUtils.logOff(getTrackUserModel(), mHandler);
                                break;
                            default:
                                break;
                        }
                        break;
                    case CWConstant.HANDLE_INPUT_ACTION: //弹出框的输入回调
                        switch (msg.arg1) {
                            case INPUT_LOCATION_REFRESH_INTERVAL:
                                String str = (String) msg.obj;
                                int inputText = Integer.parseInt(str);
                                if (inputText < 15 || inputText > 300) {
                                    showMessage(R.string.set_positioning_refresh_interval_prompt);
                                    return false;
                                }
                                for (SectionItem item : mItemList) {
                                    BaseItemBean itemBean = item.t;
                                    if (itemBean != null && itemBean.getType() == 7) {
                                        itemBean.setContent(String.format("%s%s",inputText,getString(R.string.second_new)));
                                        SettingSPUtils.getInstance().putInt(TConstant.LOCATION_REFRESH_INTERVAL,inputText);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_REFRESH_INTERVAL));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                                break;
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

    @Override
    public void onResume() {
        super.onResume();
        for (SectionItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null) {
                switch (itemBean.getType()) {
                    case 0: // 消息通知
                        itemBean.setContent(NotificationUtils.isNotificationEnabled(mActivity) ?
                                getString(R.string.is_open) : getString(R.string.is_close));
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 7:
                        itemBean.setContent(String.format("%s%s",SettingSPUtils.getInstance().getInt(TConstant.LOCATION_REFRESH_INTERVAL, CWConstant.DEFAULT_LOCAL_REFRESH_INTERVAL)
                                ,getString(R.string.second_new)));
                        mAdapter.notifyDataSetChanged();
                    default:
                        break;
                }
            }
        }
    }
}
