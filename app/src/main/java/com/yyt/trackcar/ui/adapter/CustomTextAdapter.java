package com.yyt.trackcar.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      CustomTextAdapter
 * @ author:        QING
 * @ createTime:    2020/3/6 13:11
 * @ describe:      TODO 常用文本适配器
 */
public class CustomTextAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {

    public CustomTextAdapter(List<SectionItem> data) {
        super(R.layout.item_custom_text, R.layout.item_space_section, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        helper.setText(R.id.tvTitle, model.getTitle());
        helper.setText(R.id.tvContent, model.getContent());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
        helper.getView(R.id.ivArrow).setVisibility(model.isHasArrow()? View.VISIBLE:View.GONE);
        helper.getView(R.id.ivRedDot).setVisibility(model.isSelect()? View.VISIBLE:View.GONE);
    }
}
