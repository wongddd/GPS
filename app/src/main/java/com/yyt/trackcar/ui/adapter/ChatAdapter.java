package com.yyt.trackcar.ui.adapter;

import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.widget.textview.badge.BadgeView;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;
import com.yyt.trackcar.utils.CWConstant;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.SettingSPUtils;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      ChatAdapter
 * @ author:        QING
 * @ createTime:    2020-03-04 21:52
 * @ describe:      TODO 微聊适配器
 */
public class ChatAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {

    public ChatAdapter(List<SectionItem> data) {
        super(R.layout.item_chat, R.layout.item_space_section, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        ImageView ivPortrait = helper.getView(R.id.ivPortrait);
        int imgRes;
        if (SettingSPUtils.getInstance().getInt(CWConstant.DEVICE_TYPE, 0) == 0)
            imgRes = R.mipmap.ic_device_portrait;
        else
            imgRes = R.mipmap.ic_default_pigeon_marker;
        if (model.getObject() == null && model.getImgDrawable() == 0)
            ImageLoadUtils.loadPortraitImage(mContext, "", imgRes, ivPortrait);
        else if (model.getObject() == null)
            ImageLoadUtils.loadPortraitImage(mContext, "", model.getImgDrawable(), ivPortrait);
        else
            ImageLoadUtils.loadPortraitImage(mContext, (String) model.getObject(),
                    imgRes, ivPortrait);
        helper.setText(R.id.tvTitle, model.getTitle());
        if (model.getSpanString() == null)
            helper.setText(R.id.tvContent, model.getContent());
        else
            helper.setText(R.id.tvContent, new SpannableString(model.getSpanString()));
        helper.setText(R.id.tvTime, model.getGroup());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
        View badgeView = helper.getView(R.id.badgeView);
        BadgeView badge;
        if (badgeView.getTag() == null) {
            badge = new BadgeView(mContext);
            badge.bindTarget(badgeView);
            badge.setBadgeGravity(Gravity.END | Gravity.TOP);
            badge.setGravityOffset(0, 0, true);
            badgeView.setTag(badge);
        } else
            badge = (BadgeView) badgeView.getTag();
        if (model.isSelect() && model.getType() == -1)
            badge.setBadgeText("");
        else if (model.isSelect())
            badge.setBadgeNumber(model.getType());
        else
            badge.setBadgeNumber(0);
    }

}
