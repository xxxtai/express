package com.xxxtai.express.controller;

import com.google.common.collect.Lists;
import com.xxxtai.express.model.ComGraph;
import com.xxxtai.express.model.Edge;
import com.xxxtai.express.model.Graph;
import com.xxxtai.express.model.Path;
import com.xxxtai.express.toolKit.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j(topic = "develop")
public class AStar implements Algorithm {
    @Resource
    private Graph graph;

    public synchronized Path findRoute(Edge startEdge, Edge endEdge, boolean ignoredLocked, boolean resolveDeadlock) {

        Path forwardPath = find(startEdge, endEdge, ignoredLocked);

        Path backwardPath = null;
        if (resolveDeadlock) {
            Edge oppositeStartEdge = new Edge(startEdge.endNode, startEdge.startNode, graph.getNodeMap().get(startEdge.cardNum), startEdge.realDistance);
            backwardPath = find(oppositeStartEdge, endEdge, ignoredLocked);
        }

        if (forwardPath != null && backwardPath != null) {
            if (forwardPath.getCost() <= backwardPath.getCost()) {
                return forwardPath;
            } else {
                backwardPath.setBackwards(true);
                return backwardPath;
            }
        } else if (forwardPath != null) {
            return forwardPath;
        } else if (backwardPath != null) {
            backwardPath.setBackwards(true);
            return backwardPath;
        }
        return null;
    }

    private Path find(Edge startEdge, Edge endEdge, boolean ignoredLocked) {
        Map<Integer, AStarNode> openMap = new HashMap<>();
        Map<Integer, AStarNode> closeMap = new HashMap<>();

        AStarNode startAStarNode = new AStarNode(startEdge.startNode.cardNum, null);
        startAStarNode.setValueG(0);
        startAStarNode.setValueH(0);
        closeMap.put(startAStarNode.nodeNum, startAStarNode);

        AStarNode endAStarNode = new AStarNode(startEdge.endNode.cardNum, startAStarNode);
        endAStarNode.setValueG(ComGraph.EDGE_COST);
        endAStarNode.setValueH(calculateValueH(startEdge.endNode.cardNum, endEdge));
        closeMap.put(endAStarNode.getNodeNum(), endAStarNode);

        int lateCloseAStarNodeNum = endAStarNode.getNodeNum();
        while (!closeMap.containsKey(endEdge.startNode.cardNum) && !closeMap.containsKey(endEdge.endNode.cardNum)){
            final AStarNode curAStarNode = closeMap.get(lateCloseAStarNodeNum);
            final int[] curNodePosition = Common.calculateNodePosition(curAStarNode.getNodeNum(), graph);

            if (curNodePosition[0] + 1 < graph.getRow()) {
                int[] nextPosition = new int[]{curNodePosition[0] + 1, curNodePosition[1]};
                addAndUpdateOpenMap(openMap, closeMap, nextPosition, curNodePosition,curAStarNode, endEdge, ignoredLocked);
            }
            if (curNodePosition[0] - 1 >= 0) {
                int[] nextPosition = new int[]{curNodePosition[0] - 1, curNodePosition[1]};
                addAndUpdateOpenMap(openMap, closeMap, nextPosition, curNodePosition,curAStarNode, endEdge, ignoredLocked);
            }
            if (curNodePosition[1] + 1 < graph.getColumn()) {
                int[] nextPosition = new int[]{curNodePosition[0], curNodePosition[1] + 1};
                addAndUpdateOpenMap(openMap, closeMap, nextPosition, curNodePosition,curAStarNode, endEdge, ignoredLocked);
            }
            if (curNodePosition[1] - 1 >= 0) {
                int[] nextPosition = new int[]{curNodePosition[0], curNodePosition[1] - 1};
                addAndUpdateOpenMap(openMap, closeMap, nextPosition, curNodePosition, curAStarNode, endEdge, ignoredLocked);
            }

            List<AStarNode> openList = Lists.newArrayList(openMap.values());
            openList.sort(((o1, o2) -> o2.getValueF() - o1.getValueF()));
            if (openList.size() == 0) {
                return null;
            }
            AStarNode lateCloseAStarNode = openList.get(openList.size() - 1);
            lateCloseAStarNodeNum = lateCloseAStarNode.getNodeNum();
            closeMap.put(lateCloseAStarNodeNum, lateCloseAStarNode);
            openMap.remove(lateCloseAStarNodeNum);
        }

        if (ignoredLocked && closeMap.size() == 2 && graph.getEdgeMap().get(endEdge.cardNum).isLocked()) {
            return null;
        }

        Path path = new Path();
        path.setStopNodeNum(endEdge.cardNum);
        int endEdgeStartNode = 0;
        int endEdgeEndNode = 0;
        if (closeMap.containsKey(endEdge.startNode.cardNum)) {
            endEdgeStartNode = endEdge.startNode.cardNum;
            endEdgeEndNode = endEdge.endNode.cardNum;
        } else if (closeMap.containsKey(endEdge.endNode.cardNum)){
            endEdgeStartNode = endEdge.endNode.cardNum;
            endEdgeEndNode = endEdge.startNode.cardNum;
        }
        path.setEndNodeNum(endEdge.cardNum);
        AStarNode pStarNode = closeMap.get(endEdgeStartNode);
        if (pStarNode.getParentAStarNode() == null) {
            return null;
        }
        int[] curPosition = Common.calculateNodePosition(pStarNode.getNodeNum(), graph);
        int[] prePosition = Common.calculateNodePosition(pStarNode.getParentAStarNode().getNodeNum(), graph);
        int[] endPosition = Common.calculateNodePosition(endEdgeEndNode, graph);
        if (isWalkStraight(prePosition, curPosition, endPosition)) {
            path.setCost(pStarNode.valueG + ComGraph.EDGE_COST/2);
        } else {
            path.setCost(pStarNode.valueG + ComGraph.EDGE_COST/2 + ComGraph.SWERVE_COST);
        }

        List<Integer> route = Lists.newArrayList();
        route.add(endEdge.cardNum);
        while (pStarNode != null) {
            route.add(pStarNode.nodeNum);
            pStarNode = pStarNode.parentAStarNode;
        }
        for (int i = route.size() - 1; i >= 0; i--) {
            path.addRouteNode(route.get(i));
        }

        return path;
    }

