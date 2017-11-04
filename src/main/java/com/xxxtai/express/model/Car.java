package com.xxxtai.express.model;

import com.xxxtai.express.controller.CommunicationWithAGV;
import com.xxxtai.express.controller.TrafficControl;
import com.xxxtai.express.constant.Orientation;

import java.util.List;

public interface Car {
    void init(int num);

    void setReceiveCardNum(int cardNum);

    void stepByStep();

    void heartBeat();

    Orientation getOrientation();

    int getX();

    int getY();

    void setCommunicationWithAGV(CommunicationWithAGV communicationWithAGV);

    void sendMessageToAGV(String route);

    int getAGVNum();

    Edge getAtEdge();

    void setState(int state);

    void setRouteNodeNumArray(List<Integer> arrayList);

    long getLastCommunicationTime();

    void setLastCommunicationTime(long time);

    Runnable getCommunicationRunnable();

    TrafficControl getTrafficControl();

    int getReadCardNum();

    boolean isOnDuty();

    boolean isOnEntrance();

    String getDestination();

    int getStopCardNum();
}
