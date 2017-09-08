package com.xxxtai.view;


import com.xxxtai.main.Main;
import com.xxxtai.model.Edge;
import com.xxxtai.model.Exit;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Node;
import com.xxxtai.toolKit.Common;
import com.xxxtai.constant.NodeFunction;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.xxxtai.controller.Dijkstra.MAXINT;


@Component
@Slf4j
public class DrawingGui extends JPanel implements Gui {
    private static final long serialVersionUID = 1L;
    private RoundButton schedulingGuiBtn;
    private RoundButton settingGuiBtn;
    private RoundButton drawingGuiBtn;
    private RoundButton importGraphBtn;
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

        importGraphBtn = new RoundButton("导入地图");
        importGraphBtn.setBounds(10 * screenSize.width / 12, 13 * screenSize.height / 15, screenSize.width / 9, screenSize.height / 20);
        importGraphBtn.addActionListener(e -> importExistGraph());

        confirmAddExitBtn = new RoundButton("确认添加");
        confirmAddExitBtn.setBounds(10 * screenSize.width / 12, 14 * screenSize.height / 15, screenSize.width / 9, screenSize.height / 20);
        confirmAddExitBtn.addActionListener(e -> writeExcel(graph));

        this.setLayout(null);
        this.add(schedulingGuiBtn);
        this.add(settingGuiBtn);
        this.add(drawingGuiBtn);
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
            drawingGraph.drawingMap(g);
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
    }

    private void addExit(MouseEvent e, String cityName) {
        ArrayList<Node> xNode = new ArrayList<>();
        ArrayList<Node> yNode = new ArrayList<>();
        for (Node node : graph.getNodeArray()) {
            if (Math.abs(node.x - e.getX()) < 60) {
                xNode.add(node);
            }

            if (Math.abs(node.y - e.getY()) < 60) {
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
        graph.addExit(new Exit(cityName, Arrays.asList(minxNode, nextMinNode, minYNode, nextMinYNode)));
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
        graph.getNewGraph();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                graph.addNode(++nodeNum, rlMargin + j * width, topMargin + i * height, NodeFunction.Junction.getValue());
                if (graph.getNodeArraySize() > 1 && (nodeNum - 1) % column != 0) {
                    graph.addEdge(nodeNum - 1, nodeNum, realDistance, ++cardNum);
                }

            }
        }

        for (int i = 1; i <= column && (i + column <= column * row); i++) {
            for (int j = i; j + column <= column * row; j += column) {
                graph.addEdge(j, j + column, realDistance, ++cardNum);
            }
        }

        for (Edge edge : graph.getEdgeArray()) {
            graph.addNode(edge.cardNum, (edge.startNode.x + edge.endNode.x) / 2, (edge.startNode.y + edge.endNode.y) / 2, NodeFunction.Parking.getValue());
        }

        try {
            writeExcel(graph);
            System.out.println("newing........");
            if (graph.getNodeArraySize() == (column * row + graph.getEdgeArraySize()) && graph.getEdgeArraySize() == ((column - 1) * row + (row - 1) * column))
                System.out.println("new graph success!");
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("exception:", ex);
        }

    }


    public void getGuiInstance(Main main, SchedulingGui schedulingGui, SettingGui settingGui) {
        schedulingGuiBtn.addActionListener(e -> Common.changePanel(main, schedulingGui));
        settingGuiBtn.addActionListener(e -> Common.changePanel(main, settingGui));
    }


    private static void writeExcel(Graph graph) {
        try {
            File file = new File(Graph.PATH_NAME);
            InputStream inputStream = new FileInputStream(file.getPath());
            Workbook wb = Workbook.getWorkbook(inputStream);
            WritableWorkbook wwb = Workbook.createWorkbook(new File(Graph.PATH_NAME), wb);
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
            for (java.util.List<Exit> list : graph.getExitList()) {
                for (Exit exit : list) {
                    Label name = new Label(0, i, exit.name);
                    Number x = new Number(1, i, exit.X);
                    Number y = new Number(2, i, exit.Y);
                    wsExits.addCell(name);
                    wsExits.addCell(x);
                    wsExits.addCell(y);
                    i++;
                }
            }
            wwb.write();
            wwb.close();
            wb.close();

            System.out.println("write success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("exception:", e);
        }
    }

}
