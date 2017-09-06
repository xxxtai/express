package com.xxxtai.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

@Component
@Scope("prototype")
@Slf4j(topic = "debug")
public class TrafficControlComponent implements TrafficControl{
	private ArrayList<Integer> routeNodeNumArray;
	private Node lastLockedNode;
	private Edge lastLockedEdge;
	private Node lockedNode;
	private Edge lockedEdge;
	@Resource
	private Graph graph;
	private Car car;
	
	public boolean isStopToWait(int cardNum, boolean isStart){
		if(this.routeNodeNumArray == null && this.car == null) {
			return false;
		}
		StringBuilder logMessage = new StringBuilder();
		logMessage.append(this.car.getAGVNum()).append("AGVRecCardNum:").append(cardNum).append(">>>");
		if(this.lastLockedEdge != null && this.lastLockedNode != null
				&& graph.getNodeMap().get(cardNum).FUNCTION.equals("交汇点") && cardNum != this.lastLockedNode.CARD_NUM){

			tryUnlockEdge(logMessage);
		}else if(graph.getNodeMap().get(cardNum).FUNCTION.equals("停车点")){
			if(!isStart){
				this.lastLockedEdge = this.lockedEdge;
				this.lastLockedNode = this.lockedNode;
			}
			
			tryUnlockNode(logMessage);

			Edge onTheEdge = graph.getEdgeMap().get(cardNum);			
			Node nextStartNode = null;
			Node nextEndNode = null;
			Edge nextEdge = null;
			
			for(int i = 0; i < this.routeNodeNumArray.size(); i++){
				if((this.routeNodeNumArray.get(i).equals(onTheEdge.END_NODE.CARD_NUM) && this.routeNodeNumArray.get(i+1).equals(onTheEdge.START_NODE.CARD_NUM))
						||(this.routeNodeNumArray.get(i).equals(onTheEdge.START_NODE.CARD_NUM) && this.routeNodeNumArray.get(i+1).equals(onTheEdge.END_NODE.CARD_NUM))){
					nextStartNode = graph.getNodeMap().get(this.routeNodeNumArray.get(i+1));
					if((i+1) != this.routeNodeNumArray.size()-2){
						nextEndNode = graph.getNodeMap().get(this.routeNodeNumArray.get(i+2));
					}else{
						Edge edgeTmp = graph.getEdgeMap().get(this.routeNodeNumArray.get(this.routeNodeNumArray.size()-1));
						if(edgeTmp.START_NODE.CARD_NUM.equals(nextStartNode.CARD_NUM)) {
							nextEndNode = edgeTmp.END_NODE;
						} else {
							nextEndNode = edgeTmp.START_NODE;
						}
					}
				}
			}
			
			if(nextStartNode != null && nextEndNode != null) {
				for (Edge edge : graph.getEdgeArray()) {
					if ((edge.START_NODE.CARD_NUM.equals(nextStartNode.CARD_NUM)  && edge.END_NODE.CARD_NUM.equals(nextEndNode.CARD_NUM))
							|| (edge.END_NODE.CARD_NUM.equals(nextStartNode.CARD_NUM) && edge.START_NODE.CARD_NUM.equals(nextEndNode.CARD_NUM))) {
						nextEdge = edge;
					}
				}
			} else {
				log.error("");
			}
			if(nextEdge != null){
				this.lockedEdge = nextEdge;
				this.lockedNode = nextStartNode;
				synchronized(this.lockedNode.CARD_NUM){
					synchronized(this.lockedEdge.CARD_NUM){
						if(this.lockedEdge.isLocked()){
							this.lockedEdge.waitQueue.offer(car);
							logMessage.append(car.getAGVNum()).append("AGV等待通过，因为").append(this.lockedEdge.CARD_NUM).append("边被").append(this.lockedEdge.waitQueue.peek().getAGVNum()).append("AGV占用");
							log.info(logMessage.toString());
							return true;
						}else if(this.lockedNode.isLocked()){
							this.lockedEdge.waitQueue.offer(car);
							this.lockedEdge.setLocked();
							this.lockedNode.waitQueue.offer(car);
							logMessage.append(car.getAGVNum()).append("AGV等待通过，并占用").append(this.lockedEdge.CARD_NUM).append("边，但").append(this.lockedNode).append("点被").append(this.lockedNode.waitQueue.peek().getAGVNum()).append("AGV占用");
							System.out.println(logMessage.toString());
							return true;
						}else{
							this.lockedEdge.waitQueue.offer(car);
							this.lockedEdge.setLocked();
							this.lockedNode.waitQueue.offer(car);
							this.lockedNode.setLocked();
							logMessage.append(car.getAGVNum()).append("AGV占用").append(this.lockedEdge.CARD_NUM).append("边和").append(this.lockedNode.CARD_NUM).append("点，并通过");
						}
					}	
				}		
			}
		}		
		log.info(logMessage.toString());
		return false;
	}
	
