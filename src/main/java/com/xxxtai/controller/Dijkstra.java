package com.xxxtai.controller;

import com.xxxtai.model.Edge;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Path;
import com.xxxtai.constant.NodeFunction;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class Dijkstra implements Algorithm {
    public final static int MAXINT = 655535;
    @Resource
    private Graph graph;
    private int size;

    @PostConstruct
    public void init() {
        size = graph.getNodeArraySize();
    }

    public synchronized Path findRoute(Edge startEdge, final int endNodeNum, boolean isBackToEntrance) {
        Edge removeEdge = changeEdgeArray(endNodeNum, isBackToEntrance);

        boolean adjoin = false;
        List<Path> sArray = new ArrayList<>();
        sArray.add(new Path(startEdge.endNode.cardNum, startEdge.endNode.cardNum));
        List<Path> uArray = new ArrayList<>();
        for (int i = 0; i < size; i++) {//初始化
            if (startEdge.startNode.cardNum == i + 1) {
                uArray.add(new Path(startEdge.endNode.cardNum, i + 1));
                uArray.get(i).setRealDistance(MAXINT);
                continue;
            }

            uArray.add(new Path(startEdge.endNode.cardNum, i + 1));
            for (Edge edge : graph.getEdgeArray()) {
                if ((edge.startNode.cardNum == startEdge.endNode.cardNum && edge.endNode.cardNum == (i + 1))
                        || (edge.endNode.cardNum == startEdge.endNode.cardNum && edge.startNode.cardNum == (i + 1))) {//|| (graph.getEdge(j).endNode.cardNum == startNode && graph.getEdge(j).startNode.cardNum == (i+1) && graph.getEdge(j).twoWay)
                    if (!edge.isRemove()) {//当边和点被占用或被移除后，认为不联通!edge.isLocked() &&
                        uArray.get(i).setRealDistance(edge.realDistance);
                        uArray.get(i).addRouteNode(i + 1);
                        adjoin = true;
                    }
                }
            }
            if (!adjoin) {
                uArray.get(i).setRealDistance(MAXINT);
            }
            adjoin = false;
        }

        uArray.get(startEdge.endNode.cardNum - 1).setRemove();
        int removedCount = 1;

        while (uArray.size() != removedCount) {//
            int tempMin = MAXINT;
            int indexMin = 0;
            for (int i = 0; i < uArray.size(); i++) {//取u中权值最小的点放进s中
                if (!uArray.get(i).isRemove()) {
                    if (uArray.get(i).getRealDistance() < tempMin) {
                        tempMin = uArray.get(i).getRealDistance();
                        indexMin = i;
                    }
                }
            }

            sArray.add(uArray.get(indexMin));
            uArray.get(indexMin).setRemove();
            removedCount++;

            int tempStart = sArray.get(sArray.size() - 1).endNodeNum;
            for (int i = 0; i < size; i++) {
                for (Edge edge : graph.getEdgeArray()) {
                    if ((edge.startNode.cardNum == tempStart && edge.endNode.cardNum == (i + 1))
                            || (edge.endNode.cardNum == tempStart && edge.startNode.cardNum == (i + 1))) {//|| (graph.getEdge(j).endNode.cardNum == tempStart && graph.getEdge(j).startNode.cardNum == (i+1) && graph.getEdge(j).twoWay)
                        if (edge.realDistance + sArray.get(sArray.size() - 1).getRealDistance() < uArray.get(i).getRealDistance()
                                && !edge.isRemove()) {//当边和点被占用或被移除后，认为不联通&& !edge.isLocked()
                            uArray.get(i).setRealDistance(edge.realDistance + sArray.get(sArray.size() - 1).getRealDistance());
                            uArray.get(i).newRoute(sArray.get(sArray.size() - 1).getRoute());
                            uArray.get(i).addRouteNode(i + 1);
                        }
                    }
                }
            }
        }//end while
        Path returnPath = null;
        for (Path aSArray : sArray) {
            if (aSArray.endNodeNum == endNodeNum) {
                returnPath = aSArray;
                if (true) {//!startEdge.endNode.functionNode
                    ArrayList<Integer> tempArray = new ArrayList<>(returnPath.getRoute());
                    returnPath.getRoute().clear();
                    returnPath.getRoute().add(startEdge.startNode.cardNum);
                    for (Integer aTempArray : tempArray) {
                        returnPath.getRoute().add(aTempArray);
                    }

                }

            }
        }
        if (removeEdge != null)
            recoverEdgeArray(removeEdge);
        return returnPath;
    }//end countPath


    private Edge changeEdgeArray(int endNodeCARD_NUM, boolean isBackToEntrance) {
        Edge removeEdge = null;
        if (!NodeFunction.Junction.equals(graph.getNodeMap().get(endNodeCARD_NUM).getFunction())) {
            Edge edge = graph.getEdgeMap().get(graph.getNodeMap().get(endNodeCARD_NUM).cardNum);
            edge.setRemoved();
            removeEdge = edge;
            if (isBackToEntrance) {
                if (graph.getNodeMap().get(endNodeCARD_NUM).x < edge.startNode.x) {
                    graph.addEdge(edge.startNode.cardNum, endNodeCARD_NUM, edge.realDistance / 2, 0);
                } else {
                    graph.addEdge(endNodeCARD_NUM, edge.endNode.cardNum, edge.realDistance / 2, -1);
                }
            } else {
                graph.addEdge(edge.startNode.cardNum, endNodeCARD_NUM, edge.realDistance / 2, 0);
                graph.addEdge(endNodeCARD_NUM, edge.endNode.cardNum, edge.realDistance / 2, -1);
            }

        }
        return removeEdge;
    }

    private void recoverEdgeArray(Edge removeEdge) {
        removeEdge.cancelRemove();
        graph.getEdgeMap().remove(0);
        graph.getEdgeMap().remove(-1);
    }
}
