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
    public enum Style{EXPRESS, SIMULATOR};
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

    public void drawingMap(Graphics g, Style style, boolean showNums) {
        ((Graphics2D) g).setStroke(new BasicStroke(6.0f));
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
                g.setColor(Color.YELLOW);
            }
            g.fillRect(node.x - 5, node.y - 5, 10, 10);
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
//            g.drawString(String.valueOf(entrance.getMissionCount()), node.x, node.y - 20);
        }
    }

    public void drawingAGV(Graphics g, List<Car> AGVArray, JPanel panel) {
        g.setFont(new Font("Dialog", Font.BOLD, 25));
        for (Car car : AGVArray) {
            Image leftImage;
            Image rightImage;
            Image upImage;
            Image downImage;
            if (car.getSocketChannel() == null) {
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
            g.setColor(Color.BLACK);
            if (car.getOrientation() == Orientation.LEFT) {
                int[] args = {-40, -30, 80, 60, 0, 9, -15, -20};
                draw(g, panel, leftImage, car, args);
            } else if (car.getOrientation() == Orientation.RIGHT) {
                int[] args = {-40, -30, 80, 60, -10, 9, -15, -20};
                draw(g, panel, rightImage, car, args);
            } else if (car.getOrientation() == Orientation.UP) {
                int[] args = {-30, -40, 60, 80, -5, 10, 20, 10};
                draw(g, panel, upImage, car, args);
            } else if (car.getOrientation() == Orientation.DOWN) {
                int[] args = {-30, -40, 60, 80, -5, 5, 20, 10};
                draw(g, panel, downImage, car, args);
            }
        }
    }

    private void draw(Graphics g, JPanel panel, Image image, Car car, int[] args){
        g.drawImage(image, car.getX() + args[0], car.getY() + args[1], args[2], args[3], panel);
        g.drawString(String.valueOf(car.getAGVNum()), car.getX() + args[4], car.getY() + args[5]);
        if (car.isOnDuty()) {
            String destination =  car.getDestination();
            if (destination != null) {
                g.setColor(Color.BLACK);
                g.drawString(destination, car.getX() + args[6], car.getY() + args[7]);
                g.setColor(Color.white);
            }
        }
    }
}
