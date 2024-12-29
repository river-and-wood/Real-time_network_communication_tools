package GUI_Socket;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static String c_name; // 当前用户的用户名
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    boolean connected;

    public Client() {
        connected = false;
        try {
            socket = new Socket("localhost", 12345); // 连接服务器
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            System.out.println("已连接到服务器。");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "无法连接到服务器，请检查服务器是否启动", "连接失败", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 发送用户名到服务器
    public void sendUsername() {
        if (out != null && connected) {
            out.println(c_name);
        }
    }

    // 发送普通消息到服务器
    public void sendMessage(String message) {
        if (out != null && connected) {
            out.println(message); // 发送消息到服务器
        } else {
            JOptionPane.showMessageDialog(null, "消息发送失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 发送私聊消息到服务器
    public void sendPrivateMessage(String targetUser, String message) {
        if (out != null && connected) {
            // 私聊消息格式：@目标用户名:消息内容
            out.println("@" + targetUser + ":" + message);
        } else {
            JOptionPane.showMessageDialog(null, "消息发送失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 发送广播消息到服务器
    public void sendBroadcastMessage(String targets, String message) {
        if (out != null && connected) {
            // 广播消息格式：BROADCAST:目标1,目标2,...:消息内容
            out.println("BROADCAST:" + targets + ":" + message);
        } else {
            JOptionPane.showMessageDialog(null, "广播失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 关闭连接
    public void closeConnection() {
        try {
            if (out != null && connected) {
                out.println("exit"); // 通知服务器关闭连接
            }
            if (socket != null && !socket.isClosed()) {
                socket.close(); // 关闭套接字
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
