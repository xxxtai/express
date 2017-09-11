package com.xxxtai.controller;

import com.xxxtai.constant.City;
import com.xxxtai.constant.Constant;
import com.xxxtai.model.*;
import com.xxxtai.toolKit.Absolute2Relative;
import com.xxxtai.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.xxxtai.controller.Dijkstra.MAXINT;
import static com.xxxtai.toolKit.Common.delay;

@Component
@Slf4j(topic = "develop")
public class CommunicationWithQRScan implements Runnable {
    private BufferedReader bufferedReader;
    @Resource
    private Graph graph;
    @Resource(name = "dijkstra")
    private Algorithm algorithm;

    void setSocket(Socket socket) {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            log.error("exception:", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = bufferedReader.readLine();
                if (message != null && message.endsWith(Constant.QR_SUFFIX)) {
                    String content = Constant.getContent(message);
                    String[] c = content.split(Constant.SPLIT);
                    for (Car car : SchedulingGui.AGVArray) {
                        if (car.getAtEdge() != null && car.getAtEdge().cardNum.equals(Integer.parseInt(c[0], 16))) {
                            log.debug("派遣车辆" + car.getAGVNum() + "去" + City.valueOfCode(Long.parseLong(c[1], 16)));
                            Node node = graph.getNodeMap().get(car.getAtEdge().cardNum);
                            int minDis = MAXINT;
                            int minDisNodeNum = 0;
                            for (Exit exit :graph.getExitMap().get(Long.parseLong(c[1], 16))) {
                                for (int nodeNum : exit.getExitNodeNums()) {
                                    int dis = Math.abs(node.x - graph.getNodeMap().get(nodeNum).x) + Math.abs(node.y - graph.getNodeMap().get(nodeNum).y);
                                    if (minDis > dis) {
                                        minDis = dis;
                                        minDisNodeNum = nodeNum;
                                    }
                                }
                            }

                            int exitNum = minDisNodeNum;
                            Path path = algorithm.findRoute(car.getAtEdge(), exitNum, false);
                            if (path != null) {
                                log.info(car.getAGVNum() + "AGVRoute:" + path.getRoute().toString());
                                String routeString = Absolute2Relative.convert(graph, path);
                                log.info("--relative：" + routeString);
                                car.sendMessageToAGV(routeString);
                                car.setRouteNodeNumArray(path.getRoute());
                                ((AGVCar) car).setDestination(City.valueOfCode(Long.parseLong(c[1], 16)).getName());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("exception:", e);
            }
            delay(100);
        }
    }
}
