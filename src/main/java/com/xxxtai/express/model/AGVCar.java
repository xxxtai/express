package com.xxxtai.express.model;

import com.xxxtai.express.constant.Command;
import com.xxxtai.express.constant.Constant;
import com.xxxtai.express.controller.CommunicationWithAGV;
import com.xxxtai.express.controller.TrafficControl;
import com.xxxtai.express.constant.Orientation;
import com.xxxtai.express.constant.State;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.List;
import javax.annotation.Resource;

@Slf4j(topic = "develop")
public class AGVCar implements Car {
    private Point position = new Point(-200, -200);
    private boolean finishEdge;
    private State state = State.STOP;
    private int count_3s;
    private int lastReadCardNum;
    private static final int FORWARD_PIX = 7;

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
        this.readCardNum = cardNum;
        Edge edge = null;
        Node node = graph.getNodeMap().get(this.lastReadCardNum);
        if (node != null) {
            edge = graph.getEdgeMap().get(cardNum);
        }

        if (node != null && edge != null) {
            if (edge.startNode.cardNum.equals(node.cardNum)) {
                setAtEdge(edge);
            } else if (edge.endNode.cardNum.equals(node.cardNum)) {
                setAtEdge(new Edge(edge.endNode, edge.startNode, edge.realDistance, edge.cardNum));
            }
        }
        this.lastReadCardNum = this.readCardNum;
        if (cardNum == this.stopCardNum) {
            Node n = graph.getNodeMap().get(this.stopCardNum);
            this.position.x = n.x;
            this.position.y = n.y;
            this.state = State.STOP;
        }
        if (trafficControl.isStopToWait(cardNum, false)) {
            sendMessageToAGV(Command.STOP.getCommand());
            log.info("命令" + this.AGVNum + "AGV停下来");
        }
    }

    public void stepByStep() {
        if (!finishEdge && atEdge != null && (state == State.FORWARD || state == State.BACKWARD)) {
            if (atEdge.startNode.x == atEdge.endNode.x) {
                if (atEdge.startNode.y < atEdge.endNode.y) {
                    if (this.position.y < atEdge.endNode.y) {
                        this.position.y += FORWARD_PIX;
                    } else {
                        finishEdge = true;
                    }
                } else if (atEdge.startNode.y > atEdge.endNode.y) {
                    if (this.position.y > atEdge.endNode.y) {
                        this.position.y -= FORWARD_PIX;
                    } else {
                        finishEdge = true;
                    }
                }
            } else if (atEdge.startNode.y == atEdge.endNode.y) {
                if (atEdge.startNode.x < atEdge.endNode.x) {
                    if (this.position.x < atEdge.endNode.x)
                        this.position.x += FORWARD_PIX;
                    else
                        finishEdge = true;
                } else if (atEdge.startNode.x > atEdge.endNode.x) {
                    if (this.position.x > atEdge.endNode.x)
                        this.position.x -= FORWARD_PIX;
                    else
                        finishEdge = true;
                }
            }
        }
    }

    public void heartBeat() {
        if (this.count_3s == 60) {
            this.count_3s = 0;
            sendMessageToAGV(Constant.PREFIX + Integer.toHexString(this.AGVNum) + Constant.HEART_SUFFIX);
        } else {
            this.count_3s++;
        }
    }

    private void setAtEdge(Edge edge) {
        this.atEdge = edge;
        this.position.x = this.atEdge.startNode.x;
        this.position.y = this.atEdge.startNode.y;
        this.finishEdge = false;
        this.state = State.FORWARD;
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
            this.communicationWithAGV.write(message);
        }
    }

    public void setState(int state) {
        if (state == State.FORWARD.getValue()) {
            this.state = State.FORWARD;
        } else if (state == State.STOP.getValue()) {
            Node n = graph.getNodeMap().get(this.atEdge.cardNum);
            this.position.x = n.x;
            this.position.y = n.y;
            this.state = State.STOP;
        } else if (state == State.UNLOADED.getValue()) {
            this.onDuty = false;
            this.destination = null;
        }
    }

    public void setRouteNodeNumArray(List<Integer> arrayList) {
        this.stopCardNum = arrayList.get(arrayList.size() - 1);
        this.trafficControl.setRouteNodeNumArray(arrayList);
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
