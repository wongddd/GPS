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
 * @ fileName:      PersonalCenterAdapter
 * @ author:        QING
 * @ createTime:    2020/3/11 18:33
 * @ describe:      TODO 个人中心适配器
 */
public class PersonalCenterAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {

    public PersonalCenterAdapter(List<SectionItem> data) {
        super(R.layout.item_personal_center, R.layout.item_space_section, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        if (model.getImgDrawable() == 0) {
            helper.setImageDrawable(R.id.ivIcon, null);
            helper.getView(R.id.ivIcon).setVisibility(View.GONE);
        } else {
            helper.setImageResource(R.id.ivIcon, model.getImgDrawable());
            helper.setVisible(R.id.ivIcon, true);
        }
        helper.setText(R.id.tvTitle, model.getTitle());
        helper.setText(R.id.tvContent, model.getContent());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
        helper.getView(R.id.ivArrow).setVisibility(model.isHasArrow()? View.VISIBLE:View.GONE);
    }
}
