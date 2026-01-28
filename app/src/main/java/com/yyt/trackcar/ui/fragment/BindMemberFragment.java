package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.DeviceSysMsgBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.UserBean;
import com.yyt.trackcar.dbflow.AppMsgModel;
import com.yyt.trackcar.dbflow.AppMsgModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.PortraitModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.adapter.BindMemberAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.PortraitUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BindMemberFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 09:26
 * @ describe:      TODO 绑定成员页面
 */
@Page(name = "BindMember")
public class BindMemberFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    @BindView(R.id.tvPrompt)
    TextView mTvPrompt; // 提示信息
    private BindMemberAdapter mAdapter; // 适配器
    private List<UserBean> mItemList = new ArrayList<>(); // 列表
    private long mId; // 用户id

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册订阅者
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bind_member;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.bind_member);
        return titleBar;
    }

    @Override
    protected void initViews() {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null && deviceModel.getDevice_type() == CWConstant.DEVICE_TYPE_S9)
            mTvPrompt.setText(R.string.bind_member_device_no_video_prompt);
        else if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
            mTvPrompt.setText(R.string.bind_member_prompt);
        else
            mTvPrompt.setText(R.string.bind_member_device_prompt);
        initItems();
        initAdapters();
        initRecyclerViews();
        getImeiBindUsers();
