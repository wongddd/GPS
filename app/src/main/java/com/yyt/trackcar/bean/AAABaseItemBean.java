package com.yyt.trackcar.bean;

/**
 * 列表子item对象
 * Created by zhan on 2017/9/27.
 */

public class AAABaseItemBean {
    private int type; // 类型
    private String group; // 组
    private String title; // 标题
    private String content; // 内容
    private int imgDrawable; // 图片
    private int contentSize; // 内容字体大小
    private int titleColor; // 标题颜色
    private boolean hasArrow; // 是否有指向图标
    private int bgDrawable; // 背景
    private boolean isSelect; // 是否选中
    private Object otherObj;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgDrawable() {
        return imgDrawable;
    }

    public void setImgDrawable(int imgDrawable) {
        this.imgDrawable = imgDrawable;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHasArrow() {
        return hasArrow;
    }

    public void setHasArrow(boolean hasArrow) {
        this.hasArrow = hasArrow;
    }

    public int getBgDrawable() {
        return bgDrawable;
    }

    public void setBgDrawable(int bgDrawable) {
        this.bgDrawable = bgDrawable;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public Object getOtherObj() {
        return otherObj;
    }

    public void setOtherObj(Object otherObj) {
        this.otherObj = otherObj;
    }

    public AAABaseItemBean(int type, String title, int imgDrawable, String content, boolean
            hasArrow, int bgDrawable) {
        this.type = type;
        this.title = title;
        this.imgDrawable = imgDrawable;
        this.content = content;
        this.hasArrow = hasArrow;
        this.bgDrawable = bgDrawable;
    }

    public AAABaseItemBean(int type, String title, String content, boolean hasArrow, int contentSize) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.hasArrow = hasArrow;
        this.contentSize = contentSize;
    }

    public AAABaseItemBean(int type, String title, String content, int imgDrawable) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.imgDrawable = imgDrawable;
    }

    public AAABaseItemBean(int type, String title, int imgDrawable) {
        this.type = type;
        this.title = title;
        this.imgDrawable = imgDrawable;
    }

    public AAABaseItemBean(int type, String title, String content, int imgDrawable, int bgDrawable) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.imgDrawable = imgDrawable;
        this.bgDrawable = bgDrawable;
    }

    public AAABaseItemBean(int type, String group, String title, int imgDrawable, String content,
                           int titleColor,
                           boolean hasArrow, int bgDrawable) {
        this.type = type;
        this.group = group;
        this.title = title;
        this.imgDrawable = imgDrawable;
        this.content = content;
        this.titleColor = titleColor;
        this.hasArrow = hasArrow;
        this.bgDrawable = bgDrawable;
    }

    public AAABaseItemBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public AAABaseItemBean(int type, String group, String title, int imgDrawable, String content,
                           int titleColor,
                           boolean hasArrow, int bgDrawable, boolean isSelect) {
        this.type = type;
        this.group = group;
        this.title = title;
        this.imgDrawable = imgDrawable;
        this.content = content;
        this.titleColor = titleColor;
        this.hasArrow = hasArrow;
        this.bgDrawable = bgDrawable;
        this.isSelect = isSelect;
    }

    public AAABaseItemBean(int type, String group, String title, String content) {
        this.type = type;
        this.group = group;
        this.title = title;
        this.content = content;
    }

    public AAABaseItemBean(int type, int imgDrawable, boolean isSelect) {
        this.type = type;
        this.imgDrawable = imgDrawable;
        this.isSelect = isSelect;
    }

    public AAABaseItemBean(String title, int imgDrawable, boolean isSelect) {
        this.title = title;
        this.imgDrawable = imgDrawable;
        this.isSelect = isSelect;
    }

    public AAABaseItemBean(int type, String title, boolean isSelect) {
        this.type = type;
        this.title = title;
        this.isSelect = isSelect;
    }

    public AAABaseItemBean(String title, String content, boolean isSelect) {
        this.content = content;
        this.title = title;
        this.isSelect = isSelect;
    }

    public AAABaseItemBean(String content) {
        this.content = content;
    }

    public AAABaseItemBean(String title, int imgDrawable) {
        this.title = title;
        this.imgDrawable = imgDrawable;
    }

    public AAABaseItemBean(int type, String title, String content){
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public AAABaseItemBean() {
    }
}
