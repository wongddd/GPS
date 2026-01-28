package com.yyt.trackcar.ui.adapter;

import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.utils.DensityUtils;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.CommandMessageBean;
import com.yyt.trackcar.utils.StringUtils;
import com.yyt.trackcar.utils.TextColorSizeHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      MessageCenterAdapter
 * @ author:        QING
 * @ createTime:    2023/7/11 16:05
 * @ describe:      TODO 消息中心适配器
 */
public class MessageCenterAdapter extends BaseQuickAdapter<CommandMessageBean, BaseViewHolder> {

    public MessageCenterAdapter(List<CommandMessageBean> data) {
        super(R.layout.item_message_center, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommandMessageBean model) {
        String type;
        switch (model.getCommandFlag()) {
            case 1: // 已发送
                type = mContext.getString(R.string.send_command_type_second);
                break;
            case 2: // 已确认
                type = mContext.getString(R.string.send_command_type_third);
                break;
            case 3: // 已接收
                type = mContext.getString(R.string.send_command_type_fourth);
                break;
            case 4: // 已回复
                type = mContext.getString(R.string.send_command_type_fifth);
                break;
            default: // 待发送
                type = mContext.getString(R.string.send_command_type_first);
                break;
        }
        List<TextColorSizeHelper.SpanInfo> list = new ArrayList<>();
        list.add(new TextColorSizeHelper.SpanInfo(String.format(" %s ",
                type),
                DensityUtils.sp2px(12),
                ContextCompat.getColor(mContext, R.color.white),
                ContextCompat.getColor(mContext, R.color.red),
                DensityUtils.dp2px(2), true));
        helper.setText(R.id.tvImei, TextColorSizeHelper.getTextSpan(mContext, String.format("%s " +
                "[%s]  %s ", StringUtils.getNotNullText(model.getSendTime()),
                StringUtils.getNotNullText(model.getDeviceImei()), type), list));
        helper.setText(R.id.tvContent, model.getReceiveCommand());
    }
}
