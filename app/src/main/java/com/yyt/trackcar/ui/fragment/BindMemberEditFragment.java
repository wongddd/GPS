package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.UserBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.PortraitModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.PictureSelectorUtils;
import com.yyt.trackcar.utils.PortraitUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BindMemberEditFragment
 * @ author:        QING
 * @ createTime:    2020/5/8 14:27
 * @ describe:      TODO 绑定成员资料页面
 */
@Page(name = "BindMemberEdit", params = {CWConstant.MODEL})
public class BindMemberEditFragment extends BaseFragment {
    @BindView(R.id.ivPortrait)
    ImageView mIvPortrait; // 头像
    @BindView(R.id.tvName)
    TextView mTvName; // 名称文本
    @BindView(R.id.tvContent)
    TextView mTvContent; // 名称文本
    @BindView(R.id.transferBtn)
    Button mTransferBtn; // 转让管理员按钮
    @BindView(R.id.unBindBtn)
    Button mUnBindBtn; // 解绑按钮
    @AutoWired
    UserBean model;
    private BottomSheet mBottomSheet; // 选项弹窗

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bind_member_edit;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        if (model == null)
            titleBar.setTitle(getString(R.string.info_title, ""));
        else
            titleBar.setTitle(getString(R.string.info_title, model.getName()));
        return titleBar;
    }

    @Override
    protected void initViews() {
        if (model == null)
            popToBack();
        else {
            ImageLoadUtils.loadPortraitImage(getContext(), model.getUrl(), mIvPortrait);
            mTvName.setText(model.getName());
            mTvContent.setText(model.getName());
            DeviceModel deviceModel = getDevice();
            if (deviceModel != null && deviceModel.getStatus() == 1 && model.getStatus() == 2) {
                mTransferBtn.setVisibility(View.VISIBLE);
                mUnBindBtn.setVisibility(View.VISIBLE);
            } else if (model.isMe() && model.getStatus() != 0) {
                mTransferBtn.setVisibility(View.GONE);
                mUnBindBtn.setVisibility(View.VISIBLE);
            } else {
                mTransferBtn.setVisibility(View.GONE);
                mUnBindBtn.setVisibility(View.GONE);
            }
        }
    }

