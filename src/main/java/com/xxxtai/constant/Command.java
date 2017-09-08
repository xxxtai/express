package com.xxxtai.constant;

import lombok.Getter;

public enum  Command {

    FORWARD("CC01DD", "前进"),
    STOP("CC02DD", "停止");

    private @Getter
    String command;

    private @Getter
    String description;

    Command(String command, String description){
        this.command = command;
        this.description = description;
    }
}
