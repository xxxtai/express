package com.xxxtai.express.controller;

import com.xxxtai.express.constant.City;
import com.xxxtai.express.model.*;
import com.xxxtai.express.toolKit.Absolute2Relative;
import com.xxxtai.express.toolKit.Common;
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
@Slf4j(topic = "develop")
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
            Common.delay(1000);
            for (Car car : SchedulingGui.AGVArray) {
                if (car.getAtEdge() != null && !car.isOnDuty()) {
                    if (!graph.getEntranceMap().containsKey(car.getReadCardNum())) {
                        Integer minEntrance = null;
                        for (Entrance entrance : graph.getEntranceMap().values()) {
                            if (minEntrance == null || entrance.getQueue().size() < graph.getEntranceMap().get(minEntrance).getQueue().size()) {
                                minEntrance = entrance.getCardNum();
                            }
                        }
                        log.info("派遣车辆" + car.getAGVNum() + "去" + minEntrance + "分拣入口");
                        Path path = minEntrance == null? null : algorithm.findRoute(car.getAtEdge(), graph.getEdgeMap().get(minEntrance), true);

                        if (path != null) {
                            String[] routeString = Absolute2Relative.convert(graph, path);
                            log.info(car.getAGVNum() + "AGVRoute--relative：" + routeString[1]);
                            car.sendMessageToAGV(routeString[0]);
                            car.setRouteNodeNumArray(path.getRoute());
                            graph.getEntranceMap().get(minEntrance).getQueue().offer(car);
                        }
                    } else {
                        City selectCity = cities[random.nextInt(8 - 1)];
                        log.info("派遣AGV" + car.getAGVNum() + "去 " + selectCity.getName() + " 分拣出口");
                        List<Exit> exits = graph.getExitMap().get(selectCity.getCode());
                        Exit selectExit = exits.get(exits.size() - 1 == 0 ? 0 : random.nextInt(exits.size() - 1));
                        int selectedExitNode = selectExit.getExitNodeNums()[random.nextInt(3)];
                        Path path = algorithm.findRoute(car.getAtEdge(), graph.getEdgeMap().get(selectedExitNode), true);
                        if (path != null) {
                            String[] routeString = Absolute2Relative.convert(graph, path);
                            log.info(car.getAGVNum() + "AGVRoute--relative:" + routeString[1]);
                            car.sendMessageToAGV(routeString[0]);
                            car.setRouteNodeNumArray(path.getRoute());
                            ((AGVCar) car).setDestination(selectCity.getName());
                        }
                    }
                }
            }
        }
    }
}
