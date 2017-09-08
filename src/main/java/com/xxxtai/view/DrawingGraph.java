package com.xxxtai.view;

import com.xxxtai.model.*;
import com.xxxtai.constant.Orientation;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DrawingGraph {
    @Resource
    private Graph graph;
    private Image leftImageG;

    private Image rightImageG;
    private Image upImageG;
    private Image downImageG;
    private Image leftImageR;
    private Image rightImageR;
    private Image upImageR;
    private Image downImageR;

    public DrawingGraph() {
        Toolkit tool = Toolkit.getDefaultToolkit();
        leftImageG = tool.createImage(getClass().getClassLoader().getResource("images/leftImage.png"));
        rightImageG = tool.createImage(getClass().getResource("/images/rightImage.png"));
        upImageG = tool.createImage(getClass().getResource("/images/upImage.png"));
        downImageG = tool.createImage(getClass().getResource("/images/downImage.png"));
        leftImageR = tool.createImage(getClass().getResource("/images/leftImage2.png"));
        rightImageR = tool.createImage(getClass().getResource("/images/rightImage2.png"));
        upImageR = tool.createImage(getClass().getResource("/images/upImage2.png"));
        downImageR = tool.createImage(getClass().getResource("/images/downImage2.png"));
    }

    void drawingMap(Graphics g) {
        ((Graphics2D) g).setStroke(new BasicStroke(6.0f));
        g.setColor(Color.BLACK);

        for (Edge edge : graph.getEdgeArray()) {
            if (edge.isLocked() || edge.isRemove())
                g.setColor(Color.lightGray);
            else
                g.setColor(Color.BLACK);
            g.drawLine(edge.startNode.x, edge.startNode.y, edge.endNode.x, edge.endNode.y);
        }

        for (Node node : graph.getNodeArray()) {
            if (node.isLocked())
                g.setColor(Color.red);
            else
                g.setColor(Color.YELLOW);
            g.fillRect(node.x - 5, node.y - 5, 10, 10);
            g.setColor(Color.RED);
            g.setFont(new Font("宋体", Font.BOLD, 15));
            g.drawString(String.valueOf(node.cardNum), node.x + 10, node.y - 10);
        }

        for (List<Exit> list : graph.getExitList()) {
            for (Exit exit : list) {
                g.setFont(new Font("宋体", Font.BOLD, 25));
                g.setColor(Color.darkGray);
                g.drawString(exit.name, exit.X - 20, exit.Y + 10);
            }
        }
    }

    void drawingAGV(Graphics g, ArrayList<Car> AGVArray, JPanel panel) {
        g.setFont(new Font("Dialog", Font.BOLD, 25));
        g.setColor(Color.black);
        for (int i = 0; i < AGVArray.size(); i++) {
            Image leftImage;
            Image rightImage;
            Image upImage;
            Image downImage;
            if (AGVArray.get(i).getCommunication() == null) {
                leftImage = leftImageR;
                rightImage = rightImageR;
                upImage = upImageR;
                downImage = downImageR;
            } else {
                leftImage = leftImageG;
                rightImage = rightImageG;
                upImage = upImageG;
                downImage = downImageG;
            }
            if (AGVArray.get(i).getOrientation() == Orientation.LEFT) {
                g.drawImage(leftImage, AGVArray.get(i).getX() - 20, AGVArray.get(i).getY() - 17, 40, 34, panel);
                g.drawString(String.valueOf(i + 1), AGVArray.get(i).getX(), AGVArray.get(i).getY() + 9);
            } else if (AGVArray.get(i).getOrientation() == Orientation.RIGHT) {
                g.drawImage(rightImage, AGVArray.get(i).getX() - 20, AGVArray.get(i).getY() - 17, 40, 34, panel);
                g.drawString(String.valueOf(i + 1), AGVArray.get(i).getX() - 10, AGVArray.get(i).getY() + 9);
            } else if (AGVArray.get(i).getOrientation() == Orientation.UP) {
                g.drawImage(upImage, AGVArray.get(i).getX() - 17, AGVArray.get(i).getY() - 20, 34, 40, panel);
                g.drawString(String.valueOf(i + 1), AGVArray.get(i).getX() - 10, AGVArray.get(i).getY() + 10);
            } else if (AGVArray.get(i).getOrientation() == Orientation.DOWN) {
                g.drawImage(downImage, AGVArray.get(i).getX() - 17, AGVArray.get(i).getY() - 20, 34, 40, panel);
                g.drawString(String.valueOf(i + 1), AGVArray.get(i).getX() - 5, AGVArray.get(i).getY() + 5);
            }
        }
    }

}
