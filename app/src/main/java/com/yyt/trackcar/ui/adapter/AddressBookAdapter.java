package com.yyt.trackcar.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.ContactBean;
import com.yyt.trackcar.utils.ImageLoadUtils;
import com.yyt.trackcar.utils.TextColorSizeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      AddressBookAdapter
 * @ author:        QING
 * @ createTime:    2020/3/12 18:07
 * @ describe:      TODO 宝贝通讯录适配器
 */
public class AddressBookAdapter extends BaseItemDraggableAdapter<ContactBean, BaseViewHolder> {
    private int mType;

    public AddressBookAdapter(List<ContactBean> data) {
        super(R.layout.item_address_book, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ContactBean item) {
        ImageLoadUtils.loadPortraitImage(mContext, item.getPortrait(), item.getPortraitId(),
                helper.getView(R.id.ivPortrait));
        String[] array = item.getContactString().split("\\|");
        String name = item.getName();
        if (name == null)
            name = "";
        TextView tvTitle = helper.getView(R.id.tvTitle);
        if (array.length >= 5 && "1".equals(array[4])) {
            String guard = String.format(" %s ", mContext.getString(R.string.sos));
            String text = String.format("%s  %s", name, guard);
            List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
            list.add(new TextColorSizeHelper.SpanInfo(guard,
                    com.xuexiang.xui.utils.DensityUtils.sp2px(14),
                    ContextCompat.getColor(mContext, R.color.white),
                    ContextCompat.getColor(mContext, R.color.orange),
                    com.xuexiang.xui.utils.DensityUtils.dp2px(2), true));
            tvTitle.setText(TextColorSizeHelper.getTextSpan(mContext, text, list));
        } else {
            helper.setText(R.id.tvTitle, name);
        }
        helper.setText(R.id.tvContent, item.getPhone());
//        if (TextUtils.isEmpty(item.getShortNumber()))
//            helper.setText(R.id.tvShortNumber, mContext.getString(R.string.add_short_number));
//        else
//            helper.setText(R.id.tvShortNumber, item.getShortNumber());
        if (item.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, item.getBgDrawable());
        if (mType == 0)
            helper.setImageResource(R.id.ivArrow, R.mipmap.ic_arrow);
        else if (mType == -1)
            helper.setVisible(R.id.ivArrow, false);
        else
            helper.setImageResource(R.id.ivArrow, R.mipmap.ic_drag);
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }
}
