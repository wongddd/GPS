package com.yyt.trackcar.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yyt.trackcar.R;
import com.yyt.trackcar.country.CountrySortModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.indexablerv.IndexableAdapter;


/**
 * projectName：   ChatYou
 * packageName：   com.llt.chatyou.ui.adapter
 * fileName：      CountryCodeSelectAdapter
 * author：        QING
 * createTime：    2018/9/25 14:00
 * describe：      TODO 国家或地区区号选择适配器
 */
public class CountryCodeSelectAdapter extends IndexableAdapter<CountrySortModel> {
    private LayoutInflater mInflater;

    public CountryCodeSelectAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_country_code_select_title, parent, false);
        return new IndexVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_country_code_select_item, parent, false);
        return new ContentVH(view);
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
        IndexVH vh = (IndexVH) holder;
        vh.tv.setText(indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, CountrySortModel entity) {
        ContentVH vh = (ContentVH) holder;
        vh.tvName.setText(entity.countryName);
        vh.tvMobile.setText(entity.countryNumber);
    }

    class IndexVH extends RecyclerView.ViewHolder {
        @BindView(R.id.tvItemSort)
        TextView tv;

        IndexVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ContentVH extends RecyclerView.ViewHolder {
        @BindView(R.id.tvItemCountryName)
        TextView tvName;
        @BindView(R.id.tvItemCountryCode)
        TextView tvMobile;

        ContentVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
