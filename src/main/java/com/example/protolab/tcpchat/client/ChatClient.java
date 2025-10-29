package com.example.protolab.tcpchat.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private String serverHost = "localhost";
    private int serverPort = 123;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public void start() {
        try{
            socket = new Socket(serverHost, serverPort);
            System.out.println("连接到服务器:" + serverHost + ":" +serverPort);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            new Thread(this::receiveMessages).start();
            sendMessages();
        } catch (IOException e){
            System.err.println("连接失败：" + e.getMessage());
        }
    }

    private void sendMessages() {
        try(BufferedReader console = new BufferedReader(new InputStreamReader(System.in))){
            String message;
            while((message = console.readLine()) != null){
                output.println(message);
            }
        } catch (IOException e){
            System.err.println("发送消息时失败：" + e.getMessage());
        }
    }

    private void receiveMessages() {
        try{
            String message;
            while((message = input.readLine()) != null){
                System.out.println("收到消息：" + message);
            }
        } catch (IOException e){
            System.out.println("接收消息失败：" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ChatClient().start();
    }
}
