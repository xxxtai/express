package com.xxxtai.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

public class Path {
	public final int END_NODE_NUM;
	@Getter @Setter
	private int stopNodeNum;
	@Getter @Setter
	private int realDistance;
	@Getter
	private boolean remove;
	@Getter @Setter
	private List<Integer> route;

	public Path(int startNode, int endNode){
		this.END_NODE_NUM = endNode;
		route = Lists.newArrayList();
		route.add(startNode);
	}
	
	public void addRouteNode(int node){
		route.add(node);
	}
	
	public void newRoute(List<Integer> route){
		this.route = Lists.newArrayList(route);
	}
	
	public void setRemove(){
		remove = true;
	}
}
