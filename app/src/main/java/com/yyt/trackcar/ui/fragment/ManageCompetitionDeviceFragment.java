package com.yyt.trackcar.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.dbflow.AAADeviceModel;
import com.yyt.trackcar.ui.adapter.ManageCompetitionDeviceAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.DialogUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Page(name = "manageCompetitionDeviceFragment",anim = CoreAnim.none)
public class ManageCompetitionDeviceFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noDataPrompt)
    TextView hintNoData;

    private Long pigeonRaceId; //赛事Id
    private ManageCompetitionDeviceAdapter adapter;
    private final List<AAADeviceModel> mItemList = new ArrayList<>();
    private int operatingIndex;
    private int operate;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected void initViews() {
        initDatas();
        initAdapter();
        initRecyclerView();
        showDialog();
        CarGpsRequestUtils.getDeviceListOfPigeonRace(getTrackUserModel(),pigeonRaceId,mHandler);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void initAdapter() {
        adapter  = new ManageCompetitionDeviceAdapter(mItemList);
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            operatingIndex = position;
            switch (view.getId()){
                case R.id.fl_delete:
                    mMaterialDialog = DialogUtils.customMaterialDialog(
                            getContext(),mMaterialDialog,getString(R.string.prompt)
                            ,getString(R.string.whether_delete_this_device_from_the_competition_prompt)
                            ,getString(R.string.confirm),getString(R.string.cancel)
                            ,201,mHandler);
                    break;
            }
        });
    }

    private void initDatas() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        pigeonRaceId = bundle.getLong("pigeonRaceId");
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(getString(R.string.manage_device_of_competition));
        titleBar.addAction(new TitleBar.ImageAction(R.mipmap.add_normal_white) {
            @Override
            public void performAction(View view) {
                mMaterialDialog = DialogUtils.customInputMaterialDialog(
                        getContext(),mMaterialDialog,getString(R.string.add_device)
                        ,null,getString(R.string.input_device_imei_prompt)
                ,null, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,20,15,getString(R.string.add_device)
                        ,getString(R.string.cancel),200,mHandler);
            }
        });
        return titleBar;
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            AAABaseResponseBean responseBean;
            try{
                switch (message.what){
                    case TConstant.REQUEST_GET_DEVICE_LIST_OF_PIGEON_RACE_LIVE: //获取参赛设备列表
                    case TConstant.REQUEST_QUERY_DEVICE_OF_PIGEON_COMPETITION:
                        dismisDialog();
                        hintNoData.setVisibility(View.VISIBLE);
                        if (message.obj == null){
                            showMessage(R.string.network_error_prompt);
                            return false;
                        }
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() != TConstant.RESPONSE_SUCCESS){
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                            return false;
                        }
                        List list1 = (List) responseBean.getData();
                        if (list1.size() == 0) return false;
                        List list2 = (List) list1.get(0);
                        if (list2.size() == 0) return false;
                        hintNoData.setVisibility(View.GONE);
                        for (int i = 0; i < list2.size(); i++) {
                            mItemList.add(mGson.fromJson(mGson.toJson(list2.get(i)),AAADeviceModel.class));
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case TConstant.REQUEST_UPDATE_DEVICE_OF_COMPETITION:
                        if (message.obj == null){
                            showMessage(R.string.network_error_prompt);
                            dismisDialog();
                            return false;
                        }
                        responseBean = (AAABaseResponseBean) message.obj;
                        if (responseBean.getCode() != TConstant.RESPONSE_SUCCESS){
                            showMessage(ErrorCode.getResId(responseBean.getCode()));
                            dismisDialog();
                            return false;
                        }
                        if (operate == 1) {
                            showMessage(R.string.update_succeed_prompt);
                            mItemList.clear();
                            CarGpsRequestUtils.getDeviceListOfPigeonRace(getTrackUserModel(),pigeonRaceId,mHandler);
                        } else {
                            dismisDialog();
                            mItemList.remove(operatingIndex);
                            adapter.notifyDataSetChanged();
                            showMessage(R.string.delete_succeed_prompt);
                        }
                        break;
                    case CWConstant.HANDLE_CONFIRM_ACTION:
                        showDialog();
                        switch (message.arg1){
                            case 201:
                                operate = 2;
                                CarGpsRequestUtils.updateCompetitionDevice(getTrackUserModel(),null,mItemList.get(operatingIndex).getDeviceImei(),mHandler);
                        }
                        break;
                    case CWConstant.HANDLE_INPUT_ACTION: // 输入回调
                        showDialog();
                        switch (message.arg1) {
                            case 200:
                                String inputText = (String) message.obj;
                                operate = 1;
                                CarGpsRequestUtils.updateCompetitionDevice(getTrackUserModel(),pigeonRaceId,inputText,mHandler);
                                break;
                        }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return false;
        }
    });
}
