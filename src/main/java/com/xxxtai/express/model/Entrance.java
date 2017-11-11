package com.xxxtai.express.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;

public class Entrance {
    public enum Direction{RIGHT, LEFT, NULL}
    private int missionCount;
    private Integer cardNum;
    private Direction direction;
    public Entrance(Integer cardNum, Direction direction) {
        this.cardNum = cardNum;
        this.direction = direction;
    }

    public int getMissionCount() {
        return missionCount;
    }

    public void missionCountIncrease() {
        this.missionCount++;
    }

    public Integer getCardNum() {
        return cardNum;
    }

    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
