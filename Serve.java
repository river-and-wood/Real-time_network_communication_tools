package GUI_Socket;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Serve {
    private static final Serve_GUI s_gui = new Serve_GUI(); // 服务器图形界面
    private static final Map<String, PrintWriter> clients = new HashMap<>(); // 在线用户列表

    public Serve() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            s_gui.appendMessage("服务器已启动，等待客户端连接...", 15, Color.BLACK, 1);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            s_gui.appendMessage("错误！无法启动服务器！", 17, Color.RED, 1);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Serve();
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // 接收用户名
                username = in.readLine();
                if (username != null && !username.isEmpty()) {
                    synchronized (clients) {
                        clients.put(username, out);
                        broadcastUserList(); // 广播当前在线用户列表
                    }
                    s_gui.appendMessage("用户 " + username + " 已连接", 15, Color.BLACK, 1);
                }

                // 处理消息
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("BROADCAST:")) {
                        handleBroadcastMessage(message.substring(10)); // 处理广播消息
                    } else if (message.startsWith("@")) {
                        handlePrivateMessage(message); // 处理私聊消息
                    } else {
                        broadcastMessage(username, message); // 处理普通群聊消息
                    }
                }
            } catch (IOException e) {
                s_gui.appendMessage("用户 " + username + " 断开连接", 15, Color.RED, 1);
            } finally {
                cleanup();
            }
        }

        // 广播普通消息到所有客户端
        private void broadcastMessage(String sender, String message) {
            synchronized (clients) {
                for (PrintWriter clientOut : clients.values()) {
                    clientOut.println("[" + sender + "]: " + message);
                }
            }
            s_gui.appendMessage(sender + ": " + message, 15, Color.BLACK, 1);
        }

        // 处理广播消息
        private void handleBroadcastMessage(String message) {
            int firstColon = message.indexOf(":");
            int secondColon = message.indexOf(":", firstColon + 1);
            if (firstColon > 0 && secondColon > firstColon) {
                String targetUsersString = message.substring(0, firstColon).trim();
                String broadcastMessage = message.substring(secondColon + 1).trim();
                String[] targetUsers = targetUsersString.split(",");

                synchronized (clients) {
                    for (String targetUser : targetUsers) {
                        PrintWriter targetOut = clients.get(targetUser);
                        if (targetOut != null) {
                            // 将广播消息发送给每个目标用户
                            targetOut.println("BROADCAST:" + username + ":" + broadcastMessage);
                        }
                    }
                }

                // 在服务器日志中记录广播消息
                s_gui.appendMessage("广播消息来自 " + username + "，内容: \"" + broadcastMessage + "\"，接收者: " + targetUsersString,
                        15, Color.BLUE, 1);
            }
        }

        // 处理私聊消息
        private void handlePrivateMessage(String message) {
            int colonIndex = message.indexOf(":");
            if (colonIndex > 0) {
                String targetUser = message.substring(1, colonIndex).trim();
                String privateMessage = message.substring(colonIndex + 1).trim();
                synchronized (clients) {
                    PrintWriter targetOut = clients.get(targetUser);
                    if (targetOut != null) {
                        targetOut.println("PRIVATE:" + username + ":" + privateMessage);
                        out.println("PRIVATE:" + targetUser + ":" + privateMessage);
                    } else {
                        out.println("用户 " + targetUser + " 不在线！");
                    }
                }
            }
        }

        // 广播当前在线用户列表
        private void broadcastUserList() {
            StringBuilder userList = new StringBuilder("USERLIST:");
            for (String user : clients.keySet()) {
                userList.append(user).append(",");
            }
            if (userList.length() > 10) {
                userList.setLength(userList.length() - 1);
            }
            synchronized (clients) {
                for (PrintWriter clientOut : clients.values()) {
                    clientOut.println(userList.toString());
                }
            }
        }

        // 用户断开时清理
        private void cleanup() {
            synchronized (clients) {
                if (username != null) {
                    clients.remove(username);
                    broadcastUserList();
                }
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
