package com.yyt.trackcar.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.ContactBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.AddressBookAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      AutomaticConnectionFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 15:50
 * @ describe:      TODO 自动接通界面
 */
@Page(name = "AutomaticConnection")
public class AutomaticConnectionFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private AddressBookAdapter mAdapter; // 适配器
    private List<ContactBean> mItemList = new ArrayList<>(); // 列表
    private boolean mIsOpen; // 是否开启

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view_btn;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.automatic_connection);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        initFooterView();
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel != null)
            mIsOpen = "1".equals(settingsModel.getAutomaticAnswer());
        setSwitch();
        getAutomaticAnswer();
        getContacts();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceSettingsModel settingsModel = getDeviceSettings();
        if (settingsModel != null && !TextUtils.isEmpty(settingsModel.getPhonebook())) {
            String[] array = settingsModel.getPhonebook().split("#");
            for (String str : array) {
                addItem(str);
            }
        }
        if (mItemList.size() == 1)
            mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
        else if (mItemList.size() > 1) {
            mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
            mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new AddressBookAdapter(mItemList);
        mAdapter.setType(-1);
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
        View headerView = getLayoutInflater().inflate(R.layout.header_view_info_second,
                mRecyclerView, false);
        ImageView ivBg = headerView.findViewById(R.id.ivBg);
        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
        TextView tvContent = headerView.findViewById(R.id.tvContent);
        tvTitle.setText(R.string.automatic_connection_title);
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0) {
            tvContent.setText(R.string.automatic_connection_content);
            ivBg.setImageResource(R.mipmap.bg_automatic_connection);
        } else {
            tvContent.setText(R.string.automatic_connection_devie_content);
            ivBg.setImageResource(R.mipmap.bg_automatic_connection_second);
        }
        mAdapter.addHeaderView(headerView);
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section, mRecyclerView,
                false);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 添加选项
     */
    private void addItem(String contactString) {
        String[] array = contactString.split("\\|");
        if (array.length >= 4) {
            ContactBean contactBean = new ContactBean();
            contactBean.setContactString(contactString);
            contactBean.setName(array[0]);
            contactBean.setPhone(array[1]);
            contactBean.setShortNumber(array[2]);
            if (array.length >= 6)
                contactBean.setPortrait(array[5]);
            int type = 0;
            try {
                type = Integer.parseInt(array[3]);
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            switch (type) {
                case 0: // 其他
                    contactBean.setPortraitId(R.mipmap.ic_default_pigeon_marker);
                    break;
                case 1: // 爸爸
                    contactBean.setPortraitId(R.mipmap.ic_name_type_first);
                    break;
                case 2: // 妈妈
                    contactBean.setPortraitId(R.mipmap.ic_name_type_second);
                    break;
                case 3: // 爷爷
                    contactBean.setPortraitId(R.mipmap.ic_name_type_fourth);
                    break;
                case 4: // 奶奶
                    contactBean.setPortraitId(R.mipmap.ic_name_type_fifth);
                    break;
                case 5: // 外公
                    contactBean.setPortraitId(R.mipmap.ic_name_type_seventh);
                    break;
                case 6: // 外婆
                    contactBean.setPortraitId(R.mipmap.ic_name_type_eighth);
                    break;
                default:
                    break;
            }
            mItemList.add(contactBean);
        }
    }

    /**
     * 设置开关
     */
    public void setSwitch() {
        if (mIsOpen)
            mSwitchBtn.setText(getString(R.string.close_status));
        else
            mSwitchBtn.setText(getString(R.string.open_status));
    }

    /**
     * 获取自动接听的状态
     */
    public void getAutomaticAnswer() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getAutomaticAnswer(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 自动接听的打开关闭
     */
    private void automaticAnswer() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().automaticAnswer(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), deviceModel.getImei(), mIsOpen ?
                            "1" : "0", mHandler);
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

    @SingleClick(1000)
    @OnClick({R.id.switchBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchBtn: // 开启/关闭
                mIsOpen = !mIsOpen;
                setSwitch();
                automaticAnswer();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        if (position >= 0 && position < mItemList.size()) {
//            ContactBean contactBean = mItemList.get(position);
//            Bundle bundle = new Bundle();
//            StringBuilder contactString = new StringBuilder();
//            for (int i = 0; i < mItemList.size(); i++) {
//                ContactBean item = mItemList.get(i);
//                if (i == position)
//                    contactString.append("#0");
//                else
//                    contactString.append("#").append(item.getContactString());
//            }
//            bundle.putString(CWConstant.LIST,
//                    contactString.toString().substring(1));
//            bundle.putString(CWConstant.MODEL,
//                    contactBean.getContactString());
//            String[] array = contactBean.getContactString().split("\\|");
//            if (array.length >= 5 && "1".equals(array[4]))
//                bundle.putInt(CWConstant.TYPE, 1);
//            else
//                bundle.putInt(CWConstant.TYPE, 0);
//            openNewPageForResult(ContactsDetailsFragment.class, bundle,
//                    CWConstant.REQUEST_CONTACTS_EDIT);
//        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == CWConstant.REQUEST_CONTACTS_EDIT) {
            mItemList.clear();
            initItems();
            mAdapter.notifyDataSetChanged();
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
                    case CWConstant.REQUEST_URL_GET_AUTOMATIC_ANSWER: // 获取自动接听的状态
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
                                    settingsModel.setAutomaticAnswer(bean.getType());
                                    settingsModel.save();
                                    mIsOpen = "1".endsWith(bean.getType());
                                    setSwitch();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_AUTOMATIC_ANSWER: // 自动接听的打开关闭
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
                                    CWRequestUtils.getInstance().setOther(getContext(),
                                            resultBean.getLast_online_ip(),
                                            requestBean.getToken(), requestBean.getImei(),
                                            requestBean.getD_id(), requestBean.getOther(),
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
                                    settingsModel.setAutomaticAnswer(requestBean.getType());
                                    settingsModel.save();
                                }
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else
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
                                }
                                mItemList.clear();
                                if (!TextUtils.isEmpty(bean.getPhonebook())) {
                                    String[] array = bean.getPhonebook().split("#");
                                    for (String str : array) {
                                        addItem(str);
                                    }
                                }
                                if (mItemList.size() == 1)
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_item_round_selector);
                                else if (mItemList.size() > 1) {
                                    mItemList.get(0).setBgDrawable(R.drawable.btn_custom_top_radius);
                                    mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                }
                                mAdapter.notifyDataSetChanged();
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
}
