package com.xxxtai.model;

import lombok.Getter;

import java.util.List;

/**
 * Created by Tai on 2017/5/18.
 */
public class Exit {
    public final int x;
    public final int y;
    public final String name;
    private @Getter
    List<Node> exitNode;

    public Exit(String name, List<Node> exitNode) {
        this.name = name;
        this.exitNode = exitNode;
        int x = 0;
        int y = 0;
        for (int i = 0; i < 4; i++) {
            x += exitNode.get(i).x;
            y += exitNode.get(i).y;
        }
        this.x = x / 4;
        this.y = y / 4;

    }

    Exit(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}
