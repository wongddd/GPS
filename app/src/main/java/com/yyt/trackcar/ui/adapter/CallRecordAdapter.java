package com.yyt.trackcar.ui.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.dbflow.CallRecordModel;
import com.yyt.trackcar.utils.TimeUtils;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      CallRecordAdapter
 * @ author:        QING
 * @ createTime:    2020/4/13 16:01
 * @ describe:      TODO 通话记录适配器
 */
public class CallRecordAdapter extends BaseQuickAdapter<CallRecordModel, BaseViewHolder> {

    public CallRecordAdapter(List<CallRecordModel> data) {
        super(R.layout.item_call_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CallRecordModel item) {
        if (item.getPhone_type() == 1)
            helper.setImageResource(R.id.ivIcon, R.mipmap.ic_outgoing_call);
        else
            helper.setImageResource(R.id.ivIcon, R.mipmap.ic_incoming_call);
        if (TextUtils.isEmpty(item.getNick_name()))
            helper.setText(R.id.tvTitle, String.format("%s(%s)",
                    mContext.getString(R.string.strange_number), item.getPhone()));
        else
            helper.setText(R.id.tvTitle, String.format("%s(%s)", item.getNick_name(),
                    item.getPhone()));
        String cmdType;
        if(item.getCmdType() == 2)
            cmdType = mContext.getString(R.string.cmd_type_second);
        else if(item.getCmdType() == 3)
            cmdType = mContext.getString(R.string.cmd_type_third);
        else if(item.getCmdType() == 4)
            cmdType = mContext.getString(R.string.cmd_type_fourth);
        else
            cmdType = mContext.getString(R.string.cmd_type_first);
        if (item.getPhone_status() == 1)
            helper.setText(R.id.tvContent,String.format("(%s)%s",cmdType,TimeUtils.getSecond(mContext,
                    String.valueOf(item.getCall_duration()))));
        else
            helper.setText(R.id.tvContent,String.format("(%s)%s",cmdType,mContext.getString(R.string.no_answer)));
        helper.setText(R.id.tvTime, TimeUtils.getCallDateDescriptionByNow(mContext,
                item.getPhone_time()));
        if (item.getBgDrawable() == 0)
            helper.setBackgroundRes(R.id.rootView, R.drawable.bg_white);
        else
            helper.setBackgroundRes(R.id.rootView, item.getBgDrawable());
    }
}
