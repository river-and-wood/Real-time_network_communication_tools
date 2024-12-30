package GUI_Socket;

import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * Client�ฺ��ʵ�ֿͻ��˵ĺ���ͨ���߼����������ӷ�������������Ϣ�ȹ��ܡ�
 */
public class Client {
    public static String c_name; // ��ǰ�û����û���
    Socket socket; // �ͻ������������Socket����
    BufferedReader in; // ���ڴӷ�������ȡ����
    PrintWriter out; // �������������������
    boolean connected; // ��ǿͻ����Ƿ������ӵ�������

    /**
     * ���캯�����������ӵ�����������ʼ������/�����
     */
    public Client() {
        connected = false; // ��ʼ����״̬Ϊδ����
        try {
            // �������ӵ����ط��������˿ں�Ϊ12345
            socket = new Socket("localhost", 12345);
            // ��������������������ֱ����ڶ�ȡ�ͷ�����Ϣ
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true; // �ɹ����Ӻ���Ϊ������
            System.out.println("�����ӵ���������");
        } catch (IOException e) {
            // ����ʧ��ʱ�����Ի�����ʾ�û�
            JOptionPane.showMessageDialog(null, "�޷����ӵ�������������������Ƿ�����", "����ʧ��", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * �����û�����������
     */
    public void sendUsername() {
        if (out != null && connected) {
            out.println(c_name); // ���͵�ǰ�û����û���
        }
    }

    /**
     * ������ͨ��Ϣ��������
     * @param message ��Ϣ����
     */
    public void sendMessage(String message) {
        if (out != null && connected) {
            out.println(message); // ������ͨ��Ϣ��������
        } else {
            JOptionPane.showMessageDialog(null, "��Ϣ����ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ����˽����Ϣ��������
     * @param targetUser Ŀ���û�
     * @param message ��Ϣ����
     */
    public void sendPrivateMessage(String targetUser, String message) {
        if (out != null && connected) {
            out.println("@" + targetUser + ":" + message); // ˽����Ϣ��ʽ
        } else {
            JOptionPane.showMessageDialog(null, "��Ϣ����ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * ���͹㲥��Ϣ��������
     * @param targets �㲥Ŀ���û�
     * @param message ��Ϣ����
     */
    public void sendBroadcastMessage(String targets, String message) {
        if (out != null && connected) {
            out.println("BROADCAST:" + targets + ":" + message); // �㲥��Ϣ��ʽ
        } else {
            JOptionPane.showMessageDialog(null, "�㲥ʧ�ܣ�������δ����", "����", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * �ر��������������
     */
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
