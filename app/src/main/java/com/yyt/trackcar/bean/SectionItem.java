package com.yyt.trackcar.bean;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      SectionItem
 * @ author:        QING
 * @ createTime:    2020/3/5 15:44
 * @ describe:      TODO SectionItem对象
 */
public class SectionItem extends SectionEntity<BaseItemBean> {

    public SectionItem(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public SectionItem(BaseItemBean baseItemBean) {
        super(baseItemBean);
    }
}
