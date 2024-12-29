package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class Client_GUI1 extends JFrame {
    private final JTextPane chatArea;
    private final JTextField inputField;
    private final StyledDocument doc;
    private final Client client;
    private final Runnable onCloseCallback;

    public Client_GUI1(Client client, String title, Runnable onCloseCallback) {
        this.client = client;
        this.onCloseCallback = onCloseCallback;

        setTitle(title);
        setBounds(400, 150, 400, 300);

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
        JButton sendButton = new JButton("∑¢ÀÕ");
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendPrivateMessage());

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

    private void sendPrivateMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            appendMessage("Œ“", message, Color.BLUE);
            inputField.setText("");
        }
    }
}
