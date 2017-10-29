package com.xxxtai.express.toolKit;

import com.xxxtai.express.model.Edge;
import com.xxxtai.express.model.Graph;

import javax.swing.*;

public class Common {

    public static void changePanel(JFrame main, JPanel panel) {
        main.getContentPane().removeAll();
        main.getContentPane().add(panel);
        main.repaint();
        main.validate();
    }

    public static void delay(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String toHexString(int num){
        String temp = Integer.toHexString(num);
        if (temp.length() % 2 != 0) {
            return "0" + temp;
        }
        return temp;
    }

    public static Edge calculateEdge(int startNodeNum, int endNodeNum, Graph graph){
        int[] startPosition  = calculateNodePosition(startNodeNum, graph);
        int[] endPosition = calculateNodePosition(endNodeNum, graph);
        int index = 0;
        if (startPosition[0] == endPosition[0]) {
            index = graph.getColumn()*graph.getRow() + startPosition[0]*(graph.getColumn() - 1) + (startPosition[1] > endPosition[1] ? startPosition[1] : endPosition[1]);
        } else if (startPosition[1] == endPosition[1]) {
            index = (2*graph.getColumn() - 1)*graph.getRow() + startPosition[1]*(graph.getRow() - 1) + (startPosition[0] > endPosition[0] ? startPosition[0] : endPosition[0]);
        }
        return graph.getEdgeMap().get(index);
    }

    public static int[] calculateNodePosition(int nodeNum, Graph graph){
        int row = nodeNum/graph.getColumn();
        int column = nodeNum%graph.getColumn();
        if (column == 0) {
            column = graph.getColumn();
            row = row -1;
        }
        return new int[]{row, column - 1};
    }
}
