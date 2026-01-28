package com.yyt.trackcar.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      WifiAdapter
 * @ author:        QING
 * @ createTime:    2020/3/12 11:40
 * @ describe:      TODO 选择Wifi适配器
 */
public class WifiAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {

    public WifiAdapter(List<SectionItem> data) {
        super(R.layout.item_more, R.layout.item_custom_section, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
        helper.setText(R.id.tvTitle, item.header);
        helper.getView(R.id.tvTitle).setVisibility(TextUtils.isEmpty(item.header) ? View.GONE :
                View.VISIBLE);
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        helper.setImageResource(R.id.ivIcon, model.getImgDrawable());
        if (model.isSelect()) {
            SpannableString spannableString = new SpannableString(String.format("%s%s",
                    model.getTitle(), mContext.getString(R.string.have_chosen)));
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,
                    R.color.skyblue)),
                    spannableString.length() - mContext.getString(R.string.have_chosen).length(),
                    spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView tvTitle = helper.getView(R.id.tvTitle);
            tvTitle.setText(spannableString);
        } else
            helper.setText(R.id.tvTitle, model.getTitle());
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
        helper.getView(R.id.ivRedDot).setVisibility(View.GONE);
        helper.getView(R.id.ivArrow).setVisibility(View.GONE);
    }
}
