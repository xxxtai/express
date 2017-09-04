package com.xxxtai.model;

import com.xxxtai.controller.CommunicationWithAGVRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.net.SocketException;
import java.util.ArrayList;

public class AGVCar implements Car{
	private static Logger logger = LoggerFactory.getLogger("main.logger");
	@Autowired
	private Graph graph;
	private CommunicationWithAGVRunnable communicationWithAGVRunnable;
	public enum Orientation{LEFT,RIGTH,UP,DOWN}
	public enum State{STOP, FORWARD, BACKWARD, SHIPMENT, UNLOADING, NULL}
	private Orientation orientation = Orientation.LEFT;
	private Point position = new Point(-200, -200);
	private boolean finishEdge;
	private State state = State.STOP;
	private Edge atEdge;
	private int FORWARDPIX = 7;
	public int AGVNUM;
	private int readCardNum;
	private int lastReadCardNum;
	private int stopcardNum;
	private boolean isOnduty;
	private int count_3s;
	private long lastCommunicationTime;
	@Autowired
	private TrafficControl trafficControl;
	
	public AGVCar(){
		logger.debug("kkk");
	}

	public void init(int num){
		this.AGVNUM = num;
//		System.out.print(this.getAGVNUM()+",");
	}
	
	public void setReceiveCardNum(int cardNum){
		this.readCardNum = cardNum;
		Edge edge = null;
		Node node = graph.getNodeMap().get(this.lastReadCardNum);		
		if(node != null){
			edge = graph.getEdgeMap().get(cardNum);
		}
		
		if(node != null && edge != null){
			if(edge.START_NODE.CARD_NUM == node.CARD_NUM){
				setAtEdge(edge);
			}else if(edge.END_NODE.CARD_NUM == node.CARD_NUM){
				setAtEdge(new Edge(edge.END_NODE, edge.START_NODE, edge.REAL_DISTANCE, edge.CARD_NUM));
			}
		}		
		this.lastReadCardNum = this.readCardNum;
		if(cardNum == this.stopcardNum){
			Node n = graph.getNodeMap().get(this.stopcardNum);
			this.position.x = n.X;
			this.position.y = n.Y;
			this.state = State.STOP;
		}
		if(trafficControl.isStopToWait(cardNum, false)){
			sendMessageToAGV("CC02DD");
			System.out.println("命令"+this.AGVNUM+"AGV停下来");
		}
	}	
	
	public void stepByStep(){			
		if(!finishEdge&& atEdge != null && (state == State.FORWARD || state == State.BACKWARD)){
			if(atEdge.START_NODE.X == atEdge.END_NODE.X){
				if(atEdge.START_NODE.Y < atEdge.END_NODE.Y ){
					if(this.position.y < atEdge.END_NODE.Y){
						this.position.y +=FORWARDPIX;
					}else{
						finishEdge = true;
					}	
				}else if(atEdge.START_NODE.Y > atEdge.END_NODE.Y ){
					if(this.position.y > atEdge.END_NODE.Y){
						this.position.y -=FORWARDPIX;
					}else{
						finishEdge = true;
					}
				}
			}else if(atEdge.START_NODE.Y == atEdge.END_NODE.Y){
				if(atEdge.START_NODE.X < atEdge.END_NODE.X ){
					if(this.position.x < atEdge.END_NODE.X)
						this.position.x +=FORWARDPIX;
					else
						finishEdge = true;
				}else if(atEdge.START_NODE.X > atEdge.END_NODE.X){
					if(this.position.x > atEdge.END_NODE.X)
						this.position.x -=FORWARDPIX;
					else
						finishEdge = true;
				}
			}
		}
	} 
	
	public void heartBeat(){
		if(this.count_3s == 60){
			this.count_3s = 0;
			if(this.AGVNUM < 16)
				sendMessageToAGV("AA0"+ Integer.toHexString(this.AGVNUM) +"DD");
			else
				sendMessageToAGV("AA" + Integer.toHexString(this.AGVNUM)+"DD");
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
				orientation = Orientation.RIGTH;
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
				e.printStackTrace();
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
		this.stopcardNum = arrayList.get(arrayList.size()-1);
		this.trafficControl.setRouteNodeNumArray(arrayList,this);
		this.isOnduty = true;
	}
	
	public long getLastCommunicationTime(){
		return this.lastCommunicationTime;
	}
	
	public void setlastCommunicationTime(long time){
		this.lastCommunicationTime = time;
	}
	
	public void setCommunicationWithAGVRunnable(CommunicationWithAGVRunnable communicationWithAGVRunnable){
		this.communicationWithAGVRunnable = communicationWithAGVRunnable;
	}
	
	public CommunicationWithAGVRunnable getCommunicationWithAGVRunnable(){
		return this.communicationWithAGVRunnable;
	}
	
	public int getAGVNUM(){
		return this.AGVNUM;
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

	public boolean isOnduty(){
		return this.isOnduty;
	}

	public void setOnduty(boolean f){
		this.isOnduty = f;
	}

	public boolean isOnEntrance(){
		return false;
	}
}
