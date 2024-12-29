package GUI_Socket;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Serve {
    private static final Serve_GUI s_gui = new Serve_GUI(); // ������ͼ�ν���
    private static final Map<String, PrintWriter> clients = new HashMap<>(); // �����û��б�

    public Serve() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            s_gui.appendMessage("���������������ȴ��ͻ�������...", 15, Color.BLACK, 1);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            s_gui.appendMessage("�����޷�������������", 17, Color.RED, 1);
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

                // �����û���
                username = in.readLine();
                if (username != null && !username.isEmpty()) {
                    synchronized (clients) {
                        clients.put(username, out);
                        broadcastUserList(); // �㲥��ǰ�����û��б�
                    }
                    s_gui.appendMessage("�û� " + username + " ������", 15, Color.BLACK, 1);
                }

                // ������Ϣ
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("BROADCAST:")) {
                        handleBroadcastMessage(message.substring(10));
                    } else if (message.startsWith("@")) {
                        handlePrivateMessage(message);
                    } else {
                        broadcastMessage(username, message);
                    }
                }
            } catch (IOException e) {
                s_gui.appendMessage("�û� " + username + " �Ͽ�����", 15, Color.RED, 1);
            } finally {
                cleanup();
            }
        }

        private void broadcastMessage(String sender, String message) {
            synchronized (clients) {
                for (PrintWriter clientOut : clients.values()) {
                    clientOut.println("[" + sender + "]: " + message);
                }
            }
            s_gui.appendMessage(sender + ": " + message, 15, Color.BLACK, 1);
        }

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
                            targetOut.println("BROADCAST:" + username + ":" + broadcastMessage);
                        }
                    }
                }

                // �ڷ�������־�м�¼�㲥��Ϣ
                s_gui.appendMessage("�㲥��Ϣ���� " + username + ": " + broadcastMessage + " (Ŀ���û�: " + targetUsersString + ")", 15, Color.BLACK, 1);
            }
        }

        private void handlePrivateMessage(String message) {
            int colonIndex = message.indexOf(":");
            if (colonIndex > 0) {
                String targetUser = message.substring(1, colonIndex).trim();
                String privateMessage = message.substring(colonIndex + 1).trim();
                synchronized (clients) {
                    PrintWriter targetOut = clients.get(targetUser);
                    if (targetOut != null) {
                        // �����շ�����˽����Ϣ����ʽΪ��PRIVATE:���ͷ�:��Ϣ����
                        targetOut.println("PRIVATE:" + username + ":" + privateMessage);

                        // �����ͷ�����ȷ����Ϣ
                        out.println("PRIVATE:" + targetUser + ":" + privateMessage);
                    } else {
                        out.println("�û� " + targetUser + " �����ߣ�");
                    }
                }
            }
        }

        private void broadcastUserList() {
            StringBuilder userList = new StringBuilder("USERLIST:");
            for (String user : clients.keySet()) {
                userList.append(user).append(",");
            }
            if (userList.length() > 10) {
                userList.setLength(userList.length() - 1); // ȥ�����Ķ���
            }
            synchronized (clients) {
                for (PrintWriter clientOut : clients.values()) {
                    clientOut.println(userList.toString());
                }
            }
        }

        private void cleanup() {
            synchronized (clients) {
                if (username != null) {
                    clients.remove(username);
                    broadcastUserList(); // �����û��б�
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
