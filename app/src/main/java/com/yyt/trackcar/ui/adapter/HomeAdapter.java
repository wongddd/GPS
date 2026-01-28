package com.yyt.trackcar.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.BaseItemBean;
import com.yyt.trackcar.bean.HomeMultiItemBean;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      HomeAdapter
 * @ author:        QING
 * @ createTime:    2023/3/24 17:56
 * @ describe:      TODO 主页适配器
 */
public class HomeAdapter extends BaseMultiItemQuickAdapter<HomeMultiItemBean, BaseViewHolder> {

    public HomeAdapter(List<HomeMultiItemBean> data) {
        super(data);
        addItemType(0, R.layout.item_home_first);
        addItemType(1, R.layout.item_home_second);
        addItemType(2, R.layout.item_home_third);
        addItemType(3, R.layout.item_home_fourth);
        addItemType(4, R.layout.item_home_fifth);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeMultiItemBean model) {
        List<BaseItemBean> list = model.getList();
        if (helper.getView(R.id.xlLayoutFirst) != null) {
            if (list != null && list.size() > 0) {
                BaseItemBean itemModel = list.get(0);
                helper.setBackgroundColor(R.id.xlLayoutFirst, itemModel.getBgDrawable());
                helper.setImageResource(R.id.ivIconFirst, itemModel.getImgDrawable());
                helper.setText(R.id.tvNameFirst, itemModel.getTitle());
            } else {
                helper.getView(R.id.xlLayoutFirst).setVisibility(View.INVISIBLE);
            }
            helper.addOnClickListener(R.id.xlLayoutFirst);
        }
        if (helper.getView(R.id.xlLayoutSecond) != null) {
            if (list != null && list.size() > 1) {
                BaseItemBean itemModel = list.get(1);
                helper.setBackgroundColor(R.id.xlLayoutSecond, itemModel.getBgDrawable());
                helper.setImageResource(R.id.ivIconSecond, itemModel.getImgDrawable());
                helper.setText(R.id.tvNameSecond, itemModel.getTitle());
            } else {
                helper.getView(R.id.xlLayoutSecond).setVisibility(View.INVISIBLE);
            }
            helper.addOnClickListener(R.id.xlLayoutSecond);
        }
        if (helper.getView(R.id.xlLayoutThird) != null) {
            if (list != null && list.size() > 2) {
                BaseItemBean itemModel = list.get(2);
                helper.setBackgroundColor(R.id.xlLayoutThird, itemModel.getBgDrawable());
                helper.setImageResource(R.id.ivIconThird, itemModel.getImgDrawable());
                helper.setText(R.id.tvNameThird, itemModel.getTitle());
            } else {
                helper.getView(R.id.xlLayoutThird).setVisibility(View.INVISIBLE);
            }
            helper.addOnClickListener(R.id.xlLayoutThird);
        }
    }
}
