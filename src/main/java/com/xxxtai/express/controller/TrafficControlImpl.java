package com.xxxtai.express.controller;

import com.xxxtai.express.constant.Command;
import com.xxxtai.express.model.*;
import com.xxxtai.express.constant.NodeFunction;
import com.xxxtai.express.toolKit.Common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Scope("prototype")
@Slf4j(topic = "develop")
public class TrafficControlImpl implements TrafficControl {
    private List<Integer> routeNodeNumArray;
    private Node lastLockedNode;
    private Edge lastLockedEdge;
    private Node lockedNode;
    private Edge lockedEdge;
    @Resource
    private Graph graph;
    private Car car;

    public boolean isStopToWait(int cardNum, boolean isStart) {
        if (this.routeNodeNumArray == null) {
            log.warn("{}AGV receive cardNum:{} but routeNodeNumArray:{} lastLockedEdge:{} lastLockedNode:{}",
                    car.getAGVNum(), cardNum, routeNodeNumArray, lastLockedEdge, lastLockedNode);
            tryLockEdgeWithoutRoute(cardNum);
            return false;
        }

        if (lastLockedNode != null && cardNum == lastLockedNode.cardNum) {
            log.warn(this.car.getAGVNum() + "AGV receive cardNum " + cardNum + "but duplicate");
            return false;
        }

        if (lastLockedEdge != null && NodeFunction.Junction.equals(graph.getNodeMap().get(cardNum).getFunction())) {
            try {
                tryUnlockEdge(cardNum);
            } catch (Exception e) {
                log.error("exception: "+car.getAGVNum()+"  cardNum : " + cardNum, e);
            }
            return false;
        } else if (NodeFunction.Parking.equals(graph.getNodeMap().get(cardNum).getFunction())) {
            if (!isStart) {
                this.lastLockedEdge = this.lockedEdge;
                this.lastLockedNode = this.lockedNode;
            }
            StringBuilder logMessage = new StringBuilder();
            logMessage.append(car.getAGVNum()).append("AGV receive cardNum:").append(cardNum);
            tryUnlockNode(logMessage);
            return tryLockNodeEdge(cardNum, logMessage);
        } else if (cardNum != routeNodeNumArray.get(2)) {
            log.error("不应该出现的情况! AGVNum:" + this.car.getAGVNum() + " cardNum:" + cardNum
                    + " lastLockedEdge:" + lastLockedEdge
                    + " lastLockedNode:" + lastLockedNode);
        }
        return false;
    }

    private void tryLockEdgeWithoutRoute(int cardNum){
        if (NodeFunction.Parking.equals(graph.getNodeMap().get(cardNum).getFunction())) {
            if (lastLockedEdge != null) {
                tryUnlockEdge(lockedEdge.cardNum);
            }
            lockedEdge = graph.getEdgeMap().get(cardNum);
            lockedEdge.setLocked();
            lockedEdge.waitQueue.add(car);
            lastLockedEdge = lockedEdge;
            car.sendMessageToAGV(Command.STOP.getCommand());
            log.info("命令" + car.getAGVNum() + "AGV停下来");
        } else if (lastLockedEdge != null){
            tryUnlockEdge(lockedEdge.cardNum);
        }
    }

    private boolean tryLockNodeEdge(int cardNum, StringBuilder logMessage) {
        Edge onTheEdge = graph.getEdgeMap().get(cardNum);
        Node nextStartNode = null;
        Node nextEndNode = null;
        Edge nextEdge = null;

        for (int i = 0; i < this.routeNodeNumArray.size(); i++) {
            if ((this.routeNodeNumArray.get(i).equals(onTheEdge.endNode.cardNum) && this.routeNodeNumArray.get(i + 1).equals(onTheEdge.startNode.cardNum))
                    || (this.routeNodeNumArray.get(i).equals(onTheEdge.startNode.cardNum) && this.routeNodeNumArray.get(i + 1).equals(onTheEdge.endNode.cardNum))) {
                nextStartNode = graph.getNodeMap().get(this.routeNodeNumArray.get(i + 1));
                if ((i + 1) != this.routeNodeNumArray.size() - 2) {
                    nextEndNode = graph.getNodeMap().get(this.routeNodeNumArray.get(i + 2));
                } else {
                    Edge edgeTmp = graph.getEdgeMap().get(this.routeNodeNumArray.get(this.routeNodeNumArray.size() - 1));
                    if (edgeTmp.startNode.cardNum.equals(nextStartNode.cardNum)) {
                        nextEndNode = edgeTmp.endNode;
                    } else {
                        nextEndNode = edgeTmp.startNode;
                    }
                }
            }
        }

        if (nextStartNode != null && nextEndNode != null) {
            nextEdge = Common.calculateEdge(nextStartNode.cardNum, nextEndNode.cardNum, graph);
        } else if (cardNum != routeNodeNumArray.get(routeNodeNumArray.size() - 1)) {
            log.warn("不应该出现的情况！");
        }
        if (nextEdge != null) {
            this.lockedEdge = nextEdge;
            this.lockedNode = nextStartNode;
            synchronized (this.lockedNode.cardNum) {
                synchronized (this.lockedEdge.cardNum) {
                    if (this.lockedEdge.isLocked()) {
                        this.lockedEdge.waitQueue.offer(car);
                        logMessage.append("等待通过，因为").append(lockedEdge.cardNum).append("边被").append(lockedEdge.waitQueue.peek().getAGVNum()).append("AGV占用");
                        log.info(logMessage.toString());
                        return true;
                    } else if (this.lockedNode.isLocked()) {
                        this.lockedEdge.waitQueue.offer(car);
                        this.lockedEdge.setLocked();
                        this.lockedNode.waitQueue.offer(car);
                        logMessage.append("占用").append(lockedEdge.cardNum).append("边,等待通过，因为").append(lockedNode.getCardNum()).append("点被").append(lockedNode.waitQueue.peek().getAGVNum()).append("AGV占用");
                        log.info(logMessage.toString());
                        return true;
                    } else {
                        this.lockedEdge.waitQueue.offer(car);
                        this.lockedEdge.setLocked();
                        this.lockedNode.waitQueue.offer(car);
                        this.lockedNode.setLocked();
                        logMessage.append("占用").append(this.lockedEdge.cardNum).append("边和").append(this.lockedNode.cardNum).append("点，并通过");
                    }
                }
            }
        }
        log.info(logMessage.toString());
        return false;
    }

