package com.xxxtai.express.toolKit;

import com.xxxtai.express.constant.Command;
import com.xxxtai.express.constant.Constant;
import com.xxxtai.express.model.*;

import java.util.List;

public class Absolute2Relative {
    private Absolute2Relative() {}

    public static String convert(Graph graph, Path path) {
        List<Integer> route = path.getRoute();
        StringBuilder buffer = new StringBuilder();
        buffer.append(Constant.ROUTE_PREFIX);

        for (int i = 0; i + 2 < route.size(); i++) {
            if (graph.getNodeMap().get(route.get(i)).x == graph.getNodeMap().get(route.get(i + 1)).x) {
                if (graph.getNodeMap().get(route.get(i)).y < graph.getNodeMap().get(route.get(i + 1)).y) {//down
                    if (graph.getNodeMap().get(route.get(i + 2)).x > graph.getNodeMap().get(route.get(i + 1)).x) {
                        //左1
                        System.out.print(route.get(i + 1) + "的命令左");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.TURN_LEFT.getValue()));
                    } else if (graph.getNodeMap().get(route.get(i + 2)).x == graph.getNodeMap().get(route.get(i + 1)).x) {
                        //前3
                        System.out.print(route.get(i + 1) + "的命令前/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.GO_AHEAD.getValue()));
                    } else {
                        //右2
                        System.out.print(route.get(i + 1) + "的命令右/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.TURN_RIGHT.getValue()));
                    }
                } else if (graph.getNodeMap().get(route.get(i)).y > graph.getNodeMap().get(route.get(i + 1)).y) {//up
                    if (graph.getNodeMap().get(route.get(i + 2)).x > graph.getNodeMap().get(route.get(i + 1)).x) {
                        //右
                        System.out.print(route.get(i + 1) + "的命令右/");
                        //System.out.println(commandString(graph, route.get(i), route.get(i+1), 2));
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.TURN_RIGHT.getValue()));
                    } else if (graph.getNodeMap().get(route.get(i + 2)).x == graph.getNodeMap().get(route.get(i + 1)).x) {
                        //前
                        System.out.print(route.get(i + 1) + "的命令前/");
                        //System.out.println(commandString(graph, route.get(i), route.get(i+1), 3));
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.GO_AHEAD.getValue()));
                    } else {
                        //左
                        System.out.print(route.get(i + 1) + "的命令左/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.TURN_LEFT.getValue()));
                    }
                }
            } else if (graph.getNodeMap().get(route.get(i)).y == graph.getNodeMap().get(route.get(i + 1)).y) {//right and left
                if (graph.getNodeMap().get(route.get(i)).x < graph.getNodeMap().get(route.get(i + 1)).x) {//right
                    if (graph.getNodeMap().get(route.get(i + 2)).y > graph.getNodeMap().get(route.get(i + 1)).y) {
                        //右
                        System.out.print(route.get(i + 1) + "的命令右/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.TURN_RIGHT.getValue()));
                    } else if (graph.getNodeMap().get(route.get(i + 2)).y == graph.getNodeMap().get(route.get(i + 1)).y) {
                        //前
                        System.out.print(route.get(i + 1) + "的命令前/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.GO_AHEAD.getValue()));
                    } else {
                        //左
                        System.out.print(route.get(i + 1) + "的命令左/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.TURN_LEFT.getValue()));
                    }
                } else if (graph.getNodeMap().get(route.get(i)).x > graph.getNodeMap().get(route.get(i + 1)).x) {
                    if (graph.getNodeMap().get(route.get(i + 2)).y > graph.getNodeMap().get(route.get(i + 1)).y) {
                        //左
                        System.out.print(route.get(i + 1) + "的命令左/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.TURN_LEFT.getValue()));
                    } else if (graph.getNodeMap().get(route.get(i + 2)).y == graph.getNodeMap().get(route.get(i + 1)).y) {
                        //前
                        System.out.print(route.get(i + 1) + "的命令前/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.GO_AHEAD.getValue()));
                    } else {
                        //右
                        System.out.print(route.get(i + 1) + "的命令右/");
                        buffer.append(commandString(graph, route.get(i), route.get(i + 1), Command.TURN_RIGHT.getValue()));
                    }
                }
            }
        }

        if (Constant.USE_SERIAL) {
            buffer.append(graph.getCardNumMap().get(path.getEndNodeNum())).append(Constant.SUB_SPLIT);
        } else {
            buffer.append(path.getEndNodeNum()).append(Constant.SUB_SPLIT);
        }

        if (path.isBackwards()) {
            buffer.append(Constant.BACKWARD);
        } else {
            buffer.append(Constant.FORWARD);
        }
        buffer.append(Constant.SUFFIX);

        return buffer.toString();
    }

    private static String commandString(Graph graph, int startNode, int endNode, int command) {
        StringBuilder reString = new StringBuilder();
        for (Edge edge : graph.getEdgeArray()) {
            if ((edge.startNode.cardNum == startNode && edge.endNode.cardNum == endNode) || (edge.endNode.cardNum == startNode && edge.startNode.cardNum == endNode)) {
                if (Constant.USE_SERIAL) {
                    reString.append(graph.getCardNumMap().get(edge.cardNum));
                } else {
                    reString.append(edge.cardNum);
                }
            }
        }
        reString.append(Constant.SUB_SPLIT).append(Common.toHexString(command)).append(Constant.SPLIT);
        return reString.toString();
    }
}
