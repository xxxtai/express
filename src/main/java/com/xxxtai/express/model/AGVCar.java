package com.xxxtai.express.model;

import com.xxxtai.express.constant.*;
import com.xxxtai.express.controller.TrafficControl;
import com.xxxtai.express.toolKit.Common;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.List;
import java.util.Timer;
import javax.annotation.Resource;

@Slf4j(topic = "develop")
public class AGVCar implements Car {
    private State state = State.STOP;
    private int lastReadCardNum;
    private @Setter Point position = new Point(-200, -200);
    private @Getter @Setter Command executiveCommand;
    private @Getter @Setter SocketChannel socketChannel;
    private @Getter @Setter String destination;
    private @Getter int stopCardNum;
    private @Getter Orientation orientation = Orientation.LEFT;
    private @Getter int AGVNum;
    private @Getter int readCardNum;
    private @Getter Edge atEdge;
    private @Getter @Setter boolean onDuty;
    private @Getter @Setter long lastCommunicationTime;
    @Resource
    private @Getter TrafficControl trafficControl;
    @Resource
    private Graph graph;

    public void init(int AGVNum, int positionCardNum) {
        this.AGVNum = AGVNum;
        trafficControl.setCar(this);
        new Timer().schedule(new ExeCommandTask(this), 0, 30);
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
                setAtEdge(new Edge(edge.endNode, edge.startNode, graph.getNodeMap().get(edge.cardNum), edge.realDistance));
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

    public void stepByStep() {}

    private void setAtEdge(Edge edge) {
        this.atEdge = edge;
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
        if (this.socketChannel != null) {
            ChannelFuture channelFuture = null;
            try {
                channelFuture = this.socketChannel.writeAndFlush(message).sync();
            } catch (InterruptedException e) {
                log.info(this.getAGVNum() + "AGV writeAndFlush InterruptedException:",e);
            }
            if (channelFuture == null || !channelFuture.isSuccess()) {
                log.info(this.getAGVNum() + "AGV writeAndFlush message:" + message + " failed 失败，！！！！！！！！！！！！！！！！！！！！！！！！");
            } else {
                log.debug(this.getAGVNum() + "AGV express send message " + message);
            }
        } else {
            log.info(this.getAGVNum() + "AGV socketChannel null message:" + message+ "  ！！！！！！！！！！！！！！！！！！！！！！！！！");
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
