package com.xxxtai.model;

import com.xxxtai.controller.CommunicationWithAGVRunnable;
import com.xxxtai.toolKit.Orientation;

import java.util.ArrayList;

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
	
	 void setCommunicationWithAGVRunnable(CommunicationWithAGVRunnable communicationWithAGVRunnable);
	
	 void sendMessageToAGV(String route);
	
	 int getAGVNum();
	
	 Edge getAtEdge();
	
	 void setState(int state);
	
	 void setRouteNodeNumArray(ArrayList<Integer> arrayList);
	
	 long getLastCommunicationTime();
	
	 void setLastCommunicationTime(long time);
	
	 CommunicationWithAGVRunnable getCommunicationWithAGVRunnable();

	 TrafficControl getTrafficControl();
	
	 int getReadCardNum();

	 boolean isOnDuty();

	 void setOnDuty(boolean f);

	 boolean isOnEntrance();

}
