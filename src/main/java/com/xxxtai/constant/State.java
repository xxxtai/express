package com.xxxtai.constant;

import lombok.Getter;

public enum State {
    STOP(1, "停止"),
    FORWARD(2, "前进"),
    BACKWARD(3, "后退"),
    SHIPMENT(4, "装货"),
    UNLOADING(5, "卸货");

    private @Getter
    Integer value;
    private @Getter
    String description;

    State(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
