package com.yyt.trackcar.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      SelectDeviceTypeAdapter
 * @ author:        QING
 * @ createTime:    2020/10/22 12:43
 * @ describe:      TODO 选择设备类型适配器
 */
public class SelectDeviceTypeAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {

    public SelectDeviceTypeAdapter(List<BaseItemBean> data) {
        super(R.layout.item_select_device_type, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseItemBean item) {
        helper.setImageResource(R.id.ivIcon, item.getImgDrawable());
        helper.setText(R.id.tvName, item.getTitle());
    }

}
