package com.yyt.trackcar.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.EmojiFilter;
import com.yyt.trackcar.utils.PermissionUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      ContactsAddFragment
 * @ author:        QING
 * @ createTime:    2020/3/10 17:59
 * @ describe:      TODO 添加联系人页面
 */
@Page(name = "ContactsAdd", params = {CWConstant.LIST})
public class ContactsAddFragment extends BaseFragment {
    @BindView(R.id.tvSelectName)
    TextView mTvName; // 名称文本
    @BindView(R.id.etMobile)
    EditText mEtMobile; // 手机号码文本编辑
    @BindView(R.id.etShortNumber)
    EditText mEtShortNumber; // 短号/亲情号文本编辑
    @BindView(R.id.clAddShortNumber)
    View mClAddShortNumber; // 短号/亲情号添加
    @BindView(R.id.clShortNumber)
    View mClShortNumber; // 短号/亲情号编辑
    @BindView(R.id.ivCheck)
    ImageView mIvCheck; // 是否开启守护权限
    @AutoWired
    String list; // 列表
    private int mType; // 头像类型
    private int sosNum = -1; // SOS人数

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contacts_add;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        if (type == 1)
//            titleBar.setTitle(R.string.add_sos_number_title);
//        else
        titleBar.setTitle(R.string.add_contacts);
        return titleBar;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected void initViews() {
        if(TextUtils.isEmpty(list))
            mIvCheck.setSelected(true);
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

    @SuppressLint("DefaultLocale")
    @SingleClick
    @OnClick({R.id.clName, R.id.ibShortNumber, R.id.ibMobile, R.id.saveBtn, R.id.clAddShortNumber
            , R.id.guardView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clName: // 选择名称
                openNewPageForResult(SelectNameFragment.class,
                        CWConstant.REQUEST_SELECT_NAME);
                break;
            case R.id.ibShortNumber: // 选择短号/亲情号
                PermissionUtils.checkAndRequestPermission(mActivity, Manifest.permission
                                .READ_CONTACTS,
                        CWConstant.REQUEST_PERMISSION_CONTACTS_SHORT_NUMBER, new PermissionUtils
                                .PermissionRequestSuccessCallBack() {
                            @Override
                            public void onHasPermission() {
                                // 权限已被授予
                                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                        ContactsContract.Contacts.CONTENT_URI), CWConstant
                                        .REQUEST_CONTACTS_SHORT_NUMBER);
                            }
                        });
                break;
            case R.id.ibMobile: // 选择手机号码
                PermissionUtils.checkAndRequestPermission(mActivity, Manifest.permission
                                .READ_CONTACTS,
                        CWConstant.REQUEST_PERMISSION_CONTACTS_MOBILE, new PermissionUtils
                                .PermissionRequestSuccessCallBack() {
                            @Override
                            public void onHasPermission() {
                                // 权限已被授予
                                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                        ContactsContract.Contacts.CONTENT_URI), CWConstant
                                        .REQUEST_CONTACTS_MOBILE);
                            }
                        });
                break;
            case R.id.clAddShortNumber: // 添加短号/亲情号
                mClAddShortNumber.setEnabled(false);
                mClAddShortNumber.setVisibility(View.INVISIBLE);
                mClShortNumber.setVisibility(View.VISIBLE);
                mMaterialDialog = DialogUtils.customMaterialDialog(getContext(), mMaterialDialog,
                        getString(R.string.short_number),
                        getString(R.string.short_number_input_prompt), getString(R.string.i_know));
                break;
            case R.id.guardView:  // 守护权限
                if (sosNum == -1) {
                    if (TextUtils.isEmpty(list))
                        sosNum = 0;
                    else {
                        int num = 0;
                        String[] array = list.split("#");
                        for (String str : array) {
                            String[] subArray = str.split("\\|");
                            if (subArray.length >= 5 && "1".equals(subArray[4]))
                                num++;
                        }
                        sosNum = num;
                    }
                }
                if (sosNum >= 3) {
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                            mMaterialDialog,
                            getString(R.string.prompt),
                            getString(R.string.sos_number_to_max_prompt),
                            getString(R.string.i_know));
                    return;
                } else
                    mIvCheck.setSelected(!mIvCheck.isSelected());
                break;
            case R.id.saveBtn: // 保存
                String name = mTvName.getText().toString().trim();
                String mobile = mEtMobile.getText().toString().trim();
                String shortNumber = mEtShortNumber.getText().toString().trim();
                if (TextUtils.isEmpty(name) || EmojiFilter.containsEmoji(name))
                    XToastUtils.toast(R.string.custom_name_hint);
                else if (TextUtils.isEmpty(mobile)) {
                    XToastUtils.toast(mEtMobile.getHint().toString());
                    mEtMobile.requestFocus();
                } else if (TextUtils.isEmpty(list))
                    setContacts(String.format("%s|%s|%s|%d|%s", name, mobile, shortNumber, mType,
                            mIvCheck.isSelected() ? "1" : "0"));
                else {
                    String[] array = list.split("#");
                    boolean isSameName = false;
                    String contactString = "";
                    for (String str : array) {
                        String[] subArray = str.split("\\|");
                        if (subArray.length >= 4) {
                            if (name.equals(subArray[0]))
                                isSameName = true;
                            else if (String.valueOf(mType).equals(subArray[3]) && mType != 0) {
                                XToastUtils.toast(R.string.contacts_type_equal_prompt);
                                return;
                            } else if (mobile.equals(subArray[1])) {
                                XToastUtils.toast(R.string.contacts_phone_equal_prompt);
                                return;
                            }
                            if (name.equals(subArray[0]))
                                contactString = String.format("%s#%s|%s|%s|%d|%s", contactString, name, mobile, shortNumber,
                                        mType, mIvCheck.isSelected() ? "1" : "0");
                            else
                                contactString = String.format("%s#%s", contactString, str);
                        }
                    }
                    if(isSameName)
                        mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                                mMaterialDialog, getString(R.string.prompt),
                                getString(R.string.contacts_name_equal_edit_prompt),
                                getString(R.string.confirm), getString(R.string.cancel), contactString,
                                CWConstant.DIALOG_EQUAL_EDIT_CONTACTS, mHandler);
                    else
                        setContacts(String.format("%s#%s|%s|%s|%d|%s", list, name, mobile, shortNumber,
                                mType, mIvCheck.isSelected() ? "1" : "0"));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CWConstant.REQUEST_CONTACTS_MOBILE: // 通讯录
            case CWConstant.REQUEST_CONTACTS_SHORT_NUMBER:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ContentResolver reContentResolverol = mActivity.getContentResolver();
                    Uri contactData = data.getData();
                    @SuppressWarnings("deprecation")
                    Cursor cursor = mActivity.managedQuery(contactData, null, null, null, null);
                    cursor.moveToFirst();
