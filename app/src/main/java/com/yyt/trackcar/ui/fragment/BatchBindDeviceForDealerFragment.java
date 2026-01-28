package com.yyt.trackcar.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.socks.library.KLog;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseResponseBean;
import com.yyt.trackcar.bean.AAARequestBean;
import com.yyt.trackcar.bean.BatchBindDeviceResponseBean;
import com.yyt.trackcar.dbflow.AAAUserModel;
import com.yyt.trackcar.ui.adapter.AdapterBatchBindDevice;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CarGpsRequestUtils;
import com.yyt.trackcar.utils.ErrorCode;
import com.yyt.trackcar.utils.TConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
@Page(name = "BatchBindDeviceForDealerFragment", anim = CoreAnim.none)
public class BatchBindDeviceForDealerFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private EditText inputImei = null;

    private final List<BatchBindDeviceResponseBean> mItemList = new ArrayList<>();
    private AdapterBatchBindDevice adapter = null;
    private AAAUserModel userModel = null;
    private long dealerID;
    private boolean requesting = false; // 正在请求中
    private String deviceImei;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.bind_device);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initData();
        initRecyclerViewAndAdapter();
    }

    private void initData () {
        userModel = getTrackUserModel();
        Bundle bundle = getArguments();
        if (bundle != null) {
            dealerID = bundle.getLong(TConstant.USER_ID);
        }
    }

    private void initRecyclerViewAndAdapter () {
//        Display display = mActivity.getWindowManager().getDefaultDisplay();
//        Window window = mActivity.getWindow();
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        layoutParams.width = (int) (display.getWidth() * 0.9);
//        LinearLayout linearLayout = new LinearLayout(getContext());
//        linearLayout.setLayoutParams(layoutParams);
//        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//        EditText editText = new EditText(getContext());
//        linearLayout.addView(editText);
        final View view = LayoutInflater.from(mActivity).inflate(R.layout.adapter_edittext_header_view, null, false);
        inputImei = view.findViewById(R.id.et_input);
        final Button button = view.findViewById(R.id.btn_bind);
        button.setOnClickListener(v -> {
            if (requesting) return;
            deviceImei = inputImei.getText().toString().trim();
            if (deviceImei.length() == 0) {
                showMessage(R.string.input_imei_prompt);
            } else {
                showDialog();
                requesting = true;
                CarGpsRequestUtils.bindDeviceForDealer(userModel, deviceImei, (int) dealerID, mHandler);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterBatchBindDevice(mItemList);
        recyclerView.setAdapter(adapter);
        adapter.addHeaderView(view);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (requesting) return;
                inputImei.setText(mItemList.get(position).getDeviceImei());
            }
        });
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            try {
                switch (message.what) {
                    case TConstant.REQUEST_BIND_DEVICE_FOR_DEALER:
                        requesting = false;
                        dismisDialog();
                        if (message.obj == null) {
                            showMessage(R.string.request_error_prompt);
                            return false;
                        }
                        AAABaseResponseBean response = (AAABaseResponseBean) message.obj;
                        if (response.getCode() == TConstant.RESPONSE_SUCCESS) {
                            AAARequestBean requestBean = mGson.fromJson(mGson.toJson(response.getRequestObject()),AAARequestBean.class);
                            KLog.d(mGson.toJson(requestBean));
                            showMessage(R.string.bind_device_success_tips);
                            mItemList.add(new BatchBindDeviceResponseBean().deviceImei(deviceImei).isBound(true));
                            adapter.notifyItemInserted(mItemList.size());
                        } else {
                            mItemList.add(new BatchBindDeviceResponseBean().deviceImei(deviceImei).isBound(false));
                            adapter.notifyItemInserted(mItemList.size());
                            showMessage(ErrorCode.getResId(response.getCode()));
                        }
                        break;
                }
            }catch (ClassCastException e) {
                e.printStackTrace();
            }
            return false;
        }
    });
}
