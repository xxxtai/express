package com.xxxtai.model;

import com.xxxtai.controller.Communication;
import com.xxxtai.controller.TrafficControl;
import com.xxxtai.toolKit.Orientation;
import com.xxxtai.toolKit.State;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.List;
import javax.annotation.Resource;
import java.net.SocketException;

@Slf4j(topic = "debug")
public class AGVCar implements Car{
	@Resource
	private Graph graph;
	private Communication communication;
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
		trafficControl.setCar(this);
	}
	
	public void setReceiveCardNum(int cardNum){
		this.readCardNum = cardNum;
		Edge edge = null;
		Node node = graph.getNodeMap().get(this.lastReadCardNum);
		if(node != null){
			edge = graph.getEdgeMap().get(cardNum);
		}
		
		if(node != null && edge != null){
			if(edge.START_NODE.cardNum.equals(node.cardNum)){
				setAtEdge(edge);
			}else if(edge.END_NODE.cardNum.equals(node.cardNum)){
				setAtEdge(new Edge(edge.END_NODE, edge.START_NODE, edge.REAL_DISTANCE, edge.CARD_NUM));
			}
		}
		this.lastReadCardNum = this.readCardNum;
		if(cardNum == this.stopCardNum){
			Node n = graph.getNodeMap().get(this.stopCardNum);
			this.position.x = n.x;
			this.position.y = n.y;
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
			if(atEdge.START_NODE.x == atEdge.END_NODE.x){
				if(atEdge.START_NODE.y < atEdge.END_NODE.y){
					if(this.position.y < atEdge.END_NODE.y){
						this.position.y += FORWARD_PIX;
					}else{
						finishEdge = true;
					}	
				}else if(atEdge.START_NODE.y > atEdge.END_NODE.y){
					if(this.position.y > atEdge.END_NODE.y){
						this.position.y -= FORWARD_PIX;
					}else{
						finishEdge = true;
					}
				}
			}else if(atEdge.START_NODE.y == atEdge.END_NODE.y){
				if(atEdge.START_NODE.x < atEdge.END_NODE.x){
					if(this.position.x < atEdge.END_NODE.x)
						this.position.x += FORWARD_PIX;
					else
						finishEdge = true;
				}else if(atEdge.START_NODE.x > atEdge.END_NODE.x){
					if(this.position.x > atEdge.END_NODE.x)
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
		this.position.x = this.atEdge.START_NODE.x;
		this.position.y = this.atEdge.START_NODE.y;
		this.finishEdge = false;
		this.state = State.FORWARD;
		judgeOrientation();
	}
	
	public void judgeOrientation(){
		if(atEdge.START_NODE.x == atEdge.END_NODE.x){
			if(atEdge.START_NODE.y < atEdge.END_NODE.y){
				orientation = Orientation.DOWN;
			}else{
				orientation = Orientation.UP;
			} 	
		}else if(atEdge.START_NODE.y == atEdge.END_NODE.y){
			if(atEdge.START_NODE.x < atEdge.END_NODE.x){
				orientation = Orientation.RIGHT;
			}else{
				orientation = Orientation.LEFT;
			} 				
		}
	}
	
	public void sendMessageToAGV(String message){
		if(this.communication != null){
			try {
				this.communication.write(message);
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
			this.position.x = n.x;
			this.position.y = n.y;
			this.state = State.STOP;
		}
	}
	
	public void setRouteNodeNumArray(List<Integer> arrayList){
		this.stopCardNum = arrayList.get(arrayList.size()-1);
		this.trafficControl.setRouteNodeNumArray(arrayList);
		this.onDuty = true;
	}
	
	public long getLastCommunicationTime(){
		return this.lastCommunicationTime;
	}
	
	public void setLastCommunicationTime(long time){
		this.lastCommunicationTime = time;
	}
	
	public void setCommunication(Communication communication){
		this.communication = communication;
	}
	
	public Communication getCommunication(){
		return this.communication;
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
