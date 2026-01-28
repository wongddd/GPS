package com.yyt.trackcar.ui.adapter;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      CustomSelectorAdapter
 * @ author:        QING
 * @ createTime:    2020/3/13 17:01
 * @ describe:      TODO 选择适配器
 */
public class CustomSelectorAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {

    public CustomSelectorAdapter(List<SectionItem> data) {
        super(R.layout.item_custom_selector, R.layout.item_space_section, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        if (model.getContent() != null)
            helper.setText(R.id.tvTitle, String.format("%s: %s\nIMEI: %s", mContext.getString(R.string.nickname),model.getTitle(),model.getContent()));
        else
            helper.setText(R.id.tvTitle,model.getTitle());
        helper.getView(R.id.ivCheck).setSelected(model.isSelect());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
    }
}