//                    String username = cursor.getString(cursor.getColumnIndex(ContactsContract
//                            .Contacts.DISPLAY_NAME));
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract
                            .Contacts._ID));
                    @SuppressLint("Recycle")
                    Cursor phone = reContentResolverol.query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                                            contactId, null, null);
                    if (phone != null) {
                        while (phone.moveToNext()) {
                            String usernumber = phone.getString(phone.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (requestCode == CWConstant.REQUEST_CONTACTS_MOBILE)
                                mEtMobile.setText(usernumber);
                            else
                                mEtShortNumber.setText(usernumber);
                        }
                    }
                }
                break;
            default:  // 其它
                break;
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_SELECT_NAME && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                mTvName.setText(bundle.getString(CWConstant.NAME));
                mType = bundle.getInt(CWConstant.TYPE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CWConstant.REQUEST_PERMISSION_CONTACTS_MOBILE: // 通讯录 手机号码
                if (PermissionUtils.isPermissionRequestSuccess(grantResults))
                    // 权限申请成功
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                            ContactsContract.Contacts.CONTENT_URI), CWConstant
                            .REQUEST_CONTACTS_MOBILE);
                else
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                            mMaterialDialog, getString(R.string.prompt),
                            getString(R.string.no_contact_permission_prompt),
                            getString(R.string.confirm));
                break;
            case CWConstant.REQUEST_PERMISSION_CONTACTS_SHORT_NUMBER: // 通讯录 短号/亲情号
                if (PermissionUtils.isPermissionRequestSuccess(grantResults))
                    // 权限申请成功
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                            ContactsContract.Contacts.CONTENT_URI), CWConstant
                            .REQUEST_CONTACTS_SHORT_NUMBER);
                else
                    mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
                            mMaterialDialog, getString(R.string.prompt),
                            getString(R.string.no_contact_permission_prompt),
                            getString(R.string.confirm));
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
                    case CWConstant.REQUEST_URL_SET_CONTACTS: // 设置通讯录
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (!TextUtils.isEmpty(resultBean.getService_ip()) &&
                                    !resultBean.getService_ip().equals(resultBean.getLast_online_ip())) {
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
                                }
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.LIST, requestBean.getPhonebook());
                                intent.putExtras(bundle);
                                setFragmentResult(Activity.RESULT_OK, intent);
                                popToBack();
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION: // 确认
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_EQUAL_EDIT_CONTACTS: // 更改编辑联系人
                                setContacts((String) msg.obj);
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
}