//    /**
//     * 显示上传对话框
//     *
//     * @param asyncTask 上传线程
//     */
//    private void showUploadDialog(OSSAsyncTask asyncTask) {
//        mMaterialDialog = new MaterialDialog.Builder(mActivity)
//                .title(R.string.prompt)
//                .content(R.string.upload_file_prompt)
//                .progress(true, 0)
//                .progressIndeterminateStyle(false)
//                .negativeText(R.string.cancel)
//                .cancelable(false)
//                .dismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        if (!asyncTask.isCanceled())
//                            asyncTask.cancel();
//                    }
//                })
//                .show();
//    }

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
                    userModel.getToken(), model.getId(), name, url, mHandler);
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
     * 解绑
     */
    private void deleteDevice() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().deleteDevice(MainApplication.getInstance(),
                    userModel.getToken(), model.getImei(), model.getUser_id(), mHandler);
    }

    @SingleClick
    @OnClick({R.id.ivPortrait, R.id.ibCamera, R.id.clName, R.id.transferBtn, R.id.unBindBtn})
    public void onClick(View v) {
        DeviceModel deviceModel;
        switch (v.getId()) {
            case R.id.ivPortrait: // 头像
            case R.id.ibCamera:
                deviceModel = getDevice();
                if ((deviceModel != null && deviceModel.getStatus() == 1) || (model.isMe() && model.getStatus() != 0)) {
                    if (mBottomSheet == null || !mBottomSheet.isShowing()) {
                        Context context = getContext();
                        if (context != null) {
                            mBottomSheet = new BottomSheet.BottomListSheetBuilder(context)
                                    .addItem(getString(R.string.photograph))
                                    .addItem(getString(R.string.select_from_album))
                                    .addItem(getString(R.string.cancel))
                                    .setIsCenter(true)
                                    .setOnSheetItemClickListener(new BottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(BottomSheet dialog, View itemView,
                                                            int position, String tag) {
                                            dialog.dismiss();
                                            if (position == 0)
                                                PictureSelectorUtils.selectPortrait(mActivity,BindMemberEditFragment.this, 1);
                                            else if (position == 1)
                                                PictureSelectorUtils.selectPortrait(mActivity,BindMemberEditFragment.this, 0);
                                        }
                                    })
                                    .build();
                            mBottomSheet.show();
                        }
                    }
                }
                break;
            case R.id.clName: // 名称
                deviceModel = getDevice();
                if ((deviceModel != null && deviceModel.getStatus() == 1) || (model.isMe() && model.getStatus() != 0))
                    mMaterialDialog = DialogUtils.customInputMaterialDialog(getContext(),
                            mMaterialDialog, getString(R.string.change_user_nick), null,
                            getString(R.string.contact_name_hint), model.getName(),
                            InputType.TYPE_CLASS_TEXT
                            , 12, 1, getString(R.string.confirm), getString(R.string.cancel),
                            CWConstant.DIALOG_INPUT_USER_NICK, mHandler);
                break;
            case R.id.transferBtn: // 转让管理员
//                if(model.getStatus() == 1){
//
//                }else{
                mMaterialDialog =
                        DialogUtils.customMaterialDialog(mActivity,
                                mMaterialDialog,
                                getString(R.string.manager_transfer_prompt),
                                getString(R.string.manager_transfer_content,
                                        model.getName()),
                                getString(R.string.confirm),
                                getString(R.string.cancel), model,
                                CWConstant.DIALOG_MANAGER_TRANSFER,
                                mHandler);
//                }
                break;
            case R.id.unBindBtn: // 解除绑定
                deviceModel = getDevice();
                if (model.getStatus() == 1) {
                    Context context = getContext();
                    if (context != null) {
                        mBottomSheet = new BottomSheet.BottomListSheetBuilder(context)
                                .setTitle(R.string.manager_unbind_title)
                                .addItem(getString(R.string.continue_unbind))
                                .addItem(getString(R.string.manager_transfer_prompt))
                                .addItem(getString(R.string.cancel))
                                .setIsCenter(true)
                                .setOnSheetItemClickListener(new BottomSheet
                                        .BottomListSheetBuilder.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(BottomSheet dialog, View itemView,
                                                        int position, String tag) {
                                        dialog.dismiss();
                                        if (position == 0)
                                            openNewPage(UnbindFragment.class);
                                        else
                                            popToBack();
                                    }
                                })
                                .build();
                        mBottomSheet.show();
                    }
                } else if (deviceModel != null && deviceModel.getStatus() == 1)
                    mMaterialDialog =
                            DialogUtils.customMaterialDialog(mActivity,
                                    mMaterialDialog,
                                    getString(R.string.unbind_member_title),
                                    getString(R.string.unbind_member_prompt),
                                    getString(R.string.confirm),
                                    getString(R.string.cancel), model,
                                    CWConstant.DIALOG_UNBIND,
                                    mHandler);
                else if (model.isMe() && model.getStatus() == 2)
                    mMaterialDialog =
                            DialogUtils.customMaterialDialog(getContext(),
                                    mMaterialDialog,
                                    getString(R.string.unbind),
                                    getString(R.string.unbind_prompt),
                                    getString(R.string.confirm),
                                    getString(R.string.cancel), null,
                                    CWConstant.DIALOG_UNBIND, mHandler);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CWConstant.REQUEST_GALLREY: // 相册
                case CWConstant.REQUEST_CAMERA: // 拍摄
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia localMedia : selectList) {
                        String path;
                        if (localMedia.isCompressed())
                            path = localMedia.getCompressPath();
                        else if (localMedia.isCut())
                            path = localMedia.getCutPath();
                        else
                            path = localMedia.getPath();
                        if (!TextUtils.isEmpty(path)) {
//                            OSSAsyncTask task =
//                                    CWRequestUtils.getInstance().uploadFile(getUserModel(),
//                                            path, mHandler);
//                            if (task != null)
//                                showUploadDialog(task);
                            break;
                        }
                    }
                    break;
                default:
                    break;
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
                PortraitModel portraitModel;
                Intent intent;
                Bundle bundle;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_TRANSFER_ADMIN: // 转让管理员
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.manager_transfer_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (String.valueOf(model.getUser_id()).equals(requestBean.getUser_id())) {
                                    model.setStatus(1);
                                    intent = new Intent();
                                    bundle = new Bundle();
                                    bundle.putParcelable(CWConstant.MODEL, model);
                                    intent.putExtras(bundle);
                                    setFragmentResult(Activity.RESULT_OK, intent);
                                }
                                deviceModel = getDevice();
                                boolean isSave = false;
                                if (deviceModel != null && deviceModel.getImei().equals(requestBean.getImei())) {
                                    deviceModel.setStatus(0);
                                    isSave = true;
                                    deviceModel.save();
                                }
                                for (DeviceModel model : getDeviceList()) {
                                    if (model.getImei().equals(requestBean.getImei())) {
                                        model.setStatus(0);
                                        if (!isSave)
                                            model.save();
                                        break;
                                    }
                                }
                                if (deviceModel != null && deviceModel.getStatus() == 1 && model.getStatus() == 2) {
                                    mTransferBtn.setVisibility(View.VISIBLE);
                                    mUnBindBtn.setVisibility(View.VISIBLE);
                                } else if (model.isMe() && model.getStatus() != 0) {
                                    mTransferBtn.setVisibility(View.GONE);
                                    mUnBindBtn.setVisibility(View.VISIBLE);
                                } else {
                                    mTransferBtn.setVisibility(View.GONE);
                                    mUnBindBtn.setVisibility(View.GONE);
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_UPDATE_BIND_USER_NAME: // 修改用户昵称
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS)
                                XToastUtils.toast(R.string.update_success_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_DELETE_DEVICE: // 解绑
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(getString(R.string.msg_unbind, ""));
                                userModel = getUserModel();
                                OperatorGroup oGroup =
                                        OperatorGroup.clause(OperatorGroup.clause()
                                                .and(PortraitModel_Table.userId.eq(String.valueOf(model.getUser_id())))
                                                .and(PortraitModel_Table.imei.eq(model.getImei())));
                                SQLite.delete(PortraitModel.class).where(oGroup).execute();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    if (requestBean.getU_id() == userModel.getU_id()) {
                                        List<DeviceModel> devicelist = getDeviceList();
                                        for (int i = 0; i < devicelist.size(); i++) {
                                            deviceModel = devicelist.get(i);
                                            if (deviceModel.getImei().equals(requestBean.getImei())) {
                                                OperatorGroup operatorGroup =
                                                        OperatorGroup.clause(OperatorGroup.clause()
                                                                .and(DeviceModel_Table.u_id.eq(userModel.getU_id()))
                                                                .and(DeviceModel_Table.d_id.eq(deviceModel.getD_id())));
                                                SQLite.delete(DeviceModel.class).where(operatorGroup).execute();
                                                devicelist.remove(i);
                                                break;
                                            }
                                        }
                                        if (devicelist.size() == 0) {
                                            MainApplication.getInstance().setDeviceModel(null);
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                            ActivityUtils.startActivity(BindDeviceActivity.class);
                                            return false;
                                        } else {
                                            MainApplication.getInstance().setDeviceModel(devicelist.get(0));
                                            userModel.setSelectImei(getDevice().getImei());
                                            userModel.save();
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                            return false;
                                        }
                                    }
                                }
                                model.setStatus(-88);
                                intent = new Intent();
                                bundle = new Bundle();
                                bundle.putParcelable(CWConstant.MODEL, model);
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_UPLOAD_IMAGE: // 上传图片
                        if (mMaterialDialog != null)
                            mMaterialDialog.dismiss();
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.upload_file_error_prompt);
                        else {
                            if (!TextUtils.isEmpty(model.getUrl()))
                                CWRequestUtils.getInstance().deleteFile(model.getUrl());
                            model.setUrl((String) msg.obj);
                            OperatorGroup operatorGroup =
                                    OperatorGroup.clause(OperatorGroup.clause()
                                            .and(PortraitModel_Table.imei.eq(model.getImei()))
                                            .and(PortraitModel_Table.userId.eq(String.valueOf(model.getUser_id()))));
                            portraitModel =
                                    SQLite.select().from(PortraitModel.class)
                                            .where(operatorGroup)
                                            .querySingle();
                            if (portraitModel != null) {
                                portraitModel.setUrl(model.getUrl());
                                portraitModel.save();
                                PortraitUtils.getInstance().updatePortrait(portraitModel);
                            }
                            ImageLoadUtils.loadPortraitImage(getContext(), model.getUrl(),
                                    mIvPortrait);
                            intent = new Intent();
                            bundle = new Bundle();
                            bundle.putParcelable(CWConstant.MODEL, model);
                            intent.putExtras(bundle);
                            setFragmentResult(Activity.RESULT_OK, intent);
                            updateBindUserName(model.getName(), model.getUrl());
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_MANAGER_TRANSFER: // 转让管理员
                                transferAdmin(String.valueOf(model.getUser_id()));
                                break;
                            case CWConstant.DIALOG_UNBIND: // 解除绑定
                                deleteDevice();
                                break;
                            default:
                                break;
                        }
                        break;
                    case CWConstant.HANDLE_INPUT_ACTION: // 输入回调
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_INPUT_USER_NICK: // 修改用户昵称
                                String inputText = (String) msg.obj;
                                model.setName(inputText);
                                OperatorGroup operatorGroup =
                                        OperatorGroup.clause(OperatorGroup.clause()
                                                .and(PortraitModel_Table.imei.eq(model.getImei()))
                                                .and(PortraitModel_Table.userId.eq(String.valueOf(model.getUser_id()))));
                                portraitModel =
                                        SQLite.select().from(PortraitModel.class)
                                                .where(operatorGroup)
                                                .querySingle();
                                if (portraitModel != null) {
                                    portraitModel.setName(model.getName());
                                    portraitModel.save();
                                    PortraitUtils.getInstance().updatePortrait(portraitModel);
                                }
                                mTvName.setText(model.getName());
                                mTvContent.setText(model.getName());
                                intent = new Intent();
                                bundle = new Bundle();
                                bundle.putParcelable(CWConstant.MODEL, model);
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                if (!TextUtils.isEmpty(inputText))
                                    updateBindUserName(inputText, model.getUrl());
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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        DialogUtils.dismiss(mBottomSheet);
        super.onDestroy();
    }

}
