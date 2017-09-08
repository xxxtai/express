package com.xxxtai.model;

import java.util.LinkedList;
import java.util.Queue;

public class Edge {
    public final Node START_NODE;
    public final Node END_NODE;
    public final int REAL_DISTANCE;
    public final Integer CARD_NUM;
    public final Queue<Car> waitQueue;
    private boolean remove;
    private boolean locked;

    public Edge(Node startNode, Node endNode, int distance, int cardNum) {
        this.START_NODE = startNode;
        this.END_NODE = endNode;
        this.REAL_DISTANCE = distance;
        this.CARD_NUM = cardNum;
        this.waitQueue = new LinkedList<>();
    }

    public boolean isRemove() {
        return this.remove;
    }

    public void setRemoved() {
        this.remove = true;
    }

    public void cancelRemove() {
        this.remove = false;
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
