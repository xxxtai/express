package com.xxxtai.controller;

import com.xxxtai.model.Car;
import com.xxxtai.model.Edge;
import com.xxxtai.model.Node;
import java.util.List;

public interface TrafficControl {
	void setRouteNodeNumArray(List<Integer> routeNodeNumArray);

	boolean isStopToWait(int cardNum, boolean f);

	Node getLockedNode();

	Edge getLockedEdge();

	void setCar(Car car);
}
