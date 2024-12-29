package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class Client_GUI1 extends JFrame {
    private final JTextPane chatArea;
    private final JTextField inputField;
    private final StyledDocument doc;
    private final Client client;
    private final String targetUser; // 目标用户或广播标识
    private final Runnable onCloseCallback;

    public Client_GUI1(Client client, String targetUser, Runnable onCloseCallback) {
        this.client = client;
        this.targetUser = targetUser; // 保存目标用户或广播标识
        this.onCloseCallback = onCloseCallback;

        setTitle("与 " + targetUser + " 的聊天");
        setBounds(450, 150, 400, 300);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("发送");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        add(mainPanel);
        setVisible(true);
    }

    public void appendMessage(String username, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color);
            doc.insertString(doc.getLength(), "[" + username + "]: " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            if (targetUser.startsWith("Broadcast from ")) {
                // 如果是广播消息窗口，发送私聊消息给广播发送者
                String sender = targetUser.substring("Broadcast from ".length());
                client.sendPrivateMessage(sender, message); // 回复给广播发送者
                appendMessage("我", message, Color.BLUE);
            } else {
                // 普通私聊消息
                client.sendPrivateMessage(targetUser, message); // 发送私聊消息
                appendMessage("我", message, Color.BLUE);
            }
            inputField.setText("");
        }
    }
}
