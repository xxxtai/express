package com.xxxtai.express.constant;

import lombok.Getter;

public enum State {
    FORWARD(1, "前进"),
    STOP(2, "停止"),
    BACKWARD(3, "后退"),
    UNLOADED(4, "卸货完成"),
    COLLIED(5, "相撞"),
    INFRARED_ANOMALY(6, "红外异常");

    private @Getter
    Integer value;
    private @Getter
    String description;

    State(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
