package com.yyt.trackcar.ui.adapter;

import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionMultiItem;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      BanClassesAdapter
 * @ author:        QING
 * @ createTime:    2020/3/13 14:20
 * @ describe:      TODO 上课禁用适配器
 */
public class BanClassesAdapter extends BaseSectionMultiItemQuickAdapter<SectionMultiItem,
        BaseViewHolder> {
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener;

    public BanClassesAdapter(List<SectionMultiItem> data,
                             CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        super(R.layout.item_space_section, data);
        addItemType(0, R.layout.item_custom_text);
        addItemType(1, R.layout.item_custom_switch);
        addItemType(2, R.layout.item_ban_classes);
        addItemType(3, R.layout.item_custom_remark);
        addItemType(4, R.layout.item_device_message_section);
        mCheckedChangeListener = checkedChangeListener;
    }

    @Override
    protected void convertHead(BaseViewHolder helper, final SectionMultiItem item) {
    }

    @Override
    protected void convert(BaseViewHolder helper, SectionMultiItem item) {
        BaseItemBean model = item.t;
        switch (helper.getItemViewType()) {
            case 0:
                helper.setText(R.id.tvTitle, model.getTitle());
                helper.setText(R.id.tvContent, model.getContent());
                if (model.getBgDrawable() == 0)
                    helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
                else
                    helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
                helper.getView(R.id.ivArrow).setVisibility(model.isHasArrow() ? View.VISIBLE :
                        View.GONE);
                helper.getView(R.id.ivRedDot).setVisibility(model.isSelect() ? View.VISIBLE :
                        View.GONE);
                break;
            case 1:
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
                break;
            case 2:
                helper.setText(R.id.tvContent, model.getContent());
                if (model.getBgDrawable() == 0)
                    helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
                else
                    helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
                helper.getView(R.id.ivArrow).setVisibility(model.isHasArrow() ? View.VISIBLE :
                        View.GONE);
                break;
            case 3:
                helper.setText(R.id.tvContent, model.getContent());
                if (model.getBgDrawable() == 0)
                    helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
                else
                    helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
                break;
            case 4:
                helper.setText(R.id.tvTitle, model.getTitle());
                break;
            default:
                break;
        }
    }
}
