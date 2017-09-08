package com.xxxtai.model;

import com.xxxtai.toolKit.NodeFunction;
import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;

@Data
public class Node {
    public final int x;
    public final int y;
    public final Integer cardNum;
    private final NodeFunction function;
    public final Queue<Car> waitQueue;
    private boolean locked;

    public Node(int card_num, int x, int y, NodeFunction function) {
        this.x = x;
        this.y = y;
        this.cardNum = card_num;
        this.function = function;
        this.waitQueue = new LinkedList<>();
    }

    public void setLocked() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public boolean isLocked() {
        return this.locked;
    }

}
