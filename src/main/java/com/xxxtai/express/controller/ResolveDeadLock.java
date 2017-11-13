package com.xxxtai.express.controller;

import com.google.common.collect.Lists;
import com.xxxtai.express.constant.City;
import com.xxxtai.express.model.*;
import com.xxxtai.express.toolKit.Absolute2Relative;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@Slf4j(topic = "develop")
public class ResolveDeadLock extends TimerTask {
    private LinkedBlockingDeque<Integer> deadLockEdgeList;
    @Resource
    private Graph graph;
    @Resource(name = "AStar")
    private Algorithm algorithm;

     @PostConstruct
    public void init(){
        this.deadLockEdgeList = new LinkedBlockingDeque<>(graph.getAGVSPosition().size() * 2);
    }

    @Override
    public void run() {
        int size = this.deadLockEdgeList.size();
        for (int i = 0; i < size; i++) {
            int lockEdgeNum = this.deadLockEdgeList.getFirst();
            boolean result = resolveDeadLock(lockEdgeNum);
            if (!result) {
                try {
                    this.deadLockEdgeList.put(lockEdgeNum);
                } catch (InterruptedException e) {
                    log.error("exception:", e);
                }
            }
        }
    }

    boolean resolveDeadLock(int lockEdgeNum){
        Car lockCar = graph.getEdgeMap().get(lockEdgeNum).waitQueue.peek();
        List<Path> paths = Lists.newArrayList();
        if (lockCar.getDestination() == null) {
            int X = graph.getNodeMap().get(graph.getEntranceMap().keySet().iterator().next()).x;
            for (Entrance entrance : graph.getEntranceMap().values()) {
                if ((lockCar.getX() < X && entrance.getDirection().equals(Entrance.Direction.RIGHT))||
                        (lockCar.getX() > X && entrance.getDirection().equals(Entrance.Direction.LEFT))) {
                    Path path = algorithm.findRoute(lockCar.getAtEdge(), graph.getEdgeMap().get(entrance.getCardNum()), true, true);
                    if (path != null) {
                        paths.add(path);
                    }
                }
            }
        } else {
            List<Exit> exits = graph.getExitMap().get(City.valueOfName(lockCar.getDestination()).getCode());
            for (Exit exit : exits) {
                for (int exitNum :exit.getExitNodeNums()) {
                    Path path = algorithm.findRoute(lockCar.getAtEdge(), graph.getEdgeMap().get(exitNum), true, true);
                    if (path != null) {
                        paths.add(path);
                    }
                }
            }
        }

        if (!paths.isEmpty()) {
            paths.sort(Comparator.comparingInt(Path::getCost));
            deadLockedCarPath(lockCar, paths.get(0));
            return true;
        }
        return false;
    }

    private void deadLockedCarPath(Car lockCar, Path path){
        Queue<Car> lockCarInWaitQueue = lockCar.getTrafficControl().getLockedEdge().waitQueue;
        int queueSize = lockCarInWaitQueue.size();
        for (int i = 0; i < queueSize; i++) {
            Car carTemp = lockCarInWaitQueue.poll();
            if (carTemp.getAGVNum() != lockCar.getAGVNum()) {
                lockCarInWaitQueue.offer(carTemp);
            }
        }
        lockCar.getTrafficControl().setLockedNode(null);

        String[] routeString = Absolute2Relative.convert(graph, path);
        log.info(lockCar.getAGVNum() + "AGVRoute--relative:" + routeString[1]);
        lockCar.sendMessageToAGV(routeString[0]);
        lockCar.setRouteNodeNumArray(path.getRoute());
    }


    public LinkedBlockingDeque<Integer> getDeadLockEdgeList() {
        return deadLockEdgeList;
    }

    public void setDeadLockEdgeList(LinkedBlockingDeque<Integer> deadLockEdgeList) {
        this.deadLockEdgeList = deadLockEdgeList;
    }
}
