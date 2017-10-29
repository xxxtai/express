package com.xxxtai.express.model;

import com.xxxtai.express.constant.NodeFunction;
import jxl.Sheet;
import jxl.Workbook;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ComGraph implements Graph{
    public static final String PATH_NAME = "C:\\Users\\xxxta\\work\\Graph.xls";
    private int row = 5;
    private int column = 6;
    private @Getter
    Map<Integer, Node> nodeMap;
    private @Getter
    Map<Integer, Edge> edgeMap;
    private @Getter
    Map<Long, List<Exit>> exitMap;
    private @Getter
    Map<Integer, Entrance> entranceMap;
    private @Getter
    Map<String, Integer> serialNumMap;
    private @Getter
    Map<Integer, String> cardNumMap;

    public ComGraph() {
        nodeMap = new HashMap<>();
        edgeMap = new ConcurrentHashMap<>();
        exitMap = new HashMap<>();
        serialNumMap = new HashMap<>();
        cardNumMap = new HashMap<>();
        entranceMap = new HashMap<>();
        importNewGraph(PATH_NAME);
    }

    private void importNewGraph(String pathName) {
        File file = new File(pathName);
        try {
            InputStream is = new FileInputStream(file.getPath());
            Workbook wb = Workbook.getWorkbook(is);

            Sheet sheetNodes = wb.getSheet("nodes");
            if (sheetNodes != null) {
                for (int i = 0; i < sheetNodes.getRows(); i++) {
                    this.addNode(
                            Integer.parseInt(sheetNodes.getCell(0, i).getContents()),
                            Integer.parseInt(sheetNodes.getCell(1, i).getContents()),
                            Integer.parseInt(sheetNodes.getCell(2, i).getContents()),
                            Integer.parseInt(sheetNodes.getCell(3, i).getContents()));
                }
            }

            Sheet sheetEdges = wb.getSheet("edges");
            if (sheetEdges != null) {
                for (int i = 0; i < sheetEdges.getRows(); i++) {

                    this.addEdge(
                            Integer.parseInt(sheetEdges.getCell(0, i).getContents()),
                            Integer.parseInt(sheetEdges.getCell(1, i).getContents()),
                            Integer.parseInt(sheetEdges.getCell(2, i).getContents()),
                            Integer.parseInt(sheetEdges.getCell(3, i).getContents())
                    );
                }
            }

            Sheet sheetExit = wb.getSheet("exits");
            if (sheetExit != null) {
                for (int i = 0; i < sheetExit.getRows(); i++) {
                    String name = sheetExit.getCell(0, i).getContents();
                    Long code = Long.parseLong(sheetExit.getCell(1, i).getContents());
                    int x = Integer.parseInt(sheetExit.getCell(2, i).getContents());
                    int y = Integer.parseInt(sheetExit.getCell(3, i).getContents());
                    int[] exits = {Integer.parseInt(sheetExit.getCell(4, i).getContents()),
                            Integer.parseInt(sheetExit.getCell(5, i).getContents()),
                            Integer.parseInt(sheetExit.getCell(6, i).getContents()),
                            Integer.parseInt(sheetExit.getCell(7, i).getContents())};
                    this.addExit(new Exit(name, code, x, y, exits));
                }
            }

            Sheet sheetEntrance = wb.getSheet("entrances");
            if (sheetEntrance != null) {
                for (int i = 0; i < sheetEntrance.getRows(); i++) {
                    int cardNum =Integer.parseInt(sheetEntrance.getCell(0, i).getContents());
                    int direction = Integer.parseInt(sheetEntrance.getCell(1, i).getContents());
                    Entrance.Direction direct;
                    if(direction == 1){
                        direct = Entrance.Direction.UP;
                    }else if (direction == 2){
                        direct = Entrance.Direction.DOWN;
                    }else {
                        direct = Entrance.Direction.NULL;
                    }
                    entranceMap.put(cardNum, new Entrance(cardNum, direct));
                }
            }

            Sheet sheetSerial = wb.getSheet("serial");
            if (sheetSerial != null) {
                for (int i = 0; i < sheetSerial.getRows(); i++) {
                    String serialNum = sheetSerial.getCell(1, i).getContents();
                    int cardNum = Integer.parseInt(sheetSerial.getCell(0, i).getContents());
                    cardNumMap.put(cardNum, serialNum);
                    serialNumMap.put(serialNum, cardNum);
                }
            }

            wb.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addExit(Exit exit) {
        if (!exitMap.containsKey(exit.code)) {
            exitMap.put(exit.code, new ArrayList<>());
            exitMap.get(exit.code).add(exit);
        } else {
            exitMap.get(exit.code).add(exit);
        }
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

    @Override
    public Collection<Node> getNodeArray() {
        return this.nodeMap.values();
    }

    @Override
    public Collection<Edge> getEdgeArray() {
        return this.edgeMap.values();
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }
}
