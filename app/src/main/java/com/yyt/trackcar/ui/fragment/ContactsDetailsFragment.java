package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionMultiItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.ContactsDetailsAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.PictureSelectorUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      ContactsDetailsFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 09:29
 * @ describe:      TODO 通讯录详情页面
 */
@Page(name = "ContactsDetails", params = {CWConstant.TYPE, CWConstant.LIST, CWConstant.MODEL})
public class ContactsDetailsFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private ContactsDetailsAdapter mAdapter; // 适配器
    private List<SectionMultiItem> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    String list; // 列表
    @AutoWired
    String model; // 对象
    @AutoWired
    int type; // 类型 0联系人 1SOS
    private boolean mIsDel; // 是否删除
    private boolean mIsEdit; // 是否编辑
    private BottomSheet mBottomSheet; // 选项弹窗

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.details);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initFooterView();
        PictureSelectorUtils.initRxPermissions(mActivity);
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getContactsDetailsData(mActivity, mItemList);
        String[] array = null;
        if (!TextUtils.isEmpty(model)) {
            array = model.split("\\|");
            if (array.length < 4)
                array = null;
        }
        if (array == null)
            array = "|||0".split("\\|");
        for (SectionMultiItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null) {
                switch (itemBean.getType()) {
                    case 0: // 头像
                        if (array.length >= 6)
                            itemBean.setGroup(array[5]);
                        int portrait = 0;
                        try {
                            portrait = Integer.parseInt(array[3]);
                        } catch (NumberFormatException e) {
                            if (BuildConfig.DEBUG)
                                e.printStackTrace();
                        }
                        setPortrait(itemBean, portrait);
                        break;
                    case 1: // 名称
                        String name = array[0];
                        if (TextUtils.isEmpty(name))
                            itemBean.setContent(getString(R.string.no_input));
                        else
                            itemBean.setContent(name);
                        itemBean.setGroup(name);
                        break;
                    case 2: // 手机号码
                        String phone = array[1];
                        if (TextUtils.isEmpty(phone))
                            itemBean.setContent(getString(R.string.no_input));
                        else
                            itemBean.setContent(phone);
                        itemBean.setGroup(phone);
                        break;
                    case 3: // 短号/亲情号
                        String shortNumber = array[2];
                        if (TextUtils.isEmpty(shortNumber))
                            itemBean.setContent(getString(R.string.no_input));
                        else
                            itemBean.setContent(shortNumber);
                        itemBean.setGroup(shortNumber);
                        break;
                    case 4: // 紧急通话
                        itemBean.setSelect(type == 1);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new ContactsDetailsAdapter(mItemList);
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
        tvContent.setText(R.string.del);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 设置头像
     *
     * @param itemBean 对象
     * @param type     头像类型
     */
    private void setPortrait(BaseItemBean itemBean, int type) {
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
            switch (type) {
                case 0: // 其他
                    itemBean.setImgDrawable(R.mipmap.ic_default_pigeon_marker);
                    break;
                case 1:
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_eleventh);
                    break;
                case 2:
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_twelfth);
                    break;
                case 3:
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_thirteenth);
                    break;
                case 4:
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_fourteenth);
                    break;
                case 5:
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_fifteenth);
                    break;
                default:
                    itemBean.setImgDrawable(R.mipmap.ic_default_pigeon_marker);
                    break;
            }
        } else {
            switch (type) {
                case 0: // 其他
                    itemBean.setImgDrawable(R.mipmap.ic_default_pigeon_marker);
                    break;
                case 1: // 爸爸
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_first);
                    break;
                case 2: // 妈妈
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_second);
                    break;
                case 3: // 爷爷
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_fourth);
                    break;
                case 4: // 奶奶
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_fifth);
                    break;
                case 5: // 外公
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_seventh);
                    break;
                case 6: // 外婆
                    itemBean.setImgDrawable(R.mipmap.ic_name_type_eighth);
                    break;
                default:
                    break;
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
     * 编辑联系人
     */
    @SuppressLint("DefaultLocale")
    private String editContacts() {
        if (!TextUtils.isEmpty(list)) {
            String[] contactArray = new String[]{"", "", "", "0", "0", ""};
            for (SectionMultiItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null) {
                    switch (itemBean.getType()) {
                        case 0: // 头像
                            int portrait = 0;
                            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
                                switch (itemBean.getImgDrawable()) {
                                    case R.mipmap.ic_default_pigeon_marker: // 其他
                                        portrait = 0;
                                        break;
                                    case R.mipmap.ic_name_type_eleventh:
                                        portrait = 1;
                                        break;
                                    case R.mipmap.ic_name_type_twelfth:
                                        portrait = 2;
                                        break;
                                    case R.mipmap.ic_name_type_thirteenth:
                                        portrait = 3;
                                        break;
                                    case R.mipmap.ic_name_type_fourteenth:
                                        portrait = 4;
                                        break;
                                    case R.mipmap.ic_name_type_fifteenth:
                                        portrait = 5;
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                switch (itemBean.getImgDrawable()) {
                                    case R.mipmap.ic_default_pigeon_marker: // 其他
                                        portrait = 0;
                                        break;
                                    case R.mipmap.ic_name_type_first: // 爸爸
                                        portrait = 1;
                                        break;
                                    case R.mipmap.ic_name_type_second: // 妈妈
                                        portrait = 2;
                                        break;
                                    case R.mipmap.ic_name_type_fourth: // 爷爷
                                        portrait = 3;
                                        break;
                                    case R.mipmap.ic_name_type_fifth: // 奶奶
                                        portrait = 4;
                                        break;
                                    case R.mipmap.ic_name_type_seventh: // 外公
                                        portrait = 5;
                                        break;
                                    case R.mipmap.ic_name_type_eighth: // 外婆
                                        portrait = 6;
                                        break;
                                    default:
                                        break;
                                }
                            }
                            if (TextUtils.isEmpty(itemBean.getGroup()))
                                contactArray[3] = String.valueOf(portrait);
                            else
                                contactArray[3] = "0";
                            contactArray[5] = itemBean.getGroup();
                            break;
                        case 1: // 名称
                            String name = itemBean.getGroup();
                            if (TextUtils.isEmpty(name)) {
                                XToastUtils.toast(String.format("%s%s",
                                        getString(R.string.no_input), itemBean.getTitle()));
                                return null;
                            }
                            contactArray[0] = name;
                            break;
                        case 2: // 手机号码
                            String phone = itemBean.getGroup();
                            if (TextUtils.isEmpty(phone)) {
                                XToastUtils.toast(String.format("%s%s",
                                        getString(R.string.no_input), itemBean.getTitle()));
                                return null;
                            }
                            contactArray[1] = phone;
                            break;
                        case 3: // 短号/亲情号
                            String shortNumber = itemBean.getGroup();
                            contactArray[2] = shortNumber;
                            break;
                        case 4: // 紧急通话
                            contactArray[4] = itemBean.isSelect() ? "1" : "0";
                            break;
                        default:
                            break;
                    }
                }
            }
            String[] array = list.split("#");
            StringBuilder contactString = new StringBuilder();
            for (String str : array) {
                if ("0".equals(str) && TextUtils.isEmpty(contactArray[5]))
                    contactString.append("#").append(String.format("%s|%s|%s|%s|%s", contactArray[0]
                            , contactArray[1], contactArray[2], contactArray[3], contactArray[4]));
                else if ("0".equals(str))
                    contactString.append("#").append(String.format("%s|%s|%s|%s|%s|%s",
                            contactArray[0], contactArray[1], contactArray[2], contactArray[3],
                            contactArray[4], contactArray[5]));
                else {
                    String[] subArray = str.split("\\|");
                    if (subArray.length >= 4) {
                        if (contactArray[3].equals(subArray[3]) && !"0".equals(subArray[3])) {
                            XToastUtils.toast(R.string.contacts_type_equal_prompt);
                            return null;
                        } else if (contactArray[0].equals(subArray[0])) {
                            XToastUtils.toast(R.string.contacts_name_equal_prompt);
                            return null;
                        } else if (contactArray[1].equals(subArray[1])) {
                            XToastUtils.toast(R.string.contacts_phone_equal_prompt);
                            return null;
                        }
                    }
                    contactString.append("#").append(str);
                }
            }
            if (contactString.length() == 0) {
                setContacts("");
                return "";
            } else {
                setContacts(contactString.substring(1));
                return contactString.substring(1);
            }
        }
        return null;
    }

    /**
     * 设置通讯录
     *
     * @param contactString 通讯录
     */
    private void setContacts(String contactString) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().setContacts(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getImei(), deviceModel.getD_id(),
                    contactString, mHandler);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rootView: // 删除
                mIsDel = true;
                if (!TextUtils.isEmpty(list)) {
                    String[] array = list.split("#");
                    StringBuilder contactString = new StringBuilder();
                    for (String str : array) {
                        if (!"0".equals(str))
                            contactString.append("#").append(str);
                    }
                    if (contactString.length() == 0)
                        setContacts("");
                    else
                        setContacts(contactString.substring(1));
                }
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
                    case 0: // 头像
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
                                                    PictureSelectorUtils.selectPortrait(mActivity
                                                            , ContactsDetailsFragment.this, 1);
                                                else if (position == 1)
                                                    PictureSelectorUtils.selectPortrait(mActivity
                                                            , ContactsDetailsFragment.this, 0);
                                            }
                                        })
                                        .build();
                                mBottomSheet.show();
                            }
                        }
                        break;
                    case 1: // 名称
                        openNewPageForResult(SelectNameFragment.class,
                                CWConstant.REQUEST_SELECT_NAME);
                        break;
                    case 2: // 修改手机号码
                        bundle = new Bundle();
                        bundle.putString(CWConstant.TITLE,
                                getString(R.string.phone_number));
                        bundle.putInt(CWConstant.TYPE, 7);
                        bundle.putString(CWConstant.CONTENT, itemBean.getGroup());
                        openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                CWConstant.REQUEST_CONTACTS_PHONE);
                        break;
                    case 3: // 修改短号/亲情号
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 6);
                        bundle.putString(CWConstant.TITLE,
                                getString(R.string.short_number));
                        bundle.putString(CWConstant.CONTENT, itemBean.getGroup());
                        openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                CWConstant.REQUEST_CONTACTS_EDIT_SHORT_NUMBER);
                        break;
                    case 4: // 紧急通话
                        String[] array = list.split("#");
                        int sosNum = 0;
                        for (String str : array) {
                            String[] subArray = str.split("\\|");
                            if (subArray.length >= 5 && "1".equals(subArray[4]))
                                sosNum++;
                        }
                        if (!itemBean.isSelect() && sosNum >= 3)
                            mMaterialDialog = DialogUtils.customMaterialDialog(mActivity,
                                    mMaterialDialog, getString(R.string.prompt),
                                    getString(R.string.sos_number_to_max_prompt),
                                    getString(R.string.i_know));
                        else {
                            itemBean.setSelect(!itemBean.isSelect());
                            mAdapter.notifyItemChanged(position);
                            String contactsString = editContacts();
                            if (contactsString != null && NetworkUtils.isNetworkAvailable()) {
                                DeviceSettingsModel settingsModel = getDeviceSettings();
                                if (settingsModel != null) {
                                    settingsModel.setPhonebook(contactsString);
                                    settingsModel.save();
                                }
                            }
                        }
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
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult
                            (data);
                    for (LocalMedia localMedia : selectList) {
                        String path;
                        if (localMedia.isCompressed())
                            path = localMedia.getCompressPath();
                        else if (localMedia.isCut())
                            path = localMedia.getCutPath();
                        else
                            path = localMedia.getPath();
                        if (!TextUtils.isEmpty(path)) {
//                            OSSAsyncTask task = CWRequestUtils.getInstance().uploadFile
//                            (getDevice(),
//                                    path, mHandler);
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
        if (resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (SectionMultiItem item : mItemList) {
                    BaseItemBean itemBean = item.t;
                    if (itemBean != null) {
                        switch (itemBean.getType()) {
                            case 0: // 头像
                                if (requestCode == CWConstant.REQUEST_SELECT_NAME) {
                                    int portrait = bundle.getInt(CWConstant.TYPE);
                                    setPortrait(itemBean, portrait);
                                }
                                break;
                            case 1: // 名称
                                if (requestCode == CWConstant.REQUEST_SELECT_NAME) {
                                    String name = bundle.getString(CWConstant.NAME);
                                    if (TextUtils.isEmpty(name))
                                        itemBean.setContent(getString(R.string.no_input));
                                    else
                                        itemBean.setContent(name);
                                    itemBean.setGroup(name);
                                }
                                break;
                            case 2: // 修改手机号码
                                if (requestCode == CWConstant.REQUEST_CONTACTS_PHONE) {
                                    String phone = bundle.getString(CWConstant.NAME);
                                    if (TextUtils.isEmpty(phone))
                                        itemBean.setContent(getString(R.string.no_input));
                                    else
                                        itemBean.setContent(phone);
                                    itemBean.setGroup(phone);
                                }
                                break;
                            case 3: // 修改短号/亲情号
                                if (requestCode == CWConstant.REQUEST_CONTACTS_EDIT_SHORT_NUMBER) {
                                    String shortNumber = bundle.getString(CWConstant.NAME);
                                    if (TextUtils.isEmpty(shortNumber))
                                        itemBean.setContent(getString(R.string.no_input));
                                    else
                                        itemBean.setContent(shortNumber);
                                    itemBean.setGroup(shortNumber);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                editContacts();
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
                    case CWConstant.REQUEST_URL_SET_CONTACTS: // 设置通讯录
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) && !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setIp(resultBean.getLast_online_ip());
                                    settingsModel.save();
                                    if (!NetworkUtils.isNetworkAvailable()) {
                                        RequestToastUtils.toastNetwork();
                                        return false;
                                    }
                                    CWRequestUtils.getInstance().setContacts(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getPhonebook(),
                                            mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS || resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE) {
                                if (resultBean.getCode() == CWConstant.WAIT_ONLINE_UPDATE)
                                    XToastUtils.toast(R.string.wait_online_update_prompt);
                                else
                                    XToastUtils.toast(R.string.send_success_prompt);
                                userModel = getUserModel();
                                DeviceModel deviceModel = getDevice();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setPhonebook(requestBean.getPhonebook());
                                    settingsModel.save();
                                    mIsEdit = true;
                                }
                                if (mIsDel)
                                    popToBack();
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_UPLOAD_IMAGE: // 上传图片
                        if (mMaterialDialog != null)
                            mMaterialDialog.dismiss();
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.upload_file_error_prompt);
                        else {
                            for (SectionMultiItem item : mItemList) {
                                BaseItemBean itemBean = item.t;
                                if (itemBean != null && itemBean.getType() == 0) {
                                    if (!TextUtils.isEmpty(itemBean.getGroup()))
                                        CWRequestUtils.getInstance().deleteFile(itemBean.getGroup());
                                    itemBean.setGroup((String) msg.obj);
                                    mAdapter.notifyDataSetChanged();
                                    editContacts();
                                    break;
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
    public void popToBack() {
        if (mIsEdit || mIsDel) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            setFragmentResult(Activity.RESULT_OK, intent);
        }
        super.popToBack();
    }

    @Override
    public void onDestroy() {
        DialogUtils.dismiss(mBottomSheet);
        super.onDestroy();
    }

}
