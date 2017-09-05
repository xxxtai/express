package com.xxxtai.model;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

@Component
@Scope("prototype")
public class TrafficControlComponent implements TrafficControl{
	private static Logger logger = Logger.getLogger(TrafficControlComponent.class.getName());
	private ArrayList<Integer> routeNodeNumArray;
	private Node lastLockedNode;
	private Edge lastLockedEdge;
	private Node lockedNode;
	private Edge lockedEdge;
	@Resource
	private Graph graph;
	private Car car;
	
	public boolean isStopToWait(int cardNum, boolean isStart){
		if(this.routeNodeNumArray == null && this.car == null)
			return false;
				
		StringBuffer logMessage = new StringBuffer();
		logMessage.append(this.car.getAGVNum() + "AGVRecCardNum:" + cardNum +">>>");
		if(this.lastLockedEdge != null && this.lastLockedNode != null
				&& graph.getNodeMap().get(cardNum).FUNCTION.equals("交汇点")
				&& cardNum != this.lastLockedNode.CARD_NUM){
			
			tryUnlockEdge(logMessage);
			
		}else if(graph.getNodeMap().get(cardNum).FUNCTION.equals("停车点")){
			if(!isStart){
				this.lastLockedEdge = this.lockedEdge;
				this.lastLockedNode = this.lockedNode;
			}			
			
			tryUnlockNode(logMessage);			
				
			Edge onTheEdge = graph.getEdgeMap().get(cardNum);			
			Node strNode = null;
			Node endNode = null;
			Edge nextEdge = null;
			
			for(int i = 0; i < this.routeNodeNumArray.size(); i++){
				if((this.routeNodeNumArray.get(i) == onTheEdge.END_NODE.CARD_NUM && this.routeNodeNumArray.get(i+1) == onTheEdge.START_NODE.CARD_NUM)
						||(this.routeNodeNumArray.get(i) == onTheEdge.START_NODE.CARD_NUM && this.routeNodeNumArray.get(i+1) == onTheEdge.END_NODE.CARD_NUM)){
					strNode = graph.getNodeMap().get(this.routeNodeNumArray.get(i+1));					
					if((i+1) != this.routeNodeNumArray.size()-2){
						endNode = graph.getNodeMap().get(this.routeNodeNumArray.get(i+2));
					}else{
						Edge edgeTmp = graph.getEdgeMap().get(this.routeNodeNumArray.get(this.routeNodeNumArray.size()-1));
						if(edgeTmp.START_NODE.CARD_NUM == strNode.CARD_NUM)
							endNode = edgeTmp.END_NODE;
						else
							endNode = edgeTmp.START_NODE;
					}					
				}
			}
			
			if(strNode != null && endNode != null)
				for(Edge e: graph.getEdgeArray())
					if((e.START_NODE.CARD_NUM == strNode.CARD_NUM && e.END_NODE.CARD_NUM == endNode.CARD_NUM)
						||(e.END_NODE.CARD_NUM == strNode.CARD_NUM && e.START_NODE.CARD_NUM == endNode.CARD_NUM))
						nextEdge = e;				
			
			if( nextEdge != null){
				this.lockedEdge = nextEdge;
				this.lockedNode = strNode;				
				synchronized(this.lockedNode){
					synchronized(this.lockedEdge){
						if(this.lockedEdge.isLocked()){
							this.lockedEdge.waitQueue.offer(car);
							logMessage.append(car.getAGVNum()+"AGV等待通过，因为"+this.lockedEdge.CARD_NUM
									+"边被"+this.lockedEdge.waitQueue.peek().getAGVNum()+"AGV占用");
							System.out.println(logMessage.toString());
							return true;
						}else if(this.lockedNode.isLocked()){
							this.lockedEdge.waitQueue.offer(car);
							this.lockedEdge.setLocked();
							this.lockedNode.waitQueue.offer(car);
							logMessage.append(car.getAGVNum()+"AGV等待通过，并占用"+this.lockedEdge.CARD_NUM
									+"边，但"+this.lockedNode+"点被"+this.lockedNode.waitQueue.peek().getAGVNum()+"AGV占用");
							System.out.println(logMessage.toString());
							return true;
						}else{
							this.lockedEdge.waitQueue.offer(car);
							this.lockedEdge.setLocked();
							this.lockedNode.waitQueue.offer(car);
							this.lockedNode.setLocked();
							logMessage.append(car.getAGVNum()+"AGV占用"+this.lockedEdge.CARD_NUM
									+"边和"+this.lockedNode.CARD_NUM+"点，并通过");
						}
					}	
				}		
			}
		}		
		System.out.println(logMessage.toString());
		return false;
	}
	
