package com.xxxtai.model;

import com.xxxtai.toolKit.NodeFunction;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Component
public class Graph {
    private Map<Integer, Node> nodeMap;
    private Map<Integer, Edge> edgeMap;
    private Map<Integer, Queue<Car>> entranceMap;
    private Map<String, List<Exit>> exitMap;

    public Graph() {
        nodeMap = new HashMap<>();
        edgeMap = new HashMap<>();
        exitMap = new HashMap<>();
        importNewGraph();

        entranceMap = new HashMap<>();
        for (int i = 108; i < 118; i++) {
            entranceMap.put(i, new LinkedList<>());
            if (edgeMap.size() > i) {
                edgeMap.get(i).setRemoved();
            }
        }
    }

    public void addExit(Exit exit) {
        if (exitMap.get(exit.NAME) == null) {
            exitMap.put(exit.NAME, new ArrayList<>());
            exitMap.get(exit.NAME).add(exit);
        } else {
            exitMap.get(exit.NAME).add(exit);
        }
    }

    public void importNewGraph() {
        File file = new File("C:\\Users\\xxxta\\work\\Graph.xls");
        try {
            if (file != null) {
                System.out.println(file.getPath());
                InputStream is = new FileInputStream(file.getPath());
                Workbook wb = Workbook.getWorkbook(is);

                Sheet sheetNodes = wb.getSheet("nodes");
                for (int i = 0; i < sheetNodes.getRows(); i++) {
                    this.addNode(
                            Integer.parseInt(sheetNodes.getCell(0, i).getContents()),
                            Integer.parseInt(sheetNodes.getCell(1, i).getContents()),
                            Integer.parseInt(sheetNodes.getCell(2, i).getContents()),
                            Integer.parseInt(sheetNodes.getCell(3, i).getContents()));
                }

                Sheet sheetEdges = wb.getSheet("edges");
                for (int i = 0; i < sheetEdges.getRows(); i++) {

                    this.addEdge(
                            Integer.parseInt(sheetEdges.getCell(0, i).getContents()),
                            Integer.parseInt(sheetEdges.getCell(1, i).getContents()),
                            Integer.parseInt(sheetEdges.getCell(2, i).getContents()),
                            Integer.parseInt(sheetEdges.getCell(3, i).getContents())
                    );
                }
                Sheet sheetExit = wb.getSheet("exits");
                for (int i = 0; i < sheetExit.getRows(); i++) {
                    this.addExit(
                            new Exit(
                                    sheetExit.getCell(0, i).getContents(),
                                    Integer.parseInt(sheetExit.getCell(1, i).getContents()),
                                    Integer.parseInt(sheetExit.getCell(2, i).getContents())
                            )
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNodeArraySize() {
        return this.nodeMap.values().size();
    }

    public int getEdgeArraySize() {
        return this.edgeMap.values().size();
    }

    public void addNode(int card_num, int x, int y, Integer num) {
        Node node = new Node(card_num, x, y, NodeFunction.valueOf(num));
        nodeMap.put(card_num, node);
    }

    public void addEdge(int strNodeNum, int endNodeNum, int dis, int cardNum) {
        Node startNode = null;
        Node endNode = null;
        for (Node node : getNodeArray()) {
            if (node.cardNum == strNodeNum)
                startNode = node;
            if (node.cardNum == endNodeNum)
                endNode = node;
        }
        edgeMap.put(cardNum, new Edge(startNode, endNode, dis, cardNum));
    }

    public void getNewGraph() {
        nodeMap = new HashMap<>();
        edgeMap = new HashMap<>();
    }

    public Collection<Node> getNodeArray() {
        return this.nodeMap.values();
    }

    public Collection<Edge> getEdgeArray() {
        return this.edgeMap.values();
    }

    public Map<Integer, Node> getNodeMap() {
        return this.nodeMap;
    }

    public Map<Integer, Edge> getEdgeMap() {
        return this.edgeMap;
    }

    public Map<Integer, Queue<Car>> getEntranceMap() {
        return this.entranceMap;
    }

    public Collection<List<Exit>> getExitList() {
        return this.exitMap.values();
    }
}
