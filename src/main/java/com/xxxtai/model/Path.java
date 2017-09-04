package com.xxxtai.model;

import java.util.ArrayList;

public class Path {
	public final int START_NODE_NUM;
	public final int END_NODE_NUM;
	private int stopNodeNum;
	private int REAL_DIS;
	private int NUM_OF_AGV;
	
	private boolean remove;
	private ArrayList<Integer> route;
	
	public Path(int startNode, int endNode){
		this.START_NODE_NUM = startNode;
		this.END_NODE_NUM = endNode;
		route = new ArrayList<Integer>();
		route.add(this.START_NODE_NUM);
	}
	
	public void addRouteNode(int node){
		route.add(node);
	}
	
	public void newRoute(ArrayList<Integer> route){
		this.route = new ArrayList<Integer>();
		for(int i = 0; i < route.size(); i++)
			this.route.add(route.get(i));
	}
	
	public ArrayList<Integer> getRoute(){
		return route;
	}
	
	public void setRealDis(int realDis){
		this.REAL_DIS = realDis;
	}
	
	public int getRealDis(){
		return REAL_DIS;
	}
	
	public void setRemove(){
		remove = true;
	}
	
	public boolean getRemove(){
		return remove;
	}
	
	public void setNumOfAGV(int num){
		this.NUM_OF_AGV = num;
	}
	
	public int getNumOfAGV(){
		return NUM_OF_AGV;
	}
	
	public void setStopNodeNum(int num){
		this.stopNodeNum = num;
	}
	
	public int getStopNodeNum(){
		return this.stopNodeNum;
	}
}
