package com.xxxtai.model;

import com.xxxtai.controller.CommunicationWithAGVRunnable;

import java.util.ArrayList;

public interface Car {	
	public void init(int num);
	
	 void setReceiveCardNum(int cardNum);
	
	 void setAtEdge(Edge edge);
	
	 void stepByStep();
	
	 void heartBeat();
	
	 void judgeOrientation();
	
	 AGVCar.Orientation getOrientation();
	
	 int getX();
	
	 int getY();
	
	 void setCommunicationWithAGVRunnable(CommunicationWithAGVRunnable communicationWithAGVRunnable);
	
	 void sendMessageToAGV(String route);
	
	 int getAGVNUM();
	
	 Edge getAtEdge();
	
	 void setState(int state);
	
	 void setRouteNodeNumArray(ArrayList<Integer> arrayList);
	
	 long getLastCommunicationTime();
	
	 void setlastCommunicationTime(long time);
	
	 CommunicationWithAGVRunnable getCommunicationWithAGVRunnable();

	 TrafficControl getTrafficControl();
	
	 int getReadCardNum();

	 boolean isOnduty();

	 void setOnduty(boolean f);

	 boolean isOnEntrance();

}
