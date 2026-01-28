package com.yyt.trackcar.ui.adapter;

import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.FenceBean;
import com.yyt.trackcar.utils.MapUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      FenceAdapter
 * @ author:        QING
 * @ createTime:    2020/4/9 06:49
 * @ describe:      TODO 电子围栏适配器
 */
public class FenceAdapter extends BaseQuickAdapter<FenceBean, BaseViewHolder> {
    private boolean isEdit;
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener;

    public FenceAdapter(List<FenceBean> data,
                        CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        super(R.layout.item_fence, data);
        mCheckedChangeListener = checkedChangeListener;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull FenceBean item) {
        helper.getView(R.id.ivCheck).setVisibility(isEdit ? View.VISIBLE : View.GONE);
        helper.getView(R.id.ivCheck).setSelected(item.isSelect());
        helper.setText(R.id.tvTitle, item.getFenceName());
        helper.setText(R.id.tvContent, mContext.getString(R.string.fence_info,
                item.getEntry() == 1 ? mContext.getString(R.string.open) :
                        mContext.getString(R.string.close), item.getExit() == 1 ?
                        mContext.getString(R.string.open) : mContext.getString(R.string.close),
                MapUtils.getMapDistance(item.getRadius())));
        if (item.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, item.getBgDrawable());
        SwitchButton switchButton = helper.getView(R.id.sbSwitch);
        switchButton.setTag(item.getId());
        switchButton.setOnCheckedChangeListener(null);
        switchButton.setCheckedImmediately(item.getEnable() == 1);
        switchButton.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
