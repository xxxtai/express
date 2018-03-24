package com.xxxtai.express.view;

import com.xxxtai.express.model.*;
import com.xxxtai.express.constant.Orientation;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DrawingGraph {
    public enum Style{EXPRESS, SIMULATOR}
    @Resource
    private Graph graph;

    public DrawingGraph() {}

    public void drawingMap(Graphics g, Style style, boolean showNums) {
        if (style == Style.EXPRESS) {
            Stroke dash = new BasicStroke(2.5f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 3.5f, new float[] { 15, 10, },
                    0f);
            ((Graphics2D) g).setStroke(dash);
        } else {
            ((Graphics2D) g).setStroke(new BasicStroke(2.0f));
        }
        g.setColor(Color.BLACK);

        for (Edge edge : graph.getEdgeArray()) {
            if (edge.isLocked() || edge.isRemove()) {
                g.setColor(Color.lightGray);
            } else {
                g.setColor(Color.BLACK);
            }
            g.drawLine(edge.startNode.x, edge.startNode.y, edge.endNode.x, edge.endNode.y);
        }

        for (Node node : graph.getNodeArray()) {
            if (node.isLocked()) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.orange);
            }
            g.fillRect(node.x - 3, node.y - 3, 6, 6);
            if (style == Style.EXPRESS) {
                g.setColor(Color.RED);
            } else if (style == Style.SIMULATOR) {
                g.setColor(Color.blue);
            }
            g.setFont(new Font("宋体", Font.BOLD, 15));
            if (showNums) {
                g.drawString(String.valueOf(node.cardNum), node.x + 10, node.y - 10);
            }
        }

        for (List<Exit> list : graph.getExitMap().values()) {
            for (Exit exit : list) {
                g.setFont(new Font("宋体", Font.BOLD, 25));
                g.setColor(Color.darkGray);
                g.drawString(exit.name, exit.x - 20, exit.y + 10);
            }
        }

        for (Entrance entrance : graph.getEntranceMap().values()) {
            Node node = graph.getNodeMap().get(entrance.getCardNum());
            if (node == null) {
                continue;
            }
            g.setColor(Color.GRAY);
            g.fillRect(node.x - 20, node.y - 20, 40, 40);
        }

        for (Entrance entrance : graph.getEntranceMap().values()) {
            Node node = graph.getNodeMap().get(entrance.getCardNum());
            g.drawString(String.valueOf(entrance.getMissionCount()), node.x, node.y - 20);
        }
    }

    public void drawingAGV(Graphics g, List<Car> AGVArray, JPanel panel) {
        g.setFont(new Font("Dialog", Font.BOLD, 25));
        for (Car car : AGVArray) {
            if (car.getSocketChannel() == null) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GREEN);
            }

            if (car.getOrientation() == Orientation.LEFT) {
                draw(g, car);
            } else if (car.getOrientation() == Orientation.RIGHT) {
                draw(g, car);
            } else if (car.getOrientation() == Orientation.UP) {
                draw(g, car);
            } else if (car.getOrientation() == Orientation.DOWN) {
                draw(g, car);
            }
        }
    }

    private void draw(Graphics g, Car car){
        g.fillRect(car.getX() - 20, car.getY() - 20, 40, 40);
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(car.getAGVNum()), car.getX() , car.getY());
//        if (car.isOnDuty()) {
//            String destination =  car.getDestination();
//            if (destination != null) {
//                g.setColor(Color.BLACK);
//                g.drawString(destination, car.getX() , car.getY());
//                g.setColor(Color.white);
//            }
//        }
    }
}
