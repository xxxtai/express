package com.xxxtai.model;

import com.xxxtai.constant.NodeFunction;
import jxl.Sheet;
import jxl.Workbook;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Component
public class Graph {
    public static final String PATH_NAME = "C:\\Users\\xxxta\\work\\Graph.xls";
    private Map<Integer, Node> nodeMap;
    private Map<Integer, Edge> edgeMap;
    private Map<String, List<Exit>> exitMap;
    private @Getter
    Map<Integer, Entrance> entranceMap;

    public Graph() {
        nodeMap = new HashMap<>();
        edgeMap = new HashMap<>();
        exitMap = new HashMap<>();
        importNewGraph(PATH_NAME);
        extractEntrances();
    }

    private void extractEntrances(){
        int[] entrances = {220, 227, 234, 241, 248, 255, 262, 269, 276, 283, 290, 297, 304, 311};
        entranceMap = new HashMap<>();
        for (int i = 0; i < entrances.length; i++) {
            Entrance entrance;
            if (i % 2 == 0) {
                entrance= new Entrance(entrances[i], Entrance.Direction.DOWN);
            } else {
                entrance = new Entrance(entrances[i], Entrance.Direction.UP);
            }
            entranceMap.put(entrances[i], entrance);
        }
    }

    public void addExit(Exit exit) {
        if (exitMap.get(exit.name) == null) {
            exitMap.put(exit.name, new ArrayList<>());
            exitMap.get(exit.name).add(exit);
        } else {
            exitMap.get(exit.name).add(exit);
        }
    }

    private void importNewGraph(String pathName) {
        File file = new File(pathName);
        try {
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
            }        } catch (Exception e) {
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

    public Collection<List<Exit>> getExitList() {
        return this.exitMap.values();
    }
}
