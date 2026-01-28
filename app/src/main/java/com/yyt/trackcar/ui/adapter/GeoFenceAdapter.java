package com.yyt.trackcar.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;
import com.yyt.trackcar.R;
import com.yyt.trackcar.bean.GeoFenceBean;
import com.yyt.trackcar.utils.AAAStringUtils;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.ui.adapter
 * @ fileName:      GeoFenceAdapter
 * @ author:        QING
 * @ createTime:    6/20/21 17:17
 * @ describe:      TODO
 */
public class GeoFenceAdapter extends BaseEmptyAdapter<GeoFenceBean> implements
        SwipeItemMangerInterface, SwipeAdapterInterface {
    private SwipeItemRecyclerMangerImpl mItemManger;

    public GeoFenceAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.activity_electronic_item);
        mItemManger = new SwipeItemRecyclerMangerImpl(this);
    }

    @Override
    protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
        super.setItemChildListener(helper, viewType);
        helper.setItemChildClickListener(R.id.item_delete_btn);
        helper.setItemChildClickListener(R.id.item_layout);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, GeoFenceBean model) {
        if (mData.size() == 0) {
            helper.setBackgroundRes(R.id.ivEmpty, R.mipmap.electronic_normal);
            helper.setText(R.id.tvEmpty, R.string.empty_data_electronic_tips);
        } else {
            long radius = model.getRadius() == null ? 0 : model.getRadius();
            String lat = String.valueOf(model.getLatitude() == null ? 0 : model.getLatitude());
            String lon = String.valueOf(model.getLongitude() == null ? 0 : model.getLongitude());
            helper.setText(R.id.item_title, model.getFenceName() == null ? "" :
                    model.getFenceName()).setText(R.id.item_content,
                    mContext.getString(R.string.electronic_description, lat, lon,
                            AAAStringUtils.getMapDistance(radius)));
        }
    }

    @Override
    public void setData(List<GeoFenceBean> data) {
        mData = data;
        notifyDataSetChangedWrapper();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.item_swipe;
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(mode);
    }

}
