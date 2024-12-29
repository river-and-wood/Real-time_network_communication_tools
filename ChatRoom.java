package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class ChatRoom extends JFrame {
    private final JTextPane chatArea;
    private final DefaultListModel<String> userListModel;
    private final StyledDocument doc;

    public ChatRoom() {
        setTitle("实时网络通讯工具");
        setBounds(300, 100, 600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 使用 BorderLayout 布局
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 聊天内容区域
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // 用户列表区域
        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(userScrollPane, BorderLayout.EAST);

        // 底部输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("发送");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                appendMessage("我", message, Color.BLUE);
                inputField.setText("");
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    public void addUser(String username) {
        if (!userListModel.contains(username)) {
            userListModel.addElement(username);
        }
    }

    public void appendMessage(String sender, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color);
            doc.insertString(doc.getLength(), "[" + sender + "]: " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
