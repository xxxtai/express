package com.xxxtai.controller;

import com.xxxtai.model.Edge;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
public class Dijkstra implements Algorithm{
	public final static int MAXINT = 655535;
	private int[][] graphArray; 
	@Autowired
	Graph graph;
	private int size;
	private ArrayList<Path> sArray;
	private ArrayList<Path> uArray;

	@PostConstruct
	public void init(){
		size = graph.getNodeArraySize();
		graphArray = new int[size][size];
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				graphArray[i][j] = MAXINT;
			}
		}
		
		for(Edge edge : graph.getEdgeArray()){
			graphArray[edge.START_NODE.CARD_NUM-1][edge.END_NODE.CARD_NUM-1]=edge.REAL_DISTANCE;
		}	
	}
	
	public synchronized Path findRoute(Edge startEdge, final int END_NODE_CARD_NUM, boolean isBackToEntrance){
		Edge removeEdge = changeEdgeArray(END_NODE_CARD_NUM, isBackToEntrance );

		boolean adjoin = false;
		sArray = new ArrayList();
		sArray.add(new Path(startEdge.END_NODE.CARD_NUM, startEdge.END_NODE.CARD_NUM));
		uArray = new ArrayList();
		for(int i = 0; i < size; i++){//初始化
			if(startEdge.START_NODE.CARD_NUM == i+1){
				uArray.add(new Path(startEdge.END_NODE.CARD_NUM, i + 1));
				uArray.get(i).setRealDis(MAXINT);
				continue;
			}
				
			uArray.add(new Path(startEdge.END_NODE.CARD_NUM, i + 1));
			for(Edge edge : graph.getEdgeArray()){
				if((edge.START_NODE.CARD_NUM == startEdge.END_NODE.CARD_NUM && edge.END_NODE.CARD_NUM == (i+1))
						||(edge.END_NODE.CARD_NUM == startEdge.END_NODE.CARD_NUM && edge.START_NODE.CARD_NUM == (i+1) )){//|| (graph.getEdge(j).endNode.CARD_NUM == startNode && graph.getEdge(j).startNode.CARD_NUM == (i+1) && graph.getEdge(j).twoWay)
					if(!edge.isLocked() && !edge.isRemove()){//当边和点被占用或被移除后，认为不联通
						uArray.get(i).setRealDis(edge.REAL_DISTANCE);
						uArray.get(i).addRouteNode(i+1);
						adjoin = true;
					}
				}
			}
			if(!adjoin)
				uArray.get(i).setRealDis(MAXINT);
			adjoin = false;
		}

		uArray.get(startEdge.END_NODE.CARD_NUM - 1).setRemove();
		int removedCount = 1;
		
		while(uArray.size() != removedCount){//
			int tempMin = MAXINT;
			int indexMin = 0;
			for(int i = 0; i < uArray.size(); i++){//取u中权值最小的点放进s中
				if(!uArray.get(i).getRemove()){
					if(uArray.get(i).getRealDis() < tempMin){
						tempMin = uArray.get(i).getRealDis();
						indexMin = i;
					}
				}
			}
			
			sArray.add(uArray.get(indexMin));
			uArray.get(indexMin).setRemove();
			removedCount++;
			
			int tempStart = sArray.get(sArray.size()-1).END_NODE_NUM;
			for(int i = 0; i < size; i++){
				for(Edge edge : graph.getEdgeArray()){
					if((edge.START_NODE.CARD_NUM == tempStart && edge.END_NODE.CARD_NUM == (i+1))
							||(edge.END_NODE.CARD_NUM == tempStart && edge.START_NODE.CARD_NUM == (i+1) )){//|| (graph.getEdge(j).endNode.CARD_NUM == tempStart && graph.getEdge(j).startNode.CARD_NUM == (i+1) && graph.getEdge(j).twoWay)
						if(edge.REAL_DISTANCE + sArray.get(sArray.size() - 1).getRealDis() < uArray.get(i).getRealDis()
								&& !edge.isRemove() && !edge.isLocked()){//当边和点被占用或被移除后，认为不联通
							uArray.get(i).setRealDis(edge.REAL_DISTANCE + sArray.get(sArray.size() - 1).getRealDis());
							uArray.get(i).newRoute(sArray.get(sArray.size()-1).getRoute());
							uArray.get(i).addRouteNode(i+1);							
						}
					}
				}
			}	
		}//end while
		Path returnPath = null;
		for(int i = 0; i < sArray.size(); i++){
			if(sArray.get(i).END_NODE_NUM == END_NODE_CARD_NUM){
				returnPath = sArray.get(i);
				if(true){//!startEdge.END_NODE.functionNode
					ArrayList<Integer> tempArray = new ArrayList<Integer>(returnPath.getRoute());
					returnPath.getRoute().clear();
					returnPath.getRoute().add(startEdge.START_NODE.CARD_NUM);
					for(int j = 0; j < tempArray.size(); j++){
						returnPath.getRoute().add(tempArray.get(j));
					}
					
				}
					
			}
		}
		if(removeEdge != null)
			recoverEdgeArray(removeEdge);
		return returnPath;
	}//end countPath
	
	
	
	public Edge changeEdgeArray(int endNodeCARD_NUM, boolean isBackToEntrance){
		Edge removeEdge = null;
		if(!graph.getNodeMap().get(endNodeCARD_NUM).FUNCTION.equals("交汇点")){
			Edge edge = graph.getEdgeMap().get(graph.getNodeMap().get(endNodeCARD_NUM).CARD_NUM);
			edge.setRemoved();
			removeEdge = edge;
			if(isBackToEntrance){
				if (graph.getNodeMap().get(endNodeCARD_NUM).X < edge.START_NODE.X )
					graph.addEdge(edge.START_NODE.CARD_NUM, endNodeCARD_NUM, edge.REAL_DISTANCE/2, 0);
				else
					graph.addEdge(endNodeCARD_NUM, edge.END_NODE.CARD_NUM, edge.REAL_DISTANCE/2, -1);
			}else{
				graph.addEdge(edge.START_NODE.CARD_NUM, endNodeCARD_NUM, edge.REAL_DISTANCE/2, 0);
				graph.addEdge(endNodeCARD_NUM, edge.END_NODE.CARD_NUM, edge.REAL_DISTANCE/2, -1);
			}

		}
		return removeEdge;
	}
	
	public void recoverEdgeArray(Edge removeEdge){
		removeEdge.cannelRemove();
		graph.getEdgeMap().remove(0);
		graph.getEdgeMap().remove(-1);
	}
}
