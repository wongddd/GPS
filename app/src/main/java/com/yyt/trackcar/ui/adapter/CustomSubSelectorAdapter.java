package com.yyt.trackcar.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      CustomSubSelectorAdapter
 * @ author:        QING
 * @ createTime:    2020/4/23 11:42
 * @ describe:      TODO 子标题选择适配器
 */
public class CustomSubSelectorAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {
    private boolean isEdit;

    public CustomSubSelectorAdapter(List<BaseItemBean> data) {
        super(R.layout.item_custom_sub_selector, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseItemBean model) {
        helper.getView(R.id.ivCheck).setVisibility(isEdit ? View.VISIBLE : View.GONE);
        helper.getView(R.id.ivCheck).setSelected(model.isSelect());
        helper.setText(R.id.tvTitle, model.getTitle());
        helper.setText(R.id.tvContent, model.getContent());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
        if (model.getTitleColor() == 0)
            helper.setTextColor(R.id.tvContent, ContextCompat.getColor(mContext, R.color.dimgray));
        else
            helper.setTextColor(R.id.tvContent, ContextCompat.getColor(mContext,
                    model.getTitleColor()));
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
