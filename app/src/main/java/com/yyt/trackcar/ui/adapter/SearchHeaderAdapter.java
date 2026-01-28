package com.yyt.trackcar.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.yyt.trackcar.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.indexablerv.IndexableHeaderAdapter;

/**
 * projectName：   ChatYou
 * packageName：   com.llt.chatyou.ui.adapter
 * fileName：      SearchHeaderAdapter
 * author：        QING
 * createTime：    2018/9/25 14:01
 * describe：      TODO 搜索顶部布局适配器
 */
public class SearchHeaderAdapter extends IndexableHeaderAdapter<Object> {
    private Context mContext; // 上下文
    private View.OnClickListener mClickListener;
    private static final int TYPE = 1; // 类型

    public SearchHeaderAdapter(Context context, String index, String indexTitle, List<Object>
            datas, View.OnClickListener mClickListener) {
        super(index, indexTitle, datas);
        this.mContext = context;
        this.mClickListener = mClickListener;
    }

    @Override
    public int getItemViewType() {
        return TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout
                .layout_search, parent, false);
        VH holder = new VH(view);
        holder.searchBar.setOnClickListener(mClickListener);
        return holder;
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, Object entity) {
        // 数据源为null时, 该方法不用实现
    }

    class VH extends RecyclerView.ViewHolder {
        @BindView(R.id.searchBar)
        MaterialSearchBar searchBar;

        VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
