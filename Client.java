package GUI_Socket;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static String c_name; // 当前用户的用户名
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public Client() {
        try {
            socket = new Socket("localhost", 12345); // 连接服务器
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 发送用户名到服务器
            out.println(c_name);

            JOptionPane.showMessageDialog(null, "已成功连接到服务器", "连接成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "无法连接到服务器，请检查服务器是否启动", "连接失败", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message); // 发送消息到服务器
        } else {
            JOptionPane.showMessageDialog(null, "消息发送失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendPrivateMessage(String targetUser, String message) {
        if (out != null) {
            // 私聊消息格式：@目标用户名:消息内容
            out.println("@" + targetUser + ":" + message);
        } else {
            JOptionPane.showMessageDialog(null, "消息发送失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendBroadcastMessage(String targets, String message) {
        if (out != null) {
            // 广播消息格式：BROADCAST:目标1,目标2,...:消息内容
            out.println("BROADCAST:" + targets + ":" + message);
        } else {
            JOptionPane.showMessageDialog(null, "广播失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void closeConnection() {
        try {
            if (out != null) {
                out.println("exit"); // 通知服务器关闭连接
            }
            if (socket != null) {
                socket.close(); // 关闭套接字
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
