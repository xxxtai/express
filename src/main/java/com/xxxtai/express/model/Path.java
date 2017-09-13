package com.xxxtai.express.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Path {
    public final int endNodeNum;
    @Getter
    @Setter
    private int stopNodeNum;
    @Getter
    @Setter
    private int realDistance;
    @Getter
    private boolean remove;
    @Getter
    @Setter
    private List<Integer> route;

    public Path(int startNode, int endNode) {
        this.endNodeNum = endNode;
        route = Lists.newArrayList();
        route.add(startNode);
    }

    public void addRouteNode(int node) {
        route.add(node);
    }

    public void newRoute(List<Integer> route) {
        this.route = Lists.newArrayList(route);
    }

    public void setRemove() {
        remove = true;
    }
}
