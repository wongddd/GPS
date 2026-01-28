package com.yyt.trackcar.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.TrackCircleBean;

import java.text.SimpleDateFormat;
import java.util.List;

public class TrackCircleAdapter extends BaseQuickAdapter<TrackCircleBean, BaseViewHolder> {
    private long authorId;
    public TrackCircleAdapter(@Nullable List<TrackCircleBean> data,long authorId) {
        super(R.layout.adapter_track_circle,data);
        this.authorId = authorId;
    }
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void convert(BaseViewHolder helper, TrackCircleBean item) {
        if (item.getFileinfoId() != null && item.getFileinfoId().size() > 0){ // show the image of trajectory
            Glide.with(mContext)
                    .asBitmap()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(item.getFileinfoId().get(0))
                    .placeholder(R.mipmap.ic_default_pigeon_marker)
                    .error(R.mipmap.ic_default_pigeon_marker)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ImageView imageView = (ImageView) helper.getView(R.id.iv_screenshot);
                            int width = resource.getWidth();
                            int currentWidth = imageView.getWidth();
                            int height = resource.getHeight();
                            imageView.setImageBitmap(resource);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(currentWidth, (int) Math.ceil((double)currentWidth/width*height));
                            imageView.setLayoutParams(layoutParams);
//                            KLog.d( "width: "+width+" currentWidth: "+currentWidth+" height: "+height+" currentHeight: "+(int)((float)currentWidth/(float)width*(float)height));
                        }
                    });
        }
        if (item.getStarDatetime() != null && item.getEndDatetime() != null)
            helper.setText(R.id.tv_period,String.format("%s - %s",simpleDateFormat.format(item.getStarDatetime()),simpleDateFormat.format(item.getEndDatetime())));

        if (item.getCircleUid() == authorId)
            helper.getView(R.id.fl_delete).setVisibility(View.VISIBLE);

        helper.setText(R.id.tv_username,item.getDeviceImei())
                .setText(R.id.tv_subject,item.getCircleSubject())
                .setText(R.id.tv_distance,String.format("%s: %skm",mContext.getString(R.string.straight_line_distance),item.getDistance() == null? 1 : (int) Math.ceil(item.getDistance()/1000.0d)))
                .setText(R.id.tv_count_thumbs_up,String.valueOf(item.getThumbsup() == null ? 0 : item.getThumbsup()))
                .setText(R.id.tv_views,String.format("%s:%s",mContext.getString(R.string.count_views),item.getViews() == null? 0 : item.getViews()));
        helper.getView(R.id.iv_thumbs_up).setSelected(item.getIsthumbsup() == 1);
        helper.addOnClickListener(R.id.ll_thumbs_up)
                .addOnClickListener(R.id.fl_delete)
                .addOnClickListener(R.id.fl_enter_map);
    }
}
