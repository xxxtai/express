package com.xxxtai.express.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Graph {
    Map<Integer, Node> getNodeMap();
    Map<Integer, Edge> getEdgeMap();
    Map<Long, List<Exit>> getExitMap();
    Map<Integer, Entrance> getEntranceMap();
    Map<String, Integer> getSerialNumMap();
    Map<Integer, String> getCardNumMap();
    Collection<Node> getNodeArray();
    Collection<Edge> getEdgeArray();
    int getRow();
    int getColumn();
}
