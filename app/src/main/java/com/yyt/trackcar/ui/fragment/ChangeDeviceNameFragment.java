package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserModel_Table;
import com.yyt.trackcar.ui.activity.BindSuccessActivity;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.EmojiFilter;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.PictureSelectorUtils;
import com.yyt.trackcar.utils.PortraitUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Page(name = "ChangeDeviceName", params = {CWConstant.TYPE, CWConstant.IMEI})
public class ChangeDeviceNameFragment extends BaseFragment {
    @BindView(R.id.ivPortrait)
    ImageView mIvPortrait; // 头像
    @BindView(R.id.tvInput)
    TextView mTvInput; // 输入文本
    @BindView(R.id.etInput)
    EditText mEtInput; // 输入文本编辑
    //    @BindView(R.id.tvSecondInput)
//    TextView mTvSecondInput; // 第二输入文本
//    @BindView(R.id.etSecondInput)
//    EditText mEtSecondInput; // 第二输入文本编辑
    @AutoWired
    String imei; // 设备imei
    @AutoWired
    int type; // 0.从未绑定界面 1.首页绑定新设备 2.首页绑定新设备
    @AutoWired
    String name;
    private BottomSheet mBottomSheet; // 选项弹窗
    private String mImageUrl; // 头像地址
    private DeviceModel mDeviceModel; // 设备对象
    private boolean mIsFinding = true; // 是否查询设备
    private boolean mIsSetting; // 是否正在设置

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_change_device_name;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.device_info);
        titleBar.setLeftImageDrawable(null).setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
//        mTvInput.setText(R.string.contact_name);
//        mEtInput.setHint(R.string.contact_name_hint);
        mTvInput.setText(R.string.device_name);
        mEtInput.setHint(R.string.device_name_hint);
