package com.xxxtai.express.view;

import com.xxxtai.express.model.*;
import com.xxxtai.express.constant.Orientation;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DrawingGraph {
    public enum Style{EXPRESS, SIMULATOR}
    @Resource
    private Graph graph;

    public DrawingGraph() {}

    public void drawingMap(Graphics g, Style style, boolean showNums) {
        ((Graphics2D) g).setStroke(new BasicStroke(1.0f));
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
            g.fillRect(node.x - 2, node.y - 2, 4, 4);
            if (style == Style.EXPRESS) {
                g.setColor(Color.RED);
            } else if (style == Style.SIMULATOR) {
                g.setColor(Color.blue);
            }
        }

    }

    public void drawingAGV(Graphics g, List<Car> AGVArray, JPanel panel) {
        g.setFont(new Font("Dialog", Font.BOLD, 25));
        g.setColor(new Color(0, 139, 69));
        Random random = new Random();
        Object[] nodes = graph.getNodeMap().values().toArray();
        for (int i = 0; i < 300; i++) {
            int index = random.nextInt(nodes.length);
            g.fillRect(((Node)nodes[index]).x - 5, ((Node)nodes[index]).y - 5, 10, 10);
        }
    }
}
