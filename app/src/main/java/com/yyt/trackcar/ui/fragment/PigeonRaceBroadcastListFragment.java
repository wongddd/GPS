package com.yyt.trackcar.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAAPigeonRaceBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.PigeonRaceListAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 赛鸽比赛直播列表界面
 */
@Page(name ="pigeonRaceBroadcastListFragment",anim = CoreAnim.none)
public class PigeonRaceBroadcastListFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.noDataPrompt)
    TextView hintNoData;

    private Context mContext;
    private PigeonRaceListAdapter adapter;
    private List<AAAPigeonRaceBean> pigeonRaceList = new ArrayList<>();
    private AAAUserModel mUserModel;
    private AAADeviceModel mDeviceModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initViews() {
        mContext = getContext();
        showDialog();
        initDatas();
        initAdapter();
        initRecyclerView();
    }

    private void initDatas() {
        mUserModel = getTrackUserModel();
        mDeviceModel = getTrackDeviceModel();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putLong("pigeonRaceId",pigeonRaceList.get(position).getId());
                if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE,0) == 0)
                openNewPage(RaceLiveBroadcastAMapFragment.class,bundle);
                else
                openNewPage(RaceLiveBroadcastGoogleFragment.class,bundle);
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    private void initAdapter() {
        adapter = new PigeonRaceListAdapter(pigeonRaceList);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(R.string.live_broadcast_list);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.live_broadcast_list)));
        titleBar.addAction(new TitleBar.ImageAction(R.mipmap.ic_refresh_white) {
            @Override
            public void performAction(View view) {
                showDialog();
                CarGpsRequestUtils.getPigeonRaceList(mUserModel,mHandler);
            }
        });
        return titleBar;
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            AAABaseResponseBean responseBean;
            switch (message.what){
                case TConstant.REQUEST_GET_PIGEON_RACE_LIST:
                    dismisDialog();
                    if (message.obj!=null){
                        pigeonRaceList.clear();
                        try {
                            responseBean = (AAABaseResponseBean) message.obj;
                            ArrayList sub1 = (ArrayList)responseBean.getData();
                            if (sub1.size() > 0){
                                ArrayList sub2 = (ArrayList) sub1.get(0);
                                if (sub2.size() == 0){
                                    hintNoData.setVisibility(View.VISIBLE);
                                }else {
                                    for (int i = 0; i < sub2.size(); i++) {
                                        pigeonRaceList.add(mGson.fromJson(mGson.toJson(sub2.get(i)), AAAPigeonRaceBean.class));
                                    }
                                    hintNoData.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                }
                            }else{
                                hintNoData.setVisibility(View.VISIBLE);
                            }
                        }catch (Exception e){
                        }
                    }else{
                        hintNoData.setVisibility(View.VISIBLE);
                        showMessage(R.string.network_error_prompt);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public void onResume() {
        if (mUserModel == null) mUserModel = getTrackUserModel();
        CarGpsRequestUtils.getPigeonRaceList(mUserModel,mHandler);
        super.onResume();
    }
}
