package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client_GUI1 类用于实现私聊窗口，每个用户的私聊会话独立运行。
 */
public class Client_GUI1 extends JFrame {
    private final JTextPane chatArea; // 聊天内容显示区域
    private final JTextField inputField; // 输入框
    private final StyledDocument doc; // 聊天内容的样式文档
    private final Client client; // 客户端通信逻辑对象
    private final String targetUser; // 私聊的目标用户
    private final Runnable onCloseCallback; // 窗口关闭时的回调函数

    /**
     * 构造函数：初始化私聊窗口
     * @param client 客户端对象
     * @param targetUser 私聊目标用户
     * @param onCloseCallback 窗口关闭时的回调函数
     */
    public Client_GUI1(Client client, String targetUser, Runnable onCloseCallback) {
        this.client = client;
        this.targetUser = targetUser; // 保存目标用户或广播标识
        this.onCloseCallback = onCloseCallback;

        setTitle(client.c_name + "与 " + targetUser + " 的聊天"); // 设置窗口标题
        setBounds(450, 150, 450, 400); // 设置窗口大小
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 设置关闭时的操作

        // 添加窗口关闭监听器
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (onCloseCallback != null) {
                    onCloseCallback.run(); // 执行关闭回调
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // 设置背景颜色

        // 创建聊天区域
        chatArea = new JTextPane();
        chatArea.setEditable(false); // 设置为不可编辑
        chatArea.setBackground(new Color(245, 245, 245)); // 设置聊天区域背景色
        chatArea.setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 设置字体
        doc = chatArea.getStyledDocument();

        JScrollPane chatScrollPane = new JScrollPane(chatArea); // 添加滚动条
        chatScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // 设置边框
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // 创建输入框和发送按钮
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(240, 240, 240));

        inputField = new JTextField();
        inputField.setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 设置输入框字体
        inputField.setBackground(new Color(255, 255, 255)); // 设置输入框背景色
        inputField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // 设置边框
        inputField.setPreferredSize(new Dimension(300, 30)); // 设置输入框大小

        JButton sendButton = new JButton("发送");
        sendButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        sendButton.setBackground(new Color(58, 129, 255)); // 按钮背景色
        sendButton.setForeground(Color.WHITE); // 按钮文字颜色
        sendButton.setFocusPainted(false); // 去除焦点框
        sendButton.setPreferredSize(new Dimension(100, 30)); // 设置按钮尺寸

        // 添加按钮点击事件
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true); // 设置窗口可见
    }

    /**
     * 向聊天区域追加消息
     * @param username 消息发送者
     * @param message 消息内容
     * @param color 消息显示颜色
     */
    public void appendMessage(String username, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // 设置字体颜色
            StyleConstants.setBold(style, true); // 设置加粗字体效果

            // 获取时间戳
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            doc.insertString(doc.getLength(), "[" + timestamp + "] [" + username + "]: " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

            if (targetUser.startsWith("Broadcast from ")) {
                // 如果是广播消息窗口，回复给广播发送者
                String sender = targetUser.substring("Broadcast from ".length());
                client.sendPrivateMessage(sender, message); // 回复给广播发送者
                appendMessage("我", message, new Color(58, 129, 255)); // 发出的消息使用蓝色
            } else {
                // 普通私聊消息
                client.sendPrivateMessage(targetUser, message); // 发送私聊消息
                appendMessage("我", message, new Color(58, 129, 255)); // 发出的消息使用蓝色
            }
            inputField.setText(""); // 清空输入框
        }
    }
}
