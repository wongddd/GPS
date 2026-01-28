package com.yyt.trackcar.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      CustomTextCheckAdapter
 * @ author:        QING
 * @ createTime:    2020/4/24 18:08
 * @ describe:      TODO 选中适配器
 */
public class CustomTextCheckAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {
    private boolean isEdit;

    public CustomTextCheckAdapter(List<BaseItemBean> data) {
        super(R.layout.item_custom_text_check, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseItemBean model) {
        helper.getView(R.id.ivCheck).setVisibility(isEdit ? View.VISIBLE : View.GONE);
        helper.getView(R.id.ivCheck).setSelected(model.isSelect());
        helper.setText(R.id.tvTitle, model.getTitle());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
