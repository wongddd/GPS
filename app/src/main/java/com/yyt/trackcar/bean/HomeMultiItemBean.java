package com.yyt.trackcar.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      HomeMultiItemBean
 * @ author:        QING
 * @ createTime:    2023/3/24 17:49
 * @ describe:      TODO 主页选项对象
 */
public class HomeMultiItemBean implements MultiItemEntity {

    private List<BaseItemBean> mList;
    private int mItemType;

    @Override
    public int getItemType() {
        return mItemType;
    }

    public void setItemType(int mItemType) {
        this.mItemType = mItemType;
    }

    public List<BaseItemBean> getList() {
        return mList;
    }

    public void setList(List<BaseItemBean> mList) {
        this.mList = mList;
    }

    public HomeMultiItemBean(List<BaseItemBean> mList, int mItemType) {
        this.mList = mList;
        this.mItemType = mItemType;
    }

}
