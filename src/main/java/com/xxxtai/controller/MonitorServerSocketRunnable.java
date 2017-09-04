package com.xxxtai.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class MonitorServerSocketRunnable implements Runnable{	
	private Logger logger = LoggerFactory.getLogger("main.logger");
	private ServerSocket serverSocket;
	private ExecutorService executorService;

	public MonitorServerSocketRunnable(){
		try{
			serverSocket = new ServerSocket(8001);
		}catch(Exception e){
			e.printStackTrace();			
		}
		executorService = Executors.newFixedThreadPool(20);//idea
		
	}

	@Override
	public void run(){
		System.out.println();
		while(true){
			Socket socket = null;			
			try{
				if(serverSocket != null){
					socket = serverSocket.accept();		
					System.out.println("socket connect:" + socket);
					CommunicationWithAGVRunnable runnable = getCommunicationWithAGVRunnable();//this.context.getBean("communicationWithAGVRunnable",CommunicationWithAGVRunnable.class);
					runnable.setSocket(socket);
					executorService.execute(runnable);
				}
			}catch(Exception e){
				e.printStackTrace();
				logger.error("{}", e);
			}
		}
	}

	public abstract CommunicationWithAGVRunnable getCommunicationWithAGVRunnable();
}
