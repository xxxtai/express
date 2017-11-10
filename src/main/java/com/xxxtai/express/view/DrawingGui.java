package com.xxxtai.express.view;


import com.xxxtai.express.constant.City;
import com.xxxtai.express.model.*;
import com.xxxtai.express.toolKit.MyTextField;
import com.xxxtai.express.toolKit.Common;
import com.xxxtai.express.constant.NodeFunction;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static com.xxxtai.express.controller.Dijkstra.MAXINT;


@Component
@Slf4j(topic = "develop")
public class DrawingGui extends JPanel{
    private static final long serialVersionUID = 1L;
    private RoundButton schedulingGuiBtn;
    private RoundButton settingGuiBtn;
    private RoundButton drawingGuiBtn;
    private RoundButton importGraphBtn;
    private RoundButton reflectGraphBtn;
    private MyTextField rowField;
    private MyTextField columnField;
    private MyTextField realDistanceField;
    private RoundButton confirmAddExitBtn;

    @Resource
    private Graph graph;
    @Resource
    private DrawingGraph drawingGraph;
    private boolean isImportGraph;

    public DrawingGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        schedulingGuiBtn = new RoundButton("调度界面");
        schedulingGuiBtn.setBounds(0, 0, screenSize.width / 3, screenSize.height / 20);

        settingGuiBtn = new RoundButton("设置界面");
        settingGuiBtn.setBounds(screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);

        drawingGuiBtn = new RoundButton("制图界面");
        drawingGuiBtn.setBounds(2 * screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);
        drawingGuiBtn.setForeground(new Color(30, 144, 255));
        drawingGuiBtn.setBackground(Color.WHITE);

        rowField = new MyTextField("        行数");
        rowField.setBounds(5 * screenSize.width / 12, 3 * screenSize.height / 15, screenSize.width / 6, screenSize.height / 20);
        columnField = new MyTextField("        列数");
        columnField.setBounds(5 * screenSize.width / 12, 4 * screenSize.height / 15, screenSize.width / 6, screenSize.height / 20);
        realDistanceField = new MyTextField("        距离");
        realDistanceField.setBounds(5 * screenSize.width / 12, 5 * screenSize.height / 15, screenSize.width / 6, screenSize.height / 20);
        RoundButton confirmBtn = new RoundButton("确认");
        confirmBtn.setBounds(5 * screenSize.width / 12, 6 * screenSize.height / 15, screenSize.width / 6, screenSize.height / 20);
        confirmBtn.addActionListener(e -> createNewGraph(screenSize));

        importGraphBtn = new RoundButton("导入");
        importGraphBtn.setBounds(12 * screenSize.width / 15, 23 * screenSize.height / 25, screenSize.width / 15, screenSize.height / 22);
        importGraphBtn.addActionListener(e -> importExistGraph());

        confirmAddExitBtn = new RoundButton("确认");
        confirmAddExitBtn.setBounds(13 * screenSize.width / 15, 23 * screenSize.height / 25, screenSize.width / 15, screenSize.height / 22);
        confirmAddExitBtn.addActionListener(e -> writeExcel(graph));

        reflectGraphBtn = new RoundButton("修改");
        reflectGraphBtn.setBounds(14 * screenSize.width / 15, 23 * screenSize.height / 25, screenSize.width / 15, screenSize.height / 22);
        reflectGraphBtn.addActionListener(e -> reflectGraph());

