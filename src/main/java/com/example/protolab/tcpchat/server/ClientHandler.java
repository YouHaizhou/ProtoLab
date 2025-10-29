package com.example.protolab.tcpchat.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private ChatServer server;
    private BufferedReader input;
    private PrintWriter output;
    private String clientAddress;

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

            String message;
            while ((message = input.readLine()) != null) {
                System.out.println("来自 " + clientAddress + " 的消息：" + message);
                server.broadcast(message, this);
            }

        } catch (IOException e) {
            System.err.println("客户端异常：" + clientAddress + " - " + e.getMessage());
        } finally {
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
