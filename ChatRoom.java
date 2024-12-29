package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ChatRoom类是基于Swing的聊天室界面，用于展示聊天内容和在线用户列表。
 */
public class ChatRoom extends JFrame {

    // 聊天内容区域
    private final JTextPane chatArea; // 用于显示聊天内容的文本区域
    private final DefaultListModel<String> userListModel; // 在线用户列表的模型
    private final StyledDocument doc; // 聊天区域的样式文档

    /**
     * 构造函数：初始化聊天室界面
     */
    public ChatRoom() {
        setTitle("实时网络通讯工具"); // 设置窗口标题
        setBounds(300, 100, 600, 500); // 设置窗口大小和位置
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭操作

        // 使用BorderLayout布局
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 聊天内容区域
        chatArea = new JTextPane(); // 创建聊天文本区域
        chatArea.setEditable(false); // 设置为不可编辑
        doc = chatArea.getStyledDocument(); // 获取样式文档
        JScrollPane chatScrollPane = new JScrollPane(chatArea); // 添加滚动条
        mainPanel.add(chatScrollPane, BorderLayout.CENTER); // 将聊天区域添加到主面板的中心

        // 用户列表区域
        userListModel = new DefaultListModel<>(); // 初始化用户列表模型
        JList<String> userList = new JList<>(userListModel); // 创建用户列表
        JScrollPane userScrollPane = new JScrollPane(userList); // 添加滚动条
        userScrollPane.setPreferredSize(new Dimension(150, 0)); // 设置宽度
        mainPanel.add(userScrollPane, BorderLayout.EAST); // 将用户列表添加到主面板右侧

        // 添加聊天室标题
        JLabel roomTitleLabel = new JLabel("聊天室：实时网络通讯工具", JLabel.CENTER); // 创建标题标签
        roomTitleLabel.setFont(new Font("Arial", Font.BOLD, 16)); // 设置字体
        mainPanel.add(roomTitleLabel, BorderLayout.NORTH); // 添加标题到主面板顶部

        // 底部输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField(); // 创建输入框
        JButton sendButton = new JButton("发送"); // 创建发送按钮
        inputPanel.add(inputField, BorderLayout.CENTER); // 将输入框放在中间
        inputPanel.add(sendButton, BorderLayout.EAST); // 将按钮放在右侧
        mainPanel.add(inputPanel, BorderLayout.SOUTH); // 将输入区域添加到底部

        // 监听输入框的回车键事件
        inputField.addActionListener(e -> {
            String message = inputField.getText().trim(); // 获取输入框的文本
            if (!message.isEmpty()) {
                appendMessage("我", message, Color.BLUE); // 将消息显示在聊天区域
                inputField.setText(""); // 清空输入框
            }
        });

        // 监听发送按钮的点击事件
        sendButton.addActionListener(e -> {
            String message = inputField.getText().trim(); // 获取输入框的文本
            if (!message.isEmpty()) {
                appendMessage("我", message, Color.BLUE); // 将消息显示在聊天区域
                inputField.setText(""); // 清空输入框
            }
        });

        add(mainPanel); // 添加主面板到窗口
        setVisible(true); // 设置窗口可见
    }

    /**
     * 添加新用户到用户列表
     * @param username 用户名
     */
    public void addUser(String username) {
        if (!userListModel.contains(username)) {
            userListModel.addElement(username); // 将用户名添加到列表
        }
    }

    /**
     * 向聊天区域添加消息，包括时间戳
     * @param sender 发送者
     * @param message 消息内容
     * @param color 显示的颜色
     */
    public void appendMessage(String sender, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // 设置字体颜色

            // 获取当前时间戳
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

            // 格式化消息显示
            doc.insertString(doc.getLength(), "[" + timestamp + "] [" + sender + "]: " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从用户列表中移除用户
     * @param username 用户名
     */
    public void removeUser(String username) {
        if (userListModel.contains(username)) {
            userListModel.removeElement(username); // 从列表中移除用户名
        }
    }

    /**
     * 清空聊天记录
     */
    public void clearChat() {
        chatArea.setText(""); // 清空聊天区域
    }

    /**
     * 更新聊天室的标题
     * @param roomName 新的聊天室名称
     */
    public void updateRoomTitle(String roomName) {
        JLabel roomTitleLabel = (JLabel) getContentPane().getComponent(0); // 获取标题标签
        roomTitleLabel.setText("聊天室：" + roomName); // 更新标题内容
    }
}