//        getAppMsg();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        UserBean userBean = new UserBean();
        userBean.setStatus(2);
        userBean.setImgDrawable(R.mipmap.ic_add_member);
        userBean.setName(getString(R.string.invitate_member));
        mItemList.add(userBean);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new BindMemberAdapter(mItemList);
        mAdapter.setOnItemChildClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 设备管理员查询  某个设备的绑定用户
     */
    private void getImeiBindUsers() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getImeiBindUsers(getContext(), userModel.getToken(),
                    deviceModel.getImei(), mHandler);
    }

    /**
     * 转让管理员
     */
    private void transferAdmin(String userId) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().transferAdmin(getContext(), userModel.getToken(),
                    deviceModel.getImei(), userId, mHandler);
    }

    /**
     * 获取APP系统通知消息
     */
    private void getAppMsg() {
        UserModel userModel = getUserModel();
        if (userModel != null)
            CWRequestUtils.getInstance().getAppMsg(getContext(), userModel.getToken(), mHandler);
    }

    /**
     * 管理员同意某个用户绑定
     */
    private void adminAgreeBind(AppMsgModel appMsgBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(AppMsgModel_Table.u_id.eq(userModel.getU_id()))
                            .and(AppMsgModel_Table.imei.eq(appMsgBean.getImei()))
                            .and(AppMsgModel_Table.send_id.eq(appMsgBean.getSend_id()))
                            .and(AppMsgModel_Table.type.eq(27)));
            SQLite.delete(AppMsgModel.class).where(operatorGroup).execute();
            CWRequestUtils.getInstance().adminAgreeBind(MainApplication.getInstance(),
                    userModel.getToken(), String.valueOf(appMsgBean.getSend_id()),
                    appMsgBean.getImei(), mHandler);
        }
    }

    /**
     * 管理员同意某个用户绑定
     */
    private void adminAgreeBind(UserBean userBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(AppMsgModel_Table.u_id.eq(userModel.getU_id()))
                            .and(AppMsgModel_Table.imei.eq(userBean.getImei()))
                            .and(AppMsgModel_Table.send_id.eq(userBean.getUser_id()))
                            .and(AppMsgModel_Table.type.eq(27)));
            SQLite.delete(AppMsgModel.class).where(operatorGroup).execute();
            CWRequestUtils.getInstance().adminAgreeBind(MainApplication.getInstance(),
                    userModel.getToken(),
                    String.valueOf(userBean.getUser_id()), userBean.getImei(), mHandler);
        }
    }

    /**
     * 管理员拒绝某个用户绑定
     */
    private void refuseBind(AppMsgModel appMsgBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(AppMsgModel_Table.u_id.eq(userModel.getU_id()))
                            .and(AppMsgModel_Table.imei.eq(appMsgBean.getImei()))
                            .and(AppMsgModel_Table.send_id.eq(appMsgBean.getSend_id()))
                            .and(AppMsgModel_Table.type.eq(27)));
            SQLite.delete(AppMsgModel.class).where(operatorGroup).execute();
            CWRequestUtils.getInstance().refuseBind(MainApplication.getInstance(),
                    userModel.getToken(),
                    String.valueOf(appMsgBean.getSend_id()), appMsgBean.getImei(), mHandler);
        }
    }

    /**
     * 管理员拒绝某个用户绑定
     */
    private void refuseBind(UserBean userBean) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null) {
            OperatorGroup operatorGroup =
                    OperatorGroup.clause(OperatorGroup.clause()
                            .and(AppMsgModel_Table.u_id.eq(userModel.getU_id()))
                            .and(AppMsgModel_Table.imei.eq(userBean.getImei()))
                            .and(AppMsgModel_Table.send_id.eq(userBean.getUser_id()))
                            .and(AppMsgModel_Table.type.eq(27)));
            SQLite.delete(AppMsgModel.class).where(operatorGroup).execute();
            CWRequestUtils.getInstance().refuseBind(MainApplication.getInstance(),
                    userModel.getToken(),
                    String.valueOf(userBean.getUser_id()), userBean.getImei(), mHandler);
        }
    }

    /**
     * 修改用户昵称
     *
     * @param name 昵称
     * @param url  头像
     */
    private void updateBindUserName(String name, String url) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().updateBindUserName(getContext(), deviceModel.getImei(),
                    userModel.getToken(), mId, name, url, mHandler);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            UserBean userBean = mItemList.get(position);
            Bundle bundle;
            if (userBean.getImgDrawable() == R.mipmap.ic_add_member) {
                bundle = new Bundle();
                bundle.putString(CWConstant.TITLE, userBean.getName());
                openNewPage(QRCodeFragment.class, bundle);
            } else if (userBean.isMe()) {
                bundle = new Bundle();
                bundle.putParcelable(CWConstant.MODEL, userBean);
                openNewPageForResult(BindMemberEditFragment.class, bundle,
                        CWConstant.REQUEST_BIND_MEMBER_EDIT);
//                mId = userBean.getId();
//                mMaterialDialog = TrackDialogUtils.customInputMaterialDialog(getContext(),
//                        mMaterialDialog, getString(R.string.change_user_nick), null,
//                        getString(R.string.contact_name_hint), userBean.getName(),
//                        InputType.TYPE_CLASS_TEXT
//                        , 10, 1, getString(R.string.confirm), getString(R.string.cancel),
//                        CWConstant.DIALOG_INPUT_USER_NICK, mHandler);
            } else {
                DeviceModel deviceModel = getDevice();
                if (deviceModel.getStatus() == 1 && userBean.getStatus() == 0)
                    mMaterialDialog =
                            DialogUtils.customMaterialDialog(mActivity,
                                    mMaterialDialog,
                                    getString(R.string.user_bind_device_prompt),
                                    getString(R.string.manager_bind_content,
                                            userBean.getName()),
                                    getString(R.string.confirm), getString(R.string.refuse),
                                    getString(R.string.next_handle), userBean,
                                    CWConstant.DIALOG_MANAGER_BIND_SECOND,
                                    mHandler);
                else {
                    bundle = new Bundle();
                    bundle.putParcelable(CWConstant.MODEL, userBean);
                    openNewPageForResult(BindMemberEditFragment.class, bundle,
                            CWConstant.REQUEST_BIND_MEMBER_EDIT);
                }
//                        mMaterialDialog =
//                                TrackDialogUtils.customMaterialDialog(mActivity,
//                                        mMaterialDialog,
//                                        getString(R.string.manager_transfer_prompt),
//                                        getString(R.string.manager_transfer_content,
//                                                userBean.getName()),
//                                        getString(R.string.confirm),
//                                        getString(R.string.cancel), userBean,
//                                        CWConstant.DIALOG_MANAGER_TRANSFER,
//                                        mHandler);
//            }
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == CWConstant.REQUEST_BIND_MEMBER_EDIT && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                UserBean userBean = bundle.getParcelable(CWConstant.MODEL);
                if (userBean != null) {
                    boolean isManager = userBean.getStatus() == 1;
                    for (int i = 0; i < mItemList.size(); i++) {
                        UserBean model = mItemList.get(i);
                        if (model.getUser_id() == userBean.getUser_id()) {
                            if (userBean.getStatus() == -88) {
                                mItemList.remove(i);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                            if (model.getStatus() == 1)
                                isManager = false;
                            mItemList.set(i, userBean);
                            if (!isManager) {
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        if (isManager && model.getStatus() == 1)
                            model.setStatus(2);
                    }
                    if (isManager)
                        mAdapter.notifyDataSetChanged();
                }
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
                UserModel userModel;
                DeviceModel deviceModel;
                UserBean userBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_IMEI_BIND_USERS: // 设备管理员查询  某个设备的绑定用户
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                List userList = resultBean.getUserList();
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                if (deviceModel != null) {
                                    OperatorGroup oGroup =
                                            OperatorGroup.clause(OperatorGroup.clause()
                                                    .and(PortraitModel_Table.userId.notEq(deviceModel.getImei()))
                                                    .and(PortraitModel_Table.imei.eq(deviceModel.getImei())));
                                    SQLite.delete(PortraitModel.class).where(oGroup).execute();
                                }
                                if (userList == null || userList.size() == 0) {
                                    if (userModel == null)
                                        popToBack();
                                    else {
                                        requestBean =
                                                mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                        List<DeviceModel> deviceList = getDeviceList();
                                        for (int i = 0; i < deviceList.size(); i++) {
                                            DeviceModel model = deviceList.get(i);
                                            if (model.getImei().equals(requestBean.getImei())) {
                                                OperatorGroup operatorGroup =
                                                        OperatorGroup.clause(OperatorGroup.clause()
                                                                .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                                                .and(DeviceModel_Table.d_id.eq(model.getD_id())));
                                                SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                                                deviceList.remove(i);
                                                break;
                                            }
                                        }
                                        if (deviceList.size() == 0) {
                                            MainApplication.getInstance().setDeviceModel(null);
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                            ActivityUtils.startActivity(BindDeviceActivity.class);
                                        } else {
                                            if (deviceModel != null && deviceModel.getImei().equals(requestBean.getImei()))
                                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                            MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                            userModel.setSelectImei(getDevice().getImei());
                                            userModel.save();
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                        }
                                    }
                                } else {
                                    mItemList.clear();
                                    for (Object obj : userList) {
                                        userBean = mGson.fromJson(mGson.toJson(obj),
                                                UserBean.class);
                                        if (userBean.getStatus() != 0) {
                                            PortraitModel portraitModel = new PortraitModel();
                                            portraitModel.setImei(userBean.getImei());
                                            portraitModel.setUserId(String.valueOf(userBean.getUser_id()));
                                            portraitModel.setName(userBean.getName());
                                            portraitModel.setUrl(userBean.getUrl());
                                            portraitModel.save();
                                            PortraitUtils.getInstance().updatePortrait(portraitModel);
                                        }
                                        userBean.setImgDrawable(R.mipmap.ic_default_pigeon_marker);
                                        if (userModel != null &&
                                                userBean.getUser_id() == userModel.getU_id()) {
                                            userBean.setMe(true);
                                            int status = userBean.getStatus();
                                            if (userBean.getStatus() != 1)
                                                status = 0;
                                            boolean isSave = false;
                                            if (deviceModel != null && deviceModel.getImei().equals(userBean.getImei())) {
                                                deviceModel.setStatus(status);
                                                isSave = true;
                                                deviceModel.save();
                                            }
                                            for (DeviceModel model : getDeviceList()) {
                                                if (model.getImei().equals(userBean.getImei())) {
                                                    model.setStatus(status);
                                                    if (!isSave)
                                                        model.save();
                                                    break;
                                                }
                                            }
                                        } else
                                            userBean.setMe(false);
                                        mItemList.add(userBean);
                                    }
                                    initItems();
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else if (resultBean.getCode() == CWConstant.NOT_RESULT) {
                                userModel = getUserModel();
                                deviceModel = getDevice();
                                if (deviceModel != null) {
                                    OperatorGroup oGroup =
                                            OperatorGroup.clause(OperatorGroup.clause()
                                                    .and(PortraitModel_Table.userId.notEq(deviceModel.getImei()))
                                                    .and(PortraitModel_Table.imei.eq(deviceModel.getImei())));
                                    SQLite.delete(PortraitModel.class).where(oGroup).execute();
                                }
                                if (userModel == null)
                                    popToBack();
                                else {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    List<DeviceModel> deviceList = getDeviceList();
                                    for (int i = 0; i < deviceList.size(); i++) {
                                        DeviceModel model = deviceList.get(i);
                                        if (model.getImei().equals(requestBean.getImei())) {
                                            OperatorGroup operatorGroup =
                                                    OperatorGroup.clause(OperatorGroup.clause()
                                                            .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                                            .and(DeviceModel_Table.d_id.eq(model.getD_id())));
                                            SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                                            deviceList.remove(i);
                                            break;
                                        }
                                    }
                                    if (deviceList.size() == 0) {
                                        MainApplication.getInstance().setDeviceModel(null);
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        ActivityUtils.startActivity(BindDeviceActivity.class);
                                    } else {
                                        if (deviceModel != null && deviceModel.getImei().equals(requestBean.getImei()))
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                        MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                        userModel.setSelectImei(getDevice().getImei());
                                        userModel.save();
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                    }
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_APP_MSG: // 获取APP系统通知消息
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                if (resultBean.getMsgList() != null) {
                                    userModel = getUserModel();
                                    deviceModel = getDevice();
                                    if (userModel != null)
                                        SQLite.delete(AppMsgModel.class).where(AppMsgModel_Table.u_id.eq(userModel.getU_id())).execute();
                                    for (int i = 0; i < resultBean.getMsgList().size(); i++) {
                                        AppMsgModel msgBean =
                                                mGson.fromJson(mGson.toJson(resultBean.getMsgList().get(i)), AppMsgModel.class);
                                        if (userModel != null) {
                                            msgBean.setId(i);
                                            msgBean.setU_id(userModel.getU_id());
                                            msgBean.save();
                                        }
                                        if (msgBean.getType() == 27 || deviceModel.getImei().equals(msgBean.getImei()))
                                            mMaterialDialog =
                                                    DialogUtils.customMaterialDialog(mActivity,
                                                            mMaterialDialog,
                                                            getString(R.string.user_bind_device_prompt),
                                                            getString(R.string.manager_bind_content,
                                                                    msgBean.getRemark()),
                                                            getString(R.string.confirm),
                                                            getString(R.string.refuse),
                                                            getString(R.string.next_handle),
                                                            msgBean,
                                                            CWConstant.DIALOG_MANAGER_BIND,
                                                            mHandler);
                                    }
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_ADMIN_AGREE_BIND: // 管理员同意某个用户绑定
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.manager_agree_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                boolean isFind = false;
                                for (UserBean model : mItemList) {
                                    if (String.valueOf(model.getUser_id()).equals(requestBean.getSendId())) {
                                        isFind = true;
                                        model.setStatus(2);
                                        PortraitModel portraitModel = new PortraitModel();
                                        portraitModel.setImei(model.getImei());
                                        portraitModel.setUserId(String.valueOf(model.getUser_id()));
                                        portraitModel.setName(model.getName());
                                        portraitModel.setUrl(model.getUrl());
                                        portraitModel.save();
                                        PortraitUtils.getInstance().updatePortrait(portraitModel);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (!isFind)
                                    getImeiBindUsers();
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_REFUSE_BIND: // 管理员拒绝某个用户绑定
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.manager_refuse_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                for (int i = 0; i < mItemList.size(); i++) {
                                    UserBean model = mItemList.get(i);
                                    if (String.valueOf(model.getUser_id()).equals(requestBean.getSendId())) {
                                        mItemList.remove(i);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_TRANSFER_ADMIN: // 转让管理员
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.manager_transfer_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                for (UserBean model : mItemList) {
                                    if (String.valueOf(model.getUser_id()).equals(requestBean.getUser_id()))
                                        model.setStatus(1);
                                    else if (model.getStatus() == 1)
                                        model.setStatus(2);
                                }
                                deviceModel = getDevice();
                                if (deviceModel.getImei().equals(requestBean.getImei())) {
                                    deviceModel.setStatus(0);
                                    deviceModel.save();
                                } else {
                                    for (DeviceModel model : getDeviceList()) {
                                        if (model.getImei().equals(requestBean.getImei())) {
                                            model.setStatus(0);
                                            model.save();
                                        }
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_UPDATE_BIND_USER_NAME: // 修改用户昵称
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.update_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                for (UserBean model : mItemList) {
                                    if (model.isMe()) {
                                        model.setName(requestBean.getName());
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_MANAGER_BIND: // 请求绑定
                                AppMsgModel msgBean = (AppMsgModel) msg.obj;
                                adminAgreeBind(msgBean);
                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BIND_MEMBER_HANDLE));
                                break;
                            case CWConstant.DIALOG_MANAGER_BIND_SECOND: // 请求绑定
                                userBean = (UserBean) msg.obj;
                                adminAgreeBind(userBean);
                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BIND_MEMBER_HANDLE));
                                break;
                            case CWConstant.DIALOG_MANAGER_TRANSFER: // 转让管理员
                                userBean = (UserBean) msg.obj;
                                transferAdmin(String.valueOf(userBean.getUser_id()));
                                break;
                            default:
                                break;
                        }
                        break;
                    case CWConstant.HANDLE_NEUTRAL_ACTION: // 中间
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_MANAGER_BIND: // 拒绝绑定
                                AppMsgModel msgBean = (AppMsgModel) msg.obj;
                                refuseBind(msgBean);
                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BIND_MEMBER_HANDLE));
                                break;
                            case CWConstant.DIALOG_MANAGER_BIND_SECOND: // 拒绝绑定
                                userBean = (UserBean) msg.obj;
                                refuseBind(userBean);
                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BIND_MEMBER_HANDLE));
                                break;
                            default:
                                break;
                        }
                        break;
                    case CWConstant.HANDLE_INPUT_ACTION: // 输入回调
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_INPUT_USER_NICK: // 修改用户昵称
                                String inputText = (String) msg.obj;
                                if (!TextUtils.isEmpty(inputText))
                                    updateBindUserName(inputText, "");
                                break;
                            default:
                                break;
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

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onPostMsgBean(DeviceSysMsgBean event) {
        if (CWConstant.MANAGER_BIND == event.getType()) {
            DeviceModel deviceModel = getDevice();
            if (deviceModel != null && deviceModel.getImei().equals(event.getImei()))
                getImeiBindUsers();
        } else if (CWConstant.MANAGER_UNBIND == event.getType()) {
            DeviceModel deviceModel = getDevice();
            if (deviceModel != null && deviceModel.getImei().equals(event.getImei())) {
                for (int i = 0; i < mItemList.size(); i++) {
                    UserBean model = mItemList.get(i);
                    if (String.valueOf(model.getUser_id()).equals(event.getMsg())) {
                        mItemList.remove(i);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        } else if (CWConstant.REFUSE_BIND == event.getType()) {
            DeviceModel deviceModel = getDevice();
            if (deviceModel != null && deviceModel.getImei().equals(event.getImei())) {
                for (int i = 0; i < mItemList.size(); i++) {
                    UserBean model = mItemList.get(i);
                    if (String.valueOf(model.getUser_id()).equals(event.getMsg()) && model.getStatus() == 0) {
                        mItemList.remove(i);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        } else if (CWConstant.AGREE_BIND == event.getType()) {
            DeviceModel deviceModel = getDevice();
            if (deviceModel != null && deviceModel.getImei().equals(event.getImei())) {
                for (int i = 0; i < mItemList.size(); i++) {
                    UserBean model = mItemList.get(i);
                    if (String.valueOf(model.getUser_id()).equals(event.getMsg()) && model.getStatus() == 0) {
                        model.setStatus(2);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        } else if (CWConstant.TRANSFER_MANAGER == event.getType()) {
            DeviceModel deviceModel = getDevice();
            if (deviceModel != null && deviceModel.getImei().equals(event.getImei())) {
                for (UserBean model : mItemList) {
                    if (String.valueOf(model.getUser_id()).equals(event.getMsg()))
                        model.setStatus(1);
                    else if (model.getStatus() == 1)
                        model.setStatus(2);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
