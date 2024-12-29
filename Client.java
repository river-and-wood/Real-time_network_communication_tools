package GUI_Socket;

import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * Client类负责实现客户端的核心通信逻辑，包括连接服务器、发送消息等功能。
 */
public class Client {
    public static String c_name; // 当前用户的用户名
    Socket socket; // 客户端与服务器的Socket连接
    BufferedReader in; // 用于从服务器读取数据
    PrintWriter out; // 用于向服务器发送数据
    boolean connected; // 标记客户端是否已连接到服务器

    /**
     * 构造函数：尝试连接到服务器并初始化输入/输出流
     */
    public Client() {
        connected = false; // 初始连接状态为未连接
        try {
            // 尝试连接到本地服务器，端口号为12345
            socket = new Socket("localhost", 12345);
            // 创建输入流和输出流，分别用于读取和发送消息
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true; // 成功连接后标记为已连接
            System.out.println("已连接到服务器。");
        } catch (IOException e) {
            // 连接失败时弹出对话框提示用户
            JOptionPane.showMessageDialog(null, "无法连接到服务器，请检查服务器是否启动", "连接失败", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * 发送用户名到服务器
     */
    public void sendUsername() {
        if (out != null && connected) {
            out.println(c_name); // 发送当前用户的用户名
        }
    }

    /**
     * 发送普通消息到服务器
     * @param message 消息内容
     */
    public void sendMessage(String message) {
        if (out != null && connected) {
            out.println(message); // 发送普通消息到服务器
        } else {
            JOptionPane.showMessageDialog(null, "消息发送失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 发送私聊消息到服务器
     * @param targetUser 目标用户
     * @param message 消息内容
     */
    public void sendPrivateMessage(String targetUser, String message) {
        if (out != null && connected) {
            out.println("@" + targetUser + ":" + message); // 私聊消息格式
        } else {
            JOptionPane.showMessageDialog(null, "消息发送失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 发送广播消息到服务器
     * @param targets 广播目标用户
     * @param message 消息内容
     */
    public void sendBroadcastMessage(String targets, String message) {
        if (out != null && connected) {
            out.println("BROADCAST:" + targets + ":" + message); // 广播消息格式
        } else {
            JOptionPane.showMessageDialog(null, "广播失败，服务器未连接", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 关闭与服务器的连接
     */
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
