package com.yyt.trackcar.ui.adapter;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      CustomSwitchSubAdapter
 * @ author:        QING
 * @ createTime:    2020/3/13 16:50
 * @ describe:      TODO 子标题开关适配器
 */
public class CustomSwitchSubAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener;

    public CustomSwitchSubAdapter(List<SectionItem> data,
                                  CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        super(R.layout.item_custom_switch_sub, R.layout.item_space_section, data);
        mCheckedChangeListener = checkedChangeListener;
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
        SwitchButton switchButton = helper.getView(R.id.sbSwitch);
        switchButton.setTag(model.getType());
        switchButton.setOnCheckedChangeListener(null);
        switchButton.setCheckedImmediately(model.isSelect());
        switchButton.setOnCheckedChangeListener(mCheckedChangeListener);
    }
}
