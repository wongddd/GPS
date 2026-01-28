package com.yyt.trackcar.ui.holder;

public class IconTreeItem {

    /**
     * 唯一ID
     */
    private long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 当前是否为折叠状态
     */
    private boolean isCollapsed;

    /**
     * depth 深度 default:0
     */
    private int depth = 0;

    /**
     *  该节点类型 1:经销商 2:设备
     */
    private int nodeType;

    public long getId() {
        return id;
    }

    private Object extraData;

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
    }

    public IconTreeItem Title(String title) {
        this.title = title;
        return this;
    }

    public IconTreeItem Depth(int depth){
        this.depth = depth;
        return this;
    }

    public IconTreeItem NodeType(int nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public IconTreeItem ID(long id) {
        this.id = id;
        return this;
    }

    public IconTreeItem ExtraData (Object extraData) {
        this.extraData = extraData;
        return this;
    }
}