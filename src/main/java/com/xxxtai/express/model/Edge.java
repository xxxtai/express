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

    public Edge(Node startNode, Node endNode, int distance, int cardNum) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.realDistance = distance;
        this.cardNum = cardNum;
        this.waitQueue = new LinkedList<>();
        if (this.startNode.x == this.endNode.x) {
            this.CARD_POSITION = new Point(this.startNode.x, (this.startNode.y + this.endNode.y) / 2);
        } else {
            this.CARD_POSITION = new Point((this.startNode.x + this.endNode.x) / 2, this.startNode.y);
        }
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
