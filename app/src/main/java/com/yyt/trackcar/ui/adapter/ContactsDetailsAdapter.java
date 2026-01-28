package com.yyt.trackcar.ui.adapter;

import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionMultiItem;
import com.yyt.trackcar.utils.ImageLoadUtils;

import java.io.File;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      ContactsDetailsAdapter
 * @ author:        QING
 * @ createTime:    2020/4/26 15:49
 * @ describe:      TODO 通讯录详情适配器
 */
public class ContactsDetailsAdapter extends BaseSectionMultiItemQuickAdapter<SectionMultiItem,
        BaseViewHolder> {

    public ContactsDetailsAdapter(List<SectionMultiItem> data) {
        super(R.layout.item_space_section, data);
        addItemType(0, R.layout.item_settings_section);
        addItemType(1, R.layout.item_baby_info);
        addItemType(2, R.layout.item_custom_sub_check);
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
                break;
            case 1:
                if (model.getImgDrawable() == 0) {
                    helper.setImageDrawable(R.id.ivPortrait, null);
                    helper.getView(R.id.ivPortrait).setVisibility(View.GONE);
                } else {
                    if (TextUtils.isEmpty(model.getGroup()))
                        ImageLoadUtils.loadPortraitImage(mContext, "",model.getImgDrawable(),
                                helper.getView(R.id.ivPortrait));
                    else if (new File(model.getGroup()).exists())
                        helper.setImageBitmap(R.id.ivPortrait,
                                BitmapFactory.decodeFile(model.getGroup()));
                    else
                        ImageLoadUtils.loadPortraitImage(mContext, model.getGroup(),
                                model.getImgDrawable(),
                                helper.getView(R.id.ivPortrait));
                    helper.setVisible(R.id.ivPortrait, true);
                }
                helper.setText(R.id.tvTitle, model.getTitle());
                helper.setText(R.id.tvContent, model.getContent());
                if (model.getBgDrawable() == 0)
                    helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
                else
                    helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
                helper.getView(R.id.ivArrow).setVisibility(model.isHasArrow() ? View.VISIBLE :
                        View.GONE);
                break;
            case 2:
                helper.setText(R.id.tvTitle, model.getTitle());
                helper.setText(R.id.tvContent, model.getContent());
                helper.getView(R.id.ivCheck).setSelected(model.isSelect());
                if (model.getBgDrawable() == 0)
                    helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
                else
                    helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
                break;
            default:
                break;
        }
    }
}
