package com.yyt.trackcar.bean;

import java.util.List;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      ListResponseBean
 * @ author:        QING
 * @ createTime:    9/26/21 14:28
 * @ describe:      TODO
 */
public class AAAListResponseBean {

    private List list;
    private Long maxLogId;

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public Long getMaxLogId() {
        return maxLogId;
    }

    public void setMaxLogId(long maxLogId) {
        this.maxLogId = maxLogId;
    }
}
