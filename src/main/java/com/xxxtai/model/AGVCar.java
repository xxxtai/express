package com.xxxtai.model;

import com.xxxtai.controller.CommunicationWithAGVRunnable;
import com.xxxtai.toolKit.Orientation;
import com.xxxtai.toolKit.State;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.awt.*;
import java.net.SocketException;
import java.util.ArrayList;

@Slf4j(topic = "debug")
public class AGVCar implements Car{
	@Resource
	private Graph graph;
	private CommunicationWithAGVRunnable communicationWithAGVRunnable;
	private Orientation orientation = Orientation.LEFT;
	private Point position = new Point(-200, -200);
	private boolean finishEdge;
	private State state = State.STOP;
	private Edge atEdge;
	private static final int FORWARD_PIX = 7;
	private int AGVNum;
	private int readCardNum;
	private int lastReadCardNum;
	private int stopCardNum;
	private boolean onDuty;
	private int count_3s;
	private long lastCommunicationTime;
	@Resource
	private TrafficControl trafficControl;
	
	public AGVCar(){
		log.debug("kkk");
	}

	public void init(int num){
		this.AGVNum = num;
	}
	
	public void setReceiveCardNum(int cardNum){
		this.readCardNum = cardNum;
		Edge edge = null;
		Node node = graph.getNodeMap().get(this.lastReadCardNum);
		if(node != null){
			edge = graph.getEdgeMap().get(cardNum);
		}
		
		if(node != null && edge != null){
			if(edge.START_NODE.CARD_NUM.equals(node.CARD_NUM)){
				setAtEdge(edge);
			}else if(edge.END_NODE.CARD_NUM.equals(node.CARD_NUM)){
				setAtEdge(new Edge(edge.END_NODE, edge.START_NODE, edge.REAL_DISTANCE, edge.CARD_NUM));
			}
		}
		this.lastReadCardNum = this.readCardNum;
		if(cardNum == this.stopCardNum){
			Node n = graph.getNodeMap().get(this.stopCardNum);
			this.position.x = n.X;
			this.position.y = n.Y;
			this.state = State.STOP;
			return;
		}
		if(trafficControl.isStopToWait(cardNum, false)){
			sendMessageToAGV("CC02DD");
			System.out.println("命令"+this.AGVNum+"AGV停下来");
		}
	}	
	
	public void stepByStep(){			
		if(!finishEdge&& atEdge != null && (state == State.FORWARD || state == State.BACKWARD)){
			if(atEdge.START_NODE.X == atEdge.END_NODE.X){
				if(atEdge.START_NODE.Y < atEdge.END_NODE.Y ){
					if(this.position.y < atEdge.END_NODE.Y){
						this.position.y += FORWARD_PIX;
					}else{
						finishEdge = true;
					}	
				}else if(atEdge.START_NODE.Y > atEdge.END_NODE.Y ){
					if(this.position.y > atEdge.END_NODE.Y){
						this.position.y -= FORWARD_PIX;
					}else{
						finishEdge = true;
					}
				}
			}else if(atEdge.START_NODE.Y == atEdge.END_NODE.Y){
				if(atEdge.START_NODE.X < atEdge.END_NODE.X ){
					if(this.position.x < atEdge.END_NODE.X)
						this.position.x += FORWARD_PIX;
					else
						finishEdge = true;
				}else if(atEdge.START_NODE.X > atEdge.END_NODE.X){
					if(this.position.x > atEdge.END_NODE.X)
						this.position.x -= FORWARD_PIX;
					else
						finishEdge = true;
				}
			}
		}
	} 
	
	public void heartBeat(){
		if(this.count_3s == 60){
			this.count_3s = 0;
			if(this.AGVNum < 16) {
				sendMessageToAGV("AA0" + Integer.toHexString(this.AGVNum) + "DD");
			}else {
				sendMessageToAGV("AA" + Integer.toHexString(this.AGVNum) + "DD");
			}
		}else{
			this.count_3s++;
		}
	}
	
	public void setAtEdge(Edge edge){
		this.atEdge = edge;
		this.position.x = this.atEdge.START_NODE.X;
		this.position.y = this.atEdge.START_NODE.Y;
		this.finishEdge = false;
		this.state = State.FORWARD;
		judgeOrientation();
	}
	
	public void judgeOrientation(){
		if(atEdge.START_NODE.X == atEdge.END_NODE.X){
			if(atEdge.START_NODE.Y < atEdge.END_NODE.Y){
				orientation = Orientation.DOWN;
			}else{
				orientation = Orientation.UP;
			} 	
		}else if(atEdge.START_NODE.Y == atEdge.END_NODE.Y){
			if(atEdge.START_NODE.X < atEdge.END_NODE.X){
				orientation = Orientation.RIGHT;
			}else{
				orientation = Orientation.LEFT;
			} 				
		}
	}
	
	public void sendMessageToAGV(String message){
		if(this.communicationWithAGVRunnable != null){
			try {
				this.communicationWithAGVRunnable.write(message);
			} catch (SocketException e) {
				log.error("exception:", e);
			}
		}		
	}
	
	public void setState(int state){
		if(state == 1){
			this.state = State.FORWARD;
		}else if(state == 2){
			Node n = graph.getNodeMap().get(this.atEdge.CARD_NUM);
			this.position.x = n.X;
			this.position.y = n.Y;
			this.state = State.STOP;
		}
	}
	
	public void setRouteNodeNumArray(ArrayList<Integer> arrayList){
		this.stopCardNum = arrayList.get(arrayList.size()-1);
		this.trafficControl.setRouteNodeNumArray(arrayList,this);
		this.onDuty = true;
	}
	
	public long getLastCommunicationTime(){
		return this.lastCommunicationTime;
	}
	
	public void setLastCommunicationTime(long time){
		this.lastCommunicationTime = time;
	}
	
	public void setCommunicationWithAGVRunnable(CommunicationWithAGVRunnable communicationWithAGVRunnable){
		this.communicationWithAGVRunnable = communicationWithAGVRunnable;
	}
	
	public CommunicationWithAGVRunnable getCommunicationWithAGVRunnable(){
		return this.communicationWithAGVRunnable;
	}
	
	public int getAGVNum(){
		return this.AGVNum;
	}
	
	public Edge getAtEdge(){
		return this.atEdge;
	}
	
	public Orientation getOrientation(){
		return this.orientation;
	}
	
	public int getX(){
		return this.position.x;
	}
	
	public int getY(){
		return this.position.y;
	}
	
	public TrafficControl getTrafficControl(){
		return this.trafficControl;
	}
	
	public int getReadCardNum(){
		return this.readCardNum;
	}

	public boolean isOnDuty(){
		return this.onDuty;
	}

	public void setOnDuty(boolean f){
		this.onDuty = f;
	}

	public boolean isOnEntrance(){
		return false;
	}
}
