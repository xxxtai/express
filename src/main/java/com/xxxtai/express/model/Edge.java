package com.xxxtai.express.model;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class Edge {
    public final Node startNode;
    public final Node endNode;
    public final int realDistance;
    public final Integer cardNum;
    public final Queue<Car> waitQueue;
    public final Point CARD_POSITION;
    private boolean remove;
    private boolean locked;

    public Edge(Node startNode, Node endNode, Node midNode, int distance) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.realDistance = distance;
        this.cardNum = midNode.cardNum;
        this.waitQueue = new LinkedList<>();
        this.CARD_POSITION = new Point(midNode.x, midNode.y);
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
