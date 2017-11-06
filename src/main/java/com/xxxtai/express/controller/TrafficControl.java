package com.xxxtai.express.controller;

import com.xxxtai.express.model.Car;
import com.xxxtai.express.model.Node;
import com.xxxtai.express.model.Edge;

import java.util.List;

public interface TrafficControl {
    void setRouteNodeList(List<Integer> routeNodeList);

    List<Integer> getRouteNodeList();

    void isStopToWait(int cardNum, boolean f);

    Node getLockedNode();

    Edge getLockedEdge();

    void setCar(Car car);
}
