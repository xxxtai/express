package com.xxxtai.express.controller;


import com.xxxtai.express.constant.Constant;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "develop")
public abstract class ServerSocket implements Runnable {
    private java.net.ServerSocket serverSocket;
    private ExecutorService executorService;

    public ServerSocket() {
        try {
            serverSocket = new java.net.ServerSocket(8001);
        } catch (Exception e) {
            e.printStackTrace();
        }
        executorService = Executors.newFixedThreadPool(20);
    }

    @Override
    public void run() {
        do {
            if (serverSocket == null) {
                break;
            }
            try {
                Socket socket = serverSocket.accept();
                log.info("socket connect:" + socket);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = reader.readLine();
                Runnable runnable;
                if (message.endsWith(Constant.QR_SUFFIX)) {
                    runnable = getCommunicationWithQRScan();
                    ((CommunicationWithQRScan)runnable).setSocket(socket);
                } else {
                    runnable = getCommunicationWithAGV();
                    ((CommunicationWithAGV)runnable).setSocket(socket);
                }
                executorService.execute(runnable);
            } catch (Exception e) {
                log.error("exception:", e);
            }
        } while (true);
    }

    public abstract CommunicationWithAGV getCommunicationWithAGV();

    public abstract CommunicationWithQRScan getCommunicationWithQRScan();
}
