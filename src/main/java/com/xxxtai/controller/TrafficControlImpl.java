package com.xxxtai.controller;

import com.xxxtai.model.Car;
import com.xxxtai.model.Edge;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Node;
import com.xxxtai.toolKit.NodeFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Scope("prototype")
@Slf4j(topic = "debug")
public class TrafficControlImpl implements TrafficControl {
	private List<Integer> routeNodeNumArray;
	private Node lastLockedNode;
	private Edge lastLockedEdge;
	private Node lockedNode;
	private Edge lockedEdge;
	@Resource
	private Graph graph;
	private Car car;
	
	public boolean isStopToWait(int cardNum, boolean isStart){
		if(this.routeNodeNumArray == null) {
			log.warn("{}AGV receive cardNum:{} but routeNodeNumArray:{} lastLockedEdge:{} lastLockedNode:{}",
					car.getAGVNum(), cardNum, routeNodeNumArray, lastLockedEdge, lastLockedNode);
			return false;
		}

		if (lastLockedNode != null && cardNum == lastLockedNode.cardNum) {
			log.warn(this.car.getAGVNum() + "AGV receive cardNum " + cardNum + "but duplicate");
			return false;
		}

		if(lastLockedEdge != null && NodeFunction.Junction.equals(graph.getNodeMap().get(cardNum).getFunction())){
			tryUnlockEdge(cardNum);
			return false;
		}else if(NodeFunction.Parking.equals(graph.getNodeMap().get(cardNum).getFunction())) {
			if (!isStart) {
				this.lastLockedEdge = this.lockedEdge;
				this.lastLockedNode = this.lockedNode;
			}

			tryUnlockNode(cardNum);
			return tryLockNodeEdge(cardNum);
		} else {
			log.error("不应该出现的情况! AGVNum:" + this.car.getAGVNum() + " cardNum:" + cardNum
					+ " lastLockedEdge:" + lastLockedEdge
					+ " lastLockedNode:" + lastLockedNode);
			return false;
		}
	}

	private boolean tryLockNodeEdge(int cardNum){
		Edge onTheEdge = graph.getEdgeMap().get(cardNum);
		Node nextStartNode = null;
		Node nextEndNode = null;
		Edge nextEdge = null;

		for(int i = 0; i < this.routeNodeNumArray.size(); i++){
			if((this.routeNodeNumArray.get(i).equals(onTheEdge.END_NODE.cardNum) && this.routeNodeNumArray.get(i+1).equals(onTheEdge.START_NODE.cardNum))
					||(this.routeNodeNumArray.get(i).equals(onTheEdge.START_NODE.cardNum) && this.routeNodeNumArray.get(i+1).equals(onTheEdge.END_NODE.cardNum))){
				nextStartNode = graph.getNodeMap().get(this.routeNodeNumArray.get(i+1));
				if((i+1) != this.routeNodeNumArray.size()-2){
					nextEndNode = graph.getNodeMap().get(this.routeNodeNumArray.get(i+2));
				}else{
					Edge edgeTmp = graph.getEdgeMap().get(this.routeNodeNumArray.get(this.routeNodeNumArray.size()-1));
					if(edgeTmp.START_NODE.cardNum.equals(nextStartNode.cardNum)) {
						nextEndNode = edgeTmp.END_NODE;
					} else {
						nextEndNode = edgeTmp.START_NODE;
					}
				}
			}
		}

		if(nextStartNode != null && nextEndNode != null) {
			for (Edge edge : graph.getEdgeArray()) {
				if ((edge.START_NODE.cardNum.equals(nextStartNode.cardNum)  && edge.END_NODE.cardNum.equals(nextEndNode.cardNum))
						|| (edge.END_NODE.cardNum.equals(nextStartNode.cardNum) && edge.START_NODE.cardNum.equals(nextEndNode.cardNum))) {
					nextEdge = edge;
				}
			}
		} else {
			log.warn("不应该出现的情况！");
		}
		if(nextEdge != null){
			this.lockedEdge = nextEdge;
			this.lockedNode = nextStartNode;
			synchronized(this.lockedNode.cardNum){
				synchronized(this.lockedEdge.CARD_NUM){
					if(this.lockedEdge.isLocked()){
						this.lockedEdge.waitQueue.offer(car);
						log.info("{}AGV receive cardNum:{} >>> 等待通过，因为{}边被{}AGV占用",
								car.getAGVNum(), cardNum, lockedEdge.CARD_NUM, lockedEdge.waitQueue.peek().getAGVNum());
						return true;
					}else if(this.lockedNode.isLocked()){
						this.lockedEdge.waitQueue.offer(car);
						this.lockedEdge.setLocked();
						this.lockedNode.waitQueue.offer(car);
						log.info("{}AGV receive cardNum:{} >>> 占用{}边,等待通过，因为{}点被{}AGV占用",
								car.getAGVNum(), cardNum, lockedEdge.CARD_NUM, lockedNode.getCardNum(), lockedNode.waitQueue.peek().getAGVNum());
						return true;
					}else{
						this.lockedEdge.waitQueue.offer(car);
						this.lockedEdge.setLocked();
						this.lockedNode.waitQueue.offer(car);
						this.lockedNode.setLocked();
						log.info("{}AGV receive cardNum:{} >>> 占用{}边和{}点，并通过",
								car.getAGVNum(), cardNum, this.lockedEdge.CARD_NUM, this.lockedNode.cardNum);
					}
				}
			}
		}
		return false;
	}
	
