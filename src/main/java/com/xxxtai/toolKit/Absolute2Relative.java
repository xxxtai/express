package com.xxxtai.toolKit;

import com.xxxtai.model.Edge;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Node;
import com.xxxtai.model.Path;

import java.util.List;

public class Absolute2Relative {
    private Absolute2Relative() {
    }

    public static String convert(Graph graph, Path path) {
        List<Integer> route = path.getRoute();
        StringBuffer buffer = new StringBuffer();
        buffer.append("AA");

        for (int i = 0; i + 2 < route.size(); i++) {
            if (graph.getNodeMap().get(route.get(i)).x == graph.getNodeMap().get(route.get(i + 1)).x) {
                if (graph.getNodeMap().get(route.get(i)).y < graph.getNodeMap().get(route.get(i + 1)).y) {//down
                    //System.out.print("方向下/");
                    if (graph.getNodeMap().get(route.get(i + 2)).x > graph.getNodeMap().get(route.get(i + 1)).x) {
                        //左1
                        System.out.print(route.get(i + 1) + "的命令左");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 1));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 1));
                        buffer.append("FF");
                    } else if (graph.getNodeMap().get(route.get(i + 2)).x == graph.getNodeMap().get(route.get(i + 1)).x) {
                        //前3
                        System.out.print(route.get(i + 1) + "的命令前/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 3));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 3));
                        buffer.append("FF");
                    } else {
                        //右2
                        System.out.print(route.get(i + 1) + "的命令右/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 2));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 2));
                        buffer.append("FF");
                    }
                } else if (graph.getNodeMap().get(route.get(i)).y > graph.getNodeMap().get(route.get(i + 1)).y) {//up
                    //System.out.print("方向上/");
                    if (graph.getNodeMap().get(route.get(i + 2)).x > graph.getNodeMap().get(route.get(i + 1)).x) {
                        //右
                        System.out.print(route.get(i + 1) + "的命令右/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 2));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 2));
                        buffer.append("FF");
                    } else if (graph.getNodeMap().get(route.get(i + 2)).x == graph.getNodeMap().get(route.get(i + 1)).x) {
                        //前
                        System.out.print(route.get(i + 1) + "的命令前/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 3));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 3));
                        buffer.append("FF");
                    } else {
                        //左
                        System.out.print(route.get(i + 1) + "的命令左/");
                        //	System.out.println(commondString(graph, route.get(i), route.get(i+1), 1));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 1));
                        buffer.append("FF");
                    }
                }
            } else if (graph.getNodeMap().get(route.get(i)).y == graph.getNodeMap().get(route.get(i + 1)).y) {//right and left
                if (graph.getNodeMap().get(route.get(i)).x < graph.getNodeMap().get(route.get(i + 1)).x) {//right
                    //System.out.print("方向右/");
                    if (graph.getNodeMap().get(route.get(i + 2)).y > graph.getNodeMap().get(route.get(i + 1)).y) {
                        //右
                        System.out.print(route.get(i + 1) + "的命令右/");
                        //	System.out.println(commondString(graph, route.get(i), route.get(i+1), 2));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 2));
                        buffer.append("FF");
                    } else if (graph.getNodeMap().get(route.get(i + 2)).y == graph.getNodeMap().get(route.get(i + 1)).y) {
                        //前
                        System.out.print(route.get(i + 1) + "的命令前/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 3));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 3));
                        buffer.append("FF");
                    } else {
                        //左
                        System.out.print(route.get(i + 1) + "的命令左/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 1));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 1));
                        buffer.append("FF");
                    }
                } else if (graph.getNodeMap().get(route.get(i)).x > graph.getNodeMap().get(route.get(i + 1)).x) {//leftleftleftleftleftleft
                    //System.out.print("方向左/");
                    if (graph.getNodeMap().get(route.get(i + 2)).y > graph.getNodeMap().get(route.get(i + 1)).y) {
                        //左
                        System.out.print(route.get(i + 1) + "的命令左/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 1));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 1));
                        buffer.append("FF");
                    } else if (graph.getNodeMap().get(route.get(i + 2)).y == graph.getNodeMap().get(route.get(i + 1)).y) {
                        //前
                        System.out.print(route.get(i + 1) + "的命令前/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 3));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 3));
                        buffer.append("FF");
                    } else {
                        //右
                        System.out.print(route.get(i + 1) + "的命令右/");
                        //System.out.println(commondString(graph, route.get(i), route.get(i+1), 2));
                        buffer.append(commondString(graph, route.get(i), route.get(i + 1), 2));
                        buffer.append("FF");
                    }
                }
            }
        }


        for (Node node : graph.getNodeArray()) {
            if (node.cardNum == path.endNodeNum) {
                path.setStopNodeNum(node.cardNum);
            }
        }

        if (path.getStopNodeNum() < 16) {
            buffer.append("0");
            buffer.append(Integer.toHexString(path.getStopNodeNum()));
        } else {
            buffer.append(Integer.toHexString(path.getStopNodeNum()));
        }
        buffer.append("BB");

        return buffer.toString();
    }

    public static String commondString(Graph graph, int s, int e, int commond) {
        String reString = "";
        for (Edge edge : graph.getEdgeArray()) {
            if ((edge.startNode.cardNum == s && edge.endNode.cardNum == e) || (edge.endNode.cardNum == s && edge.startNode.cardNum == e)) {
                if (edge.cardNum < 16)
                    reString = String.valueOf(0) + Integer.toHexString(edge.cardNum);
                else
                    reString += Integer.toHexString(edge.cardNum);
            }
        }
        reString += String.valueOf(0);
        return reString += String.valueOf(commond);
    }
}
