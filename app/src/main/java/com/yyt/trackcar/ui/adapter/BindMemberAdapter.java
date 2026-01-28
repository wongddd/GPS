package com.yyt.trackcar.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.UserBean;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.TextColorSizeHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      BindMemberAdapter
 * @ author:        QING
 * @ createTime:    2020/3/12 09:39
 * @ describe:      TODO 绑定成员适配器
 */
public class BindMemberAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    public BindMemberAdapter(List<UserBean> data) {
        super(R.layout.item_bind_member, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull UserBean item) {
        if (item.getImgDrawable() == R.mipmap.ic_add_member)
            ImageLoadUtils.loadPortraitImage(mContext, item.getUrl(), R.mipmap.ic_add_member,
                    helper.getView(R.id.ivPortrait));
        else
            ImageLoadUtils.loadPortraitImage(mContext, item.getUrl(),
                    helper.getView(R.id.ivPortrait));
        String name = item.getName();
        if (name == null)
            name = " ";
        TextView tvTitle = helper.getView(R.id.tvTitle);
        if (item.isMe() || item.getStatus() == 1) {
            String type;
            if (item.getStatus() == 1 && item.isMe())
                type = String.format("%s %s", mContext.getString(R.string.manager),
                        mContext.getString(R.string.me));
            else if (item.getStatus() == 1)
                type = mContext.getString(R.string.manager);
            else
                type = mContext.getString(R.string.me);
            type = String.format(" %s ", type);
            String text = String.format("%s %s", name, type);
            List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
            list.add(new TextColorSizeHelper.SpanInfo(type,
                    com.xuexiang.xui.utils.DensityUtils.sp2px(14),
                    ContextCompat.getColor(mContext, R.color.white),
                    ContextCompat.getColor(mContext, R.color.colorTexNormal),
                    com.xuexiang.xui.utils.DensityUtils.dp2px(2), true));
            tvTitle.setText(TextColorSizeHelper.getTextSpan(mContext, text, list));
        } else {
            helper.setText(R.id.tvTitle, name);
        }
//        helper.getView(R.id.tvContent).setVisibility(item.getStatus() == 0? View.VISIBLE:View
//        .GONE);
        helper.setVisible(R.id.tvContent, item.getStatus() == 0);
        helper.addOnClickListener(R.id.ivPortrait);
    }
}
