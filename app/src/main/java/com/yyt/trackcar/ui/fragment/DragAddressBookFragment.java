package com.yyt.trackcar.ui.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
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

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      DragAddressBookFragment
 * @ author:        QING
 * @ createTime:    2020/3/12 18:41
 * @ describe:      TODO 宝贝通讯录排序界面
 */
@Page(name = "DragAddressBook", params = {CWConstant.LIST})
public class DragAddressBookFragment extends BaseFragment implements OnItemDragListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private AddressBookAdapter mAdapter; // 适配器
    private List<ContactBean> mItemList = new ArrayList<>(); // 列表
    @AutoWired
    String list; // 列表

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
        titleBar.setTitle(R.string.address_book_sort);
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.save)) {
            @Override
            public void performAction(View view) {
                if (mItemList.size() > 0) {
                    StringBuilder contactString = new StringBuilder();
                    for (ContactBean contactBean : mItemList) {
                        contactString.append("#").append(contactBean.getContactString());
                    }
                    setContacts(contactString.toString().substring(1));
                }
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderAndFooterView();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        if (!TextUtils.isEmpty(list)) {
            String[] array = list.split("#");
            for (String str : array) {
                addItem(str);
            }
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new AddressBookAdapter(mItemList);
        mAdapter.setType(1);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        // 开启拖拽
        mAdapter.enableDragItem(itemTouchHelper);
        mAdapter.setOnItemDragListener(this);
    }

    /**
     * 初始化脚布局
     */
    private void initHeaderAndFooterView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_custom_section, mRecyclerView,
                false);
        TextView tvTitle = headerView.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.address_book_sort_prompt);
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section, mRecyclerView,
                false);
        mAdapter.addHeaderView(headerView);
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

    @Override
    public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
        final BaseViewHolder holder = ((BaseViewHolder) viewHolder);

        // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
        int startColor = Color.WHITE;
        int endColor = Color.rgb(245, 245, 245);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    holder.itemView.setBackgroundColor((int) animation.getAnimatedValue());
                }
            });
            v.setDuration(300);
            v.start();
        }
    }

    @Override
    public void onItemDragMoving(RecyclerView.ViewHolder source, int from,
                                 RecyclerView.ViewHolder target, int to) {

    }

    @Override
    public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
        final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
        // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
        int startColor = Color.rgb(245, 245, 245);
        int endColor = Color.WHITE;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
            v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    holder.itemView.setBackgroundColor((int) animation.getAnimatedValue());
                    if ((int) animation.getAnimatedValue() == endColor)
                        mAdapter.notifyDataSetChanged();
                }
            });
            v.setDuration(300);
            v.start();
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
