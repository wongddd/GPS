package com.yyt.trackcar.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.AAABaseItemBean;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * projectName：   CarGps
 * packageName：   com.yyt.trackcar.ui.adapter
 * fileName：      ArrowValueAdapter
 * author：        QING
 * createTime：    2019/2/20 14:44
 * describe：      TODO Arrow.值适配器
 */
public class ArrowValueAdapter extends BGARecyclerViewAdapter<AAABaseItemBean> {

    public ArrowValueAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_arrow_value);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, AAABaseItemBean model) {
        helper.setText(R.id.tvTitle, model.getTitle()).setText(R.id.tvContent, model.getContent());
        if (model.isHasArrow())
            helper.setVisibility(R.id.ivArrow, View.VISIBLE);
        else
            helper.setVisibility(R.id.ivArrow, View.GONE);
    }


    public void setData(List<AAABaseItemBean> data) {
        mData = data;
        notifyDataSetChangedWrapper();
    }
}
