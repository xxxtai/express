package com.xxxtai.express.model;

import java.sql.Timestamp;

public abstract class Base {
    private Long id;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private String features;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String feature) {
        this.features = feature;
    }
}
