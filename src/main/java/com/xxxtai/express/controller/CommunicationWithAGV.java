package com.xxxtai.express.controller;


import com.xxxtai.express.constant.Constant;
import com.xxxtai.express.model.Car;
import com.xxxtai.express.model.Graph;
import com.xxxtai.express.toolKit.Common;
import com.xxxtai.express.toolKit.ReaderWriter;
import com.xxxtai.express.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
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
            if (!connectedTest(4500)) {
                break;
            }

            String revMessage;
            if ((revMessage = read()) != null) {
                this.car.setLastCommunicationTime(System.currentTimeMillis());
                String content = Constant.getContent(revMessage);
                String[] c = content.split(Constant.SPLIT);
                if (revMessage.startsWith(Constant.CARD_PREFIX)) {
                    int cardNum;
                    if (c[0].length() % 2 == 0) {
                        cardNum = graph.getSerialNumMap().get(c[0]);
                    } else {
                        cardNum = graph.getSerialNumMap().get("0" + c[0]);
                    }
                    if (cardNum != 0) {
                        this.car.setReceiveCardNum(cardNum);
                    }
                } else if (revMessage.startsWith(Constant.STATE_PREFIX)) {
                    this.car.setState(Integer.parseInt(c[0], 16));
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
        String revMsg = null;
        try {
            revMsg= bufferedReader.readLine();
        } catch (IOException e) {
            log.error("exception:", e);
        }
        return revMsg;
    }

    public boolean writeHexString(String sendMessage) {
        boolean isSuccess = false;
        try {
            outputStream.write(ReaderWriter.hexString2Bytes(sendMessage));
            isSuccess = true;
        } catch (Exception e) {
            log.error("exception:", e);
        }
        return isSuccess;
    }
}
