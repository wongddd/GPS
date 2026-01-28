package com.yyt.trackcar.ui.fragment;

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

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.DeviceSysMsgModel;
import com.yyt.trackcar.dbflow.DeviceSysMsgModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.DeviceMessageAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;
import com.yyt.trackcar.utils.RequestToastUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TimeUtils;
import com.yyt.trackcar.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(name = "DeviceMessage")
public class DeviceMessageFragment extends BaseFragment {
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout; // 下拉加载控件
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private DeviceMessageAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.device_message);
        titleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_settings) {
            @Override
            public void performAction(View view) {
                openNewPage(SettingsFragment.class);
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        mRefreshLayout.setBackgroundResource(R.color.colorLayoutBackground);
        mRefreshLayout.setEnableLoadMore(false);
        initItems();
        initAdapters();
        initRecyclerViews();
        initEmptyView();
        getWatchMsg();
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
                    refreshLayout.finishRefresh();
                    return;
                }
                getWatchMsg();
            }
        });
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            List<DeviceSysMsgModel> list = SQLite.select().from(DeviceSysMsgModel.class)
                    .where(DeviceSysMsgModel_Table.imei.eq(deviceModel.getImei()))
                    .orderBy(DeviceSysMsgModel_Table.createtime, false)
                    .queryList();
            for (DeviceSysMsgModel model : list) {
                addMsg(model);
            }
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new DeviceMessageAdapter(mItemList);
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
        tvEmpty.setText(R.string.no_message_prompt);
        mAdapter.setEmptyView(emptyView);
    }

    /**
     * 添加设备系统消息
     *
     * @param msgBean 消息对象
     */
    private void addMsg(DeviceSysMsgModel msgBean) {
        boolean isSameDate = false;
        String msgHeader = TimeUtils.getDateDescriptionByNow(mActivity,
                TimeUtils.formatUTCC(msgBean.getCreatetime(), null));
        for (int i = mItemList.size() - 1; i >= 0; i--) {
            BaseItemBean itemBean = mItemList.get(i).t;
            if (itemBean != null) {
                if (msgHeader.equals(itemBean.getGroup()))
                    isSameDate = true;
                break;
            }
        }
        if (!isSameDate) {
            mItemList.add(new SectionItem(true, msgHeader));
        }
        BaseItemBean itemBean = new BaseItemBean();
        itemBean.setGroup(msgHeader);
//        itemBean.setObject(msgBean);
        itemBean.setTitle(msgBean.getIntroduction());
        itemBean.setContent(TimeUtils.formatUTC(msgBean.getCreatetime(), "HH:mm"));
        String introduction;
        String[] array;
        int deviceType = SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0);
        switch (msgBean.getType()) {
            case CWConstant.SOS: //SOS
                itemBean.setImgDrawable(R.mipmap.ic_sos);
                if (deviceType == 0)
                    itemBean.setTitle(getString(R.string.msg_sos));
                else
                    itemBean.setTitle(getString(R.string.msg_device_sos));
                break;
//            case CWConstant.LOCATION: //定位
//                itemBean.setImgDrawable(R.mipmap.ic_settings_location);
////                itemBean.setTitle(getString(R.string.msg_location));
//                break;
            case CWConstant.ADDFRIEND: //修改好友通讯录
                itemBean.setImgDrawable(R.mipmap.ic_add_new_friend);
                if (deviceType == 0)
                    itemBean.setTitle(getString(R.string.msg_update_contancts));
                else
                    itemBean.setTitle(getString(R.string.msg_device_update_contancts));
                break;
//            case CWConstant.STEP: //计步上报
//                itemBean.setImgDrawable(R.mipmap.ic_watch_electric);
////                itemBean.setTitle(getString(R.string.msg_step));
//                break;
            case CWConstant.UPLOADPHOTO: //图片上传
                itemBean.setImgDrawable(R.mipmap.ic_upload_photo);
                if (deviceType == 0)
                    itemBean.setTitle(getString(R.string.msg_photo));
                else
                    itemBean.setTitle(getString(R.string.msg_device_photo));
                break;
            case CWConstant.PHONELOG: //通话记录上传
                itemBean.setImgDrawable(R.mipmap.ic_call_record_log);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                array = introduction.split("_");
                if (deviceType == 0) {
                    if (array.length < 2)
                        itemBean.setTitle(getString(R.string.msg_call_record,
                                getString(R.string.strange_number)));
                    else {
                        String name = introduction.substring(0,
                                introduction.length() - array[array.length - 1].length() - 1);
                        if (TextUtils.isEmpty(name) || "0".equals(name))
                            itemBean.setTitle(getString(R.string.msg_call_record,
                                    String.format("%s(%s)", getString(R.string.strange_number),
                                            array[array.length - 1])));
                        else
                            itemBean.setTitle(getString(R.string.msg_call_record, name));
                    }
                } else {
                    if (array.length < 2)
                        itemBean.setTitle(getString(R.string.msg_device_call_record,
                                getString(R.string.strange_number)));
                    else {
                        String name = introduction.substring(0,
                                introduction.length() - array[array.length - 1].length() - 1);
                        if (TextUtils.isEmpty(name) || "0".equals(name))
                            itemBean.setTitle(getString(R.string.msg_device_call_record,
                                    String.format("%s(%s)", getString(R.string.strange_number),
                                            array[array.length - 1])));
                        else
                            itemBean.setTitle(getString(R.string.msg_device_call_record, name));
                    }
                }
                break;
//            case CWConstant.COST1: //短信上报
//                itemBean.setImgDrawable(R.mipmap.ic_watch_electric);
////                itemBean.setTitle(getString(R.string.msg_sms));
//                break;
            case CWConstant.UPGRADE: //设备软件升级完毕
                itemBean.setImgDrawable(R.mipmap.ic_settings_update);
                itemBean.setTitle(getString(R.string.msg_update_version));
                break;
            case CWConstant.FENCE: // 电子围栏
                itemBean.setImgDrawable(R.mipmap.ic_settings_fence);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                array = introduction.split("_");
                if (deviceType == 0) {
                    if (array.length < 2)
                        itemBean.setTitle(getString(R.string.unknow));
                    else if ("1".equals(array[0]))
                        itemBean.setTitle(getString(R.string.msg_entry_fence,
                                introduction.substring(2)));
                    else
                        itemBean.setTitle(getString(R.string.msg_leave_fence,
                                introduction.substring(2)));
                } else {
                    if (array.length < 2)
                        itemBean.setTitle(getString(R.string.unknow));
                    else if ("1".equals(array[0]))
                        itemBean.setTitle(getString(R.string.msg_device_entry_fence,
                                introduction.substring(2)));
                    else
                        itemBean.setTitle(getString(R.string.msg_device_leave_fence,
                                introduction.substring(2)));
                }
                break;
            case CWConstant.LOW_ENERGY: // 低电量
                itemBean.setImgDrawable(R.mipmap.ic_entry_reserved_electric);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "0";
                if (deviceType == 0)
                    itemBean.setTitle(getString(R.string.msg_low_energy, introduction));
                else
                    itemBean.setTitle(getString(R.string.msg_device_low_energy, introduction));
                break;
            case CWConstant.POWERSAVEMODEL: // 预留电量
                itemBean.setImgDrawable(R.mipmap.ic_power_save);
                if (deviceType == 0)
                    itemBean.setTitle(getString(R.string.msg_reserved_electric));
                else
                    itemBean.setTitle(getString(R.string.msg_device_reserved_electric));
                break;
            case CWConstant.POWEROFF: // 关机
                itemBean.setImgDrawable(R.mipmap.ic_power_off);
                if (deviceType == 0)
                    itemBean.setTitle(getString(R.string.msg_shutdown));
                else
                    itemBean.setTitle(getString(R.string.msg_device_shutdown));
                break;
            case CWConstant.AGREE_BIND: // 同意绑定
                itemBean.setImgDrawable(R.mipmap.ic_agree_bind);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                itemBean.setTitle(getString(R.string.msg_agree_bind, introduction));
                break;
            case CWConstant.REFUSE_BIND: // 拒绝绑定
                itemBean.setImgDrawable(R.mipmap.ic_refuse_bind);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                itemBean.setTitle(getString(R.string.msg_refuse_bind, introduction));
                break;
            case CWConstant.MANAGER_BIND: // 请求绑定
                itemBean.setImgDrawable(R.mipmap.ic_manager_bind);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                itemBean.setTitle(getString(R.string.msg_request_bind, introduction));
                break;
            case CWConstant.MANAGER_UNBIND: // 解除绑定
                itemBean.setImgDrawable(R.mipmap.ic_manager_unbind);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                itemBean.setTitle(getString(R.string.msg_unbind, introduction));
                break;
            case CWConstant.TRANSFER_MANAGER: // 转让管理
                itemBean.setImgDrawable(R.mipmap.ic_transfer_manager);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                itemBean.setTitle(getString(R.string.msg_transfer_manager, introduction));
                break;
            case CWConstant.DELETE_ALL_USER_BIND: // 解绑全部用户
                itemBean.setImgDrawable(R.mipmap.ic_manager_unbind);
                itemBean.setTitle(getString(R.string.msg_manager_unbind));
                break;
            case CWConstant.FAMILYWIFI: // wifi
                itemBean.setImgDrawable(R.mipmap.ic_family_wifi);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                array = introduction.split("_");
                if (array.length < 2)
                    itemBean.setTitle(getString(R.string.unknow));
                else if ("1".equals(array[0]))
                    itemBean.setTitle(getString(R.string.msg_wifi_success,
                            introduction.substring(2)));
                else
                    itemBean.setTitle(getString(R.string.msg_wifi, introduction.substring(2)));
                break;
            case CWConstant.OPEN_DEVICE_LOST: //设备开启挂失
                itemBean.setImgDrawable(R.mipmap.ic_device_lost);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                itemBean.setTitle(getString(R.string.msg_open_loss, introduction));
                break;
            case CWConstant.CLOSE_DEVICE_LOST: //设备解除挂失
                itemBean.setImgDrawable(R.mipmap.ic_device_lost);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                itemBean.setTitle(getString(R.string.msg_close_loss, introduction));
                break;
            case CWConstant.PASSWORD_OPEN_DEVICE: //设备使用密码解锁
                itemBean.setImgDrawable(R.mipmap.ic_pwd_open_device);
                if ("1".equals(msgBean.getIntroduction()))
                    itemBean.setTitle(getString(R.string.msg_pwd_unlock_success));
                else
                    itemBean.setTitle(getString(R.string.msg_pwd_unlock));
                break;
            case CWConstant.DEVICE_STEP_TARGET: //手表达到目标步数
                itemBean.setImgDrawable(R.mipmap.ic_settings_sport);
                itemBean.setTitle(getString(R.string.msg_step_target));
                break;
            case CWConstant.DEVICE_CHANGE_NAME: //修改昵称
                itemBean.setImgDrawable(R.mipmap.ic_message_type_other);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction))
                    introduction = "";
                itemBean.setTitle(getString(R.string.msg_change_device_name, introduction));
                break;
            case CWConstant.LAGENIO_01: //健康数据
                itemBean.setImgDrawable(R.mipmap.ic_health_heart_rate);
                introduction = msgBean.getIntroduction();
                StringBuilder healthMessage = new StringBuilder();
                if (TextUtils.isEmpty(introduction) || "0,0,0,0,0".equals(introduction))
                    healthMessage.append(getString(R.string.msg_health_data_test_fail));
                else {
                    healthMessage.append(getString(R.string.msg_health_data));
                    array = introduction.split(",");
                    if (array.length > 0 && !TextUtils.isEmpty(array[0]) && !"0".equals(array[0]))
                        healthMessage.append(",").append(getString(R.string.heart_rate)).append(":")
                                .append(array[0]).append(getString(R.string.heart_rate_unit));
                    if (array.length > 1 && !TextUtils.isEmpty(array[1]) && !"0".equals(array[1]))
                        healthMessage.append(",").append(getString(R.string.blood_oxygen)).append(":")
                                .append(array[1]).append("%");
                    if (array.length > 3 && !TextUtils.isEmpty(array[2]) && !"0".equals(array[2]))
                        healthMessage.append(",").append(getString(R.string.blood_pressure)).append(":")
                                .append(array[2]).append("/").append(array[3])
                                .append(getString(R.string.blood_pressure_unit));
                    if (array.length > 4 && !TextUtils.isEmpty(array[4]) && !"0".equals(array[4]))
                        healthMessage.append(",").append(getString(R.string.temperature))
                                .append(":").append(array[4]).append(getString(R.string.temperature_unit));
                }
                itemBean.setTitle(healthMessage.toString());
                break;
            case CWConstant.FALL_OFF: // 脱落提示
                itemBean.setImgDrawable(R.mipmap.ic_fall_off);
                introduction = msgBean.getIntroduction();
                if (TextUtils.isEmpty(introduction) || !"1".equals(introduction.substring(0, 1)))
                    itemBean.setTitle(getString(R.string.msg_deivce_put_on));
                else
                    itemBean.setTitle(getString(R.string.msg_deivce_fall_off));
                break;
            default:
                itemBean.setImgDrawable(R.mipmap.ic_message_type_other);
                itemBean.setTitle(getString(R.string.unknow));
                break;
        }
        mItemList.add(new SectionItem(itemBean));
    }

    /**
     * 获取设备系统通知消息
     */
    private void getWatchMsg() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getWatchMsg(getContext(),
                    userModel.getToken(), deviceModel.getD_id(), mHandler);
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                RequestResultBean resultBean;
                switch (msg.what) {
                    case CWConstant.REQUEST_URL_GET_WATCH_MSG: // 获取设备系统通知消息
                        if (msg.obj == null)
                            XToastUtils.toast(R.string.request_unkonow_prompt);
                        else {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                mItemList.clear();
                                if (resultBean.getMsgList() != null) {
                                    for (Object obj : resultBean.getMsgList()) {
                                        DeviceSysMsgModel msgBean =
                                                mGson.fromJson(mGson.toJson(obj),
                                                        DeviceSysMsgModel.class);
                                        msgBean.save();
                                    }
                                }
                                initItems();
                                mAdapter.notifyDataSetChanged();
                            } else
                                RequestToastUtils.toast(resultBean.getCode());
                        }
                        mRefreshLayout.finishRefresh();
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
