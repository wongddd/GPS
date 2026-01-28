package com.yyt.trackcar.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      FindAdapter
 * @ author:        QING
 * @ createTime:    2020/9/3 16:45
 * @ describe:      TODO 发现适配器
 */
public class FindAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {

    public FindAdapter(List<BaseItemBean> data) {
        super(R.layout.item_find, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseItemBean item) {
        helper.setImageResource(R.id.ivImage, item.getImgDrawable());
    }
}
