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
 * @ fileName:      SelectNameAdapter
 * @ author:        QING
 * @ createTime:    2020/3/10 20:22
 * @ describe:      TODO 选择名称适配器
 */
public class SelectNameAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {

    public SelectNameAdapter(List<SectionItem> data) {
        super(R.layout.item_select_name, R.layout.item_space_section, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        helper.setImageResource(R.id.ivIcon, model.getImgDrawable());
        helper.setText(R.id.tvContent, model.getTitle());
        helper.addOnClickListener(R.id.ivIcon);
    }
}
