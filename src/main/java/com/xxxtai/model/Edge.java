package com.xxxtai.model;

import java.util.LinkedList;
import java.util.Queue;

public class Edge {
    public final Node startNode;
    public final Node endNode;
    public final int realDistance;
    public final Integer cardNum;
    public final Queue<Car> waitQueue;
    private boolean remove;
    private boolean locked;

    public Edge(Node startNode, Node endNode, int distance, int cardNum) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.realDistance = distance;
        this.cardNum = cardNum;
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
