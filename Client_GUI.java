package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Client_GUI 类是客户端的主界面，显示群聊内容、用户列表，并支持广播和私聊功能。
 */
public class Client_GUI extends JFrame {
    private final JTextPane chatArea; // 聊天区域，显示群聊消息
    private final JTextField inputField; // 消息输入框
    private final DefaultListModel<String> userListModel; // 在线用户列表的数据模型
    private final JList<String> userList; // 显示在线用户的列表组件
    private final StyledDocument doc; // 聊天区域的样式文档
    private final Client client; // 客户端通信逻辑对象
    private final Map<String, Client_GUI1> privateChatWindows = new HashMap<>(); // 存储私聊窗口的Map

    /**
     * 构造函数：初始化客户端界面
     * @param client 客户端通信对象
     */
    public Client_GUI(Client client) {
        this.client = client;

        // 如果客户端未连接，退出程序
        if (!client.connected) {
            System.exit(0);
        }

        setTitle("实时网络通讯工具 - 用户: " + Client.c_name); // 设置窗口标题
        setBounds(300, 100, 900, 600); // 设置窗口大小和位置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 关闭窗口时退出程序

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 聊天内容区域
        chatArea = new JTextPane(); // 创建聊天区域
        chatArea.setEditable(false); // 设置为不可编辑
        doc = chatArea.getStyledDocument(); // 获取样式文档
        JScrollPane chatScrollPane = new JScrollPane(chatArea); // 添加滚动条
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 总是显示滚动条
        mainPanel.add(chatScrollPane, BorderLayout.CENTER); // 将聊天区域添加到主面板中间

        // 用户列表区域
        userListModel = new DefaultListModel<>(); // 初始化用户列表模型
        userList = new JList<>(userListModel); // 创建用户列表
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 设置为单选
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
        JScrollPane userScrollPane = new JScrollPane(userList); // 添加滚动条
        userScrollPane.setPreferredSize(new Dimension(200, 0)); // 设置宽度
        mainPanel.add(userScrollPane, BorderLayout.EAST); // 将用户列表添加到右侧

        // 底部输入框和按钮区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField(); // 创建输入框
        inputField.setPreferredSize(new Dimension(400, 30)); // 设置输入框大小
        JButton sendButton = new JButton("发送"); // 创建发送按钮
        JButton broadcastButton = new JButton("广播"); // 创建广播按钮

        // 样式设置
        sendButton.setBackground(new Color(58, 129, 255));
        sendButton.setForeground(Color.WHITE);
        broadcastButton.setBackground(new Color(58, 129, 255));
        broadcastButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // 按钮面板
        buttonPanel.add(broadcastButton);
        buttonPanel.add(sendButton);

        inputPanel.add(inputField, BorderLayout.CENTER); // 将输入框添加到中间
        inputPanel.add(buttonPanel, BorderLayout.EAST); // 将按钮面板添加到右侧
        mainPanel.add(inputPanel, BorderLayout.SOUTH); // 将输入区域添加到底部

        sendButton.addActionListener(e -> sendMessage()); // 发送按钮监听器
        broadcastButton.addActionListener(e -> openBroadcastWindow()); // 广播按钮监听器

        add(mainPanel); // 添加主面板到窗口
        setVisible(true); // 设置窗口可见

        // 启动接收线程，用于接收来自服务器的消息
        new Thread(() -> {
            try {
                String response;
                while ((response = client.in.readLine()) != null) {
                    if (response.startsWith("USERLIST:")) { // 更新用户列表
                        updateUserList(response.substring(9));
                    } else if (response.startsWith("BROADCAST:")) { // 接收到广播消息
                        String[] parts = response.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String message = parts[2];
                            openOrUpdatePrivateWindow("Broadcast from " + sender, message);
                        }
                    } else if (response.startsWith("PRIVATE:")) { // 接收到私聊消息
                        String[] parts = response.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String message = parts[2];
                            openOrUpdatePrivateWindow(sender, message);
                        }
                    } else {
                        appendMessage("服务器", response, Color.BLACK); // 显示其他消息
                    }
                }
            } catch (IOException e) {
                appendMessage("系统", "连接到服务器时发生错误。", Color.RED);
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 向聊天区域追加消息
     * @param sender 发送者
     * @param message 消息内容
     * @param color 消息显示的颜色
     */
    private void appendMessage(String sender, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // 设置字体颜色

            // 获取时间戳
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            doc.insertString(doc.getLength(), "[" + timestamp + "] [" + sender + "]: " + message + "\n", style);
            chatArea.setCaretPosition(doc.getLength()); // 滚动到底部
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送普通消息
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message); // 发送到服务器
            appendMessage("我", message, Color.BLUE); // 显示在聊天区域
            inputField.setText(""); // 清空输入框
        }
    }

    /**
     * 打开广播窗口
     */
    private void openBroadcastWindow() {
        JDialog broadcastDialog = new JDialog(this, "选择广播用户", true);
        broadcastDialog.setSize(400, 500);
        broadcastDialog.setLayout(new BorderLayout());

        // 创建复选框列表
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        Map<String, JCheckBox> checkBoxes = new HashMap<>();
        for (int i = 0; i < userListModel.size(); i++) {
            String user = userListModel.getElementAt(i);
            if (!user.equals(Client.c_name)) { // 排除自己
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
                client.sendBroadcastMessage(targets.toString(), message); // 广播消息
                JOptionPane.showMessageDialog(broadcastDialog, "广播消息已发送！", "成功", JOptionPane.INFORMATION_MESSAGE);
                broadcastDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(broadcastDialog, "请选择用户并输入消息！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        broadcastDialog.setVisible(true);
    }

    /**
     * 打开或更新私聊窗口
     * @param sender 消息发送者
     * @param message 消息内容
     */
    private void openOrUpdatePrivateWindow(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            Client_GUI1 privateWindow = privateChatWindows.computeIfAbsent(sender,
                    key -> new Client_GUI1(client, key, () -> privateChatWindows.remove(key)));
            if (!message.isEmpty()) {
                privateWindow.appendMessage(sender, message, Color.ORANGE);
            }
        });
    }

    /**
     * 更新用户列表
     * @param users 在线用户字符串，用逗号分隔
     */
    private void updateUserList(String users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users.split(",")) {
                userListModel.addElement(user);
            }
        });
    }
}
