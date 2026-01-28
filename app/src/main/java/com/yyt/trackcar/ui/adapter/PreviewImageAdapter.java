package com.yyt.trackcar.ui.adapter;

import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xuexiang.xui.widget.imageview.IconImageView;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.PhotoBean;
import com.yyt.trackcar.ui.widget.imageview.preview.GlideMediaLoader;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      PreviewImageAdapter
 * @ author:        QING
 * @ createTime:    2020/4/16 18:40
 * @ describe:      TODO 图片查看适配器
 */
public class PreviewImageAdapter extends BaseQuickAdapter<PhotoBean, BaseViewHolder> {
    private boolean isEdit; // 是否编辑

    public PreviewImageAdapter(List<PhotoBean> data) {
        super(R.layout.item_preview_image, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, @NotNull PhotoBean item) {
        IconImageView imageView = helper.getView(R.id.ivImage);
        Glide.with(imageView.getContext())
                .load(item.getUrl())
                .apply(GlideMediaLoader.getRequestOptions())
                .into(imageView);
        imageView.setTag(R.id.ivImage, item.getUrl());
        helper.getView(R.id.ivCheck).setVisibility(isEdit ? View.VISIBLE : View.GONE);
        helper.getView(R.id.ivCheck).setSelected(item.isSelect());
        helper.addOnClickListener(R.id.ivCheck);
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
