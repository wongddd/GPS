package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.net.NetworkUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.ListResponseBean;
import com.yyt.trackcar.bean.TrackCircleBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.HistoryAMapNewActivity;
import com.yyt.trackcar.ui.activity.HistoryGMapNewActivity;
import com.yyt.trackcar.ui.adapter.TrackCircleAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.SettingSPUtils;
import com.yyt.trackcar.utils.TConstant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
@Page(name = "trackCircleFragment", anim = CoreAnim.none)
public class TrackCircleFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    private TrackCircleAdapter adapter;
    private final List<TrackCircleBean> mItemList = new ArrayList<>();
    private int mDeviceType; //用户查看的轨迹所属设备的设备类型
    private int mSearchTrackId; //用户查看的轨迹的ID；
    private AAAUserModel mUserModel;
    private int deleteIndex = 0;
    private int pageIndex = 1;
    private final int PAGE_SIZE = 10;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_refresh_layout;
    }

    @Override
    protected void initViews() {
        initDatas();
        initAdapter();
        initRecyclerView();
        initSmartRefreshView();
        showDialog();
        CarGpsRequestUtils.getTrackCircleList(mUserModel, 1, 10, mHandler);
    }

    private void initSmartRefreshView() {
//        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                smartRefreshLayout.finishRefresh(500);
                mItemList.clear();
                pageIndex = 1;
                CarGpsRequestUtils.getTrackCircleList(mUserModel,pageIndex,PAGE_SIZE,mHandler);
            }
        }).setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                smartRefreshLayout.finishLoadMore(500);
                pageIndex++;
                CarGpsRequestUtils.getTrackCircleList(mUserModel,pageIndex,PAGE_SIZE,mHandler);
            }
        });
    }

    private void initDatas() {
        mUserModel = getTrackUserModel();
    }

    private void initAdapter() {
        adapter = new TrackCircleAdapter(mItemList,mUserModel.getUserId());
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mItemList.get(position) != null){
                    mSearchTrackId = mItemList.get(position).getId();
                    viewTrackDetail(position);
                }
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (mItemList.get(position) == null) return;
                mSearchTrackId = mItemList.get(position).getId();
                switch (view.getId()) {
                    case R.id.fl_enter_map:
                        viewTrackDetail(position);
                        break;
                    case R.id.ll_thumbs_up:
                        ImageView ivThumbsUp = view.findViewById(R.id.iv_thumbs_up);
                        TextView tvThumbsUp = view.findViewById(R.id.tv_count_thumbs_up);

                        if (ivThumbsUp.isSelected())
                            CarGpsRequestUtils.thumbDownRequest(mUserModel, mItemList.get(position).getId(), mHandler);
                        else
                            CarGpsRequestUtils.thumbsUpRequest(mUserModel, mItemList.get(position).getId(), null, mHandler);

                        int countThumbsUp = mItemList.get(position).getThumbsup().intValue();
                        if (mItemList.get(position).getIsthumbsup() == 1) {  //原先已点赞
                            if (ivThumbsUp.isSelected())  // 目前已点赞
                                tvThumbsUp.setText(String.valueOf(countThumbsUp - 1));
                            else  //目前未点赞
                                tvThumbsUp.setText(String.valueOf(countThumbsUp));
                        } else {  //原先未点赞
                            if (ivThumbsUp.isSelected())  //目前已点赞
                                tvThumbsUp.setText(String.valueOf(countThumbsUp));
                            else  //目前未点赞
                                tvThumbsUp.setText(String.valueOf(countThumbsUp + 1));
                        }
                        ivThumbsUp.setSelected(!ivThumbsUp.isSelected());
                        break;
                    case R.id.fl_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.prompt)
                                .setMessage(R.string.whether_delete_track_prompt)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        showDialog();
                                        deleteIndex = position;
                                        CarGpsRequestUtils.deleteMySharedTrack(mUserModel,mItemList.get(position).getId(),mHandler);
                                    }
                                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                        break;
                }
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
//        titleBar.setTitle(R.string.track_sharing_circle);
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.track_sharing_circle)));
        return titleBar;
    }

    /**
     * 查看轨迹详情
     */
    public void viewTrackDetail(int position) {
        if (!NetworkUtils.isNetworkAvailable()) {
            showMessage(R.string.network_error_prompt);
            return;
        }
        if (mItemList.get(position) != null) {
            if (mItemList.get(position).getStarDatetime() == null || mItemList.get(position).getEndDatetime() == null) return;
            showDialog();
            mDeviceType = mItemList.get(position).getDeviceType();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = simpleDateFormat.format(mItemList.get(position).getStarDatetime());
            String endTime = simpleDateFormat.format(mItemList.get(position).getEndDatetime());
            CarGpsRequestUtils.getHistoryLocation(mUserModel, mItemList.get(position).getDeviceImei(),
                    startTime, endTime, 0, 1, mHandler);
        }
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            dismisDialog();
            try {
                switch (message.what) {
                    case TConstant.REQUEST_GET_TRACK_CIRCLE_LIST: //获取轨迹圈列表
                        AAABaseResponseBean baseResponseBean;
                        AAARequestBean requestBean;
                        if (message.obj == null) {
                            showMessage(R.string.failed_to_load_data);
                            return false;
                        }
                        baseResponseBean = (AAABaseResponseBean) message.obj;
                        if (baseResponseBean.getCode() == TConstant.RESPONSE_SUCCESS
                                || baseResponseBean.getCode() == TConstant.RESPONSE_SUCCESS_NEW) {
                            List list1 = (ArrayList) baseResponseBean.getData();
                            if (list1 == null || list1.size() == 0) {
                                smartRefreshLayout.finishLoadMoreWithNoMoreData();
                                return false;
                            }
                            List list2 = (ArrayList) list1.get(0);
                            if (list2 == null || list2.size() == 0) {
                                smartRefreshLayout.finishLoadMoreWithNoMoreData();
                                return false;
                            }
                            if (list2.size() == PAGE_SIZE)
                                smartRefreshLayout.setEnableLoadMore(true);
                            for (int i = 0; i < list2.size(); i++) {
                                mItemList.add(mGson.fromJson(mGson.toJson(list2.get(i)), TrackCircleBean.class));
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            showMessage(R.string.failed_to_load_data);
                        }
                        break;

                    case TConstant.REQUEST_URL_GET_HISTORY_LOCATION:  //获取单个轨迹
                        dismisDialog();
                        if (message.obj == null) {
                            showMessage(R.string.request_unkonow_prompt);
                            return false;
                        }
                        baseResponseBean = (AAABaseResponseBean) message.obj;
                        if (baseResponseBean.getCode() == TConstant.RESPONSE_NET_ERROR)
                            showMessage(R.string.request_unkonow_prompt);
                        else if (baseResponseBean.getCode() == TConstant.RESPONSE_SUCCESS) {
                            ListResponseBean listResponseBean =
                                    mGson.fromJson(mGson.toJson(baseResponseBean.getData()),
                                            ListResponseBean.class);
                            requestBean = mGson.fromJson(baseResponseBean.getRequestObject(),
                                    AAARequestBean.class);
                            if (listResponseBean == null || listResponseBean.getList() == null
                                    || listResponseBean.getList().size() == 0
                                    || TextUtils.isEmpty(requestBean.getDeviceImei()))
                                showMessage(R.string.no_history_tips);
                            else {
                                Bundle bundle = new Bundle();
                                bundle.putString(TConstant.TITLE, getString(R.string.track_playback));
                                bundle.putString(TConstant.IMEI_NO, requestBean.getDeviceImei());
                                bundle.putString(TConstant.START_TIME, requestBean.getStartTime());
                                bundle.putString(TConstant.END_TIME, requestBean.getEndTime());
                                bundle.putInt(TConstant.DEVICE_TYPE, mDeviceType);
                                CarGpsRequestUtils.browserTrackCircle(mUserModel, mSearchTrackId, mHandler);
                                if (SettingSPUtils.getInstance().getInt(TConstant.MAP_TYPE, 0) == 1)
                                    startActivity(bundle, HistoryGMapNewActivity.class);
                                else
                                    startActivity(bundle, HistoryAMapNewActivity.class);
                            }
                        } else
                            showMessage(R.string.tip_request_error);
                        break;
                    case TConstant.REQUEST_TRACK_CIRCLE_THUMBS_UP_AND_THUMB_DOWN:
                    case TConstant.REQUEST_VIEW_TRACK_CIRCLE:
                        if (message.obj != null) {
                            baseResponseBean = (AAABaseResponseBean) message.obj;
                            if (baseResponseBean.getCode() == TConstant.RESPONSE_SUCCESS
                                    || baseResponseBean.getCode() == TConstant.RESPONSE_SUCCESS_NEW) {
//                                KLog.d("operation is executed");
                            }
                        }
                        break;
                    case TConstant.REQUEST_DELETE_MY_SHARED_TRACK:
                        dismisDialog();
                        if (message.obj != null) {
                            baseResponseBean = (AAABaseResponseBean) message.obj;
                            if (baseResponseBean.getCode() == TConstant.RESPONSE_SUCCESS
                                    || baseResponseBean.getCode() == TConstant.RESPONSE_SUCCESS_NEW){
                                showMessage(R.string.delete_succeed_prompt);
                                mItemList.remove(deleteIndex);
                                adapter.notifyDataSetChanged();
                            }else{
                                showMessage(ErrorCode.getResId(baseResponseBean.getCode()));
                            }
                        }else{
                            showMessage(R.string.network_error_prompt);
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
