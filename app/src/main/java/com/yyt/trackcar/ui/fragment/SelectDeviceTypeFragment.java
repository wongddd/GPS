package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xui.widget.tabbar.VerticalTabLayout;
import com.xuexiang.xui.widget.tabbar.vertical.ITabView;
import com.xuexiang.xui.widget.tabbar.vertical.TabAdapter;
import com.xuexiang.xui.widget.tabbar.vertical.TabView;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserModel_Table;
import com.yyt.trackcar.ui.activity.BindDeviceActivity;
import com.yyt.trackcar.ui.activity.LoginActivity;
import com.yyt.trackcar.ui.activity.MainActivity;
import com.yyt.trackcar.ui.adapter.SelectDeviceTypeAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      SelectDeviceTypeFragment
 * @ author:        QING
 * @ createTime:    2020/10/22 12:40
 * @ describe:      TODO 选择设备类型页面
 */
@Page(name = "SelectDeviceType", params = {CWConstant.TYPE})
public class SelectDeviceTypeFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.vTabLayout)
    VerticalTabLayout mVTabLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    @BindView(R.id.tvSwitchAccount)
    XUIAlphaTextView mTvSwitchAccount;
    private SelectDeviceTypeAdapter mAdapter; // 适配器
    private List<BaseItemBean> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    int type; // 0.绑定设备 1.从主页绑定设备 2.选择设备 3.需要获取列表
    private boolean mIsEntry; // 是否点击进入首页
    private boolean mIsFinish; // 是否结束

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_device_type;
    }

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.device_type_select);
        if (type == 3) {
            titleBar.setLeftImageDrawable(null).setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
//            titleBar.addAction(new TitleBar.TextAction(getString(R.string.switch_account)) {
//                @Override
//                public void performAction(View view) {
//                    mIsFinish = true;
//                    SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
//                    SettingSPUtils.getInstance().putString(CWConstant.U_ID, "");
//                    MainApplication.getInstance().setUserModel(null);
//                    MainApplication.getInstance().getDeviceList().clear();
//                    EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
//                    ActivityUtils.startActivity(LoginActivity.class);
//                }
//            });
        }
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        if (type == 3)
            mTvSwitchAccount.setVisibility(View.VISIBLE);
        mVTabLayout.setTabAdapter(new MyTabAdapter());

        mVTabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                initItems();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(final TabView tab, int position) {
            }

            @Override
            public void onTabReselected(TabView tab, int position) {
            }
        });
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        mItemList.clear();
        if (mVTabLayout.getSelectedTabPosition() <= 0) {

            BaseItemBean itemBean = new BaseItemBean(0, "L11");
            itemBean.setImgDrawable(R.mipmap.icon_l11);
            itemBean.setBgDrawable(4);
            mItemList.add(itemBean);

            itemBean = new BaseItemBean(0, "L10");
            itemBean.setImgDrawable(R.mipmap.icon_l10);
            itemBean.setBgDrawable(3);
            mItemList.add(itemBean);

            itemBean = new BaseItemBean(0, "L09");
            itemBean.setImgDrawable(R.mipmap.icon_l09);
            itemBean.setBgDrawable(1);
            mItemList.add(itemBean);

            itemBean = new BaseItemBean(0, "L08");
            itemBean.setImgDrawable(R.mipmap.icon_l08);
            itemBean.setBgDrawable(2);
            mItemList.add(itemBean);

        } else {

            BaseItemBean itemBean = new BaseItemBean(1, "S6");
            itemBean.setImgDrawable(R.mipmap.icon_s6);
            itemBean.setBgDrawable(5);
            mItemList.add(itemBean);

        }
//        mItemList.add(new SectionItem(true, null));
//        BaseItemBean itemBean = new BaseItemBean(0, getString(R.string.device_type_first));
//        itemBean.setImgDrawable(R.mipmap.device_type_third);
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        mItemList.add(new SectionItem(itemBean));
//
//        mItemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(1, getString(R.string.device_type_second));
//        itemBean.setImgDrawable(R.mipmap.device_type_first);
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        mItemList.add(new SectionItem(itemBean));

//        mItemList.add(new SectionItem(true, null));
//        itemBean = new BaseItemBean(2, "蓝牙手表");
//        itemBean.setImgDrawable(R.mipmap.device_type_second);
//        itemBean.setBgDrawable(R.drawable.btn_custom_item_round_selector);
//        mItemList.add(new SectionItem(itemBean));

//        mItemList.add(new SectionItem(true, null));
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new SelectDeviceTypeAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position);
            if (itemBean != null) {
                Bundle bundle;
                if (type == 0) {
                    bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, 0);
                    openNewPage(CameraCaptureFragment.class, bundle);
                } else if (type == 1) {
                    bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, 1);
                    openNewPage(CameraCaptureFragment.class, bundle);
                } else if (type == 3) {
                    if (getUserModel() == null) {
                        mIsFinish = true;
                        SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                        SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                        MainApplication.getInstance().setUserModel(null);
                        MainApplication.getInstance().getDeviceList().clear();
                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                        ActivityUtils.startActivity(LoginActivity.class);
                    } else if (!mIsEntry && !mIsFinish) {
                        mIsEntry = true;
                        SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE,
                                itemBean.getType());
                        SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_MODEL,
                                itemBean.getBgDrawable());
                        UserModel userModel = getUserModel();
                        if (userModel != null) {
                            int deviceType =
                                    SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, 1);
                            CWRequestUtils.getInstance().getBindDeviceList(getContext(),
                                    userModel.getU_id(),
                                    userModel.getToken(), deviceType, mHandler);
                        }
                    }
