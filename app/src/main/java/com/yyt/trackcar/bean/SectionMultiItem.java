package com.yyt.trackcar.bean;

import com.chad.library.adapter.base.entity.SectionMultiEntity;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      SectionMultiItem
 * @ author:        QING
 * @ createTime:    2020/3/13 14:27
 * @ describe:      TODO SectionMultiItem对象
 */
public class SectionMultiItem extends SectionMultiEntity<BaseItemBean> {
    private int mItemType;

    // 创建section 数据
    public SectionMultiItem(boolean isHeader, String header) {
        super(isHeader, header);
    }

    // 创建主体item数据
    public SectionMultiItem(int itemType, BaseItemBean itemBean) {
        super(itemBean);
        this.mItemType = itemType;
    }

    @Override
    public int getItemType() {
        return mItemType;
    }
}
