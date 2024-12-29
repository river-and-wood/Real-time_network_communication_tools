package GUI_Socket;

import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.text.MessageFormat;
import java.util.*;

public class Serve {
    private static final Serve_GUI s_gui = new Serve_GUI(); // 服务器图形界面
    private static final Map<String, PrintWriter> clients = new HashMap<>(); // 在线用户列表

    public Serve() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            s_gui.appendMessage("服务器已启动，等待客户端连接...", 15, Color.BLACK, StyleConstants.ALIGN_LEFT);

            do {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            } while (true);
        } catch (IOException e) {
            s_gui.appendMessage("错误！无法启动服务器！", 17, Color.RED, StyleConstants.ALIGN_LEFT);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Serve serve = new Serve();
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // 接收用户名
                username = in.readLine();
                if (username != null && !username.isEmpty()) {
                    synchronized (clients) {
                        if (clients.containsKey(username)) {
                            out.println("系统消息:用户名已存在，请重新登录！");
                            s_gui.appendMessage("拒绝重复用户名连接: " + username, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                            clientSocket.close();
                            return;
                        }
                        clients.put(username, out);
                        broadcastUserList(); // 广播当前在线用户列表
                    }
                    s_gui.appendMessage("用户 " + username + " 已连接", 15, Color.BLACK, StyleConstants.ALIGN_LEFT);
                }

                // 处理消息
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("BROADCAST:")) {
                        handleBroadcastMessage(message.substring(10)); // 处理广播消息
                    } else if (message.startsWith("@")) {
                        handlePrivateMessage(message); // 处理私聊消息
                    } else if (message.equalsIgnoreCase("exit")) {
                        // 用户主动退出
                        break;
                    } else {
                        broadcastMessage(username, message); // 处理普通群聊消息
                    }
                }
            } catch (IOException e) {
                s_gui.appendMessage("用户 " + username + " 断开连接", 15, Color.RED, StyleConstants.ALIGN_LEFT);
            } finally {
                cleanup();
            }
        }

        // 广播普通消息到所有客户端
        private void broadcastMessage(String sender, String message) {
            synchronized (clients) {
                for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
                    PrintWriter clientOut = entry.getValue();
                    clientOut.println("[" + sender + "]: " + message);
                }
            }
            s_gui.appendMessage(sender + ": " + message, 15, Color.BLACK, StyleConstants.ALIGN_LEFT);
        }

        // 处理广播消息
        private void handleBroadcastMessage(String message) {
            // 期望的消息格式: target1,target2,...:message
            int colonIndex = message.indexOf(":");
            if (colonIndex > 0) {
                String targetUsersString = message.substring(0, colonIndex).trim();
                String broadcastMessage = message.substring(colonIndex + 1).trim();
                String[] targetUsers = targetUsersString.split(",");

                synchronized (clients) {
                    for (String targetUser : targetUsers) {
                        targetUser = targetUser.trim();
                        if (!targetUser.equals(username)) { // 排除发送者自己
                            PrintWriter targetOut = clients.get(targetUser);
                            if (targetOut != null) {
                                // 将广播消息发送给每个目标用户
                                targetOut.println("BROADCAST:" + username + ":" + broadcastMessage);
                                s_gui.appendMessage("发送广播给 " + targetUser + ": " + broadcastMessage, 15, Color.MAGENTA, StyleConstants.ALIGN_LEFT);
                            } else {
                                // 如果目标用户不存在或已下线，通知发送者
                                out.println("系统消息: 用户 " + targetUser + " 不存在或已下线！");
                                s_gui.appendMessage("广播失败，目标用户不存在或已下线: " + targetUser, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                            }
                        }
                    }
                }

                // 在服务器日志中记录广播消息
                s_gui.appendMessage("广播消息来自 " + username + "，内容: \"" + broadcastMessage + "\"，接收者: " + targetUsersString,
                        15, Color.BLUE, StyleConstants.ALIGN_LEFT);
            } else {
                // 如果格式不正确，记录错误日志
                s_gui.appendMessage("收到格式错误的广播消息: " + message, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                out.println("系统消息: 广播消息格式错误！");
            }
        }

        // 处理私聊消息
        private void handlePrivateMessage(String message) {
            // 期望的消息格式: @targetUser:message
            int colonIndex = message.indexOf(":");
            if (colonIndex > 1) {
                String targetUser = message.substring(1, colonIndex).trim();
                String privateMessage = message.substring(colonIndex + 1).trim();
                synchronized (clients) {
                    PrintWriter targetOut = clients.get(targetUser);
                    if (targetOut != null) {
                        // 发送私聊消息给目标用户
                        targetOut.println("PRIVATE:" + username + ":" + privateMessage);
                        // 发送确认给发送者
                        //out.println("PRIVATE:" + targetUser + ":" + privateMessage);
                        s_gui.appendMessage("私聊消息来自 " + username + "，目标: " + targetUser + "，内容: \"" + privateMessage + "\"",
                                15, Color.GREEN, StyleConstants.ALIGN_LEFT);
                    } else {
                        out.println("系统消息: 用户 " + targetUser + " 不存在或已下线！");
                        s_gui.appendMessage("私聊失败，目标用户不存在或已下线: " + targetUser, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                    }
                }
            } else {
                // 如果格式不正确，记录错误日志
                s_gui.appendMessage("收到格式错误的私聊消息: " + message, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                out.println("系统消息: 私聊消息格式错误！");
            }
        }

        // 广播当前在线用户列表
        private void broadcastUserList() {
            StringBuilder userList;
            userList = new StringBuilder("USERLIST:");
            for (String user : clients.keySet()) {
                userList.append(user).append(",");
            }
            if (userList.length() > 9) { // "USERLIST:".length() == 9
                userList.setLength(userList.length() - 1);
            }
            synchronized (clients) {
                for (PrintWriter clientOut : clients.values()) {
                    clientOut.println(userList);
                }
            }
            s_gui.appendMessage(MessageFormat.format("已广播用户列表: {0}", userList.toString()),
                    15,
                    Color.CYAN,
                    StyleConstants.ALIGN_LEFT);
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
            s_gui.appendMessage("用户 " + username + " 已断开连接", 15, Color.RED, StyleConstants.ALIGN_LEFT);
        }
    }
}
