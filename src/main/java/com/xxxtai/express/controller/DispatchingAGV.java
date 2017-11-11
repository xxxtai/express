package com.xxxtai.express.controller;

import com.google.common.collect.Lists;
import com.xxxtai.express.constant.City;
import com.xxxtai.express.model.*;
import com.xxxtai.express.toolKit.Absolute2Relative;
import com.xxxtai.express.toolKit.Common;
import com.xxxtai.express.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
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
        Long[] cities = new Long[graph.getExitMap().size()];
        int i = 0;
        for (Long code : graph.getExitMap().keySet()) {
            cities[i] = code;
            i++;
        }
        Random random = new Random();
        int X = graph.getNodeMap().get(graph.getEntranceMap().keySet().iterator().next()).x;
        while (true) {
            Common.delay(3000);
            for (Car car : SchedulingGui.AGVArray) {
                if (car.getAtEdge() != null && !car.isOnDuty()) {
                    if (!graph.getEntranceMap().containsKey(car.getReadCardNum())) {
                        Integer minMissionCountEntrance = null;
                        List<Entrance> entranceList = Lists.newArrayList(graph.getEntranceMap().values());
                        entranceList.sort(Comparator.comparingInt(Entrance::getMissionCount));
                        for (Entrance entrance : entranceList) {
                            if ((car.getX() < X && entrance.getDirection().equals(Entrance.Direction.RIGHT))||
                                    (car.getX() > X && entrance.getDirection().equals(Entrance.Direction.LEFT))) {
                                minMissionCountEntrance = entrance.getCardNum();
                                break;
                            }
                        }
                        Path path = minMissionCountEntrance == null? null : algorithm.findRoute(car.getAtEdge(), graph.getEdgeMap().get(minMissionCountEntrance), true, false);

                        if (path != null) {
                            log.info("派遣" + car.getAGVNum() + "AGV去" + minMissionCountEntrance + "分拣入口");
                            String[] routeString = Absolute2Relative.convert(graph, path);
                            log.info(car.getAGVNum() + "AGVRoute--relative：" + routeString[1]);
                            car.sendMessageToAGV(routeString[0]);
                            car.setRouteNodeNumArray(path.getRoute());
                            graph.getEntranceMap().get(minMissionCountEntrance).missionCountIncrease();
                        }
                    } else {
                        Long selectCityCode = cities[random.nextInt(cities.length - 1)];
                        List<Exit> exits = graph.getExitMap().get(selectCityCode);
                        String cityName = exits.get(0).name;

                        List<Path> pathList = Lists.newArrayList();
                        for (Exit exit : exits) {
                            for (int nodeNum : exit.getExitNodeNums()) {
                                Path path = algorithm.findRoute(car.getAtEdge(), graph.getEdgeMap().get(nodeNum), true, false);
                                if (path != null) {
                                    pathList.add(path);
                                }
                            }
                        }
                        if (pathList.size() > 0) {
                            log.info("派遣" + car.getAGVNum() + "AGV去" + cityName + "分拣出口");
                            pathList.sort(Comparator.comparingInt(Path::getCost));
                            String[] routeString = Absolute2Relative.convert(graph, pathList.get(0));
                            log.info(car.getAGVNum() + "AGVRoute--relative:" + routeString[1]);
                            car.sendMessageToAGV(routeString[0]);
                            car.setRouteNodeNumArray(pathList.get(0).getRoute());
                            ((AGVCar) car).setDestination(cityName);
                        }
                    }
                }
            }
        }
    }
}
