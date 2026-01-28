package com.yyt.trackcar.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yyt.trackcar.R;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGARecyclerViewHolder;

/**
 * 项目名：   传信鸽
 * 包名：     com.yyt.trackcar.ui.adapter
 * 文件名：   BaseEmptyAdapter
 * 创建者：   QING
 * 创建时间： 2018/5/27 15:57
 * 描述：     TODO 空数据适配器基类
 */

public abstract class BaseEmptyAdapter<M> extends BGARecyclerViewAdapter<M> {
    private static final int EMPTY_VIEW = 0;

    public BaseEmptyAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public BaseEmptyAdapter(RecyclerView recyclerView, int defaultItemLayoutId) {
        super(recyclerView, defaultItemLayoutId);
    }

    @NonNull
    @Override
    public BGARecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY_VIEW)
            return new BGARecyclerViewHolder(this, mRecyclerView,
                    LayoutInflater.from(mContext).inflate(R.layout.layout_empty_view, parent,
                            false), null, null);
        else
            return super.onCreateViewHolder(parent, viewType);

    }

    @Override
    public M getItem(int position) {
        if (mData.size() == 0)
            return null;
        else
            return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        return mData.size() > 0 ? mData.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.size() == 0)
            return EMPTY_VIEW;
        else
            return super.getItemViewType(position);
    }
}
