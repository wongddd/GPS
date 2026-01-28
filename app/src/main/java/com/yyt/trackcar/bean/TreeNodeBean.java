package com.yyt.trackcar.bean;

/**
 * TreeNode Bean
 */
public class TreeNodeBean {
    /**
     * 显示折叠箭头
     */
    private Boolean needCollapseArrow;
    /**
     * 是否折叠列表
     */
    private Boolean isCollapse;
    /**
     * 是否需要选择框
     */
    private Boolean needCheckBox;
    /**
     * 是否已选中
     */
    private Boolean checked;
    /**
     * 树形结构item的内容
     */
    private String content;
    /**
     * 显示编辑按钮
     */
    private Boolean editBtn;
    /**
     * 显示删除按钮
     */
    private Boolean delBtn;
    /**
     * 显示解绑设备按钮
     */
    private Boolean unbindBtn;
    /**
     * 显示解绑帐号下所有设备
     */
    private Boolean unbindAllBtn;

    public Boolean getNeedCollapseArrow() {
        return needCollapseArrow;
    }

    public void setNeedCollapseArrow(Boolean needCollapseArrow) {
        this.needCollapseArrow = needCollapseArrow;
    }

    public Boolean getCollapse() {
        return isCollapse;
    }

    public void setCollapse(Boolean collapse) {
        isCollapse = collapse;
    }

    public Boolean getNeedCheckBox() {
        return needCheckBox;
    }

    public void setNeedCheckBox(Boolean needCheckBox) {
        this.needCheckBox = needCheckBox;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getEditBtn() {
        return editBtn;
    }

    public void setEditBtn(Boolean editBtn) {
        this.editBtn = editBtn;
    }

    public Boolean getDelBtn() {
        return delBtn;
    }

    public void setDelBtn(Boolean delBtn) {
        this.delBtn = delBtn;
    }

    public Boolean getUnbindBtn() {
        return unbindBtn;
    }

    public void setUnbindBtn(Boolean unbindBtn) {
        this.unbindBtn = unbindBtn;
    }

    public Boolean getUnbindAllBtn() {
        return unbindAllBtn;
    }

    public void setUnbindAllBtn(Boolean unbindAllBtn) {
        this.unbindAllBtn = unbindAllBtn;
    }
}
