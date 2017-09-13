package com.xxxtai.express.model;

import lombok.Getter;

import java.util.List;

/**
 * Created by Tai on 2017/5/18.
 */
public class Exit {
    public final int x;
    public final int y;
    public final String name;
    public final Long code;
    private @Getter
    int[] exitNodeNums;

    public Exit(String name, Long code, List<Node> exitNode) {
        this.name = name;
        this.code = code;
        exitNodeNums = new int[4];
        int x = 0;
        int y = 0;
        for (int i = 0; i < 4; i++) {
            exitNodeNums[i] = exitNode.get(i).getCardNum();
            x += exitNode.get(i).x;
            y += exitNode.get(i).y;

        }
        this.x = x / 4;
        this.y = y / 4;
    }

    Exit(String name, Long code, int x, int y, int[] exitNodeNums) {
        this.name = name;
        this.code = code;
        this.x = x;
        this.y = y;
        this.exitNodeNums = exitNodeNums;
    }
}
