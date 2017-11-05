package com.xxxtai.express.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Path {
    private int endNodeNum;
    private int stopNodeNum;
    private int cost;
    private boolean remove;
    private List<Integer> route;
    private boolean backwards;

    public Path(int startNode, int endNode) {
        this.endNodeNum = endNode;
        route = Lists.newArrayList();
        route.add(startNode);
    }
    public Path(){
        route = Lists.newArrayList();
    }

    public void addRouteNode(int node) {
        route.add(node);
    }

    public void newRoute(List<Integer> route) {
        this.route = Lists.newArrayList(route);
    }

    public List<Integer> getRoute() {
        return this.route;
    }

    public void setRemove() {
        remove = true;
    }

    public int getStopNodeNum() {
        return stopNodeNum;
    }

    public void setStopNodeNum(int stopNodeNum) {
        this.stopNodeNum = stopNodeNum;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public boolean isBackwards() {
        return backwards;
    }

    public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }

    public void setEndNodeNum(int endNodeNum){
        this.endNodeNum = endNodeNum;
    }

    public int getEndNodeNum(){
        return this.endNodeNum;
    }
}
