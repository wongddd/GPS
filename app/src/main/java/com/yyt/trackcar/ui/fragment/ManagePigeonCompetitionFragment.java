package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.yyt.trackcar.bean.DeviceRaceconfig;
import com.yyt.trackcar.bean.GpsPigeonRaceBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.activity.DeviceSettingActivity;
import com.yyt.trackcar.ui.adapter.PigeonCompetitionAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(name = "manageCompetitionFragment", anim = CoreAnim.none)
public class ManagePigeonCompetitionFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noDataPrompt)
    TextView hintNoData;

    private List<GpsPigeonRaceBean> mItemList = new ArrayList<>();
    private AAAUserModel mUserModel;
    private Context mContext;
    private PigeonCompetitionAdapter adapter;
    private int operating = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initViews() {
        mContext = getContext();
        initDatas();
        initAdapter();
        initRecyclerView();
        queryCompetitionList();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
    }

    private void initAdapter() {
        adapter = new PigeonCompetitionAdapter(mItemList);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                operating = position;
                try {
                    switch (view.getId()) {
                        case R.id.fl_delete:
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.prompt)
                                    .setMessage(R.string.whether_delete_this_competition_prompt)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            CarGpsRequestUtils.deletePigeonCompetition(mUserModel, mItemList.get(position).getId(), mHandler);
                                        }
                                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                            break;
                        case R.id.fl_edit_contestant: {
                            Bundle bundle = new Bundle();
                            bundle.putLong("pigeonRaceId", mItemList.get(position).getId());
                            openNewPage(ManageCompetitionDeviceFragment.class, bundle);
                        }
                        break;
                        case R.id.fl_edit_configuration:
                            CarGpsRequestUtils.queryPigeonCompetitionConfiguration(mUserModel, mItemList.get(position).getId(), mHandler);
                            break;
                        case R.id.fl_modify_info: {
                            Bundle bundle = new Bundle();
                            operating = position;
                            bundle.putInt(TConstant.TYPE, 1);
                            bundle.putParcelable(TConstant.PARCELABLE, mItemList.get(position));
                            openNewPageForResult(CreateAndModifyPigeonCompetitionFragment.class, bundle, 201);
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
//        titleBar.setTitle(getString(R.string.manage_competition));
        titleBar.setTitle(String.format("%s%s", getString(R.string.pet_real_time),
                getString(R.string.manage_competition)));
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.create_pigeon_competition)) {
            @Override
            public void performAction(View view) {
                openNewPageForResult(CreateAndModifyPigeonCompetitionFragment.class, 200);
            }
        });
        return titleBar;
    }

    private void initDatas() {
        mUserModel = getTrackUserModel();
    }

    private void queryCompetitionList() {
        showDialog();
        CarGpsRequestUtils.queryPigeonCompetition(mUserModel, null, mHandler);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode != 1) return;
        if (requestCode == 200) {
//            pigeonRaceBean = data.getParcelableExtra("newCompetitionCreated");
//            mItemList.add(0, pigeonRaceBean);
//            adapter.notifyDataSetChanged();
            mItemList.clear();
            CarGpsRequestUtils.queryPigeonCompetition(mUserModel, null, mHandler);
        } else if (requestCode == 201) {
            GpsPigeonRaceBean gpsPigeonRaceBean = data.getParcelableExtra("newCompetitionCreated");
            mItemList.remove(operating);
            mItemList.add(0, gpsPigeonRaceBean);
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            dismisDialog();
            try {
                if (msg.obj == null) {
                    showMessage(R.string.request_error_prompt);
                    return false;
                }
                AAABaseResponseBean responseBean;
                responseBean = (AAABaseResponseBean) msg.obj;
                if (responseBean.getCode() != TConstant.RESPONSE_SUCCESS) {
                    showMessage(ErrorCode.getResId(responseBean.getCode()));
                    return false;
                }
                switch (msg.what) {
                    case TConstant.REQUEST_QUERY_PIGEON_COMPETITION: {
                        hintNoData.setVisibility(View.VISIBLE);
                        List list1 = (ArrayList) responseBean.getData();
                        if (list1 == null || list1.size() == 0) return false;
                        List list2 = (ArrayList) list1.get(0);
                        if (list2 == null || list2.size() == 0) return false;
                        hintNoData.setVisibility(View.GONE);
                        for (int i = 0; i < list2.size(); i++) {
                            mItemList.add(mGson.fromJson(mGson.toJson(list2.get(i)), GpsPigeonRaceBean.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                    case TConstant.REQUEST_DELETE_PIGEON_COMPETITION: {
                        showMessage(R.string.delete_succeed_prompt);
                        mItemList.remove(operating);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                    case TConstant.REQUEST_QUERY_PIGEON_COMPETITION_CONFIGURATION: {
                        List list1 = (ArrayList) responseBean.getData();
                        if (list1 == null || list1.size() == 0) return false;
                        List list2 = (ArrayList) list1.get(0);
                        if (list2 == null || list2.size() == 0) return false;
                        DeviceRaceconfig deviceRaceconfig = mGson.fromJson(mGson.toJson(list2.get(0)), DeviceRaceconfig.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(TConstant.TYPE, 3);
                        bundle.putString(TConstant.TITLE, getString(R.string.competition_parameter));
                        bundle.putParcelable(TConstant.CONFIGURATION, deviceRaceconfig);
                        startActivity(bundle, DeviceSettingActivity.class);
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
