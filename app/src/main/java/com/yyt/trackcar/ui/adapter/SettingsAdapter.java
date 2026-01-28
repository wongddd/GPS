package com.yyt.trackcar.ui.adapter;

import android.view.View;
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
 * @ fileName:      SettingsAdapter
 * @ author:        QING
 * @ createTime:    2020/3/6 12:29
 * @ describe:      TODO 设置适配器
 */
public class SettingsAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener;

    public SettingsAdapter(List<SectionItem> data,
                           CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        super(R.layout.item_settings, R.layout.item_settings_section, data);
        mCheckedChangeListener = checkedChangeListener;
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
        helper.setText(R.id.tvTitle, item.header);
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        boolean iconVisible = (model.getImgDrawable() != 0);
        helper.getView(R.id.ivIcon).setVisibility(iconVisible? View.VISIBLE:View.GONE);
        if (iconVisible)
            helper.setImageResource(R.id.ivIcon, model.getImgDrawable());
        else
            helper.setImageDrawable(R.id.ivIcon, null);
        helper.setText(R.id.tvTitle, model.getTitle());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
        helper.setEnabled(R.id.rootView, false);
        SwitchButton switchButton = helper.getView(R.id.sbSwitch);
        switchButton.setTag(model.getType());
        switchButton.setOnCheckedChangeListener(null);
        switchButton.setCheckedImmediately(model.isSelect());
        switchButton.setOnCheckedChangeListener(mCheckedChangeListener);
    }
}
