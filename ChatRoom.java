package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class ChatRoom extends JFrame {
    private final JTextPane chatArea;
    private final DefaultListModel<String> userListModel;
    private final StyledDocument doc;

    public ChatRoom() {
        setTitle("ʵʱ����ͨѶ����");
        setBounds(300, 100, 600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ʹ�� BorderLayout ����
        JPanel mainPanel = new JPanel(new BorderLayout());

        // ������������
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        doc = chatArea.getStyledDocument();
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // �û��б�����
        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(userScrollPane, BorderLayout.EAST);

        // �ײ���������
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("����");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                appendMessage("��", message, Color.BLUE);
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
