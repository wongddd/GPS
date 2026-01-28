package com.yyt.trackcar.bean;

import java.util.List;

/**
 * @ projectName:
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      ListResultBean
 * @ author:        QING
 * @ createTime:    6/20/21 15:51
 * @ describe:      TODO
 */
public class ListResultBean {

    private int total;
    private int page;
    private int records;
    private List rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
