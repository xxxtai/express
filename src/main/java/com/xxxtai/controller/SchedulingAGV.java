package com.xxxtai.controller;

import com.xxxtai.model.Car;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Path;
import com.xxxtai.toolKit.Absolute2Relative;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Tai on 2017/5/18.
 */
@Component
@Slf4j
public class SchedulingAGV implements Runnable {
    private List<Car> AGVArray;
    @Resource
    private Graph graph;
    @Resource(name = "dijkstra")
    private Algorithm algorithm;

    public SchedulingAGV() {
    }

    public void setAGVArray(ArrayList<Car> AGVArray) {
        this.AGVArray = AGVArray;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Car car : AGVArray) {
                if (car.getAtEdge() != null && !car.isOnDuty()) {
                    int minEntrance = 0;
                    for (Map.Entry<Integer, Queue<Car>> entry : graph.getEntranceMap().entrySet()) {
                        if (minEntrance == 0 || entry.getValue().size() < graph.getEntranceMap().get(minEntrance).size()) {
                            minEntrance = entry.getKey();
                        }
                    }
                    log.debug("派遣车辆" + car.getAGVNum() + "去" + minEntrance);
                    Path path = algorithm.findRoute(car.getAtEdge(), minEntrance, true);
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
                        graph.getEntranceMap().get(minEntrance).add(car);
                    }
                }

                if (car.getAtEdge() != null && car.isOnDuty() && car.isOnEntrance()) {

                }
            }
        }
    }
}
