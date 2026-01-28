package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.DeviceSettingActivity;
import com.yyt.trackcar.ui.adapter.SearchDeviceResultAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
@Page(name = "searchDeviceFragment",anim = CoreAnim.none)
public class SearchDeviceFragment extends BaseFragment {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.ll_search_bar)
    LinearLayout llSearchBar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private final List<AAADeviceModel> mItemList  = new ArrayList<>();
    private SearchDeviceResultAdapter adapter;

    private String searchContent;
    private AAAUserModel mUserModel;
    private Context mContext;
    private final int PAGE_SIZE = 10;
    private int pageIndex = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_and_manage;
    }

    @Override
    protected void initViews() {
        etSearch.setHint(R.string.input_device_imei_prompt);
        initDatas();
        initAdapter();
        initRecyclerView();
        initSmartRefresh();
    }

    private void initDatas() {
        mUserModel = getTrackUserModel();
    }

    private void initSmartRefresh() {
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(500);
                mItemList.clear();
                pageIndex = 1;
                CarGpsRequestUtils.searchDevice(mUserModel,searchContent,pageIndex,PAGE_SIZE,mHandler);
            }
        }).setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageIndex++;
                refreshLayout.finishLoadMore(500);
                CarGpsRequestUtils.searchDevice(mUserModel,searchContent,pageIndex,PAGE_SIZE,mHandler);
            }
        }).setRefreshHeader(new MaterialHeader(mContext).setShowBezierWave(false))
                .setRefreshFooter(new ClassicsFooter(mContext));
    }

    private void initAdapter() {
        adapter = new SearchDeviceResultAdapter(mItemList);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.iv_race_setting:
                        break;
                    case R.id.iv_device_setting:
                        Bundle bundle = new Bundle();
                        bundle.putInt(TConstant.TYPE,2);
                        bundle.putString("deviceModel", mGson.toJson(mItemList.get(position)));
                        startActivity(bundle,DeviceSettingActivity.class);
                        break;
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(TConstant.TYPE,2);
                bundle.putString("deviceModel", mGson.toJson(mItemList.get(position)));
                startActivity(bundle,DeviceSettingActivity.class);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(R.string.search_device);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.search_device)));
        return titleBar;
    }

    @Override
    protected void initListeners() {
        btnSearch.setOnClickListener(this::onSearch);
    }

    @SuppressLint("StringFormatMatches")
    private void onSearch(View view){
        searchContent = etSearch.getText().toString().trim();
//        if (TextUtils.isEmpty(searchContent)){
//            showMessage(getString(R.string.device_imei,getString(R.string.cannot_empty_prompt)));
//            return;
//        }
        showDialog();
        CarGpsRequestUtils.searchDevice(getTrackUserModel(),searchContent,1,20,mHandler);
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            try {
                AAABaseResponseBean responseBean;
                switch (message.what){
                    case TConstant.REQUEST_SEARCH_DEVICE_BY_IMEI:
                        if (message.obj == null){
                            dismisDialog();
                            showMessage(R.string.failed_to_load_data);
                            return false;
                        }
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS
                        || responseBean.getCode() == TConstant.RESPONSE_SUCCESS_NEW){
                            refreshLayout.setEnableRefresh(true);
                            refreshLayout.setEnableLoadMore(true);
                            dismisDialog();
                            List list1 = (List) responseBean.getData();
                            if (list1 == null || list1.size() == 0){
                                refreshLayout.finishLoadMoreWithNoMoreData();
                                showMessage(R.string.cannot_find_out_any_device_prompt);
                                return false;
                            }
                            List list2 = (List) list1.get(0);
                            if (list2 == null || list2.size() == 0){
                                refreshLayout.finishLoadMoreWithNoMoreData();
                                showMessage(R.string.cannot_find_out_any_device_prompt);
                                return  false;
                            }
                            if (list2.size() != PAGE_SIZE){
                                refreshLayout.finishLoadMoreWithNoMoreData();
                            }
                            llSearchBar.setVisibility(View.GONE);
                            refreshLayout.setVisibility(View.VISIBLE);
                            for (int i = 0; i < list2.size(); i++) {
                                mItemList.add(mGson.fromJson(mGson.toJson(list2.get(i)),AAADeviceModel.class));
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            dismisDialog();
                            showMessage(R.string.network_error_prompt);
                        }
                        break;
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });
}