	private void tryUnlockEdge(int cardNum){
		Car myself = this.lastLockedEdge.waitQueue.poll();
		if(myself.getAGVNum() != car.getAGVNum()){
			log.error(" 不应该出现的情况！ " + car.getAGVNum() + "AGV通过交汇点准备解除lockedEdge,但是该AGV不在waitQueue顶端");
		}
		
		if(!this.lastLockedEdge.waitQueue.isEmpty()){
			Car carTmp = this.lastLockedEdge.waitQueue.peek();
			synchronized(carTmp.getTrafficControl().getLockedNode().cardNum){
				if(!carTmp.getTrafficControl().getLockedNode().isLocked()){
					carTmp.getTrafficControl().getLockedNode().setLocked();
					carTmp.sendMessageToAGV("CC01DD");
					log.info("{}AGV receive cardNum:{} >>> 解除占用{}边 >>> {}AGV前进占用{}边",
							car.getAGVNum(), cardNum, this.lastLockedEdge.CARD_NUM, carTmp.getAGVNum(), lastLockedEdge.CARD_NUM);
				}else{
					log.info("{}AGV receive cardNum:{} >>> 解除占用{}边 >>> {}AGV继续等待{}边，因为{}点被{}AGV占用！",
							car.getAGVNum(), cardNum, lastLockedEdge.CARD_NUM, carTmp.getAGVNum(), lastLockedEdge.CARD_NUM,
							carTmp.getTrafficControl().getLockedNode().cardNum,
							carTmp.getTrafficControl().getLockedNode().waitQueue.peek().getAGVNum());
				}
				carTmp.getTrafficControl().getLockedNode().waitQueue.offer(carTmp);
			}
		}else{
			log.info("{}AGV receive cardNum:{} >>> {}边完全被解除占用",car.getAGVNum(), cardNum, lastLockedEdge.CARD_NUM);
			this.lastLockedEdge.unlock();
		}		
		this.lastLockedEdge = this.lockedEdge;
		this.lastLockedNode = this.lockedNode;
	}
	
	private void tryUnlockNode(int cardNum){
		if(this.lockedNode != null && this.lockedNode.isLocked()){
			Car myself = this.lockedNode.waitQueue.poll();
			if(myself.getAGVNum() != car.getAGVNum()){
				log.error("不应该出现的情况！ {}AGV通过停车点准备解除lockedNode,但是该AGV不在waitQueue顶端", car.getAGVNum());
			}
			if(!this.lockedNode.waitQueue.isEmpty()){
				Car carTmp = this.lockedNode.waitQueue.peek();
				if(carTmp.getTrafficControl().getLockedEdge().isLocked() &&
						carTmp.getTrafficControl().getLockedEdge().waitQueue.peek().getAGVNum() == carTmp.getAGVNum()){
					carTmp.sendMessageToAGV("CC01DD");
					log.info("{}AGV receive cardNum:{} >>> 解除占用{}点 >>> {}AGV前进占用 >>>", car.getAGVNum(), cardNum, this.lockedNode.cardNum, carTmp.getAGVNum());
				}else{
					log.error("不应该出现的情况！ {}AGV没有锁住{}边，就想通过！", carTmp.getAGVNum(),carTmp.getTrafficControl().getLockedEdge().CARD_NUM);
				}
			}else{
				log.info("{}AGV receive cardNum:{} >>> {}点完全被解除占用 >>>",car.getAGVNum(), cardNum, this.lockedNode.cardNum);
				this.lockedNode.unlock();
				this.lockedNode = null;
			}
		}
	}
	
	public void setRouteNodeNumArray(List<Integer> routeNodeNumArray){
		this.routeNodeNumArray = routeNodeNumArray;
		if(isStopToWait(car.getReadCardNum(), true)){
			car.sendMessageToAGV("CC02DD");
			log.info("命令"+car.getAGVNum()+"AGV停下来");
		}
	}
	
	public Node getLockedNode(){
		return this.lockedNode;
	}
	
	public Edge getLockedEdge(){
		return this.lastLockedEdge;
	}

	@Override
	public void setCar(Car car) {
		this.car = car;
	}
}
