package com.xxxtai.express.controller;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xxxtai.express.dao.SortingDAO;
import com.xxxtai.express.model.*;
import com.xxxtai.express.toolKit.Absolute2Relative;
import com.xxxtai.express.toolKit.Common;
import com.xxxtai.express.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by Tai on 2017/5/18.
 */
@Component
@Slf4j(topic = "develop")
public class DispatchingAGV implements Runnable {
    private long startTime;
    @Resource
    private Graph graph;
    @Resource(name = "AStar")
    private Algorithm algorithm;
    @Resource
    private SortingDAO sortingDAO;

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        Long[] cities = new Long[graph.getExitMap().size()];
        int i = 0;
        for (Long code : graph.getExitMap().keySet()) {
            cities[i] = code;
            i++;
        }
        Random random = new Random();
        int X = graph.getEntranceMap().isEmpty()? 0 : graph.getNodeMap().get(graph.getEntranceMap().keySet().iterator().next()).x;
        while (true) {
            Common.delay(4000);
            for (Car car : SchedulingGui.AGVArray) {
                if (car.getAtEdge() != null && !car.isOnDuty()) {
                    if (!graph.getEntranceMap().containsKey(car.getReadCardNum())) {
                        Entrance minDisEntrance = null;
                        int minDis = Integer.MAX_VALUE;
                        for (Entrance entrance : graph.getEntranceMap().values()) {
                            if ((car.getX() < X && entrance.getDirection().equals(Entrance.Direction.RIGHT))||
                                    (car.getX() > X && entrance.getDirection().equals(Entrance.Direction.LEFT))) {
                                int tmpDis = Math.abs(car.getX() - graph.getNodeMap().get(entrance.getCardNum()).getX())
                                        + Math.abs(car.getY() - graph.getNodeMap().get(entrance.getCardNum()).getY());
                                if (tmpDis < minDis) {
                                    minDis = tmpDis;
                                    minDisEntrance = entrance;
                                }
                            }
                        }
                        Path path = minDisEntrance == null? null : algorithm.findRoute(car.getAtEdge(), graph.getEdgeMap().get(minDisEntrance.getCardNum()), true, false);

                        if (path != null) {
                            log.info("派遣" + car.getAGVNum() + "AGV去" + minDisEntrance.getCardNum() + "分拣入口");
                            String[] routeString = Absolute2Relative.convert(graph, path);
                            log.info(car.getAGVNum() + "AGVRoute--relative：" + routeString[1]);
                            car.sendMessageToAGV(routeString[0]);
                            car.setRouteNodeNumArray(path.getRoute());
                            minDisEntrance.missionCountIncrease();
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

    public void saveSorting(){
        Sorting sorting = new Sorting();
        long count = 0;
        for (Entrance entrance : graph.getEntranceMap().values()) {
            count += entrance.getMissionCount();
        }
        sorting.setTotalQuantity(count);
        sorting.setTotalTime((System.currentTimeMillis() - startTime)/1000);
        sorting.setPerHour(count * 3600 / sorting.getTotalTime());
        sorting.setFeatures(new Gson().toJson(graph.getEntranceMap().values()));
        sortingDAO.insert(sorting);
    }
}
