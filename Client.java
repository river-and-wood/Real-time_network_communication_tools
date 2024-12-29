package GUI_Socket;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    public static String c_name; // ��ǰ�û����û���
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public Client() {
        try {
            socket = new Socket("localhost", 12345); // ���ӷ�����
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // �����û�����������
            out.println(c_name);

            JOptionPane.showMessageDialog(null, "�ѳɹ����ӵ�������", "���ӳɹ�", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "�޷����ӵ�������������������Ƿ�����", "����ʧ��", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message); // ������Ϣ��������
        } else {
            JOptionPane.showMessageDialog(null, "��Ϣ����ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendPrivateMessage(String targetUser, String message) {
        if (out != null) {
            // ˽����Ϣ��ʽ��@Ŀ���û���:��Ϣ����
            out.println("@" + targetUser + ":" + message);
        } else {
            JOptionPane.showMessageDialog(null, "��Ϣ����ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendBroadcastMessage(String targets, String message) {
        if (out != null) {
            // �㲥��Ϣ��ʽ��BROADCAST:Ŀ��1,Ŀ��2,...:��Ϣ����
            out.println("BROADCAST:" + targets + ":" + message);
        } else {
            JOptionPane.showMessageDialog(null, "�㲥ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void closeConnection() {
        try {
            if (out != null) {
                out.println("exit"); // ֪ͨ�������ر�����
            }
            if (socket != null) {
                socket.close(); // �ر��׽���
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
