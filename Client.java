package GUI_Socket;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static String c_name; // ��ǰ�û����û���
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    boolean connected;

    public Client() {
        connected = false;
        try {
            socket = new Socket("localhost", 12345); // ���ӷ�����
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            System.out.println("�����ӵ���������");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "�޷����ӵ�������������������Ƿ�����", "����ʧ��", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // �����û�����������
    public void sendUsername() {
        if (out != null && connected) {
            out.println(c_name);
        }
    }

    // ������ͨ��Ϣ��������
    public void sendMessage(String message) {
        if (out != null && connected) {
            out.println(message); // ������Ϣ��������
        } else {
            JOptionPane.showMessageDialog(null, "��Ϣ����ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ����˽����Ϣ��������
    public void sendPrivateMessage(String targetUser, String message) {
        if (out != null && connected) {
            // ˽����Ϣ��ʽ��@Ŀ���û���:��Ϣ����
            out.println("@" + targetUser + ":" + message);
        } else {
            JOptionPane.showMessageDialog(null, "��Ϣ����ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ���͹㲥��Ϣ��������
    public void sendBroadcastMessage(String targets, String message) {
        if (out != null && connected) {
            // �㲥��Ϣ��ʽ��BROADCAST:Ŀ��1,Ŀ��2,...:��Ϣ����
            out.println("BROADCAST:" + targets + ":" + message);
        } else {
            JOptionPane.showMessageDialog(null, "�㲥ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    // �ر�����
    public void closeConnection() {
        try {
            if (out != null && connected) {
                out.println("exit"); // ֪ͨ�������ر�����
            }
            if (socket != null && !socket.isClosed()) {
                socket.close(); // �ر��׽���
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
