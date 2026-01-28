package com.yyt.trackcar.ui.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAAPigeonRaceBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.PigeonRaceListAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(name ="LiveRoomListFragment",anim = CoreAnim.none)
public class LiveBroadcastListFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private Context mContext;
    private PigeonRaceListAdapter adapter;
    private List<AAAPigeonRaceBean> pigeonRaceList = new ArrayList<>();
    private AAAUserModel userModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recyclerview;
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
        userModel = getTrackUserModel();
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE,0) == 0)
                openNewPage(RaceLiveBroadcastAMapFragment.class);
                else
                openNewPage(RaceLiveBroadcastGoogleFragment.class);
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
        titleBar.setTitle(R.string.live_broadcast_list);
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
                            if (responseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                                ArrayList sub1 = (ArrayList) responseBean.getData();
                                if (sub1.size() > 0) {
                                    ArrayList sub2 = (ArrayList) sub1.get(0);
                                    for (int i = 0; i < sub2.size(); i++) {
                                        pigeonRaceList.add(mGson.fromJson(mGson.toJson(sub2.get(i)), AAAPigeonRaceBean.class));
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                showMessage(ErrorCode.getResId(responseBean.getCode()));
                            }
                        }catch (Exception e){
                        }
                    }else{
                        showMessage(R.string.network_error_prompt);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public void onResume() {
        CarGpsRequestUtils.getPigeonRaceList(userModel,mHandler);
        super.onResume();
    }
}
