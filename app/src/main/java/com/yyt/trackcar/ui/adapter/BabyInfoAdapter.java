package com.yyt.trackcar.ui.adapter;

import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.utils.ImageLoadUtils;

import java.io.File;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      BabyInfoAdapter
 * @ author:        QING
 * @ createTime:    2020/3/11 20:01
 * @ describe:      TODO 宝贝信息适配器
 */
public class BabyInfoAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {

    public BabyInfoAdapter(List<SectionItem> data) {
        super(R.layout.item_baby_info, R.layout.item_space_section, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        if (model.getImgDrawable() == 0) {
            helper.setImageDrawable(R.id.ivPortrait, null);
            helper.getView(R.id.ivPortrait).setVisibility(View.GONE);
        } else {
            if (TextUtils.isEmpty(model.getGroup()))
                ImageLoadUtils.loadPortraitImage(mContext, model.getGroup(), model.getImgDrawable(),
                        helper.getView(R.id.ivPortrait));
            else if (new File(model.getGroup()).exists())
                helper.setImageBitmap(R.id.ivPortrait, BitmapFactory.decodeFile(model.getGroup()));
            else {
                int imgRes = R.mipmap.ic_default_pigeon_marker;
                ImageLoadUtils.loadPortraitImage(mContext, model.getGroup(),
                        imgRes, helper.getView(R.id.ivPortrait));
            }
            helper.setVisible(R.id.ivPortrait, true);
        }
        helper.setText(R.id.tvTitle, model.getTitle());
        helper.setText(R.id.tvContent, model.getContent());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
        helper.getView(R.id.ivArrow).setVisibility(model.isHasArrow() ? View.VISIBLE : View.GONE);
    }
}
