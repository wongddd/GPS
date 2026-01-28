package com.yyt.trackcar.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseItemBean;

import java.util.List;

public class DeviceSettingItemAdapter extends BaseQuickAdapter<AAABaseItemBean, BaseViewHolder> {
    public DeviceSettingItemAdapter(@Nullable List<AAABaseItemBean> data) {
        super(R.layout.item_arrow_value,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AAABaseItemBean item) {
        helper.setText(R.id.tvTitle, item.getTitle()).setText(R.id.tvContent, item.getContent());
        if (item.isHasArrow())
            helper.getView(R.id.ivArrow).setVisibility(View.VISIBLE);
        else
        helper.getView(R.id.ivArrow).setVisibility(View.GONE);
    }
}
