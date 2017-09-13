package com.xxxtai.express.constant;

import lombok.Getter;

public enum Orientation {
    LEFT(1, "向左"),
    RIGHT(2, "向右"),
    UP(3, "向上"),
    DOWN(4, "向下");

    private @Getter
    Integer value;
    private @Getter
    String description;

    Orientation(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
