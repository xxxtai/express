package com.xxxtai.express.constant;

import lombok.Getter;

public enum State {
    FORWARD(1, "前进"),
    STOP(2, "停止"),
    BACKWARD(3, "后退"),
    UNLOADED(4, "卸货完成");

    private @Getter
    Integer value;
    private @Getter
    String description;

    State(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
