package com.xxxtai.express.constant;

import lombok.Getter;

public enum NodeFunction {
    Junction(1, "交叉路口"),
    Parking(2, "停车点"),
    NULL(0, "空");

    private @Getter
    Integer value;
    private @Getter
    String description;

    NodeFunction(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public static NodeFunction valueOf(Integer num) {
        if (num == null) {
            return null;
        }
        if (num.equals(Junction.value)) {
            return Junction;
        } else if (num.equals(Parking.value)) {
            return Parking;
        }
        return null;
    }
}
