package com.xxxtai.express.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Graph {
    Map<Integer, Node> getNodeMap();
    void setNodeMap(Map<Integer, Node> nodeMap);
    Map<Integer, Edge> getEdgeMap();
    void setEdgeMap(Map<Integer, Edge> edgeMap);
    Map<Long, List<Exit>> getExitMap();
    Map<Integer, Entrance> getEntranceMap();
    Map<String, Integer> getSerialNumMap();
    Map<Integer, String> getCardNumMap();
    Collection<Node> getNodeArray();
    Collection<Edge> getEdgeArray();
    Map<Integer, Integer> getAGVSPosition();
    int getRow();
    int getColumn();
}
