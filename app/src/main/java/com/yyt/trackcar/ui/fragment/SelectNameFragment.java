package com.yyt.trackcar.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.yyt.trackcar.BuildConfig;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.data.DataServer;
import com.yyt.trackcar.ui.adapter.SelectNameAdapter;
import com.yyt.trackcar.ui.base.BaseFragment;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.DialogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.fragment
 * @ fileName:      SelectNameFragment
 * @ author:        QING
 * @ createTime:    2020/3/10 20:08
 * @ describe:      TODO 选择名称页面
 */
@Page(name = "SelectName")
public class SelectNameFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView; // RecyclerView
    private SelectNameAdapter mAdapter; // 适配器
    private List<SectionItem> mItemList = new ArrayList<>(); // 列表

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recycler_view;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.setTitle(R.string.name_select);
        return titleBar;
    }

    @Override
    protected void initViews() {
        initItems();
        initAdapters();
        initRecyclerViews();
    }

    /**
     * 初始化列表信息
     */
    private void initItems() {
        DataServer.getSelectNameData(mActivity, mItemList);
    }

    /**
     * 初始化适配器
     */
    private void initAdapters() {
        mAdapter = new SelectNameAdapter(mItemList);
        mAdapter.setOnItemChildClickListener(this);
    }

    /**
     * 初始化ViewPager
     */
    private void initRecyclerViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (position >= 0 && position < mItemList.size()) {
            BaseItemBean itemBean = mItemList.get(position).t;
            if (itemBean != null) {
                if (itemBean.getType() == 0)
                    mMaterialDialog = DialogUtils.customInputMaterialDialog(getContext(),
                            mMaterialDialog, getString(R.string.name_type_tenth), null,
                            getString(R.string.custom_name_hint), null, InputType.TYPE_CLASS_TEXT
                            , 12, 1, getString(R.string.confirm), getString(R.string.cancel),
                            CWConstant.DIALOG_INPUT_NAME, mHandler);
                else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt(CWConstant.TYPE, itemBean.getType());
                    bundle.putString(CWConstant.NAME, itemBean.getTitle());
                    intent.putExtras(bundle);
                    setFragmentResult(Activity.RESULT_OK, intent);
                    popToBack();
                }
            }
        }
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            try {
                switch (msg.what) {
                    case CWConstant.HANDLE_INPUT_ACTION: // 输入回调
                        switch (msg.arg1) {
                            case CWConstant.DIALOG_INPUT_NAME: // 自定义名称
                                String inputText = (String) msg.obj;
                                if (!TextUtils.isEmpty(inputText)) {
                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(CWConstant.TYPE, 0);
                                    bundle.putString(CWConstant.NAME, inputText);
                                    intent.putExtras(bundle);
                                    setFragmentResult(Activity.RESULT_OK, intent);
                                    popToBack();
                                }
                                break;
                            default:
                                break;
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
