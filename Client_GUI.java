package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    // 存储私聊窗口
    private final Map<String, Client_GUI1> privateChatWindows = new HashMap<>();

    public Client_GUI(Client client) {
        this.client = client;

        if (!client.connected) {
            // Connection failed, do not proceed
            System.exit(0);
        }

        setTitle("实时网络通讯工具 - 用户: " + Client.c_name);
        setBounds(300, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 聊天内容区域
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // 用户列表区域
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击用户列表中的某个用户
                    String selectedUser = userList.getSelectedValue();
                    if (selectedUser != null && !selectedUser.equals(Client.c_name)) {
                        openOrUpdatePrivateWindow(selectedUser, ""); // 打开私聊窗口
                    }
                }
            }
        });
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(userScrollPane, BorderLayout.EAST);

        // 底部输入框和按钮区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("发送");
        JButton broadcastButton = new JButton("广播");
        inputPanel.add(broadcastButton, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        broadcastButton.addActionListener(e -> openBroadcastWindow());

        add(mainPanel);
        setVisible(true);

        // 启动接收线程
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
                            // 在接收方新建私聊窗口显示广播消息
                            openOrUpdatePrivateWindow("Broadcast from " + sender, message);
                        }
                    } else if (response.startsWith("PRIVATE:")) {
                        String[] parts = response.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String message = parts[2];
                            openOrUpdatePrivateWindow(sender, message);
                        }
                    } else if (response.startsWith("[")) {
                        // 普通群聊消息
                        appendMessage("服务器", response, Color.BLACK);
                    } else if (response.startsWith("系统消息:")) {
                        // 系统消息
                        appendMessage("系统", response.substring(5), Color.RED);
                    } else {
                        // 其他消息
                        appendMessage("服务器", response, Color.BLACK);
                    }
                }
            } catch (IOException e) {
                appendMessage("系统", "连接到服务器时发生错误。", Color.RED);
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
            appendMessage("我", message, Color.BLUE);
            inputField.setText("");
        }
    }

    private void openBroadcastWindow() {
        JDialog broadcastDialog = new JDialog(this, "选择广播用户", true);
        broadcastDialog.setSize(400, 500);
        broadcastDialog.setLayout(new BorderLayout());

        // 添加复选框列表
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        Map<String, JCheckBox> checkBoxes = new HashMap<>();
        for (int i = 0; i < userListModel.size(); i++) {
            String user = userListModel.getElementAt(i);
            if (!user.equals(Client.c_name)) { // 确保不包含发送者自己
                JCheckBox checkBox = new JCheckBox(user);
                userPanel.add(checkBox);
                checkBoxes.put(user, checkBox);
            }
        }
        JScrollPane userScrollPane = new JScrollPane(userPanel);
        broadcastDialog.add(userScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel("消息内容：");
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("发送");
        inputPanel.add(messageLabel, BorderLayout.NORTH);
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
                targets.setLength(targets.length() - 1); // 移除最后一个逗号
            }
            String message = messageField.getText().trim();
            if (!message.isEmpty() && targets.length() > 0) {
                client.sendBroadcastMessage(targets.toString(), message); // 调用广播发送方法
                JOptionPane.showMessageDialog(broadcastDialog, "广播消息已发送！", "成功", JOptionPane.INFORMATION_MESSAGE);
                broadcastDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(broadcastDialog, "请选择用户并输入消息！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        broadcastDialog.setVisible(true);
    }

    private void openOrUpdatePrivateWindow(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            Client_GUI1 privateWindow = privateChatWindows.computeIfAbsent(sender,
                    key -> new Client_GUI1(client, key, () -> privateChatWindows.remove(key)));
            if (!message.isEmpty()) {
                if (sender.startsWith("Broadcast from ")) {
                    // 对于广播消息，标识为广播消息
                    privateWindow.appendMessage("广播消息", message, Color.MAGENTA);
                } else {
                    privateWindow.appendMessage(sender, message, Color.ORANGE);
                }
            }
        });
    }

    private void updateUserList(String users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users.split(",")) {
                userListModel.addElement(user);
            }
            System.out.println("用户列表已更新: " + users); // 调试日志
        });
    }
}