	private void tryUnlockEdge(StringBuilder logMessage){
		Car myself = this.lastLockedEdge.waitQueue.poll();
		if(myself.getAGVNum() != car.getAGVNum()){
			logMessage.append(car.getAGVNum()).append("AGV通过交汇点准备解除lockedEdge,但是该AGV不在waitqueue顶端");
			log.error(car.getAGVNum() + "AGV通过交汇点准备解除lockedEdge,但是该AGV不在waitqueue顶端");
		}
		
		if(!this.lastLockedEdge.waitQueue.isEmpty()){
			Car carTmp = this.lastLockedEdge.waitQueue.peek();
			synchronized(carTmp.getTrafficControl().getLockedNode().CARD_NUM){
				if(!carTmp.getTrafficControl().getLockedNode().isLocked()){
					carTmp.getTrafficControl().getLockedNode().setLocked();
					carTmp.sendMessageToAGV("CC01DD");
					logMessage.append(car.getAGVNum()).append("AGV解除占用").append(this.lastLockedEdge.CARD_NUM).append("边>>>").append(carTmp.getAGVNum()).append("AGV前进占用").append(this.lastLockedEdge.CARD_NUM).append("边");
				}else{
					logMessage.append(car.getAGVNum()).append("AGV解除占用").append(this.lastLockedEdge.CARD_NUM).append("边>>>").append(carTmp.getAGVNum()).append("AGV继续等待").append(this.lastLockedEdge.CARD_NUM).append("边，因为").append(carTmp.getTrafficControl().getLockedNode().CARD_NUM).append("点被").append(car.getAGVNum()).append("AGV占用！");
				}
				carTmp.getTrafficControl().getLockedNode().waitQueue.offer(carTmp);
			}					
		}else{
			logMessage.append(this.lastLockedEdge.CARD_NUM).append("边完全被解除占用>>>");
			this.lastLockedEdge.unlock();
			this.lastLockedEdge = null;
		}		
		this.lastLockedEdge = this.lockedEdge;
		this.lastLockedNode = this.lockedNode;
	}
	
	private void tryUnlockNode(StringBuilder logMessage){
		if(this.lockedNode != null && this.lockedNode.isLocked()){
			Car myself = this.lockedNode.waitQueue.poll();
			if(myself.getAGVNum() != car.getAGVNum()){
				logMessage.append(car.getAGVNum()).append("AGV通过停止准备解除lockedNode,但是该AGV不在waitqueue顶端");
				log.error(car.getAGVNum() + "AGV通过停止准备解除lockedNode,但是该AGV不在waitqueue顶端");
			}
			if(!this.lockedNode.waitQueue.isEmpty()){
				Car carTmp = this.lockedNode.waitQueue.peek();
				if(carTmp.getTrafficControl().getLockedEdge().waitQueue.peek().getAGVNum() == carTmp.getAGVNum()){
					carTmp.sendMessageToAGV("CC01DD");
					logMessage.append(car.getAGVNum()).append("AGV解除占用").append(this.lockedNode.CARD_NUM).append("点>>>").append(carTmp.getAGVNum()).append("AGV前进占用").append(this.lockedNode.CARD_NUM).append("点");
				}else{
					log.error(carTmp.getAGVNum()+"AGV没有锁住"+carTmp.getTrafficControl().getLockedEdge().CARD_NUM+"边，就想通过！！！！！");
					logMessage.append(carTmp.getAGVNum()).append("AGV没有锁住").append(carTmp.getTrafficControl().getLockedEdge().CARD_NUM).append("边，就想通过！！！！！");
				}
			}else{
				logMessage.append(this.lockedNode.CARD_NUM).append("点完全被解除占用>>>");
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
