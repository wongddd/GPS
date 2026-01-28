package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.RequestBean;
import com.yyt.trackcar.bean.RequestResultBean;
import com.yyt.trackcar.dbflow.DeviceModel;
import com.yyt.trackcar.dbflow.SmsModel;
import com.yyt.trackcar.dbflow.SmsModel_Table;
import com.yyt.trackcar.dbflow.UserModel;
import com.yyt.trackcar.ui.adapter.SmsCollectionAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CWRequestUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      SmsCollectionFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 10:17
 * @ describe:      TODO 代收手表短信页面
 */
@Page(name = "SmsCollection")
public class SmsCollectionFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.clBottom)
    View mBottomView; // 底部布局
    @BindView(R.id.clDel)
    View mDelView; // 删除布局
    @BindView(R.id.switchBtn)
    Button mSwitchBtn; // 开启/关闭按钮
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    @BindView(R.id.clEmpty)
    View mEmptyView;
    private SmsCollectionAdapter mAdapter; // 适配器
    private List<SmsModel> mItemList = new ArrayList<>(); // 列表
    private TextView mTvContent; // 开启/关闭文本
    private TitleBar mTitleBar; // 标题栏
    private boolean mIsEdit; // 是否编辑
    private boolean mIsOpen = true; // 是否开启

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sms_collection;
    }

    @Override
    protected TitleBar initTitle() {
        mTitleBar = super.initTitle();
        mTitleBar.setTitle(R.string.sms_collection_title);
        mTitleBar.addAction(getAction());
        return mTitleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderView();
        initFooterView();
        initEmptyView();
        initSmsCollectionContent();
        getDeviceSms();
        mBottomView.setVisibility(View.GONE);
        mSwitchBtn.setVisibility(View.GONE);
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DeviceModel deviceModel = getDevice();
        if (deviceModel != null) {
            mItemList.addAll(SQLite.select().from(SmsModel.class)
                    .where(SmsModel_Table.imei.eq(deviceModel.getImei()))
                    .orderBy(SmsModel_Table.get_time, false)
                    .queryList());
            if (mItemList.size() == 1)
                mItemList.get(0).setBgDrawable(R.drawable.bg_white_round);
            else if (mItemList.size() > 1) {
                mItemList.get(0).setBgDrawable(R.drawable.bg_custom_top_radius);
                mItemList.get(mItemList.size() - 1).setBgDrawable(R.drawable.bg_custom_bottom_radius);
            }
        }
        if(mItemList.size() == 0)
            mEmptyView.setVisibility(View.VISIBLE);
        else
            mEmptyView.setVisibility(View.GONE);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new SmsCollectionAdapter(mItemList);
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
    private void initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_view_sms_collection,
                mRecyclerView, false);
        mTvContent = headerView.findViewById(R.id.tvContent);
        if (mIsOpen)
            mTvContent.setText(R.string.sms_collection_open);
        else
            mTvContent.setText(R.string.sms_collection_close);
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
     * 初始化空布局
     */
    private void initEmptyView() {
        mEmptyView = findViewById(R.id.clEmpty);
        mEmptyView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color
                .white));
        ImageView ivEmpty = findViewById(R.id.ivEmpty);
        TextView tvEmpty = findViewById(R.id.tvEmpty);
        ivEmpty.setImageResource(R.mipmap.ic_no_query_data);
        tvEmpty.setText(R.string.no_data_prompt);
    }

    /**
     * 初始化标题栏动作
     *
     * @return 动作
     */
    private TitleBar.Action getAction() {
        TitleBar.Action action;
        if (mIsEdit)
            action = new TitleBar.TextAction(getString(R.string.complete)) {
                @Override
                public void performAction(View view) {
                    mIsEdit = false;
                    initHeaderView();
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    mDelView.setVisibility(View.GONE);
                    mBottomView.setVisibility(View.GONE);
//                    mSwitchBtn.setVisibility(View.VISIBLE);
                    mTitleBar.removeAllActions();
                    mTitleBar.addAction(getAction());
                }
            };
        else
            action = new TitleBar.TextAction(getString(R.string.edit)) {
                @Override
                public void performAction(View view) {
                    mIsEdit = true;
                    for (SmsModel smsModel : mItemList) {
                        smsModel.setSelect(false);
                    }
                    mAdapter.removeAllHeaderView();
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    mDelView.setVisibility(View.VISIBLE);
                    mBottomView.setVisibility(View.VISIBLE);
//                    mSwitchBtn.setVisibility(View.GONE);
                    mTitleBar.removeAllActions();
                    mTitleBar.addAction(getAction());
                }
            };
        return action;
    }

    /**
     * 初始化代收手表短信状态
     */
    private void initSmsCollectionContent() {
        if (mIsOpen) {
            mTvContent.setText(R.string.sms_collection_open);
            mSwitchBtn.setText(getString(R.string.open_status));
        } else {
            mTvContent.setText(R.string.sms_collection_close);
            mSwitchBtn.setText(getString(R.string.close_status));
        }
    }

    /**
     * 用户获取上报短信
     */
    private void getDeviceSms() {
        UserModel userModel = getUserModel();
        DeviceModel deviceModel = getDevice();
        if (userModel != null && deviceModel != null)
            CWRequestUtils.getInstance().getDeviceSms(getContext(), userModel.getToken(),
                    deviceModel.getD_id(), mHandler);
    }

    @SingleClick
    @OnClick({R.id.clDel, R.id.switchBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clDel: // 删除
                if (mIsEdit) { // 删除
                    boolean isDel = false;
                    for (int i = mItemList.size() - 1; i >= 0; i--) {
                        SmsModel smsModel = mItemList.get(i);
                        if (smsModel.isSelect()) {
                            isDel = true;
                            OperatorGroup operatorGroup =
                                    OperatorGroup.clause(OperatorGroup.clause()
                                            .and(SmsModel_Table.imei.eq(smsModel.getImei()))
                                            .and(SmsModel_Table.phone.eq(smsModel.getPhone()))
                                            .and(SmsModel_Table.get_time.eq(smsModel.get_time())));
                            SQLite.delete(SmsModel.class).where(operatorGroup).execute();
                            mItemList.remove(i);
                        }
                    }
                    if (isDel)
                        mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.switchBtn: // 开启/关闭
                mIsOpen = !mIsOpen;
                initSmsCollectionContent();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size() && mIsEdit) {
            SmsModel smsModel = mItemList.get(position);
            smsModel.setSelect(!smsModel.isSelect());
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
                    case CWConstant.REQUEST_URL_GET_DEVICE_SMS: // 用户获取上报短信
                        if (msg.obj != null) {
                            resultBean = (RequestResultBean) msg.obj;
                            if (resultBean.getCode() == CWConstant.SUCCESS) {
                                DeviceModel deviceModel = getDevice();
                                if (deviceModel != null) {
                                    if (resultBean.getList() != null && resultBean.getList().size() > 0) {
                                        mItemList.clear();
                                        for (Object obj : resultBean.getList()) {
                                            SmsModel smsModel = mGson.fromJson(mGson.toJson(obj),
                                                    SmsModel.class);
                                            smsModel.setImei(deviceModel.getImei());
                                            smsModel.save();
                                        }
                                        initItems();
                                        mAdapter.notifyDataSetChanged();
                                    }
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

}
