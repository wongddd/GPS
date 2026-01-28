package com.yyt.trackcar.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.SmsModel;
import com.yyt.trackcar.utils.TimeUtils;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      SmsCollectionAdapter
 * @ author:        QING
 * @ createTime:    2020/3/13 10:37
 * @ describe:      TODO 代收手表短信适配器
 */
public class SmsCollectionAdapter extends BaseQuickAdapter<SmsModel, BaseViewHolder> {
    private boolean isEdit;

    public SmsCollectionAdapter(List<SmsModel> data) {
        super(R.layout.item_sms_collection, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SmsModel model) {
        helper.getView(R.id.ivCheck).setVisibility(isEdit ? View.VISIBLE : View.GONE);
        helper.getView(R.id.ivCheck).setSelected(model.isSelect());
        helper.setText(R.id.tvTitle, model.getPhone());
        helper.setText(R.id.tvContent, model.getRmsg());
        helper.setText(R.id.tvTime, TimeUtils.getSmsDateDescriptionByNow(mContext, model.get_time()));
        if (model.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.bg_white);
        else
            helper.setBackgroundRes(R.id.rootView, model.getBgDrawable());
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
