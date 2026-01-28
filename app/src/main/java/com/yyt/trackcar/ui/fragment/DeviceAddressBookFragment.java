package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xutil.display.DensityUtils;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.ContactBean;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.data.DataServer;
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

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      DeviceAddressBookFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 17:39
 * @ describe:      TODO 宝贝通讯录页面
 */
@Page(name = "DeviceAddressBook")
public class DeviceAddressBookFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout; // 下拉加载控件
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private AddressBookAdapter mAdapter; // 适配器
    private ArrayList<ContactBean> mItemList = new ArrayList<>(); // 列表
    private XUISimplePopup mMenuPopup; // 弹出菜单

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1)
            titleBar.setTitle(R.string.address_book);
        else
            titleBar.setTitle(R.string.device_address_book);
        titleBar.addAction(new TitleBar.ImageAction(R.mipmap.ic_add_contacts) {
            @Override
            public void performAction(View view) {
                if (mMenuPopup == null)
                    initMenuPopup();
                mMenuPopup.showDown(view);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mRefreshLayout.setBackgroundResource(R.color.colorPrimary);
        mRefreshLayout.setEnableLoadMore(false);
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderAndFooterView();
        initEmptyView();
        getContacts();
    }

    @Override
    protected void initListeners() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtils.isNetworkAvailable()) {
                    RequestToastUtils.toastNetwork();
                    mRefreshLayout.finishRefresh();
                    return;
                }
                getContacts();
            }
        });
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
     * 初始化头脚布局
     */
    private void initHeaderAndFooterView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView,
                false);
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section,
                mRecyclerView,
                false);
        mAdapter.addHeaderView(headerView);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 初始化空布局
     */
    private void initEmptyView() {
        View emptyView = getLayoutInflater().inflate(R.layout.layout_empty_view,
                mRecyclerView, false);
        emptyView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color
                .white));
        ImageView ivEmpty = emptyView.findViewById(R.id.ivEmpty);
        TextView tvEmpty = emptyView.findViewById(R.id.tvEmpty);
        ivEmpty.setImageResource(R.mipmap.ic_no_query_data);
        tvEmpty.setText(R.string.no_contacts_prompt);
        mAdapter.setEmptyView(emptyView);
    }

    /**
     * 初始化弹出菜单
     */
    private void initMenuPopup() {
        mMenuPopup = new XUISimplePopup(mActivity, DataServer.getAddressBookMenuData(mActivity))
                .create(DensityUtils.dip2px(224), DensityUtils.dip2px(240),
                        new XUISimplePopup.OnPopupItemClickListener() {
                            @Override
                            public void onItemClick(XUISimpleAdapter adapter, AdapterItem item,
                                                    int position) {
                                if (mItemList.size() >= 15 && position == 0)
                                    XToastUtils.toast(R.string.contacts_to_max_prompt);
                                else {
                                    Bundle bundle = new Bundle();
//                                    int sosNum = 0;
                                    if (mItemList.size() > 0) {
                                        StringBuilder contactString = new StringBuilder();
                                        for (ContactBean contactBean : mItemList) {
//                                            String[] array =
//                                                    contactBean.getContactString().split("\\|");
//                                            if (array.length >= 5 && "1".equals(array[4]))
//                                                sosNum++;
                                            contactString.append("#").append(contactBean.getContactString());
                                        }
                                        bundle.putString(CWConstant.LIST,
                                                contactString.toString().substring(1));
                                    }
                                    switch (position) {
                                        case 0: // 添加联系人
//                                            bundle.putInt(CWConstant.TYPE, 0);
                                            openNewPageForResult(ContactsAddFragment.class,
                                                    bundle,
                                                    CWConstant.REQUEST_CONTACTS_ADD);
                                            break;
//                                        case 1: // 添加SOS
//                                            if (sosNum >= 3)
//                                                XToastUtils.toast(R.string
//                                                .sos_number_to_max_prompt);
//                                            else {
//                                                bundle.putInt(CWConstant.TYPE, 1);
//                                                openNewPageForResult(ContactsAddFragment.class,
//                                                        bundle,
//                                                        CWConstant.REQUEST_CONTACTS_ADD);
//                                            }
//                                            break;
                                        case 1: // 排序
                                            openNewPageForResult(DragAddressBookFragment.class,
                                                    bundle, CWConstant.REQUEST_CONTACTS_DRAG);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        });
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
            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 1) {
                switch (type) {
                    case 0: // 其他
                        contactBean.setPortraitId(R.mipmap.ic_default_pigeon_marker);
                        break;
                    case 1:
                        contactBean.setPortraitId(R.mipmap.ic_name_type_eleventh);
                        break;
                    case 2:
                        contactBean.setPortraitId(R.mipmap.ic_name_type_twelfth);
                        break;
                    case 3:
                        contactBean.setPortraitId(R.mipmap.ic_name_type_thirteenth);
                        break;
                    case 4:
                        contactBean.setPortraitId(R.mipmap.ic_name_type_fourteenth);
                        break;
                    case 5:
                        contactBean.setPortraitId(R.mipmap.ic_name_type_fifteenth);
                        break;
                    default:
                        contactBean.setPortraitId(R.mipmap.ic_default_pigeon_marker);
                        break;
                }
            } else {
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
                        contactBean.setPortraitId(R.mipmap.ic_default_pigeon_marker);
                        break;
                }
            }
            mItemList.add(contactBean);
        }
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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            ContactBean contactBean = mItemList.get(position);
            Bundle bundle = new Bundle();
            StringBuilder contactString = new StringBuilder();
            for (int i = 0; i < mItemList.size(); i++) {
                ContactBean item = mItemList.get(i);
                if (i == position)
                    contactString.append("#0");
                else
                    contactString.append("#").append(item.getContactString());
            }
            bundle.putString(CWConstant.LIST,
                    contactString.toString().substring(1));
            bundle.putString(CWConstant.MODEL,
                    contactBean.getContactString());
            String[] array = contactBean.getContactString().split("\\|");
            if (array.length >= 5 && "1".equals(array[4]))
                bundle.putInt(CWConstant.TYPE, 1);
            else
                bundle.putInt(CWConstant.TYPE, 0);
            openNewPageForResult(ContactsDetailsFragment.class, bundle,
                    CWConstant.REQUEST_CONTACTS_EDIT);
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                switch (requestCode) {
                    case CWConstant.REQUEST_CONTACTS_ADD: // 添加通讯录
                    case CWConstant.REQUEST_CONTACTS_DRAG: // 通讯录排序
                        String contactString = bundle.getString(CWConstant.LIST);
                        mItemList.clear();
                        if (contactString != null) {
                            String[] array = contactString.split("#");
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
                        break;
                    case CWConstant.REQUEST_CONTACTS_EDIT: // 通讯录编辑
                        mItemList.clear();
                        initItems();
                        mAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        } else if (requestCode == CWConstant.REQUEST_CONTACTS_EDIT) {
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
                    case CWConstant.REQUEST_URL_GET_CONTACTS: // 获取通讯录
                        mRefreshLayout.finishRefresh();
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
