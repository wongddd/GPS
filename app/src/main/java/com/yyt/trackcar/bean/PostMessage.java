package com.yyt.trackcar.bean;
/**
 * @ projectName:   传信鸽
 * @ packageName:   com.yyt.trackcar.bean
 * @ fileName:      PostMessage
 * @ author:        QING
 * @ createTime:    2020/3/10 03:52
 * @ describe:      TODO 传递数据对象
 */
public class PostMessage {
    private int type; // 类型
    private String message; // 信息

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PostMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public PostMessage(int type) {
        this.type = type;
    }

    public PostMessage() {
    }
}
