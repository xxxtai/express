package com.xxxtai.constant;

import lombok.Getter;

public enum  Command {

    FORWARD(Constant.PREFIX + Integer.toHexString(1) + Constant.COMMAND_SUFFIX, "前进"),
    STOP(Constant.PREFIX + Integer.toHexString(2) + Constant.COMMAND_SUFFIX, "停止");

    private @Getter
    String command;

    private @Getter
    String description;

    Command(String command, String description){
        this.command = command;
        this.description = description;
    }
}
