package com.xxxtai.express.controller;

import com.xxxtai.express.constant.Constant;
import com.xxxtai.express.constant.State;
import com.xxxtai.express.model.AGVCar;
import com.xxxtai.express.model.Car;
import com.xxxtai.express.model.Graph;
import com.xxxtai.express.model.Node;
import com.xxxtai.express.toolKit.Common;
import com.xxxtai.express.toolKit.ReaderWriter;
import com.xxxtai.express.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.awt.*;
import java.io.*;
import java.net.Socket;

@Slf4j(topic = "develop")
public class CommunicationWithAGV implements Runnable {
    @Resource
    private Graph graph;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private OutputStream outputStream;
    private Car car;

    public CommunicationWithAGV() {
    }

    void setSocket(Socket socket) {
        try {
            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            log.error("exception:", e);
        }
    }

    @Override
    public void run() {
        setup();

        while (true) {
            if (!connectedTest(7500)) {
                break;
            }

            String revMessage;
            if ((revMessage = read()) != null) {
                this.car.setLastCommunicationTime(System.currentTimeMillis());
                String content = Constant.getContent(revMessage);
                String[] c = content.split(Constant.SPLIT);
                if (revMessage.startsWith(Constant.CARD_PREFIX)) {
                    int cardNum;
                    if (Constant.USE_SERIAL) {
                        if (c[0].length() % 2 == 0) {
                            cardNum = graph.getSerialNumMap().get(c[0]);
                        } else {
                            cardNum = graph.getSerialNumMap().get("0" + c[0]);
                        }
                    } else {
                        cardNum = Integer.parseInt(c[0]);
                    }

                    if (cardNum != 0) {
                        this.car.setReceiveCardNum(cardNum);
                    }
                } else if (revMessage.startsWith(Constant.STATE_PREFIX)) {
                    int stateValue = Integer.parseInt(c[0], 16);
                    if (stateValue == State.FORWARD.getValue()) {
                        this.car.setState(State.FORWARD);
                    } else if (stateValue == State.STOP.getValue() && this.car.getAtEdge() != null) {
                        Node n = graph.getNodeMap().get(this.car.getAtEdge().cardNum);
                        ((AGVCar)this.car).setPosition(new Point(n.x, n.y));
                        this.car.setState(State.STOP);
                    } else if (stateValue == State.UNLOADED.getValue()) {
                        if (this.car.getReadCardNum() == this.car.getStopCardNum()) {
                            ((AGVCar)this.car).setOnDuty(false);
                            ((AGVCar)this.car).setDestination(null);
                        }
                    } else if (stateValue == State.COLLIED.getValue()) {
                        this.car.setState(State.COLLIED);
                    }
                }
            }
           Common.delay(20);
        }
    }

    private void setup(){
        while (this.car == null) {
            String revMessage;
            StringBuilder builder = new StringBuilder();
            builder.append(this.hashCode()).append(" CommunicationWithAGV Runnable start\n");
            if ((revMessage = read()) != null) {
                builder.append("receive msg:").append(revMessage);
                if (revMessage.startsWith(Constant.HEART_PREFIX)) {
                    int AGVNum = Integer.parseInt(Constant.getContent(revMessage), 16);
                    for (Car car : SchedulingGui.AGVArray) {
                        if (car.getAGVNum() == AGVNum) {
                            car.setCommunicationWithAGV(this);
                            this.car = car;
                            this.car.setLastCommunicationTime(System.currentTimeMillis());
                            builder.append(" confirmed AGVNum :").append(AGVNum).append("号AGV");
                            log.info(builder.toString());
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean connectedTest(int interval){
        if (System.currentTimeMillis() - this.car.getLastCommunicationTime() > interval) {//通讯中断
            this.car.setCommunicationWithAGV(null);
            try {
                printWriter.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.error("break......................." + System.currentTimeMillis());
            return false;
        }
        return true;
    }

    private String read() {
        String recMsg = null;
        try {
            if (!bufferedReader.ready()){
                return null;
            }
            recMsg= bufferedReader.readLine();
//            log.info(recMsg);
        } catch (IOException e) {
            log.error("exception:", e);
        }
        return recMsg;
    }

    public boolean writeHexString(String sendMessage) {
        boolean isSuccess = false;
        try {
            outputStream.write(ReaderWriter.hexString2Bytes(sendMessage));
//            log.info("send message:" + sendMessage);
            isSuccess = true;


        } catch (Exception e) {
//            log.error("exception:", e);
        }
        return isSuccess;
    }
}