    private void addAndUpdateOpenMap(Map<Integer, AStarNode> openMap, Map<Integer, AStarNode> closeMap, int[] nextPosition,
                                     int[] curPosition, AStarNode curAStarNode, Edge endEdge, boolean ignoredLocked){
        int nodeNum = Common.calculateNodeNum(nextPosition, graph);
        if (ignoredLocked) {
            Edge edge = Common.calculateEdge(nodeNum, curAStarNode.nodeNum, graph);
            if (edge.isLocked() || graph.getEntranceMap().containsKey(edge.cardNum)) {
                return;
            }
        }

        if (!openMap.containsKey(nodeNum) && !closeMap.containsKey(nodeNum)) {
            AStarNode starNode = new AStarNode(nodeNum, curAStarNode);
            int[] parentPosition = Common.calculateNodePosition(curAStarNode.getParentAStarNode().getNodeNum(), graph);
            if (isWalkStraight(parentPosition, curPosition, nextPosition)) {
                starNode.setValueG(curAStarNode.getValueG() + ComGraph.EDGE_COST);
            } else {
                starNode.setValueG(curAStarNode.getValueG() + ComGraph.EDGE_COST + ComGraph.SWERVE_COST);
            }
            starNode.setValueH(calculateValueH(nodeNum, endEdge));
            openMap.put(nodeNum, starNode);
        } else if (openMap.containsKey(nodeNum)) {
            int swerveCost = 0;
            int[] parentPosition = Common.calculateNodePosition(curAStarNode.getParentAStarNode().getNodeNum(), graph);
            if (!isWalkStraight(parentPosition, curPosition, nextPosition)) {
                swerveCost = ComGraph.SWERVE_COST;
            }
            AStarNode existAStarNode = openMap.get(nodeNum);
            if ((curAStarNode.getValueG() + ComGraph.EDGE_COST + swerveCost) < existAStarNode.getValueG()) {
                existAStarNode.setParentAStarNode(curAStarNode);
                existAStarNode.setValueG(curAStarNode.getValueG() + ComGraph.EDGE_COST + swerveCost);
            }
        }
    }

    private int calculateValueH(int nodeNum, Edge endEdge){
        int[] nodePosition = Common.calculateNodePosition(nodeNum, graph);
        int[] endEdgeStartPosition = Common.calculateNodePosition(endEdge.startNode.cardNum, graph);
        int[] endEdgeEndPosition = Common.calculateNodePosition(endEdge.endNode.cardNum, graph);
        if (endEdgeStartPosition[0] == endEdgeEndPosition[0]) {
            return ComGraph.EDGE_COST * Math.abs(nodePosition[0] - endEdgeStartPosition[0])
                    + Math.abs(ComGraph.EDGE_COST * nodePosition[1] - (ComGraph.EDGE_COST * endEdgeStartPosition[1] + (ComGraph.EDGE_COST / 2) * (endEdgeEndPosition[1] - endEdgeStartPosition[1])));
        } else if (endEdgeStartPosition[1] == endEdgeEndPosition[1]) {
            return ComGraph.EDGE_COST * Math.abs(nodePosition[1] - endEdgeStartPosition[1])
                    + Math.abs(ComGraph.EDGE_COST *nodePosition[0] - (ComGraph.EDGE_COST *endEdgeStartPosition[0] + (ComGraph.EDGE_COST / 2) * (endEdgeEndPosition[0] - endEdgeStartPosition[0])));
        }
        return 0;
    }

    private boolean isWalkStraight(int[] parentPosition, int[] curPosition, int[] nextPosition) {
        return (parentPosition[0] == curPosition[0] ? 0 : 1) == (curPosition[0] == nextPosition[0] ? 0 : 1)
                && (parentPosition[1] == curPosition[1] ? 0 : 1) == (curPosition[1] == nextPosition[1] ? 0 : 1);
    }

    private class AStarNode {
        private int nodeNum;
        private int valueH;
        private int valueG;
        private AStarNode parentAStarNode;
        AStarNode(int nodeNum, AStarNode parentAStarNode){
            this.nodeNum = nodeNum;
            this.parentAStarNode = parentAStarNode;
        }

        int getNodeNum() {
            return nodeNum;
        }

        void setValueH(int valueH) {
            this.valueH = valueH;
        }

        int getValueG() {
            return valueG;
        }

        void setValueG(int valueG) {
            this.valueG = valueG;
        }

        int getValueF(){
            return this.valueG + this.valueH;
        }

        AStarNode getParentAStarNode() {
            return this.parentAStarNode;
        }

        void setParentAStarNode(AStarNode parentAStarNode) {
            this.parentAStarNode = parentAStarNode;
        }
    }
}
