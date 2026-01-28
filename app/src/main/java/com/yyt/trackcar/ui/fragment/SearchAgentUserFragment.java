package com.yyt.trackcar.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.ManageCompetitionDeviceAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Page(name = "searchUserFragment", anim = CoreAnim.none)
public class SearchAgentUserFragment extends BaseFragment {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.ll_search_bar)
    LinearLayout searchBar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private static final int PAGE_SIZE = 20;
    private int pageIndex = 1;

    private TitleBar titleBar;
    private AAAUserModel agentUser = null;
    private ManageCompetitionDeviceAdapter adapter;
    private final List<AAADeviceModel> mItemList = new ArrayList<>();
    private volatile int operateIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_and_manage;
    }

    @Override
    protected void initViews() {
        etSearch.setHint(R.string.please_enter_the_reseller_user_account);
        etSearch.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        initRecyclerView();
        initRefreshLayout();
        Bundle bundle= getArguments();
        if (bundle != null) {
            String username = bundle.getString(TConstant.USERNAME);
            long userId = bundle.getLong(TConstant.USER_ID, 0);
            if (userId != 0 && username != null) {
                agentUser = new AAAUserModel();
                agentUser.setUserName(username);
                agentUser.setUserId(userId);
                switchVisualAfterFoundDealer();
            }
        }
    }

    @Override
    protected TitleBar initTitle() {
        titleBar = super.initTitle();
        titleBar.setTitle(getString(R.string.agent_sale_device_management));
        return titleBar;
    }

    private void initRefreshLayout() {
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageIndex++;
                CarGpsRequestUtils.queryDealerBoundDevices(getTrackUserModel(),pageIndex,PAGE_SIZE,agentUser.getUserId(),mHandler);
            }
        }).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex = 1;
                mItemList.clear();
                CarGpsRequestUtils.queryDealerBoundDevices(getTrackUserModel(),pageIndex,PAGE_SIZE,agentUser.getUserId(),mHandler);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ManageCompetitionDeviceAdapter(mItemList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.fl_delete){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.prompt)
                            .setMessage("是否将该设备取消绑定")
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    operateIndex = position;
                                    CarGpsRequestUtils.unbindDeviceFromDealer(getTrackUserModel(),mItemList.get(position).getDeviceImei(),mHandler);
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                }
            }
        });
    }


    private void setAdapterHeaderView(){
        if (adapter == null) return;
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.header_user_info, null);
        TextView username = view.findViewById(R.id.tv_username);
        TextView uid = view.findViewById(R.id.tv_uid);
        username.setText(agentUser.getUserName());
//        uid.setText(String.valueOf(agentUser.getUserId()));
        adapter.addHeaderView(view);
    }

    @OnClick({R.id.btn_search})
    protected void onClick(View view) {
        if (view.getId() == R.id.btn_search) {
            String str = etSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(str)) {
                showDialog();
                CarGpsRequestUtils.searchAgencyUser(getTrackUserModel(), str, mHandler);
            } else
                showMessage(getString(R.string.content) + getString(R.string.cannot_empty_prompt));
        }
    }

    @Override
    public void onResume() {
        if (agentUser != null) {
            mItemList.clear();
            pageIndex = 1;
            CarGpsRequestUtils.queryDealerBoundDevices(getTrackUserModel(), pageIndex, PAGE_SIZE, agentUser.getUserId(), mHandler);
        }
        super.onResume();
    }

    /**
     * 找到经销商账号之后改变布局
     */
    private void switchVisualAfterFoundDealer () {
        setAdapterHeaderView();
        searchBar.setVisibility(View.GONE);
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.add_device)) {
            @Override
            public void performAction(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putInt(CWConstant.TYPE, 3);
//                bundle.putInt(TConstant.USER_ID, (int) agentUser.getUserId());
//                openNewPage(InputImeiFragment.class, bundle);
                Bundle bundle = new Bundle();
                bundle.putLong(TConstant.USER_ID, agentUser.getUserId());
                openNewPage(BatchBindDeviceForDealerFragment.class, bundle);
            }
        });
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            AAABaseResponseBean responseBean;
            try {
                switch (message.what) {
                    case TConstant.REQUEST_QUERY_AGENT_USER: {
                        dismisDialog();
                        if (message.obj == null) {
                            showMessage(R.string.network_error_prompt);
                            return false;
                        }
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() != TConstant.RESPONSE_SUCCESS) {
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                            return false;
                        }
                        List list = (List) responseBean.getData();
                        if (list.size() == 0) {
                            return false;
                        }
                        agentUser = mGson.fromJson(mGson.toJson(list.get(0)), AAAUserModel.class);
                        switchVisualAfterFoundDealer();
                        CarGpsRequestUtils.queryDealerBoundDevices(getTrackUserModel(), pageIndex, PAGE_SIZE, agentUser.getUserId(), mHandler);
                    }
                    break;
                    case TConstant.REQUEST_QUERY_DEALER_BOUND_DEVICES:
                        if (message.obj == null) {
                            showMessage(R.string.network_error_prompt);
                            refreshLayout.finishRefresh().finishLoadMore();
                        }else {
                            responseBean = (AAABaseResponseBean) message.obj;
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                List list1 = (List) responseBean.getData();
                                if (list1 == null || list1.size() == 0) {
                                    refreshLayout.finishRefresh();
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                    return false;
                                }
                                List list2 = (List) list1.get(0);
                                if (list2 == null || list2.size() == 0){
                                    refreshLayout.finishRefresh();
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                    return false;
                                }
                                if (list2.size() != PAGE_SIZE){
                                    refreshLayout.finishRefresh();
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                                for (int i = 0; i < list2.size(); i++) {
                                    mItemList.add(mGson.fromJson(mGson.toJson(list2.get(i)),AAADeviceModel.class));
                                }
                                adapter.notifyDataSetChanged();
                            } else{
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                                refreshLayout.finishRefresh().finishLoadMore();
                            }
                        }
                        break;
                    case TConstant.REQUEST_UNBIND_DEVICE_FROM_AGENCY:
                        if (message.obj == null){
                            showMessage(R.string.network_error_prompt);
                            return false;
                        }
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS){
                            showMessage(getString(R.string.unbind_succeed_prompt));
                            mItemList.remove(operateIndex);
                            adapter.notifyItemRemoved(operateIndex+1);
                        }else{
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });
}