	private void tryUnlockEdge(StringBuffer logMessage){	
		Car myself = this.lastLockedEdge.waitQueue.poll();
		if(myself.getAGVNum() != car.getAGVNum()){
			logMessage.append(car.getAGVNum() + "AGV通过交汇点准备解除lockedEdge,但是该AGV不在waitqueue顶端");
			logger.error(car.getAGVNum() + "AGV通过交汇点准备解除lockedEdge,但是该AGV不在waitqueue顶端");
		}
		
		if(!this.lastLockedEdge.waitQueue.isEmpty()){		
			Car carTmp = this.lastLockedEdge.waitQueue.peek();
			synchronized(carTmp.getTrafficControl().getLockedNode()){
				if(!carTmp.getTrafficControl().getLockedNode().isLocked()){
					carTmp.getTrafficControl().getLockedNode().setLocked();						
					carTmp.sendMessageToAGV("CC01DD");;//go!
					logMessage.append(car.getAGVNum()+"AGV解除占用"+this.lastLockedEdge.CARD_NUM+"边>>>"+
							carTmp.getAGVNum()+"AGV前进占用"+this.lastLockedEdge.CARD_NUM+"边");
				}else{
					logMessage.append(car.getAGVNum()+"AGV解除占用"+this.lastLockedEdge.CARD_NUM+"边>>>"+
							carTmp.getAGVNum()+"AGV继续等待"+this.lastLockedEdge.CARD_NUM
							+"边，因为"+carTmp.getTrafficControl().getLockedNode().CARD_NUM+"点被"+car.getAGVNum()+"AGV占用！");
				}
				carTmp.getTrafficControl().getLockedNode().waitQueue.offer(carTmp);
			}					
		}else{
			logMessage.append(this.lastLockedEdge.CARD_NUM+"边完全被解除占用>>>");
			this.lastLockedEdge.unlock();
			this.lastLockedEdge = null;
		}		
		this.lastLockedEdge = this.lockedEdge;
		this.lastLockedNode = this.lockedNode;		
	}
	
	private void tryUnlockNode(StringBuffer logMessage){		
		if(this.lockedNode != null && this.lockedNode.isLocked()){
			Car myself = this.lockedNode.waitQueue.poll();
			if(myself.getAGVNum() != car.getAGVNum()){
				logMessage.append(car.getAGVNum() + "AGV通过停止准备解除lockedNode,但是该AGV不在waitqueue顶端");
				logger.error(car.getAGVNum() + "AGV通过停止准备解除lockedNode,但是该AGV不在waitqueue顶端");
			}
			if(!this.lockedNode.waitQueue.isEmpty()){
				Car carTmp = this.lockedNode.waitQueue.peek();
				if(carTmp.getTrafficControl().getLockedEdge().waitQueue.peek().getAGVNum() == carTmp.getAGVNum()){
					carTmp.sendMessageToAGV("CC01DD");;//go!
					logMessage.append(car.getAGVNum()+"AGV解除占用"+this.lockedNode.CARD_NUM+"点>>>"
							+carTmp.getAGVNum()+"AGV前进占用"+this.lockedNode.CARD_NUM+"点");
				}else{
					logger.error(carTmp.getAGVNum()+"AGV没有锁住"+carTmp.getTrafficControl().getLockedEdge().CARD_NUM+"边，就想通过！！！！！");
					logMessage.append(carTmp.getAGVNum()+"AGV没有锁住"+carTmp.getTrafficControl().getLockedEdge().CARD_NUM+"边，就想通过！！！！！");
				}
			}else{
				logMessage.append(this.lockedNode.CARD_NUM+"点完全被解除占用>>>");
				this.lockedNode.unlock();
				this.lockedNode = null;
			}
		}		
	}
	
	public void setRouteNodeNumArray(ArrayList<Integer> routeNodeNumArray, Car car){
		this.routeNodeNumArray = routeNodeNumArray;
		this.car = car;
		if(isStopToWait(car.getReadCardNum(), true)){
			car.sendMessageToAGV("CC02DD");
			System.out.println("命令"+car.getAGVNum()+"AGV停下来");
		}
		
	}
	
	public Node getLockedNode(){
		return this.lockedNode;
	}
	
	public Edge getLockedEdge(){
		return this.lastLockedEdge;
	}
	
	
}
