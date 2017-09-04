package com.xxxtai.model;

import java.util.ArrayList;

public interface TrafficControl {
	void setRouteNodeNumArray(ArrayList<Integer> routeNodeNumArray, Car car);
	boolean isStopToWait(int cardNum, boolean f);
	Node getLockedNode();
	Edge getLockedEdge();
}
