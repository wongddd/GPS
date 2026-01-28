package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.MainApplication;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.PostMessage;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.adapter.CustomSelectorAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(name = "deviceFragmentPage",anim = CoreAnim.none)
public class DeviceListOldFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener{

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private CustomSelectorAdapter mAdapter; // 适配器
    private final List<SectionItem> mItemList = new ArrayList<>(); // 列表
    private List<AAADeviceModel> devices = new ArrayList<>();
    private AAADeviceModel deviceModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initViews() {
        initDatas();
        initItems();
        initAdapters();
        initRecyclerViews();
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setLeftImageDrawable(null);
        titleBar.setTitle(R.string.device_list);
        titleBar.setLeftClickListener(null);
        return titleBar;
    }

    private void initDatas() {
        deviceModel = getTrackDeviceModel();
        devices = getTrackDeviceList();
        if (devices == null || devices.size() == 0){
            devices = SQLite.select().from(AAADeviceModel.class).queryList();
        }
    }

    private void initItems() {
        mItemList.clear();
        mItemList.add(new SectionItem(true, null));
        BaseItemBean itemBean;
        for (int i = 0; i < devices.size(); i++) {
            itemBean = new BaseItemBean(i,devices.get(i).getDeviceImei());
            itemBean.setTitle(devices.get(i).getDeviceName());
            itemBean.setContent(devices.get(i).getDeviceImei());
            itemBean.setSelect(deviceModel.getDeviceImei().equals(devices.get(i).getDeviceImei()));
            mItemList.add(new SectionItem(itemBean));
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new CustomSelectorAdapter(mItemList);
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean == null) return;
            if (!itemBean.isSelect()) {
                for (SectionItem item : mItemList) {
                    BaseItemBean bean = item.t;
                    if (bean != null)
                        bean.setSelect(false);
                }
                showDialog();
                MainApplication.getInstance().setTrackDeviceModel(devices.get(position - 1));
                SettingSPUtils.getInstance().putString(TConstant.SELECTED_IMEI, devices.get(position - 1).getDeviceImei());
                SettingSPUtils.getInstance().putInt(CWConstant.DEVICE_TYPE,devices.get(position-1).getDeviceType());
                itemBean.setSelect(true);
                mAdapter.notifyDataSetChanged();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep((int)(Math.random()*720)+500);
                            EventBus.getDefault().post(new PostMessage(CWConstant.POST_MESSAGE_CHANGE_DEVICE));
                            dismisDialog();
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    @Subscribe(threadMode =ThreadMode.MAIN)
    public void handlePostMessage(PostMessage event) {
        if (CWConstant.POST_MESSAGE_CHANGE_DEVICE == event.getType()
        ||  CWConstant.POST_MESSAGE_BACK_TO_MAIN == event.getType()){
            initDatas();
            initItems();
            if (mAdapter!= null)
                mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        mItemList.clear();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
