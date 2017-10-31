package com.xxxtai.express.controller;

import com.xxxtai.express.model.*;
import com.xxxtai.express.toolKit.Absolute2Relative;
import com.xxxtai.express.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Tai on 2017/5/18.
 */
@Component
@Slf4j
public class DispatchingAGV implements Runnable {
    @Resource
    private Graph graph;
    @Resource(name = "AStar")
    private Algorithm algorithm;

    public DispatchingAGV() {}

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Car car : SchedulingGui.AGVArray) {
                if (car.getAtEdge() != null && !car.isOnDuty()) {
                    Integer minEntrance = null;
                    for (Entrance entrance : graph.getEntranceMap().values()) {
                        if (minEntrance == null || entrance.getQueue().size() < graph.getEntranceMap().get(minEntrance).getQueue().size()) {
                            minEntrance = entrance.getCardNum();
                        }
                    }
                    log.debug("派遣车辆" + car.getAGVNum() + "去" + minEntrance);
                    Path path = minEntrance == null? null : algorithm.findRoute(car.getAtEdge(), graph.getEdgeMap().get(minEntrance), true);

                    if (path != null) {
                        System.out.println();
                        System.out.print(car.getAGVNum() + "AGVRoute:");
                        for (Integer n : path.getRoute()) {
                            System.out.print(n + "/");
                        }
                        System.out.print("--relative：");
                        String routeString = Absolute2Relative.convert(graph, path);
                        System.out.println(routeString);
                        car.sendMessageToAGV(routeString);
                        car.setRouteNodeNumArray(path.getRoute());
                        graph.getEntranceMap().get(minEntrance).getQueue().offer(car);
                    }
                }

                if (car.getAtEdge() != null && car.isOnDuty() && car.isOnEntrance()) {

                }
            }
        }
    }
}
