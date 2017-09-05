package com.xxxtai.view;


import com.xxxtai.main.Main;
import com.xxxtai.model.Edge;
import com.xxxtai.model.Exit;
import com.xxxtai.model.Graph;
import com.xxxtai.model.Node;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.xxxtai.controller.Dijkstra.MAXINT;


@Component
public class DrawingGui extends JPanel implements Gui{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DrawingGui.class.getName());
	private RoundButton schedulingGuiBtn;
	private RoundButton setingGuiBtn;
	private RoundButton drawingGuiBtn;
	private RoundButton importGraphBtn;
	private MyTextField inputRowField;
	private MyTextField inputColumnField;
	private MyTextField inputRealdisField;
	private RoundButton confirmBtn;
	private RoundButton confirmAddExitBtn;

	@Resource
	private Graph graph;
	@Resource
	private DrawingGraph drawingGraph;
	private Timer timer;
	private boolean isImportGraph;

	public DrawingGui(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);

		drawingGuiBtn = new RoundButton("制图界面");
		drawingGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		drawingGuiBtn.setForeground(new Color(30, 144, 255));
		drawingGuiBtn.setBackground(Color.WHITE);
		
		inputRowField = new MyTextField("        行数");
		inputRowField.setBounds(5*screenSize.width/12, 3*screenSize.height/15, screenSize.width/6, screenSize.height/20);
		inputColumnField = new MyTextField("        列数");
		inputColumnField.setBounds(5*screenSize.width/12, 4*screenSize.height/15, screenSize.width/6, screenSize.height/20);
		inputRealdisField = new MyTextField("        距离");
		inputRealdisField.setBounds(5*screenSize.width/12, 5*screenSize.height/15, screenSize.width/6, screenSize.height/20);
		confirmBtn = new RoundButton("确认");
		confirmBtn.setBounds(5*screenSize.width/12, 6*screenSize.height/15, screenSize.width/6, screenSize.height/20);
		confirmBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				createNewGraph(screenSize);
			}
		});

		importGraphBtn = new RoundButton("导入地图");
		importGraphBtn.setBounds(10*screenSize.width/12, 13*screenSize.height/15, screenSize.width/9, screenSize.height/20);
		importGraphBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				importExistGraph();
			}
		});

		confirmAddExitBtn = new RoundButton("确认添加");
		confirmAddExitBtn.setBounds(10*screenSize.width/12, 14*screenSize.height/15, screenSize.width/9, screenSize.height/20);
		confirmAddExitBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				writeExcel(graph);
			}
		});

		this.setLayout(null);
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(drawingGuiBtn);
		this.add(inputColumnField);
		this.add(inputRealdisField);
		this.add(inputRowField);
		this.add(confirmBtn);
		this.add(importGraphBtn);
		this.add(confirmAddExitBtn);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getButton() == MouseEvent.BUTTON3){
					FileNameDialog dialog = new FileNameDialog("请输入城市名称:");
					dialog.setOnDialogListener(new FileNameDialogListener() {
						@Override
						public void getFileName(String cityName, boolean buttonState) {
							dialog.dispose();
							if (buttonState){
								addExit(e, cityName);
							}
						}
					});

				}

			}
		});
	}
	@Override
	public void paint(Graphics g){
		super.paint(g);
		if(isImportGraph)
			drawingGraph.drawingMap(g);
	}

	public void importExistGraph(){
		isImportGraph = true;
		timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		timer.start();
		this.removeAll();
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(drawingGuiBtn);
		this.add(confirmAddExitBtn);
		this.add(importGraphBtn);
	}

	public void addExit(MouseEvent e , String cityName){
		ArrayList<Node> xNode = new ArrayList<>();
		ArrayList<Node> yNode = new ArrayList<>();
		for (Node node : graph.getNodeArray()){
			if (Math.abs(node.X - e.getX()) < 60){
				xNode.add(node);
			}

			if(Math.abs(node.Y - e.getY()) < 60){
				yNode.add(node);
			}

		}

		Node minxNode = new Node(0,MAXINT,MAXINT,"");
		Node nextxMinNode = new Node(0,MAXINT,MAXINT,"");
		for (Node node : xNode){
			if(Math.abs(node.Y - e.getY()) < Math.abs(minxNode.Y - e.getY())){
				minxNode = node;
			}
		}
		xNode.remove(minxNode);
		for (Node node : xNode){
			if(Math.abs(node.Y - e.getY()) < Math.abs(nextxMinNode.Y - e.getY())){
				nextxMinNode = node;
			}
		}

		Node minyNode = new Node(0,MAXINT,MAXINT,"");
		Node nextyMinNode = new Node(0,MAXINT,MAXINT,"");
		for (Node node : yNode){
			if(Math.abs(node.X - e.getX()) < Math.abs(minyNode.X - e.getX())){
				minyNode = node;
			}
		}
		yNode.remove(minyNode);
		for (Node node : yNode){
			if(Math.abs(node.X - e.getX()) < Math.abs(nextyMinNode.X - e.getX())){
				nextyMinNode = node;
			}
		}
		graph.addExit(new Exit(cityName , Arrays.asList(minxNode, nextxMinNode, minyNode, nextyMinNode)));
	}
	
	public void createNewGraph(Dimension screenSize){

		int row = Integer.valueOf(inputRowField.getText());
		int column = Integer.valueOf(inputColumnField.getText());
		int realdis = Integer.valueOf(inputRealdisField.getText());
		int rlMargin = 100;
		int topMargin = 140;
		int downMargin = 100;
		if(row > 15){
			topMargin = 90;
			downMargin = 50;
		}
			
		if(column > 15)
			rlMargin = 50;
		int width = (screenSize.width - 2*rlMargin)/(column-1);
		int height = (screenSize.height - (topMargin + downMargin))/(row-1);
		if(width < height){
			height = width;
			topMargin = (screenSize.height - height*(row-1))/2;
		}else{
			width = height;
			rlMargin = (screenSize.width - width*(column-1))/2;
		}
			
		int nodeNum = 0;
		int cardNum = row*column;
		graph.getNewGraph();
		for(int i = 0; i < row; i++){
			for(int j = 0; j < column; j++){
				graph.addNode( ++nodeNum, rlMargin + j*width, topMargin + i*height,  "交汇点");
				if(graph.getNodeArraySize() > 1 && (nodeNum - 1)%column != 0){
					graph.addEdge(nodeNum - 1, nodeNum, realdis, ++cardNum);
				}
					
			}
		}
		
		for(int i = 1; i <= column && (i+column <= column*row); i++){
			for(int j = i; j + column <= column*row; j+=column){
				graph.addEdge(j, j+column, realdis, ++cardNum);	
			}			
		}
		
		for(Edge edge : graph.getEdgeArray()){
			graph.addNode(edge.CARD_NUM, (edge.START_NODE.X+edge.END_NODE.X)/2, (edge.START_NODE.Y+edge.END_NODE.Y)/2, "停车点");
		}
		
		try{
			writeExcel(graph);
			System.out.println("newing........");
			if(graph.getNodeArraySize() == (column*row + graph.getEdgeArraySize()) && graph.getEdgeArraySize() == ((column-1)*row+(row-1)*column))
				System.out.println("new graph success!");
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex);
		}
	
	}
	
	
	public void getGuiInstance(Main main, SchedulingGui schedulingGui, SettingGui settingGui, DrawingGui drawingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(schedulingGui);
				main.repaint();
				main.validate();
			}
		});
		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(settingGui);
				main.repaint();
				main.validate();
			}
		});
		drawingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(drawingGui);
				main.repaint();
				main.validate();
			}
		});		
	}
	
	
	public static void writeExcel(Graph graph){		
		try{
			File file = new File("C:\\Users\\xxxta\\work\\Graph.xls");
			InputStream inputStream = new FileInputStream(file.getPath());
			Workbook wb = Workbook.getWorkbook(inputStream);
			WritableWorkbook wwb = Workbook.createWorkbook(new File("C:\\Users\\xxxta\\work\\Graph.xls"), wb);
			wwb.removeSheet(0);
			WritableSheet wsNode = wwb.createSheet("nodes", 0);
			int i = 0;
			for(Node node : graph.getNodeArray()){				
				Number numberNum = new Number(0, i, node.CARD_NUM);
				Number numberX = new Number(1, i, node.X);
				Number numberY = new Number(2, i, node.Y);
				Label functionString = new Label(3, i, node.FUNCTION);
				wsNode.addCell(numberX);
				wsNode.addCell(numberY);
				wsNode.addCell(numberNum);
				wsNode.addCell(functionString);
				i++;
			}
			wwb.removeSheet(1);
			WritableSheet wsEdge = wwb.createSheet("edges", 1);
			i = 0;
			for(Edge edge: graph.getEdgeArray()){
				Number numberStrNode = new Number(0, i, edge.START_NODE.CARD_NUM);
				Number numberEndNode = new Number(1, i, edge.END_NODE.CARD_NUM);
				Number numberDis = new Number(2, i, edge.REAL_DISTANCE);
				Number cardNum = new Number(3, i, edge.CARD_NUM);
				wsEdge.addCell(numberStrNode);
				wsEdge.addCell(numberEndNode);
				wsEdge.addCell(numberDis);
				wsEdge.addCell(cardNum);
				i++;
			}

			wwb.removeSheet(2);
			WritableSheet wsExits = wwb.createSheet("exits", 2);
			i = 0;
			for(java.util.List<Exit> list: graph.getExitList()){
				for (Exit exit: list){
					Label name = new Label(0, i, exit.NAME);
					Number x = new Number(1, i, exit.X);
					Number y = new Number(2, i, exit.Y);
					wsExits.addCell(name);
					wsExits.addCell(x);
					wsExits.addCell(y);
					i++;
				}
			}
			wwb.write();
			wwb.close();
			wb.close();

			System.out.println("write success");
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
	} 

}