        this.setLayout(null);
        this.add(schedulingGuiBtn);
        this.add(settingGuiBtn);
        this.add(drawingGuiBtn);
        this.add(reflectGraphBtn);
        this.add(columnField);
        this.add(realDistanceField);
        this.add(rowField);
        this.add(confirmBtn);
        this.add(importGraphBtn);
        this.add(confirmAddExitBtn);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    FileNameDialog dialog = new FileNameDialog("请输入城市名称:");
                    dialog.setOnDialogListener((cityName, buttonState) -> {
                        dialog.dispose();
                        if (buttonState) {
                            addExit(e, cityName);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (isImportGraph)
            drawingGraph.drawingMap(g, DrawingGraph.Style.EXPRESS, false);
    }

    private void importExistGraph() {
        isImportGraph = true;
        Timer timer = new Timer(50, e -> repaint());
        timer.start();
        this.removeAll();
        this.add(schedulingGuiBtn);
        this.add(settingGuiBtn);
        this.add(drawingGuiBtn);
        this.add(confirmAddExitBtn);
        this.add(importGraphBtn);
        this.add(reflectGraphBtn);
    }

    private void addExit(MouseEvent e, String cityName) {
        ArrayList<Node> xNode = new ArrayList<>();
        ArrayList<Node> yNode = new ArrayList<>();
        for (Node node : graph.getNodeArray()) {
            if (Math.abs(node.x - e.getX()) < 30) {
                xNode.add(node);
            }

            if (Math.abs(node.y - e.getY()) < 30) {
                yNode.add(node);
            }
        }

        Node minxNode = new Node(0, MAXINT, MAXINT, NodeFunction.NULL);
        Node nextMinNode = new Node(0, MAXINT, MAXINT, NodeFunction.NULL);
        for (Node node : xNode) {
            if (Math.abs(node.y - e.getY()) < Math.abs(minxNode.y - e.getY())) {
                minxNode = node;
            }
        }
        xNode.remove(minxNode);
        for (Node node : xNode) {
            if (Math.abs(node.y - e.getY()) < Math.abs(nextMinNode.y - e.getY())) {
                nextMinNode = node;
            }
        }

        Node minYNode = new Node(0, MAXINT, MAXINT, NodeFunction.NULL);
        Node nextMinYNode = new Node(0, MAXINT, MAXINT, NodeFunction.NULL);
        for (Node node : yNode) {
            if (Math.abs(node.x - e.getX()) < Math.abs(minYNode.x - e.getX())) {
                minYNode = node;
            }
        }
        yNode.remove(minYNode);
        for (Node node : yNode) {
            if (Math.abs(node.x - e.getX()) < Math.abs(nextMinYNode.x - e.getX())) {
                nextMinYNode = node;
            }
        }
        log.info(minxNode.getCardNum() + " /" + nextMinNode.getCardNum() + "/" + minYNode.getCardNum() + "/" + nextMinYNode.getCardNum());
        City city = City.valueOfName(cityName);
        ((ComGraph)graph).addExit(new Exit(city.getName(), city.getCode(), Arrays.asList(minxNode, nextMinNode, minYNode, nextMinYNode)));
    }

    private void createNewGraph(Dimension screenSize) {

        int row = Integer.valueOf(rowField.getText());
        int column = Integer.valueOf(columnField.getText());
        int realDistance = Integer.valueOf(realDistanceField.getText());
        int rlMargin = 100;
        int topMargin = 140;
        int downMargin = 100;
        if (row % 2 != 0) {
            log.error("输入行数必须为偶数");
        }
        if (row > 15) {
            topMargin = 90;
            downMargin = 50;
        }

        if (column > 15)
            rlMargin = 50;
        int width = (screenSize.width - 2 * rlMargin) / (column - 1);
        int height = (screenSize.height - (topMargin + downMargin)) / (row - 1);
        if (width < height) {
            height = width;
            topMargin = (screenSize.height - height * (row - 1)) / 2;
        } else {
            width = height;
            rlMargin = (screenSize.width - width * (column - 1)) / 2;
        }

        int nodeNum = 0;
        int cardNum = row * column;
        ((ComGraph)graph).getNewGraph();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                ((ComGraph)graph).addNode(++nodeNum, rlMargin + j * width, topMargin + i * height, NodeFunction.Junction.getValue());
                if (graph.getNodeArray().size() > 1 && (nodeNum - 1) % column != 0) {
                    ((ComGraph)graph).addEdge(nodeNum - 1, nodeNum, realDistance, ++cardNum);
                }

            }
        }

        for (int i = 1; i <= column && (i + column <= column * row); i++) {
            for (int j = i; j + column <= column * row; j += column) {
                ((ComGraph)graph).addEdge(j, j + column, realDistance, ++cardNum);
            }
        }

        for (Edge edge : graph.getEdgeArray()) {
            ((ComGraph)graph).addNode(edge.cardNum, (edge.startNode.x + edge.endNode.x) / 2, (edge.startNode.y + edge.endNode.y) / 2, NodeFunction.Parking.getValue());
        }

        try {
            writeExcel(graph);
            System.out.println("newing........");
            if (graph.getNodeArray().size() == (column * row + graph.getEdgeArray().size()) && graph.getEdgeArray().size() == ((column - 1) * row + (row - 1) * column))
                System.out.println("new graph success!");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("exception:", ex);
        }

    }


    public void getGuiInstance(JFrame main, JPanel schedulingGui, JPanel settingGui) {
        schedulingGuiBtn.addActionListener(e -> Common.changePanel(main, schedulingGui));
        settingGuiBtn.addActionListener(e -> Common.changePanel(main, settingGui));
    }

