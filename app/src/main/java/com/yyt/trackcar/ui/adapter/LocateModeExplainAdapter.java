package com.yyt.trackcar.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      LocateModeExplain
 * @ author:        QING
 * @ createTime:    2020/3/9 18:04
 * @ describe:      TODO 定位方式适配器
 */
public class LocateModeExplainAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {

    public LocateModeExplainAdapter(List<BaseItemBean> data) {
        super(R.layout.item_locate_mode_explain, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseItemBean item) {
        helper.setImageResource(R.id.ivIcon, item.getImgDrawable());
        helper.setText(R.id.tvTitle, item.getTitle());
        helper.setText(R.id.tvContent, item.getContent());
    }
}
