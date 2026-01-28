package com.yyt.trackcar.ui.adapter;

import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;

import java.util.List;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      HealthRecordAdapter
 * @ author:        QING
 * @ createTime:    6/3/21 15:48
 * @ describe:      TODO
 */
public class HealthRecordAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {

    public HealthRecordAdapter(List<BaseItemBean> data) {
        super(R.layout.item_health_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseItemBean item) {
        helper.setText(R.id.tvNum, item.getTitle());
        helper.setText(R.id.tvUnit, item.getContent());
        if (item.getTitleColor() != 0)
            helper.setTextColor(R.id.tvNum, ContextCompat.getColor(mContext, item.getTitleColor()));
        helper.setText(R.id.tvTime, item.getGroup());
        if (item.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.btn_custom_item_selector);
        else
            helper.setBackgroundRes(R.id.rootView, item.getBgDrawable());
    }
}
