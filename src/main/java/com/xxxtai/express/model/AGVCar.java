package com.xxxtai.express.model;

import com.xxxtai.express.constant.*;
import com.xxxtai.express.controller.CommunicationWithAGV;
import com.xxxtai.express.controller.TrafficControl;
import com.xxxtai.express.toolKit.Common;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.List;
import javax.annotation.Resource;

@Slf4j(topic = "develop")
public class AGVCar implements Car {
    private boolean finishEdge;
    private State state = State.STOP;
    private int count_3s;
    private int count_1s;
    private int lastReadCardNum;
    private static final int FORWARD_PIX = 7;
    private boolean firstlyExecutiveCommand = true;
    private @Setter
    Point position = new Point(-200, -200);
    private @Getter @Setter
    Command executiveCommand;

    private @Getter @Setter
    String destination;

    private @Getter
    int stopCardNum;

    private @Setter
    CommunicationWithAGV communicationWithAGV;

    private @Getter
    Orientation orientation = Orientation.LEFT;

    private @Getter
    int AGVNum;

    private @Getter
    int readCardNum;

    private @Getter
    Edge atEdge;

    private @Getter @Setter
    boolean onDuty;

    private @Getter @Setter
    long lastCommunicationTime;

    @Resource
    private @Getter
    TrafficControl trafficControl;
    @Resource
    private Graph graph;

    public void init(int num) {
        this.AGVNum = num;
        trafficControl.setCar(this);
    }

    public void setReceiveCardNum(int cardNum) {
        if (cardNum == readCardNum) {
            return;
        }
        this.readCardNum = cardNum;
        Node node = graph.getNodeMap().get(this.lastReadCardNum);
        if (node != null && node.getFunction().equals(NodeFunction.Junction)) {
            Edge edge = graph.getEdgeMap().get(this.readCardNum);
            this.position.x = edge.CARD_POSITION.x;
            this.position.y = edge.CARD_POSITION.y;
            if (edge.startNode.cardNum.equals(node.cardNum)) {
                setAtEdge(edge);
            } else if (edge.endNode.cardNum.equals(node.cardNum)) {
                setAtEdge(new Edge(edge.endNode, edge.startNode, edge.realDistance, edge.cardNum));
            }
        } else {
            Node node1 = graph.getNodeMap().get(this.readCardNum);
            this.position.x = node1.x;
            this.position.y = node1.y;
        }

        this.lastReadCardNum = this.readCardNum;
        if (this.readCardNum == this.stopCardNum) {
            Node n = graph.getNodeMap().get(this.stopCardNum);
            this.position.x = n.x;
            this.position.y = n.y;
            this.state = State.STOP;
            this.onDuty = false;
            this.destination = null;
        }

        trafficControl.isStopToWait(this.readCardNum, false);
    }

    public void stepByStep() {
//        if (!finishEdge && atEdge != null && (state == State.FORWARD || state == State.BACKWARD)) {
//            if (atEdge.startNode.x == atEdge.endNode.x) {
//                if (atEdge.startNode.y < atEdge.endNode.y) {
//                    if (this.position.y < atEdge.endNode.y) {
//                        this.position.y += FORWARD_PIX;
//                    } else {
//                        finishEdge = true;
//                    }
//                } else if (atEdge.startNode.y > atEdge.endNode.y) {
//                    if (this.position.y > atEdge.endNode.y) {
//                        this.position.y -= FORWARD_PIX;
//                    } else {
//                        finishEdge = true;
//                    }
//                }
//            } else if (atEdge.startNode.y == atEdge.endNode.y) {
//                if (atEdge.startNode.x < atEdge.endNode.x) {
//                    if (this.position.x < atEdge.endNode.x)
//                        this.position.x += FORWARD_PIX;
//                    else
//                        finishEdge = true;
//                } else if (atEdge.startNode.x > atEdge.endNode.x) {
//                    if (this.position.x > atEdge.endNode.x)
//                        this.position.x -= FORWARD_PIX;
//                    else
//                        finishEdge = true;
//                }
//            }
//        }
    }

    public void heartBeat() {
        if (this.count_3s == 60) {
            this.count_3s = 0;
            sendMessageToAGV(Constant.HEART_PREFIX + Common.toHexString(this.AGVNum) + Constant.SUFFIX);
        } else {
            this.count_3s++;
        }

        if (this.count_1s == 20) {
            this.count_1s = 0;
            if (this.executiveCommand != null && this.executiveCommand.getValue() != this.state.getValue()
                    && !this.state.equals(State.COLLIED) && !this.firstlyExecutiveCommand) {
                sendMessageToAGV(executiveCommand.getCommand());
                log.info("111111111111111111111111111111111111111111111111111" + executiveCommand.getDescription());
            }
        } else {
            this.count_1s++;
        }

        if (this.executiveCommand != null && this.executiveCommand.getValue() != this.state.getValue()
                && !this.state.equals(State.COLLIED) && this.firstlyExecutiveCommand) {
            sendMessageToAGV(executiveCommand.getCommand());
            this.firstlyExecutiveCommand = false;
        } else if (this.executiveCommand != null && this.executiveCommand.getValue() == this.state.getValue()
                && !this.firstlyExecutiveCommand) {
            this.firstlyExecutiveCommand = true;
        }
    }

    private void setAtEdge(Edge edge) {
        this.atEdge = edge;
//        this.position.x = this.atEdge.startNode.x;
//        this.position.y = this.atEdge.startNode.y;
        this.finishEdge = false;
        judgeOrientation();
    }

    private void judgeOrientation() {
        if (atEdge.startNode.x == atEdge.endNode.x) {
            if (atEdge.startNode.y < atEdge.endNode.y) {
                orientation = Orientation.DOWN;
            } else {
                orientation = Orientation.UP;
            }
        } else if (atEdge.startNode.y == atEdge.endNode.y) {
            if (atEdge.startNode.x < atEdge.endNode.x) {
                orientation = Orientation.RIGHT;
            } else {
                orientation = Orientation.LEFT;
            }
        }
    }

    public void sendMessageToAGV(String message) {
        if (this.communicationWithAGV != null) {
            this.communicationWithAGV.writeHexString(message);
        }
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public State getState() {
        return this.state;
    }

    public void setRouteNodeNumArray(List<Integer> arrayList) {
        this.stopCardNum = arrayList.get(arrayList.size() - 1);
        this.trafficControl.setRouteNodeList(arrayList);
        this.onDuty = true;
    }

    @Override
    public Runnable getCommunicationRunnable() {
        return communicationWithAGV;
    }

    public int getX() {
        return this.position.x;
    }

    public int getY() {
        return this.position.y;
    }

    public boolean isOnEntrance() {
        return false;
    }
}
