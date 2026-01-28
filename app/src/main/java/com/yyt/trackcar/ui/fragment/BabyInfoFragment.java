package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener;
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener;
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
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.dbflow.DeviceInfoModel;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSettingsModel;
import com.yyt.trackcar.dbflow.PortraitModel;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.BabyInfoAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DeviceType;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.PictureSelectorUtils;
import com.yyt.trackcar.utils.PortraitUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.StringUtils;
import com.yyt.trackcar.utils.TConstant;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      BabyInfoFragment
 * @ author:        QING
 * @ createTime:    2020/3/11 19:48
 * @ describe:      TODO 宝贝信息页面
 */
@Page(name = "BabyInfo", params = {TConstant.DEVICE_IMEI})
public class BabyInfoFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private BabyInfoAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private BottomSheet mBottomSheet; // 选项弹窗
    private OptionsPickerView mOptionsPicker; // 选择器
    private TimePickerView mDatePicker; // 日期选择器
    private String[] mSexArray; // 性别列表
    private String[] mGradeArray; // 年级列表
    private String[] mClassArray; // 班级列表
    private String[] mHeightArray; // 身高列表
    private String[] mWeightArray; // 体重列表
    @AutoWired
    String deviceImei; // 设备号
    private AAADeviceModel mDeviceModel; // 设备对象

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
//        titleBar.setTitle(R.string.device_info);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.device_info)));
        return titleBar;
    }

    @Override
    protected void initViews() {
        if (!TextUtils.isEmpty(deviceImei)) {
            for (AAADeviceModel model : getTrackDeviceList()) {
                if (deviceImei.equals(model.getDeviceImei())) {
                    mDeviceModel = model;
                    break;
                }
            }
        }
        if (mDeviceModel == null) {
            mDeviceModel = getTrackDeviceModel();
        }
        initItems();
        initAdapters();
        initRecyclerViews();
        refreshDeviceInfo();
//        getWatchInfo();
//        getFamilyWifi();
        PictureSelectorUtils.initRxPermissions(mActivity);
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getBabyInfoData(mActivity, mItemList);

        if (mDeviceModel != null) {
            BaseItemBean itemBean = new BaseItemBean(3, getString(R.string.imei),
                    StringUtils.getNotNullText(mDeviceModel.getDeviceImei()));
            itemBean.setBgDrawable(R.drawable.btn_custom_top_radius);
            mItemList.add(new SectionItem(itemBean));
            itemBean = new BaseItemBean(4, getString(R.string.device_type),
                    String.valueOf(mDeviceModel.getDeviceType()));
            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
            mItemList.add(new SectionItem(itemBean));
            if (DeviceType.PIGEON.getValue() == mDeviceModel.getDeviceType()) {
                itemBean = new BaseItemBean(5, getString(R.string.number_of_pigeon_ring),
                        StringUtils.getNotNullText(mDeviceModel.getRingNo()));
                itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
                mItemList.add(new SectionItem(itemBean));
            }
            itemBean = new BaseItemBean(6, getString(R.string.device_version),
                    StringUtils.getNotNullText(mDeviceModel.getVersion()));
            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
            mItemList.add(new SectionItem(itemBean));
//            itemBean = new BaseItemBean(15, getString(R.string.online_status),
//                    mDeviceModel.isOnlineStatus() ? getString(R.string.online) :
//                            getString(R.string.offline));
//            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
//            mItemList.add(new SectionItem(itemBean));
//            float vol = 0;
//            if (!TextUtils.isEmpty(mDeviceModel.getLastDeviceVol())) {
//                try {
//                    vol = Float.parseFloat(mDeviceModel.getLastDeviceVol());
//                    if (vol < 0)
//                        vol = 0;
//                    else if (vol > 100)
//                        vol = 100;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            itemBean = new BaseItemBean(17, getString(R.string.device_power),
//                    String.format("%s%%", vol));
//            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
//            mItemList.add(new SectionItem(itemBean));
            boolean isActivated =
                    mDeviceModel.getActivated() != null && mDeviceModel.getActivated() == 1;
            itemBean = new BaseItemBean(7, getString(R.string.activated_status),
                    isActivated ? getString(R.string.device_list_selector_activated) :
                            getString(R.string.device_list_selector_not_active));
            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
            mItemList.add(new SectionItem(itemBean));
            itemBean = new BaseItemBean(8, getString(R.string.activated_time),
                    StringUtils.getNotNullText(mDeviceModel.getActivatedDatetime()));
            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
            mItemList.add(new SectionItem(itemBean));
            itemBean = new BaseItemBean(9, getString(R.string.expire_time),
                    StringUtils.getNotNullText(mDeviceModel.getExpireDate()));
            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
            mItemList.add(new SectionItem(itemBean));
            itemBean = new BaseItemBean(10, getString(R.string.bind_time),
                    StringUtils.getNotNullText(mDeviceModel.getBindDatetime()));
            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
            mItemList.add(new SectionItem(itemBean));
            itemBean = new BaseItemBean(11, getString(R.string.unbind_time),
                    StringUtils.getNotNullText(mDeviceModel.getUnbindDatetime()));
            itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
            mItemList.add(new SectionItem(itemBean));
            if (DeviceType.PIGEON.getValue() == mDeviceModel.getDeviceType()) {
                itemBean = new BaseItemBean(12, getString(R.string.lose_time),
                        StringUtils.getNotNullText(mDeviceModel.getLoseCreatetime()));
                itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
                mItemList.add(new SectionItem(itemBean));
                itemBean = new BaseItemBean(13, getString(R.string.retrieve_time),
                        StringUtils.getNotNullText(mDeviceModel.getRetrieveCreatetime()));
                itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
                mItemList.add(new SectionItem(itemBean));
            }
            if (DeviceType.PET.getValue() == mDeviceModel.getDeviceType()) {
                int positionInterval = 0;
                int positionType = 1;
                String positionMode;
                if (mDeviceModel.getPositionInterval() != null) {
                    positionInterval = mDeviceModel.getPositionInterval();
                }
                if (mDeviceModel.getPositionType() != null) {
                    positionType = mDeviceModel.getPositionType();
                }
                if (positionType == 2) {
                    positionMode = getString(R.string.pet_location_type_second,
                            positionInterval);
                } else if (positionType == 3) {
                    positionMode = getString(R.string.pet_location_mode_third);
                } else {
                    positionMode = getString(R.string.pet_location_mode_first);
                }
                itemBean = new BaseItemBean(14, getString(R.string.location_mode), positionMode);
                itemBean.setBgDrawable(R.drawable.btn_custom_item_selector);
                mItemList.add(new SectionItem(itemBean));
            }
            itemBean = new BaseItemBean(16, getString(R.string.last_gps_time),
                    StringUtils.getNotNullText(mDeviceModel.getLastGpsTime()));
            itemBean.setBgDrawable(R.drawable.btn_custom_bottom_radius);
            mItemList.add(new SectionItem(itemBean));
            mItemList.add(new SectionItem(true, null));
        }
//        mSexArray = DataServer.getSexArray(mActivity);
//        mGradeArray = DataServer.getGradeArray(mActivity);
//        mClassArray = new String[]{"（1）班", "（2）班", "（3）班", "（4）班", "（5）班", "（6）班", "（7）班",
//                "（8）班", "（9）班", "（10）班", "（11）班", "（12）班", "（13）班", "（14）班", "（15）班", "（16）班",
//                "（17）班", "其他"};
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new BabyInfoAdapter(mItemList);
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
     * 性别选择
     */
    private void showSexPickerView() {
        if (mOptionsPicker == null || !mOptionsPicker.isShowing()) {
            int selectOption = 0;
            for (SectionItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null && itemBean.getType() == 3) {
                    for (int i = 0; i < mSexArray.length; i++) {
                        if (mSexArray[i].equals(itemBean.getContent())) {
                            selectOption = i;
                            break;
                        }
                    }
                    break;
                }
            }
            mOptionsPicker = new OptionsPickerBuilder(mActivity,
                    new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3,
                                                    View v) {
                            for (SectionItem item : mItemList) {
                                BaseItemBean itemBean = item.t;
                                if (itemBean != null && itemBean.getType() == 3) {
                                    itemBean.setContent(mSexArray[options1]);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    })
                    .setTitleText(getString(R.string.sex_select))
                    .setSubmitText(getString(R.string.confirm))
                    .setCancelText(getString(R.string.cancel))
                    .setSelectOptions(selectOption)
                    .build();
            mOptionsPicker.setPicker(mSexArray);
            mOptionsPicker.show();
        }
    }

    /**
     * 年级选择
     */
    private void showGradePickerView() {
        if (mOptionsPicker == null || !mOptionsPicker.isShowing()) {
            int selectOption = 0;
            for (SectionItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null && itemBean.getType() == 6) {
                    for (int i = 0; i < mGradeArray.length; i++) {
                        if (mGradeArray[i].equals(itemBean.getContent())) {
                            selectOption = i;
                            break;
                        }
                    }
                    break;
                }
            }
            mOptionsPicker = new OptionsPickerBuilder(mActivity,
                    new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3,
                                                    View v) {
                            for (SectionItem item : mItemList) {
                                BaseItemBean itemBean = item.t;
                                if (itemBean != null && itemBean.getType() == 6) {
                                    itemBean.setContent(mGradeArray[options1]);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    })
                    .setTitleText(getString(R.string.grade_select))
                    .setSubmitText(getString(R.string.confirm))
                    .setCancelText(getString(R.string.cancel))
                    .setSelectOptions(selectOption)
                    .build();
            mOptionsPicker.setPicker(mGradeArray);
            mOptionsPicker.show();
        }
    }

    /**
     * 班级选择
     */
    private void showClassPickerView() {
//        int selectOption = 0;
//        for (SectionItem item : mItemList) {
//            BaseItemBean itemBean = item.t;
//            if (itemBean != null && itemBean.getType() == 7) {
//                for (int i = 0; i < mGradeArray.length; i++) {
//                    if (mClassArray[i].equals(itemBean.getContent()))
//                        selectOption = i;
//                }
//                break;
//            }
//        }
//        OptionsPickerView pvOptions = new OptionsPickerBuilder(mActivity,
//                new OnOptionsSelectListener() {
//                    @Override
//                    public void onOptionsSelect(int options1, int options2, int options3, View
//                    v) {
//                        for (SectionItem item : mItemList) {
//                            BaseItemBean itemBean = item.t;
//                            if (itemBean != null && itemBean.getType() == 7) {
//                                itemBean.setContent(mClassArray[options1]);
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                })
//                .setTitleText("选择班级")
//                .setSelectOptions(selectOption)
//                .build();
//        pvOptions.setPicker(mClassArray);
//        pvOptions.show();
    }

    /**
     * 日期选择器
     */
    private void showDatePicker() {
        if (mDatePicker == null || !mDatePicker.isShowing()) {
            Calendar calendar = Calendar.getInstance();
            for (SectionItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null && itemBean.getType() == 4) {
                    Date date = TimeUtils.formatUTC(itemBean.getContent(), "yyyy/MM/dd");
                    if (date == null)
                        date = new Date();
                    calendar.setTime(date);
                    break;
                }
            }
            mDatePicker = new TimePickerBuilder(mActivity, new OnTimeSelectListener() {
                @Override
                public void onTimeSelected(Date date, View v) {
                    for (SectionItem item : mItemList) {
                        BaseItemBean itemBean = item.t;
                        if (itemBean != null && itemBean.getType() == 4) {
                            itemBean.setContent(TimeUtils.formatUTC(date.getTime(), "yyyy/MM/dd"));
                            itemBean.setSelect(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            })
                    .setTitleText(getString(R.string.birth_select))
                    .setCancelText(getString(R.string.cancel))
                    .setSubmitText(getString(R.string.confirm))
                    .setLabel(getString(R.string.picker_year), getString(R.string.picker_month),
                            getString(R.string.picker_day), getString(R.string.picker_hour),
                            getString(R.string.picker_minute), getString(R.string.picker_second))
                    .build();
            mDatePicker.setDate(calendar);
            mDatePicker.show();
        }
    }

    /**
     * 身高/体重选择
     */
    private void showDataPickerView(int type, String content) {
        if (mOptionsPicker == null || !mOptionsPicker.isShowing()) {
            int selectOption = 0;
            String[] array;
            String unit;
            String pickerTitle;
            if (type == 10) {
                unit = getString(R.string.height_unit);
                pickerTitle = getString(R.string.selelct_type, getString(R.string.height));
                if (mHeightArray == null) {
                    mHeightArray = new String[151];
                    for (int i = 0; i < mHeightArray.length; i++) {
                        mHeightArray[i] = String.valueOf(i + 100);
                    }
                }
                for (int i = 0; i < mHeightArray.length; i++) {
                    if (String.format("%s%s", mHeightArray[i], unit).equals(content)) {
                        selectOption = i;
                        break;
                    }
                }
                array = mHeightArray;
            } else {
                unit = getString(R.string.weight_unit);
                pickerTitle = getString(R.string.selelct_type, getString(R.string.weight));
                if (mWeightArray == null) {
                    mWeightArray = new String[181];
                    for (int i = 0; i < mWeightArray.length; i++) {
                        mWeightArray[i] = String.valueOf(i + 20);
                    }
                }
                for (int i = 0; i < mWeightArray.length; i++) {
                    if (String.format("%s%s", mWeightArray[i], unit).equals(content)) {
                        selectOption = i;
                        break;
                    }
                }
                array = mWeightArray;
            }
            mOptionsPicker = new OptionsPickerBuilder(mActivity,
                    new OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int options2, int options3,
                                                    View v) {
                            for (SectionItem item : mItemList) {
                                BaseItemBean itemBean = item.t;
                                if (itemBean != null && itemBean.getType() == type) {
                                    if (type == 10)
                                        itemBean.setContent(String.format("%s%s",
                                                mHeightArray[options1],
                                                getString(R.string.height_unit)));
                                    else
                                        itemBean.setContent(String.format("%s%s",
                                                mWeightArray[options1],
                                                getString(R.string.weight_unit)));
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    })
                    .setLabels(unit, null, null)
                    .setTitleText(pickerTitle)
                    .setSubmitText(getString(R.string.confirm))
                    .setCancelText(getString(R.string.cancel))
                    .setSelectOptions(selectOption)
                    .build();
            mOptionsPicker.setPicker(array);
            mOptionsPicker.show();
        }
    }

    /**
     * 刷新设备信息
     */
    private void refreshDeviceInfo() {
        AAADeviceModel deviceModel = mDeviceModel;
        for (SectionItem item : mItemList) {
            BaseItemBean itemBean = item.t;
            if (itemBean != null) {
                switch (itemBean.getType()) {
                    case 0: // 头像
                        if (deviceModel != null && !TextUtils.isEmpty(deviceModel.getHeadPic())) {
                            itemBean.setGroup(deviceModel.getHeadPic());
                        } else {
                            itemBean.setGroup(null);
                        }
                        break;
                    case 1: // 宝贝名称
                        if (deviceModel != null && !TextUtils.isEmpty(deviceModel.getDeviceName())) {
                            itemBean.setContent(deviceModel.getDeviceName());
                            itemBean.setSelect(true);
                        } else {
                            itemBean.setContent(getString(R.string.no_input));
                            itemBean.setSelect(false);
                        }
                        break;
                    case 2: // 备注
                        if (deviceModel != null && !TextUtils.isEmpty(deviceModel.getDeviceRemark())) {
                            itemBean.setContent(deviceModel.getDeviceRemark());
                            itemBean.setSelect(true);
                        } else {
                            itemBean.setContent(getString(R.string.no_input));
                            itemBean.setSelect(false);
                        }
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
     * 修改宝贝资料
     */
    private void updateWatchInfo() {
        if (!NetworkUtils.isNetworkAvailable()) {
            RequestToastUtils.toastNetwork();
            return;
        }
//        UserModel userModel = getUserModel();
//        DeviceModel deviceModel = getDevice();
        AAAUserModel userModel = getTrackUserModel();
        AAADeviceModel deviceModel = getTrackDeviceModel();
        if (userModel != null && deviceModel != null) {
//            DeviceInfoModel infoModel = getDeviceInfo();
//            if (TextUtils.isEmpty(infoModel.getImei())) {
//                infoModel.setImei(deviceModel.getImei());
//            }
//            infoModel.setU_id(userModel.getU_id());
            boolean isChange = false;
            for (SectionItem item : mItemList) {
                BaseItemBean itemBean = item.t;
                if (itemBean != null) {
                    switch (itemBean.getType()) {
                        case 0: // 头像
//                            if (TextUtils.isEmpty(itemBean.getGroup())) {
//                                if (!TextUtils.isEmpty(infoModel.getHead()))
//                                    isChange = true;
//                                infoModel.setHead("");
//                            } else {
//                                if (!itemBean.getGroup().equals(infoModel.getHead()))
//                                    isChange = true;
//                                infoModel.setHead(itemBean.getGroup());
//                            }
                            break;
//                        case 3: // 性别
//                            if (mSexArray[1].equals(itemBean.getContent())) {
//                                if (infoModel.getSex() != 1)
//                                    isChange = true;
//                                infoModel.setSex(1);
//                            } else {
//                                if (infoModel.getSex() != 0)
//                                    isChange = true;
//                                infoModel.setSex(0);
//                            }
//                            break;
//                        case 4: // 生日
//                            if (itemBean.isSelect()) {
//                                if (!itemBean.getContent().equals(infoModel.getBirday()))
//                                    isChange = true;
//                                infoModel.setBirday(itemBean.getContent());
//                            } else {
//                                if (!TextUtils.isEmpty(infoModel.getBirday()))
//                                    isChange = true;
//                                infoModel.setBirday("");
//                            }
//                            break;
//                        case 6: // 年级
//                            String grade = infoModel.getSchool_age();
//                            boolean isFind = false;
//                            for (int i = 0; i < mGradeArray.length; i++) {
//                                if (mGradeArray[i].equals(itemBean.getContent())) {
//                                    infoModel.setSchool_age(String.valueOf(i));
//                                    isFind = true;
//                                }
//                            }
//                            if (!isFind)
//                                infoModel.setSchool_age("0");
//                            if (!infoModel.getSchool_age().equals(grade))
//                                isChange = true;
//                            break;
//                        case 10: // 身高
//                            String height = infoModel.getHeight();
//                            if (TextUtils.isEmpty(height))
//                                height = "175";
//                            if (!String.format("%s%s", height, getString(R.string.height_unit))
//                            .equals(itemBean.getContent())) {
//                                isChange = true;
//                                infoModel.setHeight(itemBean.getContent().split(getString(R
//                                .string.height_unit))[0]);
//                            }
//                            break;
//                        case 11: // 体重
//                            String weight = infoModel.getWeight();
//                            if (TextUtils.isEmpty(weight))
//                                weight = "60";
//                            if (!String.format("%s%s", weight, getString(R.string.weight_unit))
//                            .equals(itemBean.getContent())) {
//                                isChange = true;
//                                infoModel.setWeight(itemBean.getContent().split(getString(R
//                                .string.weight_unit))[0]);
//                            }
//                            break;
                        default:
                            break;
                    }
                }
            }
//            if (isChange)
//                CWRequestUtils.getInstance().updateWatchInfo(MainApplication.getInstance(),
//                        userModel.getToken(),
//                        deviceModel.getD_id(), infoModel, mHandler);
        }
    }

    /**
     * 获取家庭wifi
     */
    private void getFamilyWifi() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getFamilyWifi(getContext(), getIp(), userModel.getToken(),
                    deviceModel.getD_id(), mHandler);
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
                                                            , BabyInfoFragment.this, 1);
                                                else if (position == 1)
                                                    PictureSelectorUtils.selectPortrait(mActivity
                                                            , BabyInfoFragment.this, 0);
                                            }
                                        })
                                        .build();
                                mBottomSheet.show();
                            }
                        }
                        break;
                    case 1: // 宝贝名称
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 4);
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        if (itemBean.isSelect())
                            bundle.putString(CWConstant.CONTENT, itemBean.getContent());
                        else
                            bundle.putString(CWConstant.CONTENT, "");
                        bundle.putString(TConstant.DEVICE_IMEI, deviceImei);
                        openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                CWConstant.REQUEST_SELECT_NAME);
                        break;
                    case 2: // 备注
                        bundle = new Bundle();
                        bundle.putInt(CWConstant.TYPE, 8);
                        bundle.putString(CWConstant.TITLE, itemBean.getTitle());
                        if (itemBean.isSelect())
                            bundle.putString(CWConstant.CONTENT, itemBean.getContent());
                        else
                            bundle.putString(CWConstant.CONTENT, "");
                        bundle.putString(TConstant.DEVICE_IMEI, deviceImei);
                        openNewPageForResult(CustomInputSecondFragment.class, bundle,
                                CWConstant.REQUEST_OTHER);
                        break;
