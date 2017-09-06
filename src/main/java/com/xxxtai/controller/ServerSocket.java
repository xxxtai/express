package com.xxxtai.controller;



import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public abstract class ServerSocket implements Runnable{
	private java.net.ServerSocket serverSocket;
	private ExecutorService executorService;

	public ServerSocket(){
		try{
			serverSocket = new java.net.ServerSocket(8001);
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
					Communication runnable = getCommunicationRunnable();
					runnable.setSocket(socket);
					executorService.execute(runnable);
				}
			}catch(Exception e){
				e.printStackTrace();
				log.error("{}", e);
			}
		}
	}

	public abstract Communication getCommunicationRunnable();
}
