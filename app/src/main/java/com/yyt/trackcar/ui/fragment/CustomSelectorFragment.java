package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.DeviceSettingsBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel_Table;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceModel_Table;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.ServerAddressModel;
import com.yyt.trackcar.dbflow.ServerAddressModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.dbflow.UserModel_Table;
import com.yyt.trackcar.ui.adapter.CustomSelectorAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.ViewDataUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      CustomSelectorFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 17:04
 * @ describe:      TODO 选择页面
 */
@Page(name = "CustomSelector", params = {CWConstant.TYPE, CWConstant.TITLE, CWConstant.CONTENT})
public class CustomSelectorFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomSelectorAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    String title; // 标题
    @AutoWired
    String content; // 内容
    @AutoWired
    int type; // 类型 0时间显示格式 1重复 2.定位模式 3切换手表 4选择语言 5选择服务器 6.宠物定位模式
    private View mHeaderView; // 头布局

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
        titleBar.setTitle(title);
        if (type == 21) {
            titleBar.addAction(new TitleBar.ImageAction(R.drawable.add) {
                @Override
                public void performAction(View view) {
//                    final EditText editText = new EditText(getContext());
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    AlertDialog alertDialog = builder.create();
//                    builder.setTitle(R.string.add_server_address_title)
//                            .setView(editText)
//                            .setPositiveButton(R.string.confirm, new DialogInterface
//                            .OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String str = editText.getText().toString().trim();
//                                    if (TextUtils.isEmpty(str))
//                                        showMessage(R.string.electronic_name_empty_tips);
//                                    else {
//                                        new ServerAddressModel(str).save();
//                                        mItemList.add(new SectionItem(new BaseItemBean
//                                        (mItemList.size(),str)));
//                                        EmoticonsKeyboardUtils.closeSoftKeyboard(editText);
//                                    }
//                                    alertDialog.cancel();
//                                }
//                            })
//                            .setNegativeButton(R.string.cancel, new DialogInterface
//                            .OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    alertDialog.cancel();
//                                }
//                            }).show();
                    mMaterialDialog = DialogUtils.customInputMaterialDialog(getContext(),
                            mMaterialDialog, getString(R.string.add_server_address_title), null,
                            null, null,
                            InputType.TYPE_CLASS_TEXT
                            , 100, 1, getString(R.string.confirm), getString(R.string.cancel),
                            TConstant.ADD_SERVER_ADDRESS, mHandler);
                }
            });
        }
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        switch (type) {
            case 2: // 定位模式
                initFooterView();
                DeviceSettingsModel settingsModel = getDeviceSettings();
                String time;
                if (settingsModel == null || TextUtils.isEmpty(settingsModel.getLocationMode()))
                    time = "0";
                else
                    time = settingsModel.getLocationMode();
                getLocationFrequency();
                for (SectionItem item : mItemList) {
                    BaseItemBean bean = item.t;
                    if (bean != null && time.equals(String.valueOf(bean.getType()))) {
                        bean.setSelect(true);
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
                for (int i = mItemList.size() - 1; i >= 0; i--) {
                    BaseItemBean bean = mItemList.get(i).t;
                    if (bean != null) {
                        bean.setSelect(true);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case 3: // 切换手表
                getBindDeviceList();
                break;
            case 6: // 宠物定位模式
                initHeaderView();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        mItemList.add(new SectionItem(true, null));
        BaseItemBean itemBean;
        switch (type) {
            case 0: // 时间显示格式
                itemBean = new BaseItemBean(0, getString(R.string.hour_system, "24"));
                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
                itemBean.setSelect("24".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(1, getString(R.string.hour_system, "12"));
                itemBean.setSelect("12".equals(content));
                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                mItemList.add(new SectionItem(itemBean));
                break;
            case 1: // 重复
                itemBean = new BaseItemBean(1, getString(R.string.monday));
                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
                mItemList.add(new SectionItem(itemBean));
                mItemList.add(new SectionItem(new BaseItemBean(2, getString(R.string.tuesday))));
                mItemList.add(new SectionItem(new BaseItemBean(3, getString(R.string.wednesday))));
                mItemList.add(new SectionItem(new BaseItemBean(4, getString(R.string.thursday))));
                mItemList.add(new SectionItem(new BaseItemBean(5, getString(R.string.friday))));
                mItemList.add(new SectionItem(new BaseItemBean(6, getString(R.string.saturday))));
                itemBean = new BaseItemBean(0, getString(R.string.sunday));
                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                mItemList.add(new SectionItem(itemBean));
                if (!TextUtils.isEmpty(content) && content.length() == 7) {
                    for (SectionItem item : mItemList) {
                        itemBean = item.t;
                        if (itemBean != null && itemBean.getType() >= 0 && itemBean.getType() < 7) {
                            itemBean.setSelect("1".equals(content.substring(itemBean.getType(),
                                    itemBean.getType() + 1)));
                        }
                    }
                }
                break;
            case 2: // 定位模式
                itemBean = new BaseItemBean(9, getString(R.string.location_type_first));
                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
                mItemList.add(new SectionItem(itemBean));
                mItemList.add(new SectionItem(new BaseItemBean(60,
                        getString(R.string.location_type_second))));
                itemBean = new BaseItemBean(0, getString(R.string.location_type_third));
                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                mItemList.add(new SectionItem(itemBean));
                break;
            case 3: // 切换手表
            case 13: // 切换手表
                DeviceModel deviceModel = getDevice();
                UserModel userModel = getUserModel();
                for (DeviceModel model : getDeviceList()) {
                    DeviceInfoModel infoModel = null;
                    if (!TextUtils.isEmpty(model.getName())) {
                        itemBean = new BaseItemBean(0, model.getName());
                    } else {
                        if (userModel != null) {
                            OperatorGroup operatorGroup =
                                    OperatorGroup.clause(OperatorGroup.clause()
                                            .and(DeviceInfoModel_Table.u_id.eq(userModel.getU_id()))
                                            .and(DeviceInfoModel_Table.imei.eq(model.getImei())));
                            infoModel = SQLite.select().from(DeviceInfoModel.class)
                                    .where(operatorGroup)
                                    .querySingle();
                        }
                        if (infoModel == null || TextUtils.isEmpty(infoModel.getNickname())) {
                            if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
                                itemBean = new BaseItemBean(0, String.format("%s(%s)",
                                        getString(R.string.baby), model.getImei()));
                            else
                                itemBean = new BaseItemBean(0, String.format("%s(%s)",
                                        getString(R.string.device), model.getImei()));
                        } else
                            itemBean = new BaseItemBean(0, infoModel.getNickname());
                    }
                    itemBean.setGroup(model.getImei());
                    itemBean.setSelect(deviceModel != null && deviceModel.getImei().equals(model.getImei()));
                    mItemList.add(new SectionItem(itemBean));
                }
                if (mItemList.size() == 2)
                    mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_item_round_selector);
                else if (mItemList.size() > 2) {
                    mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_top_radius);
                    mItemList.get(mItemList.size() - 1).t.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                }
                break;
            case 4: // 选择语言
                itemBean = new BaseItemBean(0, getString(R.string.language_type_first));
                itemBean.setSelect("zh".equals(content));
                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(5, getString(R.string.language_type_sixth));
                itemBean.setSelect("tw".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(1, getString(R.string.language_type_second));
                boolean isEn =
                        !("zh".equals(content) || "in".equals(content) || "pt".equals(content)
                                || "es".equals(content) || "tw".equals(content) || "vi".equals(content)
                                || "ar".equals(content) || "ru".equals(content) || "fr".equals(content)
                                || "ja".equals(content) || "de".equals(content));
                itemBean.setSelect(isEn);
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(2, getString(R.string.language_type_third));
                itemBean.setSelect("in".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(3, getString(R.string.language_type_fourth));
                itemBean.setSelect("pt".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(4, getString(R.string.language_type_fifth));
                itemBean.setSelect("es".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(6, getString(R.string.language_type_seventh));
                itemBean.setSelect("vi".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(7, getString(R.string.language_type_tenth));
                itemBean.setSelect("ar".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(8, getString(R.string.language_type_ninth));
                itemBean.setSelect("ru".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(9, getString(R.string.language_type_twelfth));
                itemBean.setSelect("fr".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(10, getString(R.string.language_type_eighth));
                itemBean.setSelect("ja".equals(content));
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(11, getString(R.string.language_type_eleventh));
                itemBean.setSelect("de".equals(content));
                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                mItemList.add(new SectionItem(itemBean));
                break;
            case 5: // 选择服务器
//                itemBean = new BaseItemBean(0, getString(R.string.country_cn));
//                itemBean.setSelect("0".equals(content));
//                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
//                mItemList.add(new SectionItem(itemBean));
//                if (CWConstant.APP_TEST) {
//                    itemBean = new BaseItemBean(2, getString(R.string.country_asia));
//                    itemBean.setSelect("2".equals(content));
//                    mItemList.add(new SectionItem(itemBean));
//                }
//                itemBean = new BaseItemBean(1, getString(R.string.country_en));
//                itemBean.setSelect("1".equals(content));
//                mItemList.add(new SectionItem(itemBean));
//                itemBean = new BaseItemBean(3, getString(R.string.country_europe));
//                itemBean.setSelect("3".equals(content));
////                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
//                mItemList.add(new SectionItem(itemBean));

                itemBean = new BaseItemBean(5, getString(R.string.country_en_two));
                itemBean.setSelect("5".equals(content));
                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
                mItemList.add(new SectionItem(itemBean));

                itemBean = new BaseItemBean(4, getString(R.string.country_asia_two));
                itemBean.setSelect(!"5".equals(content));
                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                mItemList.add(new SectionItem(itemBean));
                break;

//                ============================================================================
            case 6: // 宠物定位模式
                AAADeviceModel model = getTrackDeviceModel();
                int positionInterval = 0;
                int positionType = 1;
                if (model != null) {
                    if (model.getPositionInterval() != null) {
                        positionInterval = model.getPositionInterval();
                    }
                    if (model.getPositionType() != null) {
                        positionType = model.getPositionType();
                    }
                }
                itemBean = new BaseItemBean(1, getString(R.string.pet_location_type_first));
                itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
                itemBean.setSelect(positionType == 1);
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(2, getString(R.string.pet_location_type_second,
                        positionInterval));
                itemBean.setSelect(positionType == 2);
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(3, getString(R.string.pet_location_type_third));
                itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                itemBean.setSelect(positionType == 3);
                mItemList.add(new SectionItem(itemBean));
                break;
            case 20: //选择当前设备
                List<AAADeviceModel> aaaDeviceModel = getTrackDeviceList();
                if (aaaDeviceModel.size() > 0) {
                    int index = 0;
                    for (AAADeviceModel device : aaaDeviceModel) {
                        itemBean = new BaseItemBean(index++, device.getDeviceImei());
//                        itemBean.setGroup(device.getDeviceImei());
                        itemBean.setTitle(device.getDeviceName());
                        itemBean.setContent(device.getDeviceImei());
                        if (device.getDeviceImei().equals(MainApplication.getInstance().getTrackDeviceModel().getDeviceImei())) {
                            itemBean.setSelect(true);
                        }
                        mItemList.add(new SectionItem(itemBean));
                    }
                }
                if (mItemList.size() == 2) {
                    mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_item_round_selector);
                } else if (mItemList.size() > 2) {
                    mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_top_radius);
                    mItemList.get(mItemList.size() - 1).t.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                }
                break;
            case 21:  //选择服务器
                String serverIp =
                        SettingSPUtils.getInstance().getString(TConstant.SERVER_ADDRESS,
                                TConstant.DEFAULT_IP);
                boolean isFind =
                        TextUtils.isEmpty(serverIp) || TConstant.DEFAULT_IP.equals(serverIp);
                List<ServerAddressModel> serverAddressModelList =
                        SQLite.select().from(ServerAddressModel.class).queryList();
                itemBean = new BaseItemBean(0, getString(R.string.word_default), isFind);
                mItemList.add(new SectionItem(itemBean));
                for (ServerAddressModel serverModel : serverAddressModelList) {
                    if (!TConstant.DEFAULT_IP.equals(serverModel.getServerAddress())) {
                        itemBean = new BaseItemBean(1, serverModel.getServerAddress(),
                                serverIp.equals(serverModel.getServerAddress()));
                        mItemList.add(new SectionItem(itemBean));
                        if (itemBean.isSelect()) {
                            isFind = true;
                        }
                    }
                }
                if (!isFind) {
                    SettingSPUtils.getInstance().put(TConstant.SERVER_ADDRESS,
                            TConstant.DEFAULT_IP);
                    mItemList.get(1).t.setSelect(true);
                }
                if (mItemList.size() == 2) {
                    mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_item_round_selector);
                } else if (mItemList.size() > 2) {
                    mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_top_radius);
                    mItemList.get(mItemList.size() - 1).t.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                }
//                for (int i = 0; i < serverAddressModelList.size(); i++) {
//                    // 自己服务器ip地址则显示default，避免暴露ip
//                    itemBean = new BaseItemBean(0,
//                            serverAddressModelList.get(i).getServerAddress().equals(TConstant
//                            .DEFAULT_IP) ? getString(R.string.word_default) :
//                            serverAddressModelList.get(i).getServerAddress());
//                    mItemList.add(new SectionItem(itemBean));
//                }
                break;
            default:
                break;
        }
        mItemList.add(new SectionItem(true, null));
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CustomSelectorAdapter(mItemList);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
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
        mHeaderView = getLayoutInflater().inflate(R.layout.header_view_device_info,
                mRecyclerView, false);
        ViewDataUtils.initDeviceInfoView(getContext(), mHeaderView, getTrackDeviceModel());
        mAdapter.addHeaderView(mHeaderView);
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.footer_view_text, mRecyclerView,
                false);
        TextView tvContent = footerView.findViewById(R.id.tvContent);
        if (type == 2)
            tvContent.setText(R.string.location_mode_prompt);
        mAdapter.addFooterView(footerView);
    }

    /**
     * 获取定位频率设置
     */
    private void getLocationFrequency() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getLocationFrequency(getContext(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 定位频率设置
     */
    private void locationFrequency(String time) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null) {
//            DeviceSettingsModel settingsModel = getDeviceSettings();
//            settingsModel.setLocationMode(time);
//            settingsModel.save();
            CWRequestUtils.getInstance().locationFrequency(MainApplication.getInstance(), getIp(),
                    userModel.getToken(), deviceModel.getD_id(), deviceModel.getImei(), time,
                    mHandler);
        }
    }

    /**
     * 用户查询绑定设备列表
     */
    private void getBindDeviceList() {
        UserModel userModel = getUserModel();
        if (userModel != null) {
            int deviceType = SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_MODEL, 1);
            CWRequestUtils.getInstance().getBindDeviceList(getContext(), userModel.getU_id(),
                    userModel.getToken(), deviceType, mHandler);
        }
    }

    /**
     * 设置定位间隔
     *
     * @param positionType 定位类型
     * @param interval     定位间隔
     */
    private void setPositionInterval(int positionType, Integer interval) {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (userModel != null && deviceModel != null && !TextUtils.isEmpty(deviceModel.getDeviceImei())) {
            showDialog();
            CarGpsRequestUtils.setPositionInterval(userModel, deviceModel.getDeviceImei(),
                    positionType, interval, mHandler);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                Intent intent;
                Bundle bundle;
                if (type == 1) { // 重复
                    itemBean.setSelect(!itemBean.isSelect());
                    boolean allUnSelect = true; // 全部未选择
                    for (SectionItem item : mItemList) {
                        BaseItemBean bean = item.t;
                        if (bean != null && bean.isSelect()) {
                            allUnSelect = false;
                            break;
                        }
                    }
                    if (allUnSelect)
                        XToastUtils.toast(R.string.select_at_least_one_prompt);
                    else {
                        String[] array = new String[7];
                        for (SectionItem item : mItemList) {
                            BaseItemBean bean = item.t;
                            if (bean != null && bean.getType() >= 0 && bean.getType() < array.length) {
                                array[bean.getType()] = bean.isSelect() ? "1" : "0";
                            }
                        }
                        StringBuilder repeat = new StringBuilder();
                        for (String str : array) {
                            repeat.append(str);
                        }
                        intent = new Intent();
                        bundle = new Bundle();
                        bundle.putString(CWConstant.CONTENT, repeat.toString());
                        intent.putExtras(bundle);
                        setFragmentResult(Activity.RESULT_OK, intent);
                    }
                    mAdapter.notifyItemChanged(position);
                } else if (type == 2) {
                    locationFrequency(String.valueOf(itemBean.getType()));
                } else if (type == 6) {
                    if (itemBean.getType() != 2) {
                        setPositionInterval(itemBean.getType(), null);
                    } else {
                        mMaterialDialog =
                                DialogUtils.customInputMaterialDialog(getContext(),
                                        mMaterialDialog,
                                        getString(R.string.set_position_interval), null,
                                        getString(R.string.hint_set_position_interval),
                                        null,
                                        InputType.TYPE_CLASS_NUMBER
                                        , 4, 1, getString(R.string.confirm),
                                        getString(R.string.cancel),
                                        100, mHandler);
                    }
                } else {
                    for (SectionItem item : mItemList) {
                        BaseItemBean bean = item.t;
                        if (bean != null)
                            bean.setSelect(false);
                    }
                    itemBean.setSelect(true);
                    mAdapter.notifyDataSetChanged();
                    switch (type) {
                        case 2: // 定位模式
                            locationFrequency(String.valueOf(itemBean.getType()));
                            break;
                        case 3: // 切换手表
                        case 13:
                            DeviceModel deviceModel = getDevice();
                            if (deviceModel == null || !deviceModel.getImei().equals(itemBean.getGroup())) {
                                List<DeviceModel> deviceList = getDeviceList();
                                UserModel userModel = getUserModel();
                                if (userModel != null) {
                                    for (DeviceModel model : deviceList) {
                                        if (model.getImei().equals(itemBean.getGroup())) {
                                            MainApplication.getInstance().setDeviceModel(model);
                                            userModel.setSelectImei(model.getImei());
                                            userModel.save();
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case 4: // 选择语言
                        case 5: // 选择服务器
                            intent = new Intent();
                            bundle = new Bundle();
                            bundle.putInt(CWConstant.TYPE, itemBean.getType());
                            bundle.putString(CWConstant.CONTENT, itemBean.getTitle());
                            intent.putExtras(bundle);
                            setFragmentResult(Activity.RESULT_OK, intent);
                            break;
                        case 6: // 宠物定位模式
                            if (itemBean.getType() != 2) {
                                setPositionInterval(itemBean.getType(), null);
                            } else {
                                mMaterialDialog =
                                        DialogUtils.customInputMaterialDialog(getContext(),
                                                mMaterialDialog,
                                                getString(R.string.set_position_interval), null,
                                                getString(R.string.hint_set_position_interval),
                                                null,
                                                InputType.TYPE_CLASS_NUMBER
                                                , 5, 1, getString(R.string.confirm),
                                                getString(R.string.cancel),
                                                100, mHandler);
                            }
                            break;
                        //====================================================================================

                        case 20: //切换当前选中的设备
                            AAADeviceModel aaaDeviceModel = getTrackDeviceModel();
                            if (aaaDeviceModel == null || !aaaDeviceModel.getDeviceImei().equals(itemBean.getContent())) {
                                List<AAADeviceModel> deviceList = getTrackDeviceList();
                                AAAUserModel userModel = getTrackUserModel();
                                if (userModel != null) {
                                    for (AAADeviceModel model : deviceList) {
                                        if (model.getDeviceImei().equals(itemBean.getContent())) {
                                            MainApplication.getInstance().setTrackDeviceModel(model);
                                            userModel.setSelectDeviceId(model.getDeviceImei());
                                            userModel.save();
//                                            EventBus.getDefault().post(new PostMessage
//                                            (CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                            SettingSPUtils.getInstance().putString(TConstant.SELECTED_IMEI, model.getDeviceImei());
                                            SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE, model.getDeviceType());
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
//                                            if (SettingSPUtils.getInstance().getInt(CWConstant
//                                            .DEVICE_TYPE,0) == model.getDeviceType()){
//                                                EventBus.getDefault().post(new PostMessage
//                                                (CWConstant.POST_MESSAGE_CHANGE_DEVICE));
//                                            }else{
//                                                SettingSPUtils.getInstance().putInt(CWConstant
//                                                .DEVICE_TYPE, model.getDeviceType());
//                                                EventBus.getDefault().post(new PostMessage
//                                                (CWConstant.POST_MESSAGE_CHANGE_DEVICE_TYPE));
//                                            }
                                            break;
                                        }
                                    }
                                }
                            }

//                            List<AAADeviceModel> deviceModelList = MainApplication.getInstance
//                            ().getTrackDeviceList();
//                            MainApplication.getInstance().setSelectIndex(position-1);
//                            MainApplication.getInstance().getTrackUserModel().setSelectDeviceId
//                            (deviceModelList.get(position-1).getDeviceImei());
//                            MainApplication.getInstance().setTrackDeviceModel(deviceModelList
//                            .get(position-1));
//                            if (position > 0 && deviceModelList.get(position-1).getDeviceType()
//                            != 0)
//                            SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE,
//                            deviceModelList.get(position-1).getDeviceType());
////                            KLog.d("\nposition: "+position +"\nDeviceType: "+SettingSPUtils
// .getInstance().getInt(CWConstant.DEVICE_TYPE,0));
//                            EventBus.getDefault().post(new PostMessage(CWConstant
//                            .POST_MESSAGE_CHANGE_DEVICE_TYPE));
                            break;
                        case 21:  //切换服务器地址
                            if (itemBean.getType() == 1) {
                                SettingSPUtils.getInstance().put(TConstant.SERVER_ADDRESS,
                                        itemBean.getTitle());
                            } else {
                                SettingSPUtils.getInstance().put(TConstant.SERVER_ADDRESS,
                                        TConstant.DEFAULT_IP);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                RequestBean requestBean;
                UserModel userModel;
                AAABaseResponseBean responseBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_LOCATION_FREQUENCY: // 定位频率设置
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
                                    CWRequestUtils.getInstance().locationFrequency(getContext(),
                                            resultBean.getLast_online_ip(), requestBean.getToken(),
                                            requestBean.getD_id(), requestBean.getImei(),
                                            requestBean.getTime(), mHandler);
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
                                    settingsModel.setLocationMode(requestBean.getTime());
                                    settingsModel.save();
                                    boolean isSelect = false;
                                    for (SectionItem item : mItemList) {
                                        BaseItemBean bean = item.t;
                                        if (bean != null && String.valueOf(bean.getType()).equals(settingsModel.getLocationMode())) {
                                            bean.setSelect(true);
                                            isSelect = true;
                                        } else if (bean != null)
                                            bean.setSelect(false);
                                    }
                                    if (!isSelect) {
                                        for (int i = mItemList.size() - 1; i >= 0; i--) {
                                            BaseItemBean bean = mItemList.get(i).t;
                                            if (bean != null) {
                                                bean.setSelect(true);
                                                break;
                                            }
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else if (resultBean.getCode() == CWConstant.ERROR)
                                XToastUtils.toast(R.string.send_error_prompt);
                            else if (resultBean.getCode() != CWConstant.SUCCESS)
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_LOCATION_FREQUENCY: // 获取定位频率设置
                        if (msg.obj != null) {
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
                                    CWRequestUtils.getInstance().getLocationFrequency(getContext(),
                                            resultBean.getLast_online_ip(), userModel.getToken(),
                                            deviceModel.getD_id(), mHandler);
                                }
                            } else if (resultBean.getCode() == CWConstant.SUCCESS) {
                                DeviceModel deviceModel = getDevice();
                                userModel = getUserModel();
                                requestBean =
                                        mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                if (userModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                    DeviceSettingsBean settingsBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getResultBean()),
                                                    DeviceSettingsBean.class);
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    settingsModel.setLocationMode(settingsBean.getTime());
                                    settingsModel.save();
                                    boolean isSelect = false;
                                    for (SectionItem item : mItemList) {
                                        BaseItemBean bean = item.t;
                                        if (bean != null && String.valueOf(bean.getType()).equals(settingsBean.getTime())) {
                                            bean.setSelect(true);
                                            isSelect = true;
                                        } else if (bean != null)
                                            bean.setSelect(false);
                                    }
                                    if (!isSelect) {
                                        for (int i = mItemList.size() - 1; i >= 0; i--) {
                                            BaseItemBean bean = mItemList.get(i).t;
                                            if (bean != null) {
                                                bean.setSelect(true);
                                                break;
                                            }
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_BIND_DEVICE_LIST: // 用户查询绑定设备列表
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
                                    List<DeviceModel> deviceList = new ArrayList<>();
                                    boolean isFind = false;
                                    if (resultBean.getDeviceList() != null) {
                                        for (Object obj : resultBean.getDeviceList()) {
                                            DeviceModel deviceModel =
                                                    mGson.fromJson(mGson.toJson(obj),
                                                            DeviceModel.class);
                                            saveDeviceIp(requestBean.getU_id(),
                                                    deviceModel.getImei(),
                                                    deviceModel.getIp());
                                            deviceModel.setU_id(requestBean.getU_id());
                                            deviceModel.save();
                                            deviceList.add(deviceModel);
                                            if (selectImei.equals(deviceModel.getImei())) {
                                                isFind = true;
                                                MainApplication.getInstance().setDeviceModel(deviceModel);
                                            }
                                        }
                                        if (!isFind && deviceList.size() > 0) {
                                            MainApplication.getInstance().setDeviceModel(deviceList.get(0));
                                            userModel.setSelectImei(getDevice().getImei());
                                            userModel.save();
                                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                                        }
                                    }
                                    MainApplication.getInstance().setUserModel(userModel);
                                    getDeviceList().clear();
                                    getDeviceList().addAll(deviceList);
                                    mItemList.clear();
                                    initItems();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        break;
                    case TConstant.REQUEST_COMMAND_UPLOAD_INTERVAL: // 设置定位间隔
                        dismisDialog();
                        responseBean = (AAABaseResponseBean) msg.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            XToastUtils.toast(getContext(),
                                    getString(R.string.send_success_prompt));
                            AAABaseResponseBean requestModel =
                                    mGson.fromJson(mGson.toJson(responseBean.getRequestObject()),
                                            AAABaseResponseBean.class);
                            requestBean = mGson.fromJson(mGson.toJson(requestModel.getData()),
                                    RequestBean.class);
                            if (requestBean != null && !TextUtils.isEmpty(requestBean.getDeviceImei())
                                    && requestBean.getPositionType() != null) {
                                String deviceImei = requestBean.getDeviceImei();
                                int positionType = requestBean.getPositionType();
                                for (SectionItem item : mItemList) {
                                    BaseItemBean bean = item.t;
                                    if (bean != null) {
                                        bean.setSelect(positionType == bean.getType());
                                        if (positionType == 2 && bean.isSelect()) {
                                            bean.setTitle(getString(R.string.pet_location_type_second,
                                                    requestBean.getPositionInterval() == null ? 0 :
                                                            requestBean.getPositionInterval()));
                                        }
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                                for (AAADeviceModel deviceModel : getTrackDeviceList()) {
                                    if (deviceImei.equals(deviceModel.getDeviceImei())) {
                                        deviceModel.setPositionType(positionType);
                                        if (positionType == 2) {
                                            deviceModel.setPositionInterval(requestBean.getPositionInterval());
                                        }
                                        ViewDataUtils.initDeviceInfoView(getContext(),
                                                mHeaderView, deviceModel);
                                        break;
                                    }
                                }
                                AAADeviceModel deviceModel = getTrackDeviceModel();
                                if (deviceImei.equals(deviceModel.getDeviceImei())) {
                                    deviceModel.setPositionType(positionType);
                                    if (positionType == 2) {
                                        deviceModel.setPositionInterval(requestBean.getPositionInterval());
                                    }
                                }

                            }
                        } else {
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                        }
                        break;
                    case CWConstant.HANDLE_INPUT_ACTION: // 输入回调
                        switch (msg.arg1) {
                            case 100: // 定位间隔
                                int interval = Integer.parseInt((String) msg.obj);
                                if (interval < 0 || interval > 1440) {
                                    XToastUtils.toast(getContext(),
                                            getString(R.string.hint_set_position_interval));
                                } else {
                                    setPositionInterval(2, interval);
                                }
                                break;
                            case TConstant.ADD_SERVER_ADDRESS: //
                                String serverIp = (String) msg.obj;
                                if (!(TextUtils.isEmpty(serverIp) || TConstant.DEFAULT_IP.equals(serverIp))) {
                                    ServerAddressModel serverAddressModel = SQLite.select()
                                            .from(ServerAddressModel.class)
                                            .where(ServerAddressModel_Table.serverAddress.eq(serverIp))
                                            .querySingle();
                                    if (serverAddressModel == null) {
                                        ServerAddressModel sub = new ServerAddressModel(serverIp);
                                        sub.save();
                                        BaseItemBean itemBean = new BaseItemBean(mItemList.size(),
                                                serverIp);
                                        mItemList.add(mItemList.size() - 1,
                                                new SectionItem(itemBean));
                                        if (mItemList.size() == 3) {
                                            mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_top_radius);
                                            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                        } else if (mItemList.size() > 3) {
                                            mItemList.get(mItemList.size() - 3).t.setBgDrawable(0);
                                            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
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
    public void popToBack() {
        Intent intent = null;
        Bundle bundle = null;
        switch (type) {
            case 0: // 时间显示格式
                content = "24";
                for (SectionItem item : mItemList) {
                    BaseItemBean itemBean = item.t;
                    if (itemBean != null && itemBean.getType() == 1 && itemBean.isSelect()) {
                        content = "12";
                        break;
                    }
                }
                intent = new Intent();
                bundle = new Bundle();
                bundle.putString(CWConstant.CONTENT, content);
                break;
            case 21:
                for (SectionItem item : mItemList) {
                    BaseItemBean itemBean = item.t;
                    if (itemBean != null && itemBean.isSelect()) {
                        content = itemBean.getTitle();
                        break;
                    }
                }
                intent = new Intent();
                bundle = new Bundle();
                bundle.putString(CWConstant.CONTENT, content);
                break;
            default:
                break;
        }
        if (intent != null) {
            intent.putExtras(bundle);
            setFragmentResult(Activity.RESULT_OK, intent);
        }
        super.popToBack();
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null && itemBean.getType() == 1) {
                String serverIp = itemBean.getTitle();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                AlertDialog alertDialog = builder.create();
                builder.setTitle(R.string.prompt)
                        .setMessage(String.format("%s: %s",
                                getString(R.string.delete_server_address), serverIp))
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int type) {
                                SQLite.delete(ServerAddressModel.class)
                                        .where(ServerAddressModel_Table.serverAddress.eq(serverIp)).execute();
                                for (int i = 0; i < mItemList.size(); i++) {
                                    BaseItemBean model = mItemList.get(i).t;
                                    if (model != null && serverIp.equals(model.getTitle())) {
                                        mItemList.remove(i);
                                        if (model.isSelect()) {
                                            SettingSPUtils.getInstance().put(TConstant.SERVER_ADDRESS,
                                                    TConstant.DEFAULT_IP);
                                            mItemList.get(1).t.setSelect(true);
                                        }
                                        if (model.getBgDrawable() != 0) {
                                            if (mItemList.size() == 3) {
                                                mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_item_round_selector);
                                            } else if (mItemList.size() > 3) {
                                                mItemList.get(1).t.setBgDrawable(R.drawable.btn_custom_top_radius);
                                                mItemList.get(mItemList.size() - 1).t.setBgDrawable(R.drawable.btn_custom_bottom_radius);
                                            }
                                        }
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                alertDialog.cancel();
                            }
                        }).setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        alertDialog.cancel();
                                    }
                                }).show();
            }
        }
//        mMaterialDialog = DialogUtils.customMaterialDialog(getContext(),
//                mMaterialDialog, getString(R.string.prompt), getString(R.string
//                .delete_server_address),
//                getString(R.string.confirm), null,
//                getString(R.string.cancel), null,
//                TConstant.ADD_SERVER_ADDRESS, mHandler);
        return false;
    }
}
