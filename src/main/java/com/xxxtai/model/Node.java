package com.xxxtai.model;

import java.util.LinkedList;
import java.util.Queue;

public class Node {
	public final int X;
	public final int Y;
	public final int CARD_NUM;
	public final String FUNCTION;
	public final Queue<Car> waitQueue;
	private boolean locked;
	
	public Node(int card_num, int x, int y, String function){
		this.X = x;
		this.Y = y;
		this.CARD_NUM =  card_num;
		this.FUNCTION = function;
		this.waitQueue = new LinkedList<>();
	}
	
	public void setLocked(){
		this.locked = true;
	}
	
	public void unlock(){
		this.locked = false;
	}
	
	public boolean isLocked(){
		return this.locked;
	}

}