//                    case 3: // 性别
//                        showSexPickerView();
//                        break;
//                    case 4: // 生日
//                        showDatePicker();
//                        break;
//                    case 5: // 学校
//                        break;
//                    case 6: // 年级
//                        showGradePickerView();
//                        break;
//                    case 7: // 班级
//                        showClassPickerView();
//                        break;
//                    case 8: // 家庭地址
//                        break;
//                    case 9: // 家里wifi
//                        openNewPageForResult(WifiFragment.class, CWConstant.REQUEST_OTHER);
//                        break;
//                    case 10: // 身高
//                    case 11: // 体重
//                        showDataPickerView(itemBean.getType(), itemBean.getContent());
//                        break;
                    default:
                        break;
                }
            }
        }
    }

    @SuppressLint("CheckResult")
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
//                        for (SectionItem item : mItemList) {
//                            BaseItemBean itemBean = item.t;
//                            if (itemBean != null && itemBean.getType() == 0) {
//                                itemBean.setGroup(path);
//                                mAdapter.notifyDataSetChanged();
//                                break;
//                            }
//                        }
                        if (!TextUtils.isEmpty(path)) {
                            CarGpsRequestUtils.uploadDeviceHeadPortrait(getTrackUserModel(),
                                    mDeviceModel.getDeviceImei(),
                                    convertFileToBase64(path), "123456", mHandler);
//                            OSSAsyncTask task =
//                                    CWRequestUtils.getInstance().uploadFile(getDevice(),
//                                            path, mHandler);
//                            if (task != null)
//                                showUploadDialog(task);

//                            File compressfile = new File(localMedia.getPath());
//                            if (compressfile.exists() && compressfile.length() > 100 * 1024) {
//                                new Compressor(mActivity)
//                                        .setMaxWidth(480)
//                                        .setMaxHeight(480)
//                                        .setQuality(75)
//                                        .setCompressFormat(Bitmap.CompressFormat.PNG)
//                                        .compressToFileAsFlowable(compressfile)
//                                        .subscribeOn(Schedulers.io())
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(new Consumer<File>() {
//                                            @Override
//                                            public void accept(File file) {
//                                                KLog.d(file.getPath() + "," + file.length());
//                                                OSSAsyncTask task =
//                                                        CWRequestUtils.getInstance().uploadFile
//                                                        (getDevice(),
//                                                                file.getPath(), mHandler);
//                                                if (task != null)
//                                                    showUploadDialog(task);
//                                            }
//                                        }, new Consumer<Throwable>() {
//                                            @Override
//                                            public void accept(Throwable throwable) {
//                                                XToastUtils.toast(R.string
//                                                .upload_file_error_prompt);
//                                                if (BuildConfig.DEBUG)
//                                                    throwable.printStackTrace();
//                                            }
//                                        });
//                            } else if (compressfile.exists()) {
//                                OSSAsyncTask task =
//                                        CWRequestUtils.getInstance().uploadFile(getDevice(),
//                                                path, mHandler);
//                                if (task != null)
//                                    showUploadDialog(task);
//                            }
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
                case CWConstant.REQUEST_SELECT_NAME: // 修改宝贝名称
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            for (SectionItem item : mItemList) {
                                BaseItemBean itemBean = item.t;
                                if (itemBean != null && itemBean.getType() == 1) {
                                    itemBean.setContent(bundle.getString(CWConstant.NAME));
                                    itemBean.setSelect(true);
                                    mAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case CWConstant.REQUEST_OTHER: // 其它
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            for (SectionItem item : mItemList) {
                                BaseItemBean itemBean = item.t;
                                if (itemBean != null && itemBean.getType() == 2) {
                                    itemBean.setContent(bundle.getString(CWConstant.NAME));
                                    itemBean.setSelect(true);
                                    mAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
//                    refreshDeviceInfo(null);
                    break;
                default:
                    break;
            }
        }
//        else if (requestCode == CWConstant.REQUEST_OTHER)
//            refreshDeviceInfo(null);
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
                    case CWConstant.REQUEST_URL_GET_WATCH_INFO: // 查看宝贝资料
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    DeviceModel deviceModel = getDevice();
                                    if (deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                        DeviceInfoModel infoModel =
                                                mGson.fromJson(mGson.toJson(resultBean.getResultBean()), DeviceInfoModel.class);
                                        infoModel.setU_id(userModel.getU_id());
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
                                        refreshDeviceInfo();
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
                                }
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        break;
                    case CWConstant.REQUEST_URL_GET_FAMILY_WIFI: // 获取家庭wifi
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                userModel = getUserModel();
                                if (userModel != null) {
                                    requestBean =
                                            mGson.fromJson(mGson.toJson(resultBean.getRequestObject()), RequestBean.class);
                                    DeviceModel deviceModel = getDevice();
                                    DeviceSettingsModel settingsModel = getDeviceSettings();
                                    if (settingsModel != null && deviceModel != null && deviceModel.getD_id() == requestBean.getD_id()) {
                                        RequestBean model =
                                                mGson.fromJson(mGson.toJson(resultBean.getResultBean()), RequestBean.class);
                                        settingsModel.setWifi(model.getWifi());
                                        settingsModel.setWifiType(model.getStatus());
                                        settingsModel.save();
                                        for (SectionItem item : mItemList) {
                                            BaseItemBean itemBean = item.t;
                                            if (itemBean != null && itemBean.getType() == 9) {
                                                if (TextUtils.isEmpty(settingsModel.getWifi()))
                                                    itemBean.setContent(getString(R.string.no_input));
                                                else {
                                                    String[] array =
                                                            settingsModel.getWifi().split(CWConstant.WIFI_SEPARATE);
                                                    String wifiSelectWifiName;
                                                    if (array.length == 0)
                                                        wifiSelectWifiName = "";
                                                    else
                                                        wifiSelectWifiName = array[0];
                                                    itemBean.setContent(wifiSelectWifiName);
                                                }
                                                mAdapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
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
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(CWConstant.NAME, requestBean.getNickname());
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
                            String headPath = (String) msg.obj;
                            for (SectionItem item : mItemList) {
                                BaseItemBean itemBean = item.t;
                                if (itemBean != null && itemBean.getType() == 0) {
                                    itemBean.setGroup(headPath);
                                    mAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            setBabyNameAndHead(headPath);
                            DeviceInfoModel infoModel = getDeviceInfo();
                            if (!TextUtils.isEmpty(infoModel.getHead()))
                                CWRequestUtils.getInstance().deleteFile(infoModel.getHead());
                            infoModel.setHead(headPath);
                        }
                        break;
                    case TConstant.REQUEST_UPLOAD_DEVICE_HEAD_PORTRAIT:
                        if (msg.obj == null) {
                            showMessage(R.string.network_error_prompt);
                        } else {
                            try {
                                AAABaseResponseBean response = (AAABaseResponseBean) msg.obj;
                                if (response.getCode() == 1) {
                                    String imgUrl = response.getMsg();
                                    if (imgUrl != null) {
                                        mDeviceModel.setHeadPic(imgUrl);
                                        List<AAADeviceModel> trackDeviceList = getTrackDeviceList();
                                        for (AAADeviceModel model : trackDeviceList) {
                                            if (mDeviceModel.getDeviceImei().equals(model.getDeviceImei())) {
                                                model.setHeadPic(imgUrl);
                                                break;
                                            }
                                        }
                                        for (SectionItem item : mItemList) {
                                            BaseItemBean itemBean = item.t;
                                            if (itemBean != null && itemBean.getType() == 0) {
                                                itemBean.setGroup(imgUrl);
                                                mAdapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
                                    showMessage(R.string.update_success_prompt);
                                } else {
                                    showMessage(R.string.update_failed_prompt);
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
        if (mDatePicker != null && mDatePicker.isShowing())
            mDatePicker.dismiss();
        if (mOptionsPicker != null && mOptionsPicker.isShowing())
            mOptionsPicker.dismiss();
        updateWatchInfo();
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
