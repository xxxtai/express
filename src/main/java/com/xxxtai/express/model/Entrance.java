package com.xxxtai.express.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;

@Data
public class Entrance {
    public enum Direction{RIGHT, LEFT, NULL}
    private Queue<Car> queue = new LinkedList<>();
    private Integer cardNum;
    private Direction direction;
    public Entrance(Integer cardNum, Direction direction) {
        this.cardNum = cardNum;
        this.direction = direction;
    }
}