    private void tryUnlockEdge(int cardNum) {
        synchronized (this.lastLockedEdge.cardNum) {
            Car myself = this.lastLockedEdge.waitQueue.poll();
            if (myself.getAGVNum() != car.getAGVNum()) {
                log.error(" 不应该出现的情况！ " + car.getAGVNum() + "AGV通过交汇点准备解除lockedEdge,但是该AGV不在waitQueue顶端");
            }

            if (!this.lastLockedEdge.waitQueue.isEmpty()) {
                Car carTmp = this.lastLockedEdge.waitQueue.peek();
                synchronized (carTmp.getTrafficControl().getLockedNode().cardNum) {
                    if (!carTmp.getTrafficControl().getLockedNode().isLocked()) {
                        carTmp.getTrafficControl().getLockedNode().setLocked();
                        carTmp.sendMessageToAGV(Command.FORWARD.getCommand());
                        log.info("{}AGV receive cardNum:{} >>> 解除占用{}边 >>> {}AGV前进占用{}边",
                                car.getAGVNum(), cardNum, this.lastLockedEdge.cardNum, carTmp.getAGVNum(), lastLockedEdge.cardNum);
                    } else {
                        log.info("{}AGV receive cardNum:{} >>> 解除占用{}边 >>> {}AGV继续等待{}边，因为{}点被{}AGV占用！",
                                car.getAGVNum(), cardNum, lastLockedEdge.cardNum, carTmp.getAGVNum(), lastLockedEdge.cardNum,
                                carTmp.getTrafficControl().getLockedNode().cardNum,
                                carTmp.getTrafficControl().getLockedNode().waitQueue.peek().getAGVNum());
                    }
                    carTmp.getTrafficControl().getLockedNode().waitQueue.offer(carTmp);
                }
            } else {
                log.info("{}AGV receive cardNum:{} >>> {}边完全被解除占用", car.getAGVNum(), cardNum, lastLockedEdge.cardNum);
                this.lastLockedEdge.unlock();
            }
            this.lastLockedEdge = this.lockedEdge;
            this.lastLockedNode = this.lockedNode;
        }
    }

    private void tryUnlockNode(StringBuilder logMessage) {
        if (this.lockedNode != null && this.lockedNode.isLocked()) {
            synchronized (this.lockedNode.cardNum) {
                Car myself = this.lockedNode.waitQueue.poll();
                if (myself.getAGVNum() != car.getAGVNum()) {
                    log.error("不应该出现的情况！ {}AGV通过停车点准备解除lockedNode,但是该AGV不在waitQueue顶端", car.getAGVNum());
                }
                if (!this.lockedNode.waitQueue.isEmpty()) {
                    Car carTmp = this.lockedNode.waitQueue.peek();
                    if (carTmp.getTrafficControl().getLockedEdge().isLocked() &&
                            carTmp.getTrafficControl().getLockedEdge().waitQueue.peek().getAGVNum() == carTmp.getAGVNum()) {
                        carTmp.sendMessageToAGV(Command.FORWARD.getCommand());
                        logMessage.append(" >>> 解除占用").append(this.lockedNode.cardNum).append("点 >>> ").append(carTmp.getAGVNum()).append("AGV前进占用 >>>");
                    } else {
                        log.error("不应该出现的情况！ {}AGV没有锁住{}边，就想通过！", carTmp.getAGVNum(), carTmp.getTrafficControl().getLockedEdge().cardNum);
                    }
                } else {
                    logMessage.append(" >>> ").append(this.lockedNode.cardNum).append("点完全被解除占用 >>>");
                    this.lockedNode.unlock();
                    this.lockedNode = null;
                }
            }
        }
    }

    public void setRouteNodeNumArray(List<Integer> routeNodeNumArray) {
        this.routeNodeNumArray = routeNodeNumArray;
        if (isStopToWait(car.getReadCardNum(), true)) {
            car.sendMessageToAGV(Command.STOP.getCommand());
            log.info("命令" + car.getAGVNum() + "AGV停下来");
        }
    }

    public Node getLockedNode() {
        return this.lockedNode;
    }

    public Edge getLockedEdge() {
        return this.lastLockedEdge;
    }

    @Override
    public void setCar(Car car) {
        this.car = car;
    }
}
