package com.xxxtai.model;

import java.util.List;

/**
 * Created by Tai on 2017/5/18.
 */
public class Exit {
    public final int X;
    public final int Y;
    public final String NAME;

    public Exit(String name,List<Node> exitNode ) {
        this.NAME = name;
        int x = 0;
        int y = 0;
        for (int i = 0; i < 4; i++){
            x+=exitNode.get(i).X;
            y+=exitNode.get(i).Y;
        }
        this.X = x/4;
        this.Y = y/4;

    }

    public Exit(String name, int x, int y){
        this.NAME = name;
        this.X = x;
        this.Y = y;
    }
}
