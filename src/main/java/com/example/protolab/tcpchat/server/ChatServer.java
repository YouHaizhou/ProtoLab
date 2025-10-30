package com.example.protolab.tcpchat.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private int port = 123;
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器已启动，监听端口： " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("新客户端已连接：" + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败：" + e.getMessage());
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        for(ClientHandler client : clients) {
            if(client != sender){
                client.sendMessage(message);
            }
        }
        System.out.println("[广播] " +message);
    }

    public String getOnlineUsers() {
        StringBuilder users = new StringBuilder("在线用户：");
        for(ClientHandler client : clients) {
            users.append(client.getNickname()).append(", ");
        }
        return users.toString();
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("客户端断开连接：" + client.getClientAddress());
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