//                    EventBus.getDefault().post(new PostMessage(CWConstant
//                            .POST_MESSAGE_FINISH));
//                    SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE, itemBean
//                    .getType());
//                    Intent intent = new Intent(XUtil.getContext(), BindDeviceActivity.class);
//                    bundle = new Bundle();
//                    bundle.putString(CWConstant.TYPE, "1");
//                    intent.putExtras(bundle);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    ActivityUtils.startActivity(intent);
                } else if (type == 2) {
                    if (getUserModel() == null) {
                        mIsFinish = true;
                        SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                        SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                        MainApplication.getInstance().setUserModel(null);
                        MainApplication.getInstance().getDeviceList().clear();
                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                        ActivityUtils.startActivity(LoginActivity.class);
                    } else if (!mIsEntry && !mIsFinish) {
                        mIsEntry = true;
                        int oldDeviceType =
                                SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, -1);
                        int oldDeviceModel =
                                SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, -1);
                        if (itemBean.getType() != oldDeviceType || itemBean.getBgDrawable() != oldDeviceModel) {
                            SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE,
                                    itemBean.getType());
                            SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_MODEL,
                                    itemBean.getBgDrawable());
                            UserModel userModel = getUserModel();
                            if (userModel != null) {
                                int deviceType =
                                        SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, 1);
                                CWRequestUtils.getInstance().getBindDeviceList(getContext(),
                                        userModel.getU_id(),
                                        userModel.getToken(), deviceType, mHandler);
                            }
                        } else {
                            bundle = new Bundle();
                            bundle.putInt(CWConstant.TYPE, 13);
                            bundle.putString(CWConstant.TITLE,
                                    getString(R.string.change_device_title));
                            openNewPage(CustomSelectorFragment.class, bundle);
                            popToBack();
                        }
                    }
                }
            }
        }
    }

    @SingleClick
    @OnClick({R.id.tvSwitchAccount})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSwitchAccount: // 切换账号
//                SettingSPUtils.getInstance().putString(CWConstant.PASSWORD, "");
                mIsFinish = true;
                SettingSPUtils.getInstance().putString(CWConstant.TOKEN, "");
                SettingSPUtils.getInstance().putLong(CWConstant.U_ID, -1);
                MainApplication.getInstance().setUserModel(null);
                MainApplication.getInstance().getDeviceList().clear();
                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                ActivityUtils.startActivity(LoginActivity.class);
                break;
            default:
                break;
        }
    }

    private class MyTabAdapter implements TabAdapter {
        List<BaseItemBean> menus;

        public MyTabAdapter() {
            menus = new ArrayList<>();
            Collections.addAll(menus, new BaseItemBean(getString(R.string.device_type_first))
                    , new BaseItemBean(getString(R.string.device_type_second)));
        }

        @Override
        public int getCount() {
            return menus.size();
        }

        @Override
        public ITabView.TabBadge getBadge(int position) {
            return null;
        }

        @Override
        public ITabView.TabIcon getIcon(int position) {
            return null;
        }

        @Override
        public TabView.TabTitle getTitle(int position) {
            BaseItemBean menu = menus.get(position);
            return new TabView.TabTitle.Builder()
                    .setContent(menu.getContent())
                    .setTextSize(14)
                    .setTextColor(0xFFFFFFFF, 0xFF000000)
                    .build();
        }

        @Override
        public int getBackground(int position) {
            return -1;
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
                        if (!mIsFinish) {
                            if (msg.obj == null) {
                                XToastUtils.toast(R.string.request_unkonow_prompt);
                            } else {
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
                                            }
                                            boolean isFind = false;
                                            for (DeviceModel deviceModel : deviceList) {
                                                if (selectImei.equals(deviceModel.getImei())) {
                                                    isFind = true;
                                                    MainApplication.getInstance().setDeviceModel(deviceModel);
                                                    break;
                                                }
                                            }
                                            if (!isFind) {
                                                if (deviceList.size() > 0) {
                                                    MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                                    userModel.setSelectImei(getDevice().getImei());
                                                    userModel.save();
                                                } else
                                                    MainApplication.getInstance().setDeviceModel(null);
                                            }
                                        } else
                                            MainApplication.getInstance().setDeviceModel(null);
                                        MainApplication.getInstance().setUserModel(userModel);
                                        getDeviceList().clear();
                                        getDeviceList().addAll(deviceList);
                                        mIsFinish = true;
                                        EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE_TYPE));
                                        if (deviceList.size() > 0) {
                                            if (type == 2) {
                                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                                Bundle bundle = new Bundle();
                                                bundle.putInt(CWConstant.TYPE, 13);
                                                bundle.putString(CWConstant.TITLE,
                                                        getString(R.string.change_device_title));
                                                openNewPage(CustomSelectorFragment.class, bundle);
                                                popToBack();
                                            } else if (type == 3) {
                                                EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                                ActivityUtils.startActivity(MainActivity.class);
                                            }
                                        } else {
                                            XToastUtils.toast(R.string.user_unbind_device_prompt);
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_FINISH));
                                            ActivityUtils.startActivity(BindDeviceActivity.class);
                                        }
                                    }
                                } else
                                    RequestToastUtils.toast(resultBean.getCode());
                            }
                            mIsEntry = false;
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
