package com.xxxtai.controller;

import com.xxxtai.model.Car;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Path;
import com.xxxtai.myToolKit.AbsoluteToRelativeCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SchedulingAGVRunnable implements Runnable{
    public List<Car> AGVArray;
    @Autowired
    private Graph graph;
    @Resource(name = "dijkstra")
    private Algorithm algorithm;

    public SchedulingAGVRunnable(){
    }

    public void setAGVArray(ArrayList<Car> AGVArray){
        this.AGVArray = AGVArray;
    }
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Car car: AGVArray){
                if(car.getAtEdge() != null && !car.isOnduty()){
                    int minEntrance = 0;
                    for (Map.Entry<Integer, Queue<Car>> entry : graph.getEntranceMap().entrySet()){
                        if (minEntrance == 0 || entry.getValue().size() < graph.getEntranceMap().get(minEntrance).size()){
                            minEntrance = entry.getKey();
                        }
                    }
                    System.out.println("jjjjjjj"+minEntrance);
                    Path path = algorithm.findRoute(car.getAtEdge(), minEntrance, true);
                    if(path != null ) {
                        System.out.println();
                        System.out.print(car.getAGVNUM() + "AGVRoute:");
                        for (Integer n : path.getRoute()) {
                            System.out.print(n + "/");
                        }
                        System.out.print("--relative：");
                        String routeString = AbsoluteToRelativeCoordinates.convert(graph, path);
                        System.out.println(routeString);
                        car.sendMessageToAGV(routeString);
                        car.setRouteNodeNumArray(path.getRoute());
                        graph.getEntranceMap().get(minEntrance).add(car);
                    }
                }

                if(car.getAtEdge() != null && car.isOnduty() && car.isOnEntrance()){

                }
            }
        }
    }
}
