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
		executorService = Executors.newFixedThreadPool(20);
		
	}

	@Override
	public void run(){
		while(true){
			try{
				if(serverSocket != null){
					Socket socket = serverSocket.accept();
					System.out.println("socket connect:" + socket);
					CommunicationWithAGVRunnable runnable = getCommunicationWithAGVRunnable();
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
