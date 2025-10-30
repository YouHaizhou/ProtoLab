package com.example.protolab.tcpchat.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private ChatServer server;
    private BufferedReader input;
    private PrintWriter output;
    private String clientAddress;
    private String nickName;

    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.clientAddress = socket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try {
            // 初始化输入输出流
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            //用户昵称
            output.println("输入你的昵称：");
            nickName = input.readLine();
            if(nickName == null || nickName.trim().isEmpty()) {
                nickName = "匿名用户" + "(" + clientAddress + ")";
            } else {
                nickName = nickName.trim();
            }
            server.broadcast("系统：" + nickName + "加入了聊天室", this);

            //广播信息
            String message;
            while ((message = input.readLine()) != null) {
                System.out.println(nickName + "(" + clientAddress + ") 说：" +message);
                server.broadcast(nickName + ":" + message, this);
            }

        } catch (IOException e) {
            System.err.println("客户端异常：" + clientAddress + " - " + e.getMessage());
        } finally {
            if(nickName != null) {
                server.broadcast("系统: " + nickName + " 离开了聊天室", null);
            }
            closeConnection();
            server.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getNickname() {
        return nickName;
    }

    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.err.println("关闭连接失败：" + e.getMessage());
        }
    }
}
