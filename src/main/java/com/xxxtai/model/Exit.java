package com.xxxtai.model;

import java.util.List;

/**
 * Created by Tai on 2017/5/18.
 */
public class Exit {
    public final int X;
    public final int Y;
    public final String name;

    public Exit(String name, List<Node> exitNode) {
        this.name = name;
        int x = 0;
        int y = 0;
        for (int i = 0; i < 4; i++) {
            x += exitNode.get(i).x;
            y += exitNode.get(i).y;
        }
        this.X = x / 4;
        this.Y = y / 4;

    }

    Exit(String name, int x, int y) {
        this.name = name;
        this.X = x;
        this.Y = y;
    }
}
