package com.xxxtai.express.model;

import com.xxxtai.express.constant.Command;
import com.xxxtai.express.constant.State;
import com.xxxtai.express.controller.CommunicationWithAGV;
import com.xxxtai.express.controller.TrafficControl;
import com.xxxtai.express.constant.Orientation;

import java.util.List;

public interface Car {
    void init(int AGVNum, int positionCardNum);

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

    void setState(State state);

    State getState();

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

    void setExecutiveCommand(Command command);
}