    private void reflectGraph(){
        int X = graph.getNodeMap().get(graph.getEntranceMap().keySet().iterator().next()).x;
        isImportGraph = false;
        Map<Integer, Node> newNodeMap = new HashMap<>();
        for (Node node : graph.getNodeMap().values()) {
            if (node.x < X || (graph.getEntranceMap().containsKey(node.cardNum) && graph.getEntranceMap().get(node.cardNum).getDirection().equals(Entrance.Direction.LEFT))) {
                newNodeMap.put(node.cardNum, new Node(node.cardNum, node.x - 100, node.y, node.getFunction()));
            } else if (node.x > X || (graph.getEntranceMap().containsKey(node.cardNum) && graph.getEntranceMap().get(node.cardNum).getDirection().equals(Entrance.Direction.RIGHT))){
                newNodeMap.put(node.cardNum, new Node(node.cardNum, node.x + 100, node.y, node.getFunction()));
            }
        }
        graph.setNodeMap(newNodeMap);

        Map<Integer, Edge> newEdgeMap = new HashMap<>();
        for (Edge edge : graph.getEdgeArray()) {
            newEdgeMap.put(edge.cardNum, new Edge(graph.getNodeMap().get(edge.startNode.cardNum), graph.getNodeMap().get(edge.endNode.cardNum),
                    graph.getNodeMap().get(edge.cardNum), edge.realDistance));
        }
        graph.setEdgeMap(newEdgeMap);

        writeExcel(graph);
        isImportGraph = true;
    }


    private static void writeExcel(Graph graph) {
        try {
            File file = new File(ComGraph.PATH_NAME);
            InputStream inputStream = new FileInputStream(file.getPath());
            Workbook wb = Workbook.getWorkbook(inputStream);
            WritableWorkbook wwb = Workbook.createWorkbook(new File(ComGraph.PATH_NAME), wb);
            wwb.removeSheet(0);
            WritableSheet wsNode = wwb.createSheet("nodes", 0);
            int i = 0;
            for (Node node : graph.getNodeArray()) {
                Number numberNum = new Number(0, i, node.cardNum);
                Number numberX = new Number(1, i, node.x);
                Number numberY = new Number(2, i, node.y);
                Label functionString = new Label(3, i, node.getFunction().getValue().toString());
                wsNode.addCell(numberX);
                wsNode.addCell(numberY);
                wsNode.addCell(numberNum);
                wsNode.addCell(functionString);
                i++;
            }
            wwb.removeSheet(1);
            WritableSheet wsEdge = wwb.createSheet("edges", 1);
            i = 0;
            for (Edge edge : graph.getEdgeArray()) {
                Number numberStrNode = new Number(0, i, edge.startNode.cardNum);
                Number numberEndNode = new Number(1, i, edge.endNode.cardNum);
                Number numberDis = new Number(2, i, edge.realDistance);
                Number cardNum = new Number(3, i, edge.cardNum);
                wsEdge.addCell(numberStrNode);
                wsEdge.addCell(numberEndNode);
                wsEdge.addCell(numberDis);
                wsEdge.addCell(cardNum);
                i++;
            }

            wwb.removeSheet(2);
            WritableSheet wsExits = wwb.createSheet("exits", 2);
            i = 0;
            for (java.util.List<Exit> list : graph.getExitMap().values()) {
                for (Exit exit : list) {
                    Label name = new Label(0, i, exit.name);
                    Number code = new Number(1, i, exit.code);
                    Number x = new Number(2, i, exit.x);
                    Number y = new Number(3, i, exit.y);
                    Number exit1 = new Number(4, i, exit.getExitNodeNums()[0]);
                    Number exit2 = new Number(5, i, exit.getExitNodeNums()[1]);
                    Number exit3 = new Number(6, i, exit.getExitNodeNums()[2]);
                    Number exit4 = new Number(7, i, exit.getExitNodeNums()[3]);
                    wsExits.addCell(name);
                    wsExits.addCell(code);
                    wsExits.addCell(x);
                    wsExits.addCell(y);
                    wsExits.addCell(exit1);
                    wsExits.addCell(exit2);
                    wsExits.addCell(exit3);
                    wsExits.addCell(exit4);
                    i++;
                }
            }
            wwb.write();
            wwb.close();
            wb.close();

            System.out.println("write success");
        } catch (Exception e) {
            log.error("exception:", e);
        }
    }

}
