package com.yyt.trackcar.ui.adapter;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.SectionItem;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      DeviceMessageAdapter
 * @ author:        QING
 * @ createTime:    2020/3/6 17:53
 * @ describe:      TODO 系统消息适配器
 */
public class DeviceMessageAdapter extends BaseSectionQuickAdapter<SectionItem, BaseViewHolder> {

    public DeviceMessageAdapter(List<SectionItem> data) {
        super(R.layout.item_device_message, R.layout.item_device_message_section, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SectionItem item) {
        helper.setText(R.id.tvTitle, item.header);
    }


    @Override
    protected void convert(BaseViewHolder helper, SectionItem item) {
        BaseItemBean model = item.t;
        helper.setImageResource(R.id.ivIcon, model.getImgDrawable());
        helper.setText(R.id.tvContent, model.getTitle());
        helper.setText(R.id.tvTime, model.getContent());
    }
}
