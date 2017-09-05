package com.xxxtai.controller;


import com.xxxtai.model.Car;
import com.xxxtai.toolKit.ReaderWriter;
import com.xxxtai.view.SchedulingGui;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
@Slf4j
public class CommunicationWithAGVRunnable implements Runnable{
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Car car;
	
	public CommunicationWithAGVRunnable(){
	}
	
	void setSocket(Socket socket){
		this.socket = socket;
		try {
			this.inputStream = this.socket.getInputStream();
			this.outputStream = this.socket.getOutputStream();
		} catch (IOException e) {
			log.error("exception:", e);
		}
	}
	@Override
	public void run() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.hashCode()).append(" CommunicationWithAGV Runnable start\n");
		String revMessage;
		while(this.car == null){
			if((revMessage = read()) != null){				
				builder.append("receive msg:").append(revMessage);
				if(revMessage.endsWith("BB")){
					int AGVNum = Integer.parseInt(revMessage.substring(0, 2), 16);
					for(Car car : SchedulingGui.AGVArray){
						if(car.getAGVNum() == AGVNum){
							car.setCommunicationWithAGVRunnable(this);
							this.car = car;
							this.car.setLastCommunicationTime(System.currentTimeMillis());
							builder.append(" confirmed AGVNum :").append(AGVNum).append("号AGV");
							System.out.println(builder.toString());
							break;
						}						
					}
				}
			}
		}		

		while(true){
			if(System.currentTimeMillis() - this.car.getLastCommunicationTime() > 4500){//通讯中断
				this.car.setCommunicationWithAGVRunnable(null);
				try {
					this.inputStream.close();
					this.outputStream.close();
					this.socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
				System.out.println("break......................." + System.currentTimeMillis());
				break;
			}
			
			if((revMessage = read()) != null){
				this.car.setLastCommunicationTime(System.currentTimeMillis());
				//System.out.println(revMessage);
				if(revMessage.endsWith("BB")){
					int cardNum = Integer.parseInt(revMessage.substring(2, 4), 16);
					if(cardNum != 0) {
						this.car.setReceiveCardNum(cardNum);
					}
					this.car.setState(Integer.parseInt(revMessage.substring(4, 6), 16));
				}
			}
				
//			try {
//				write("AA111111BB");
//			} catch (SocketException e1) {
//				e1.printStackTrace();
//				try {
//					this.inputStream.close();
//					this.outputStream.close();
//					this.socket.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				break;
//				
//			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private String read(){
		boolean readSuccess = false;
		boolean foundStart = false;
		String message = "";
		try {
			if(inputStream.available() > 0){
				byte[] endCode = new byte[1];
				inputStream.read(endCode);
				message = ReaderWriter.bytes2HexString(endCode);
				if(message.equals("CC") || message.equals("AA")){
					foundStart = true;
				}
				if(foundStart){
					byte[] buff = new byte[4];
					inputStream.read(buff);
					message = ReaderWriter.bytes2HexString(buff);
					readSuccess = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(readSuccess){
			return message;
		}else{
			return null;
		}
	}
	
	public boolean write(String sendMessage)throws SocketException{
		//System.out.println("ready to send to AGV :"+ sendMessage);
		boolean isSuccess = false;
		try {
			this.outputStream.write(ReaderWriter.hexString2Bytes(sendMessage));//ReaderWriter.hexString2Bytes(sendMessage)
			isSuccess = true;
		} catch (IOException e) {
			if(e instanceof SocketException)
				throw new SocketException();
			else
				e.printStackTrace();
		}
		//if(isSuccess)
		//	System.out.println("success to send to AGV :"+ sendMessage);
		return isSuccess;
	}

}
