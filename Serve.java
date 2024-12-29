package GUI_Socket;

import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.text.MessageFormat;
import java.util.*;

public class Serve {
    private static final Serve_GUI s_gui = new Serve_GUI(); // ������ͼ�ν���
    private static final Map<String, PrintWriter> clients = new HashMap<>(); // �����û��б�

    public Serve() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            s_gui.appendMessage("���������������ȴ��ͻ�������...", 15, Color.BLACK, StyleConstants.ALIGN_LEFT);

            do {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            } while (true);
        } catch (IOException e) {
            s_gui.appendMessage("�����޷�������������", 17, Color.RED, StyleConstants.ALIGN_LEFT);
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

                // �����û���
                username = in.readLine();
                if (username != null && !username.isEmpty()) {
                    synchronized (clients) {
                        if (clients.containsKey(username)) {
                            out.println("ϵͳ��Ϣ:�û����Ѵ��ڣ������µ�¼��");
                            s_gui.appendMessage("�ܾ��ظ��û�������: " + username, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                            clientSocket.close();
                            return;
                        }
                        clients.put(username, out);
                        broadcastUserList(); // �㲥��ǰ�����û��б�
                    }
                    s_gui.appendMessage("�û� " + username + " ������", 15, Color.BLACK, StyleConstants.ALIGN_LEFT);
                }

                // ������Ϣ
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("BROADCAST:")) {
                        handleBroadcastMessage(message.substring(10)); // ����㲥��Ϣ
                    } else if (message.startsWith("@")) {
                        handlePrivateMessage(message); // ����˽����Ϣ
                    } else if (message.equalsIgnoreCase("exit")) {
                        // �û������˳�
                        break;
                    } else {
                        broadcastMessage(username, message); // ������ͨȺ����Ϣ
                    }
                }
            } catch (IOException e) {
                s_gui.appendMessage("�û� " + username + " �Ͽ�����", 15, Color.RED, StyleConstants.ALIGN_LEFT);
            } finally {
                cleanup();
            }
        }

        // �㲥��ͨ��Ϣ�����пͻ���
        private void broadcastMessage(String sender, String message) {
            synchronized (clients) {
                for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
                    PrintWriter clientOut = entry.getValue();
                    clientOut.println("[" + sender + "]: " + message);
                }
            }
            s_gui.appendMessage(sender + ": " + message, 15, Color.BLACK, StyleConstants.ALIGN_LEFT);
        }

        // ����㲥��Ϣ
        private void handleBroadcastMessage(String message) {
            // ��������Ϣ��ʽ: target1,target2,...:message
            int colonIndex = message.indexOf(":");
            if (colonIndex > 0) {
                String targetUsersString = message.substring(0, colonIndex).trim();
                String broadcastMessage = message.substring(colonIndex + 1).trim();
                String[] targetUsers = targetUsersString.split(",");

                synchronized (clients) {
                    for (String targetUser : targetUsers) {
                        targetUser = targetUser.trim();
                        if (!targetUser.equals(username)) { // �ų��������Լ�
                            PrintWriter targetOut = clients.get(targetUser);
                            if (targetOut != null) {
                                // ���㲥��Ϣ���͸�ÿ��Ŀ���û�
                                targetOut.println("BROADCAST:" + username + ":" + broadcastMessage);
                                s_gui.appendMessage("���͹㲥�� " + targetUser + ": " + broadcastMessage, 15, Color.MAGENTA, StyleConstants.ALIGN_LEFT);
                            } else {
                                // ���Ŀ���û������ڻ������ߣ�֪ͨ������
                                out.println("ϵͳ��Ϣ: �û� " + targetUser + " �����ڻ������ߣ�");
                                s_gui.appendMessage("�㲥ʧ�ܣ�Ŀ���û������ڻ�������: " + targetUser, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                            }
                        }
                    }
                }

                // �ڷ�������־�м�¼�㲥��Ϣ
                s_gui.appendMessage("�㲥��Ϣ���� " + username + "������: \"" + broadcastMessage + "\"��������: " + targetUsersString,
                        15, Color.BLUE, StyleConstants.ALIGN_LEFT);
            } else {
                // �����ʽ����ȷ����¼������־
                s_gui.appendMessage("�յ���ʽ����Ĺ㲥��Ϣ: " + message, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                out.println("ϵͳ��Ϣ: �㲥��Ϣ��ʽ����");
            }
        }

        // ����˽����Ϣ
        private void handlePrivateMessage(String message) {
            // ��������Ϣ��ʽ: @targetUser:message
            int colonIndex = message.indexOf(":");
            if (colonIndex > 1) {
                String targetUser = message.substring(1, colonIndex).trim();
                String privateMessage = message.substring(colonIndex + 1).trim();
                synchronized (clients) {
                    PrintWriter targetOut = clients.get(targetUser);
                    if (targetOut != null) {
                        // ����˽����Ϣ��Ŀ���û�
                        targetOut.println("PRIVATE:" + username + ":" + privateMessage);
                        // ����ȷ�ϸ�������
                        //out.println("PRIVATE:" + targetUser + ":" + privateMessage);
                        s_gui.appendMessage("˽����Ϣ���� " + username + "��Ŀ��: " + targetUser + "������: \"" + privateMessage + "\"",
                                15, Color.GREEN, StyleConstants.ALIGN_LEFT);
                    } else {
                        out.println("ϵͳ��Ϣ: �û� " + targetUser + " �����ڻ������ߣ�");
                        s_gui.appendMessage("˽��ʧ�ܣ�Ŀ���û������ڻ�������: " + targetUser, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                    }
                }
            } else {
                // �����ʽ����ȷ����¼������־
                s_gui.appendMessage("�յ���ʽ�����˽����Ϣ: " + message, 15, Color.RED, StyleConstants.ALIGN_LEFT);
                out.println("ϵͳ��Ϣ: ˽����Ϣ��ʽ����");
            }
        }

        // �㲥��ǰ�����û��б�
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
            s_gui.appendMessage(MessageFormat.format("�ѹ㲥�û��б�: {0}", userList.toString()),
                    15,
                    Color.CYAN,
                    StyleConstants.ALIGN_LEFT);
        }

        // �û��Ͽ�ʱ����
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
            s_gui.appendMessage("�û� " + username + " �ѶϿ�����", 15, Color.RED, StyleConstants.ALIGN_LEFT);
        }
    }
}
