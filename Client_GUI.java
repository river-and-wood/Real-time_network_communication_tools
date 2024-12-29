package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Client_GUI extends JFrame {
    private final JTextPane chatArea;
    private final JTextField inputField;
    private final DefaultListModel<String> userListModel;
    private final JList<String> userList;
    private final StyledDocument doc;
    private final Client client;

    // �洢˽�Ĵ���
    private final Map<String, Client_GUI1> privateChatWindows = new HashMap<>();

    public Client_GUI() {
        client = new Client();

        setTitle("ʵʱ����ͨѶ����");
        setBounds(300, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // ������������
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // �û��б�����
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(userScrollPane, BorderLayout.EAST);

        // �ײ������Ͱ�ť����
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("����");
        JButton broadcastButton = new JButton("�㲥");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(broadcastButton, BorderLayout.WEST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        broadcastButton.addActionListener(e -> openBroadcastWindow());

        add(mainPanel);
        setVisible(true);

        // ���շ�������Ϣ
        new Thread(() -> {
            try {
                String response;
                while ((response = client.in.readLine()) != null) {
                    if (response.startsWith("USERLIST:")) {
                        updateUserList(response.substring(9));
                    } else if (response.startsWith("BROADCAST:")) {
                        String[] parts = response.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String message = parts[2];
                            openOrUpdatePrivateWindow(sender, "[�㲥] " + message);
                        }
                    } else {
                        appendMessage("������", response, Color.BLACK);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void appendMessage(String sender, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color);
            doc.insertString(doc.getLength(), "[" + sender + "]: " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            appendMessage("��", message, Color.BLUE);
            inputField.setText("");
        }
    }

    private void openBroadcastWindow() {
        JDialog broadcastDialog = new JDialog(this, "ѡ��㲥�û�", true);
        broadcastDialog.setSize(300, 400);
        broadcastDialog.setLayout(new BorderLayout());

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        Map<String, JCheckBox> checkBoxes = new HashMap<>();
        for (int i = 0; i < userListModel.size(); i++) {
            String user = userListModel.getElementAt(i);
            if (!user.equals(Client.c_name)) {
                JCheckBox checkBox = new JCheckBox(user);
                userPanel.add(checkBox);
                checkBoxes.put(user, checkBox);
            }
        }
        JScrollPane userScrollPane = new JScrollPane(userPanel);
        broadcastDialog.add(userScrollPane, BorderLayout.CENTER);

        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("����");
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        broadcastDialog.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            StringBuilder targets = new StringBuilder();
            for (Map.Entry<String, JCheckBox> entry : checkBoxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    targets.append(entry.getKey()).append(",");
                }
            }
            if (targets.length() > 0) {
                targets.setLength(targets.length() - 1);
            }
            String message = messageField.getText().trim();
            if (!message.isEmpty() && targets.length() > 0) {
                client.sendMessage("BROADCAST:" + targets + ":" + message);
                broadcastDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(broadcastDialog, "��ѡ���û���������Ϣ��", "����", JOptionPane.ERROR_MESSAGE);
            }
        });

        broadcastDialog.setVisible(true);
    }

    private void openOrUpdatePrivateWindow(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            Client_GUI1 privateWindow = privateChatWindows.computeIfAbsent(sender,
                    key -> new Client_GUI1(client, sender + " (�㲥��Ϣ)", () -> privateChatWindows.remove(sender)));
            privateWindow.appendMessage(sender, message, Color.ORANGE);
        });
    }

    private void updateUserList(String users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users.split(",")) {
                userListModel.addElement(user);
            }
        });
    }
}