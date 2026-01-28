package com.yyt.trackcar.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
 * @ fileName:      WatchBillFragment
 * @ author:        QING
 * @ createTime:    2020/3/13 11:08
 * @ describe:      TODO
 */
@Page(name = "WatchBill")
public class WatchBillFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.clDel)
    View mDelView; // 删除布局
    @BindView(R.id.clKeyboard)
    View mKeyboardView; // 输入布局
    @BindView(R.id.clInstruct)
    View mInstructView; // 指令布局
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private SmsCollectionAdapter mAdapter; // 适配器
    private List<SmsModel> mItemList = new ArrayList<>(); // 列表
    private TitleBar mTitleBar; // 标题栏
    private boolean mIsEdit; // 是否编辑
    private boolean mIsKeyBoard; // 是否输入模式

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_watch_bill;
    }

    @Override
    protected TitleBar initTitle() {
        mTitleBar = super.initTitle();
        mTitleBar.setTitle(R.string.watch_bill);
        mTitleBar.addAction(getAction());
        return mTitleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
        initHeaderAndFooterView();
        initEmptyView();
        getDeviceSms();
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
     * 初始化头和脚布局
     */
    private void initHeaderAndFooterView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_space_section, mRecyclerView,
                false);
        View footerView = getLayoutInflater().inflate(R.layout.item_space_section, mRecyclerView,
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
        tvEmpty.setText(R.string.no_query_data_prompt);
        mAdapter.setEmptyView(emptyView);
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
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    mDelView.setVisibility(View.GONE);
                    mKeyboardView.setVisibility(mIsKeyBoard ? View.VISIBLE : View.GONE);
                    mInstructView.setVisibility(mIsKeyBoard ? View.GONE : View.VISIBLE);
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
                    mAdapter.setEdit(mIsEdit);
                    mAdapter.notifyDataSetChanged();
                    mDelView.setVisibility(View.VISIBLE);
                    mKeyboardView.setVisibility(View.GONE);
                    mInstructView.setVisibility(View.GONE);
                    mTitleBar.removeAllActions();
                    mTitleBar.addAction(getAction());
                }
            };
        return action;
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
    @OnClick({R.id.clDel, R.id.sendBtn, R.id.ibMore, R.id.ibKeyboard, R.id.queryBillBtn,
            R.id.queryFlowBtn})
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
            case R.id.sendBtn: // 发送
                break;
            case R.id.ibMore: // 切换查询模式
                mIsKeyBoard = false;
                mKeyboardView.setVisibility(View.GONE);
                mInstructView.setVisibility(View.VISIBLE);
                break;
            case R.id.ibKeyboard: // 切换输入模式
                mIsKeyBoard = true;
                mKeyboardView.setVisibility(View.VISIBLE);
                mInstructView.setVisibility(View.GONE);
                break;
            case R.id.queryBillBtn: // 查话费
                break;
            case R.id.queryFlowBtn: // 查流量
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