//        mEtInput.setFilters(new InputFilter[]{new InputFilter
//                .LengthFilter(12)});
//        mEtInput.setInputType(InputType.TYPE_CLASS_TEXT);
        mEtInput.setFilters(new InputFilter[]{new InputFilter
                .LengthFilter(12)});
        mEtInput.setInputType(InputType.TYPE_CLASS_TEXT);
        UserModel userModel = getUserModel();
        if (userModel != null) {
            int deviceType = SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, 1);
            CWRequestUtils.getInstance().getBindDeviceList(MainApplication.getContext(),
                    userModel.getU_id(),
                    userModel.getToken(), deviceType, mHandler);
        }
        int imgRes;
        imgRes = R.mipmap.ic_default_pigeon_marker;
        ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
                mIvPortrait);
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

    @SingleClick
    @OnClick({R.id.ivPortrait, R.id.ibCamera, R.id.confirmBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPortrait: // 头像
            case R.id.ibCamera:
                if (mDeviceModel == null) {
                    if (!mIsFinding) {
                        UserModel userModel = getUserModel();
                        if (userModel != null) {
                            int deviceType =
                                    SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, 1);
                            CWRequestUtils.getInstance().getBindDeviceList(MainApplication.getContext(), userModel.getU_id(),
                                    userModel.getToken(), deviceType, mHandler);
                        }
                    }
                } else if (mBottomSheet == null || !mBottomSheet.isShowing()) {
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
                                            PictureSelectorUtils.selectPortrait(mActivity,
                                                    ChangeDeviceNameFragment.this, 1);
                                        else if (position == 1)
                                            PictureSelectorUtils.selectPortrait(mActivity,
                                                    ChangeDeviceNameFragment.this, 0);
                                    }
                                })
                                .build();
                        mBottomSheet.show();
                    }
                }
                break;
            case R.id.confirmBtn: // 确认按钮
                if (mDeviceModel == null) {
                    if (!mIsFinding) {
                        UserModel userModel = getUserModel();
                        if (userModel != null) {
                            int deviceType =
                                    SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, 1);
                            CWRequestUtils.getInstance().getBindDeviceList(MainApplication.getContext(), userModel.getU_id(),
                                    userModel.getToken(), deviceType, mHandler);
                        }
                    }
                } else {
                    String inputText = mEtInput.getText().toString().trim();
                    if (TextUtils.isEmpty(inputText) || EmojiFilter.containsEmoji(inputText)) {
                        XToastUtils.toast(mEtInput.getHint().toString());
                        mEtInput.requestFocus();
                    } else
                        setBabyNameAndHead(inputText);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置设备昵称和头像
     */
    private void setBabyNameAndHead(String babyName) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        if (userModel != null && !mIsSetting) {
            mIsSetting = true;
            CWRequestUtils.getInstance().setBabyNameAndHead(getContext(), getUserModel().getToken(),
                    String.valueOf(mDeviceModel.getD_id()), mDeviceModel.getImei(), babyName,
                    mImageUrl == null ? "" : mImageUrl, mHandler);
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
//                                    CWRequestUtils.getInstance().uploadFile(mDeviceModel, path,
//                                            mHandler);
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
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_BIND_DEVICE_LIST: // 用户查询绑定设备列表
                        mIsFinding = false;
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                RequestBean.class);
                                userModel =
                                        SQLite.select().from(UserModel.class)
                                                .where(UserModel_Table.u_id.eq(requestBean.getU_id()))
                                                .querySingle();
                                if (userModel != null) {
                                    SQLite.delete(DeviceModel.class).where(DeviceModel_Table.u_id.eq(userModel.getU_id())).execute();
                                    String selectImei = userModel.getSelectImei();
                                    if (selectImei == null)
                                        selectImei = "";
                                    if (imei == null)
                                        imei = "";
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
                                            if (imei.equals(deviceModel.getActiveCode())) {
                                                MainApplication.getInstance().setDeviceModel(deviceModel);
                                                mDeviceModel = deviceModel;
                                                if (!selectImei.equals(deviceModel.getImei())) {
                                                    userModel.setSelectImei(deviceModel.getImei());
                                                    userModel.save();
                                                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                                }
                                            }
                                        }
                                        if (getDevice() == null) {
                                            for (DeviceModel deviceModel : deviceList) {
                                                if (selectImei.equals(deviceModel.getImei())) {
                                                    MainApplication.getInstance().setDeviceModel(deviceModel);
                                                    break;
                                                }
                                            }
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
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_BABY_NAME_AND_HEAD: // 设置设备头像和昵称
                        if (mIsSetting) {
                            if (msg.obj == null)
                                XToastUtils.toast(R.string.request_unkonow_prompt);
                            else {
                                resultBean = (RequestResultBean) msg.obj;
                                if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()),
                                                    RequestBean.class);
                                    userModel = getUserModel();
                                    if (userModel != null && mDeviceModel != null) {
                                        OperatorGroup operatorGroup =
                                                OperatorGroup.clause(OperatorGroup.clause()
                                                        .and(DeviceInfoModel_Table.u_id.eq(userModel.getU_id()))
                                                        .and(DeviceInfoModel_Table.imei.eq(mDeviceModel.getImei())));
                                        DeviceInfoModel infoModel =
                                                SQLite.select().from(DeviceInfoModel.class)
                                                        .where(operatorGroup)
                                                        .querySingle();
                                        if (infoModel == null) {
                                            infoModel = new DeviceInfoModel();
                                            infoModel.setU_id(userModel.getU_id());
                                            infoModel.setImei(mDeviceModel.getImei());
                                            infoModel.setBirday("");
                                            infoModel.setFamilyNumber("");
                                            infoModel.setWeight("60");
                                            infoModel.setHeight("175");
                                            infoModel.setHead("");
                                            infoModel.setNickname("");
                                            infoModel.setSchool_age("");
                                            infoModel.setPhone("");
                                            infoModel.setFamilyNumber("");
                                            infoModel.setSchool_info("");
                                            infoModel.setHome_info("");
                                        }
                                        infoModel.setHead(requestBean.getHeadurl());
                                        infoModel.setNickname(requestBean.getNickname());
                                        infoModel.save();
                                        PortraitModel portraitModel = new PortraitModel();
                                        portraitModel.setImei(mDeviceModel.getImei());
                                        portraitModel.setUserId(mDeviceModel.getImei());
                                        if (TextUtils.isEmpty(infoModel.getNickname())) {
                                            portraitModel.setName("");
                                        } else
                                            portraitModel.setName(infoModel.getNickname());
                                        portraitModel.setUrl(infoModel.getHead());
                                        portraitModel.save();
                                        PortraitUtils.getInstance().updatePortrait(portraitModel);

                                    }
                                    Bundle bundle;
                                    if (type == 0) {
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                        Intent intent = new Intent(XUtil.getContext(),
                                                BindSuccessActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        bundle = new Bundle();
                                        bundle.putInt(CWConstant.TYPE, 0);
                                        bundle.putString(CWConstant.IMEI, imei);
                                        intent.putExtras(bundle);
                                        ActivityUtils.startActivity(intent);
                                    } else {
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_BACK_TO_MAIN));
                                        bundle = new Bundle();
                                        bundle.putInt(CWConstant.TYPE, 1);
                                        bundle.putString(CWConstant.IMEI, imei);
                                        openNewPage(BindSuccessFragment.class, bundle);
                                    }
                                } else
                                    RequestToastUtils.toast(resultBean.getCode());
                            }
                            mIsSetting = false;
                        }
                        break;
                    case CWConstant.REQUEST_UPLOAD_IMAGE: // 上传图片
                        if (mMaterialDialog != null)
                            mMaterialDialog.dismiss();
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.upload_file_error_prompt);
                        else {
                            if (!TextUtils.isEmpty(mImageUrl))
                                CWRequestUtils.getInstance().deleteFile(mImageUrl);
                            mImageUrl = (String) msg.obj;
                            int imgRes;
                            imgRes = R.mipmap.ic_default_pigeon_marker;
                            ImageLoadUtils.loadPortraitImage(getContext(), mImageUrl,
                                    imgRes, mIvPortrait);
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
    public void onDestroy() {
        DialogUtils.dismiss(mBottomSheet);
        super.onDestroy();
    }
}
