package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.PersonalCenterAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.PictureSelectorUtils;
import com.yyt.trackcar.utils.PortraitUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      PersonalCenterFragment
 * @ author:        QING
 * @ createTime:    2020/3/11 16:37
 * @ describe:      TODO 个人中心页面
 */
@Page(name = "PersonalCenter")
public class PersonalCenterFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private PersonalCenterAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private TitleBar mTitleBar; // 标题栏
    private ImageView mIvPortrait; // 头像
    private TextView mTvName; // 昵称
    private TextView mTvId; // 设备号
    private TextView mTvPoint; // 积分
    private TextView mTvFriend; // 好友数
    private TextView mTvContacts; // 联系人数
    private BottomSheet mBottomSheet; // 选项弹窗

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        mTitleBar = super.initTitle();
            mTitleBar.setTitle(R.string.device_center_second);
        return mTitleBar;
    }

    @Override
    protected void initViews() {
//        //设置沉浸式状态栏
//        StatusBarUtils.translucent(mActivity);
//        StatusBarUtils.setStatusBarLightMode(mActivity);
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
//        initFooterView();
        getWatchInfo();
//        getContacts();
        refreshDeviceInfo(null);
        PictureSelectorUtils.initRxPermissions(mActivity);
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getPersonalCenterData(mActivity, mItemList);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new PersonalCenterAdapter(mItemList);
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
     * 初始化头布局
     */
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_personal_center,
                mRecyclerView,
                false);
        headerView.findViewById(R.id.ivPortrait).setOnClickListener(this);
        headerView.findViewById(R.id.ibCamera).setOnClickListener(this);
        mIvPortrait = headerView.findViewById(R.id.ivPortrait);
        mTvName = headerView.findViewById(R.id.tvName);
        mTvId = headerView.findViewById(R.id.tvId);
        mTvPoint = headerView.findViewById(R.id.tvPoint);
        mTvFriend = headerView.findViewById(R.id.tvFriend);
        mTvContacts = headerView.findViewById(R.id.tvContacts);
        mTvFriend.setText("0");
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel == null || TextUtils.isEmpty(settingsModel.getPhonebook()))
            mTvContacts.setText("0");
        else
            mTvContacts.setText(String.valueOf(settingsModel.getPhonebook().split("#").length));
        mAdapter.addHeaderView(headerView);
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
        tvContent.setText(R.string.new_watch_login);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 刷新设备信息
     */
    private void refreshDeviceInfo(DeviceInfoModel infoModel) {
//        DeviceModel deviceModel = getDevice();
//        if (infoModel == null) {
//            UserModel userModel = getUserModel();
//            if (userModel != null && deviceModel != null) {
//                OperatorGroup operatorGroup = OperatorGroup.clause(OperatorGroup.clause()
//                        .and(DeviceInfoModel_Table.u_id.eq(userModel.getU_id()))
//                        .and(DeviceInfoModel_Table.imei.eq(deviceModel.getImei())));
//                infoModel = SQLite.select().from(DeviceInfoModel.class)
//                        .where(operatorGroup)
//                        .querySingle();
//            }
//        }
//        int imgRes;
//        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
//            imgRes = R.mipmap.ic_device_portrait;
//        else
//            imgRes = R.mipmap.ic_name_type_tenth;
//        if (infoModel == null)
//            ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
//                    mIvPortrait);
//        else
//            ImageLoadUtils.loadPortraitImage(getContext(), infoModel.getHead(),
//                    imgRes, mIvPortrait);
//        if (deviceModel == null) {
//            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
//                mTvName.setText(R.string.baby);
//            else
//                mTvName.setText(R.string.device);
//            mTvId.setText(getString(R.string.device_imei, ""));
//        } else {
//            if (infoModel == null || TextUtils.isEmpty(infoModel.getNickname())) {
//                if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
//                    mTvName.setText(R.string.baby);
//                else
//                    mTvName.setText(R.string.device);
//            } else
//                mTvName.setText(infoModel.getNickname());
//            mTvId.setText(getString(R.string.device_imei, deviceModel.getImei()));
//        }
        AAADeviceModel deviceModel = getTrackDeviceModel();
        int imgRes;
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
            imgRes = R.mipmap.ic_device_portrait;
        else
            imgRes = R.mipmap.ic_default_pigeon_marker;
        if (deviceModel == null) {
            ImageLoadUtils.loadPortraitImage(getContext(), "", imgRes,
                    mIvPortrait);
                mTvName.setText(R.string.device);
        } else {
            ImageLoadUtils.loadPortraitImage(getContext(), deviceModel.getHeadPic(),
                    imgRes, mIvPortrait);
            if (TextUtils.isEmpty(deviceModel.getDeviceName())) {
                    mTvName.setText(R.string.device);
            } else
                mTvName.setText(deviceModel.getDeviceName());

            mTvId.setText(getString(R.string.device_imei, deviceModel.getDeviceImei()));
        }
        for (SectionItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null) {
                switch (itemBean.getType()) {
                    case 0: // 手表号码
                        if (deviceModel == null || TextUtils.isEmpty(deviceModel.getBindMobile()))
                            itemBean.setContent(getString(R.string.no_input));
                        else
                            itemBean.setContent(deviceModel.getBindMobile());
                        break;
                    case 1: // 手表二维码
                        break;
                    case 2: // 宝贝信息
                        if (deviceModel == null)
                            itemBean.setContent(getString(R.string.incomplete));
                        else
                            itemBean.setContent("");
                        break;
                    case 3: // 宝贝短号/亲情号
                        if (infoModel == null || TextUtils.isEmpty(infoModel.getShortNumber()))
                            itemBean.setContent(getString(R.string.no_input));
                        else
                            itemBean.setContent(infoModel.getShortNumber());
                        break;
                    default:
                        break;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
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
     * 查看宝贝资料
     */
    private void getWatchInfo() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getWatchInfo(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), deviceModel.getImei(), mHandler);
    }

    /**
     * 获取通讯录
     */
    private void getContacts() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getContacts(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 修改宝贝资料
     */
    private void updateWatchInfo(DeviceInfoModel model) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            if (TextUtils.isEmpty(model.getImei())) {
                model.setImei(deviceModel.getImei());
            }
            model.setU_id(userModel.getU_id());
            CWRequestUtils.getInstance().updateWatchInfo(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), model, mHandler);
        }
    }

    /**
     * 设置设备昵称和头像
     */
    private void setBabyNameAndHead(String headUrl) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
            DeviceInfoModel infoModel = getDeviceInfo();
            CWRequestUtils.getInstance().setBabyNameAndHead(MainApplication.getInstance(),
                    getUserModel().getToken(),
                    String.valueOf(deviceModel.getD_id()), deviceModel.getImei(),
                    infoModel == null || infoModel.getNickname() == null ? "" :
                            infoModel.getNickname(), headUrl, mHandler);
        }
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPortrait:
            case R.id.ibCamera: // 设置头像
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
                                            PictureSelectorUtils.selectPortrait(mActivity,
                                                    PersonalCenterFragment.this, 1);
                                        else if (position == 1)
                                            PictureSelectorUtils.selectPortrait(mActivity,
                                                    PersonalCenterFragment.this, 0);
                                    }
                                })
                                .build();
                        mBottomSheet.show();
                    }
                }
                break;
            case R.id.rootView: // 在新手表上登录
                openNewPage(NewWatchLoginFragment.class);
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
                    case 0: // 手表号码
                        mMaterialDialog =
                                DialogUtils.customMaterialDialog(getContext(),
                                        mMaterialDialog,
                                        getString(R.string.prompt),
                                        getString(R.string.change_device_prompt),
                                        getString(R.string.confirm),
                                        getString(R.string.cancel), null,
                                        CWConstant.DIALOG_CHANGE_MOBILE, mHandler);
                        break;
                    case 1: // 手表二维码
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE,
                                getString(R.string.device_qrcode_new));
                        openNewPage(QRCodeFragment.class, bundle);
                        break;
                    case 2: // 宝贝信息
                        openNewPageForResult(BabyInfoFragment.class, CWConstant.REQUEST_OTHER);
                        break;
                    case 3: // 宝贝短号/亲情号
                        mMaterialDialog =
                                DialogUtils.customMaterialDialog(getContext(),
                                        mMaterialDialog,
                                        getString(R.string.short_number),
                                        getString(R.string.short_number_input_prompt),
                                        getString(R.string.goto_set),
                                        getString(R.string.cancel), null,
                                        CWConstant.DIALOG_CHANGE_SHORT_NUMBER, mHandler);
                        break;
                    default:
                        break;
                }
            }
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
//                            OSSAsyncTask task = CWRequestUtils.getInstance().uploadFile(getDevice(),
//                                    path, mHandler);
                            showDialog();
                            CarGpsRequestUtils.uploadDeviceHeadPortrait(getTrackUserModel(), getTrackDeviceModel().getDeviceImei()
                                    , convertFileToBase64(path), "123456", mHandler);
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

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CWConstant.REQUEST_OTHER: // 其它
                    refreshDeviceInfo(null);
                    break;
                default:
                    break;
            }
        } else if (requestCode == CWConstant.REQUEST_OTHER)
            refreshDeviceInfo(null);
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
                Bundle bundle;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_WATCH_INFO: // 查看宝贝资料
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    DeviceModel deviceModel = getDevice();
                                    DeviceInfoModel infoModel =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()), DeviceInfoModel.class);
                                    if (deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                        infoModel.setU_id(userModel.getU_id());
                                        infoModel.save();
                                        PortraitModel portraitModel = new PortraitModel();
                                        portraitModel.setImei(deviceModel.getImei());
                                        portraitModel.setUserId(deviceModel.getImei());
                                        if (TextUtils.isEmpty(infoModel.getNickname()))
                                            portraitModel.setName(getString(R.string.baby));
                                        else
                                            portraitModel.setName(infoModel.getNickname());
                                        portraitModel.setUrl(infoModel.getHead());
                                        portraitModel.save();
                                        refreshDeviceInfo(infoModel);
                                    }
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_UPDATE_WATCH_INFO: // 修改宝贝资料
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                XToastUtils.toast(R.string.update_success_prompt);
                                DeviceInfoModel infoModel =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), DeviceInfoModel.class);
                                DeviceInfoModel model = getDeviceInfo();
                                if (!TextUtils.isEmpty(model.getImei()) && model.getU_id() != 0) {
                                    infoModel.setU_id(model.getU_id());
                                    infoModel.setImei(model.getImei());
                                    infoModel.setCreatetime(model.getCreatetime());
                                    infoModel.setHome_info(model.getHome_info());
                                    infoModel.setSchool_info(model.getSchool_info());
                                    infoModel.save();
                                }
                                DeviceModel deviceModel = getDevice();
                                if (deviceModel != null) {
                                    PortraitModel portraitModel = new PortraitModel();
                                    portraitModel.setImei(deviceModel.getImei());
                                    portraitModel.setUserId(deviceModel.getImei());
                                    if (infoModel == null || TextUtils.isEmpty(infoModel.getNickname())) {
                                        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                                            portraitModel.setName(getString(R.string.baby));
                                        else
                                            portraitModel.setName(getString(R.string.device));
                                    } else
                                        portraitModel.setName(infoModel.getNickname());
                                    portraitModel.setUrl(infoModel == null ? "" :
                                            infoModel.getHead());
                                    portraitModel.save();
                                    PortraitUtils.getInstance().updatePortrait(portraitModel);
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_CONTACTS: // 获取通讯录
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                RequestBean bean =
                                        mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setPhonebook(bean.getPhonebook());
                                    settingsModel.save();
                                    if (TextUtils.isEmpty(bean.getPhonebook()))
                                        mTvContacts.setText("0");
                                    else
                                        mTvContacts.setText(String.valueOf(bean.getPhonebook().split("#").length));
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_SET_BABY_NAME_AND_HEAD: // 设置设备头像和昵称
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.update_success_prompt);
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                DeviceModel deviceModel = getDevice();
                                if (deviceModel != null && deviceModel.getImei().equals(requestBean.getImei())) {
                                    DeviceInfoModel infoModel = getDeviceInfo();
                                    infoModel.setNickname(requestBean.getNickname());
                                    infoModel.setHead(requestBean.getHeadurl());
                                    infoModel.save();
                                    PortraitModel portraitModel = new PortraitModel();
                                    portraitModel.setImei(deviceModel.getImei());
                                    portraitModel.setUserId(deviceModel.getImei());
                                    if (TextUtils.isEmpty(infoModel.getNickname())) {
                                        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                                            portraitModel.setName(getString(R.string.baby));
                                        else
                                            portraitModel.setName(getString(R.string.device));
                                    } else
                                        portraitModel.setName(infoModel.getNickname());
                                    portraitModel.setUrl(infoModel.getHead());
                                    portraitModel.save();
                                    PortraitUtils.getInstance().updatePortrait(portraitModel);
                                }
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
                            DeviceInfoModel infoModel = getDeviceInfo();
                            if (!TextUtils.isEmpty(infoModel.getHead()))
                                CWRequestUtils.getInstance().deleteFile(infoModel.getHead());
                            infoModel.setHead((String) msg.obj);
                            int imgRes;
                            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                                imgRes = R.mipmap.ic_device_portrait;
                            else
                                imgRes = R.mipmap.ic_default_pigeon_marker;
                            ImageLoadUtils.loadPortraitImage(getContext(), infoModel.getHead(),
                                    imgRes, mIvPortrait);
                            setBabyNameAndHead(infoModel.getHead());
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_CHANGE_MOBILE: // 修改手机号码
                                bundle = new Bundle();
                                bundle.putString(CWConstant.TITLE,
                                        getString(R.string.phone_number));
                                bundle.putString(CWConstant.CONTENT,
                                        getDeviceInfo().getPhone());
                                bundle.putInt(CWConstant.TYPE, 3);
                                openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                        CWConstant.REQUEST_OTHER);
                                break;
                            case CWConstant.DIALOG_CHANGE_SHORT_NUMBER: // 修改短号/亲情号
                                bundle = new Bundle();
                                bundle.putInt(CWConstant.TYPE, 1);
                                bundle.putString(CWConstant.TITLE,
                                        getString(R.string.short_number));
                                bundle.putString(CWConstant.CONTENT,
                                        getDeviceInfo().getShortNumber());
                                openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                        CWConstant.REQUEST_OTHER);
                                break;
                            default:
                                break;
                        }
                        break;
                    case TConstant.REQUEST_UPLOAD_DEVICE_HEAD_PORTRAIT:  //update head portrait of device
                        if (msg.obj == null) {
//                            KLog.d("uploadImageFileFailed");
                            showMessage(R.string.network_error_prompt);
                        } else {
                            try {
                                AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                                if (response.getCode() == TConstant.RESPONSE_SUCCESS
                                        || response.getCode() == TConstant.RESPONSE_SUCCESS_NEW) {
//                                    KLog.d("uploadImageFileSucceed");
                                    String imgUrl = response.getMsg();
                                    if (imgUrl != null) {
                                        AAADeviceModel deviceModel = getTrackDeviceModel();
                                        List<AAADeviceModel> deviceList = new ArrayList<>();
                                        List<AAADeviceModel> trackDeviceList = getTrackDeviceList();
                                        deviceModel.setHeadPic(imgUrl);
                                        MainApplication.getInstance().setTrackDeviceModel(deviceModel);
                                        for (int i = 0; i < trackDeviceList.size(); i++) {
                                            if (deviceModel.getDeviceImei().equals(trackDeviceList.get(i).getDeviceImei()))
                                                deviceList.add(deviceModel);
                                            else
                                                deviceList.add(trackDeviceList.get(i));
                                        }
                                        MainApplication.getInstance().setTrackDeviceList(deviceList);
                                        Glide.with(mActivity).load(imgUrl).into(mIvPortrait);
                                    }
                                    dismisDialog();
                                    showMessage(R.string.update_success_prompt);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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
    public void onDestroy() {
        DialogUtils.dismiss(mBottomSheet);
        super.onDestroy();
    }

    public static String convertFileToBase64(String imgPath) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
//            System.out.println("文件大小（字节）=" + in.available());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组进行Base64编码，得到Base64编码的字符串
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

}
