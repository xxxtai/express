package com.xxxtai.controller;


import com.xxxtai.constant.Constant;
import com.xxxtai.model.Car;
import com.xxxtai.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

import static com.xxxtai.toolKit.Common.delay;

@Slf4j(topic = "develop")
public class CommunicationWithAGV implements Runnable {
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private Car car;

    public CommunicationWithAGV() {
    }

    void setSocket(Socket socket) {
        try {
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
//                log.info(car.getAGVNum() + "AGV receive:" +revMessage);
                String content = Constant.getContent(revMessage);
                String[] c = content.split(Constant.SPLIT);
                if (revMessage.endsWith(Constant.CARD_SUFFIX)) {
                    int cardNum = Integer.parseInt(c[1], 16);
                    if (cardNum != 0) {
                        this.car.setReceiveCardNum(cardNum);
                    }
                } else if (revMessage.endsWith(Constant.STATE_SUFFIX)) {
                    this.car.setState(Integer.parseInt(c[1], 16));
                }
            }
           delay(20);
        }
    }

    private void setup(){
        while (this.car == null) {
            String revMessage;
            StringBuilder builder = new StringBuilder();
            builder.append(this.hashCode()).append(" CommunicationWithAGV Runnable start\n");
            if ((revMessage = read()) != null) {
                builder.append("receive msg:").append(revMessage);
                if (revMessage.endsWith(Constant.HEART_SUFFIX)) {
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
//            log.info(revMsg);
        } catch (IOException e) {
            log.error("exception:", e);
        }
        return revMsg;
    }

    public boolean write(String sendMessage) {
        boolean isSuccess = false;
        try {
            printWriter.println(sendMessage);
            printWriter.flush();
            isSuccess = true;
        } catch (Exception e) {
           log.error("exception:", e);
        }
        return isSuccess;
    }

}
