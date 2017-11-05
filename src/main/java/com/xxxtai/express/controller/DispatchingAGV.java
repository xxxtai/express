package com.xxxtai.express.controller;

import com.xxxtai.express.constant.City;
import com.xxxtai.express.model.*;
import com.xxxtai.express.toolKit.Absolute2Relative;
import com.xxxtai.express.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

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
        City[] cities = City.values();
        Random random = new Random();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Car car : SchedulingGui.AGVArray) {
                if (car.getAtEdge() != null && !car.isOnDuty() && !graph.getEntranceMap().containsKey(car.getReadCardNum())) {
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
                } else if (car.getAtEdge() != null && !car.isOnDuty() && graph.getEntranceMap().containsKey(car.getReadCardNum())) {
                    City selectCity = cities[random.nextInt(cities.length - 1)];
                    log.debug("派遣车辆" + car.getAGVNum() + "去" + selectCity.getName());
                    List<Exit> exits = graph.getExitMap().get(selectCity.getCode());
                    Exit selectExit = exits.get(exits.size() - 1 == 0 ? 0 : random.nextInt(exits.size() - 1));
                    int selectedExitNode = selectExit.getExitNodeNums()[random.nextInt(3)];
                    Path path = algorithm.findRoute(car.getAtEdge(), graph.getEdgeMap().get(selectedExitNode), true);
                    if (path != null) {
                        log.info(car.getAGVNum() + " AGVRoute:" + path.getRoute().toString());
                        String routeString = Absolute2Relative.convert(graph, path);
                        log.info("--relative：" + routeString);
                        car.sendMessageToAGV(routeString);
                        car.setRouteNodeNumArray(path.getRoute());
                        ((AGVCar) car).setDestination(selectCity.getName());
                    }
                }
            }
        }
    }
}
