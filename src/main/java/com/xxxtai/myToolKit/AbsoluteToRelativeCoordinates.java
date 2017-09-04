package com.xxxtai.myToolKit;

import com.xxxtai.model.Edge;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Node;
import com.xxxtai.model.Path;

import java.util.ArrayList;

public class AbsoluteToRelativeCoordinates {
	private AbsoluteToRelativeCoordinates(){}
	
	public static String convert(Graph graph, Path path){
		ArrayList<Integer> route = path.getRoute();
		StringBuffer buffer = new StringBuffer();
		buffer.append("AA");
		
		for(int i = 0; i+2 < route.size(); i++){
			if(graph.getNodeMap().get(route.get(i)).X == graph.getNodeMap().get(route.get(i+1)).X){
				if(graph.getNodeMap().get(route.get(i)).Y < graph.getNodeMap().get(route.get(i+1)).Y){//down
					//System.out.print("方向下/");
					if(graph.getNodeMap().get(route.get(i+2)).X > graph.getNodeMap().get(route.get(i+1)).X){
						//左1
						System.out.print(route.get(i+1) + "的命令左");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 1));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 1));
						buffer.append("FF");
					}else if(graph.getNodeMap().get(route.get(i+2)).X == graph.getNodeMap().get(route.get(i+1)).X){
						//前3
						System.out.print(route.get(i+1) + "的命令前/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 3));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 3));
						buffer.append("FF");
					}else{
						//右2
						System.out.print(route.get(i+1) + "的命令右/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 2));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 2));
						buffer.append("FF");
					}
				}else if(graph.getNodeMap().get(route.get(i)).Y > graph.getNodeMap().get(route.get(i+1)).Y){//up
					//System.out.print("方向上/");
					if(graph.getNodeMap().get(route.get(i+2)).X > graph.getNodeMap().get(route.get(i+1)).X){
						//右
						System.out.print(route.get(i+1) + "的命令右/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 2));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 2));
						buffer.append("FF");
					}else if(graph.getNodeMap().get(route.get(i+2)).X == graph.getNodeMap().get(route.get(i+1)).X){
						//前
						System.out.print(route.get(i+1) + "的命令前/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 3));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 3));
						buffer.append("FF");
					}else{
						//左
						System.out.print(route.get(i+1) + "的命令左/");
					//	System.out.println(commondString(graph, route.get(i), route.get(i+1), 1));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 1));
						buffer.append("FF");
					}
				}
			}else if(graph.getNodeMap().get(route.get(i)).Y == graph.getNodeMap().get(route.get(i+1)).Y){//right and left
				if(graph.getNodeMap().get(route.get(i)).X < graph.getNodeMap().get(route.get(i+1)).X){//right
					//System.out.print("方向右/");
					if(graph.getNodeMap().get(route.get(i+2)).Y > graph.getNodeMap().get(route.get(i+1)).Y){
						//右
						System.out.print(route.get(i+1) + "的命令右/");
					//	System.out.println(commondString(graph, route.get(i), route.get(i+1), 2));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 2));
						buffer.append("FF");
					}else if(graph.getNodeMap().get(route.get(i+2)).Y == graph.getNodeMap().get(route.get(i+1)).Y){
						//前
						System.out.print(route.get(i+1) + "的命令前/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 3));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 3));
						buffer.append("FF");
					}else {
						//左
						System.out.print(route.get(i+1) + "的命令左/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 1));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 1));
						buffer.append("FF");
					}
				}else if(graph.getNodeMap().get(route.get(i)).X > graph.getNodeMap().get(route.get(i+1)).X){//leftleftleftleftleftleft
					//System.out.print("方向左/");
					if(graph.getNodeMap().get(route.get(i+2)).Y > graph.getNodeMap().get(route.get(i+1)).Y){
						//左
						System.out.print(route.get(i+1) + "的命令左/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 1));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 1));
						buffer.append("FF");
					}else if(graph.getNodeMap().get(route.get(i+2)).Y == graph.getNodeMap().get(route.get(i+1)).Y){
						//前
						System.out.print(route.get(i+1) + "的命令前/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 3));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 3));
						buffer.append("FF");
					}else {
						//右
						System.out.print(route.get(i+1) + "的命令右/");
						//System.out.println(commondString(graph, route.get(i), route.get(i+1), 2));
						buffer.append(commondString(graph, route.get(i), route.get(i+1), 2));
						buffer.append("FF");
					}
				}
			}
		}		
		
		
		for(Node node: graph.getNodeArray()){
			if(node.CARD_NUM == path.END_NODE_NUM){
				path.setStopNodeNum(node.CARD_NUM);
			}
		}
		
		if(path.getStopNodeNum() < 16){
			buffer.append("0");
			buffer.append(Integer.toHexString(path.getStopNodeNum()));
		}else{
			buffer.append(Integer.toHexString(path.getStopNodeNum()));
		}
		buffer.append("BB");
		
		return buffer.toString();
	}
	
	public static String commondString(Graph graph, int s, int e, int commond){
		String reString = "";
		for(Edge edge : graph.getEdgeArray()){
			if((edge.START_NODE.CARD_NUM == s && edge.END_NODE.CARD_NUM == e)|| (edge.END_NODE.CARD_NUM == s && edge.START_NODE.CARD_NUM == e)){
				if(edge.CARD_NUM < 16)
					reString = String.valueOf(0) + Integer.toHexString(edge.CARD_NUM);
				else
					reString += Integer.toHexString(edge.CARD_NUM);
			}
		}
		reString+=String.valueOf(0);
		return reString+=String.valueOf(commond);
	}
}
