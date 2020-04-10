package com.yupont.tree.model;


/**
 * 树配置
 * @author fenghuaigang
 */
public class TreeConfig {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 风格(样式)
     */
    private String type;

    /**
     * 描述
     */
    private String descript;

    /**
     * 可用
     */
    private Boolean available;

    /**
     * 编辑
     */
    private String editor;

    /**
     * 模版
     */
    private String model;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取风格(样式)
     *
     * @return type - 风格(样式)
     */
    public String getType() {
        return type;
    }

    /**
     * 设置风格(样式)
     *
     * @param type 风格(样式)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取描述
     *
     * @return descript - 描述
     */
    public String getDescript() {
        return descript;
    }

    /**
     * 设置描述
     *
     * @param descript 描述
     */
    public void setDescript(String descript) {
        this.descript = descript;
    }

    /**
     * 获取可用
     *
     * @return available - 可用
     */
    public Boolean getAvailable() {
        return available;
    }

    /**
     * 设置可用
     *
     * @param available 可用
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * 获取编辑
     *
     * @return editor - 编辑
     */
    public String getEditor() {
        return editor;
    }

    /**
     * 设置编辑
     *
     * @param editor 编辑
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }

    /**
     * 获取模版
     *
     * @return model - 模版
     */
    public String getModel() {
        return model;
    }

    /**
     * 设置模版
     *
     * @param model 模版
     */
    public void setModel(String model) {
        this.model = model;
    }
}