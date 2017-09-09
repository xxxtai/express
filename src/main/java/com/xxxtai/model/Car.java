package com.xxxtai.model;

import com.xxxtai.controller.CommunicationWithAGV;
import com.xxxtai.controller.TrafficControl;
import com.xxxtai.constant.Orientation;

import java.util.List;

public interface Car {
    void init(int num);

    void setReceiveCardNum(int cardNum);

    void setAtEdge(Edge edge);

    void stepByStep();

    void heartBeat();

    void judgeOrientation();

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

    CommunicationWithAGV getCommunicationWithAGV();

    TrafficControl getTrafficControl();

    int getReadCardNum();

    boolean isOnDuty();

    void setOnDuty(boolean f);

    boolean isOnEntrance();

}
