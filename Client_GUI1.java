package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client_GUI1 ������ʵ��˽�Ĵ��ڣ�ÿ���û���˽�ĻỰ�������С�
 */
public class Client_GUI1 extends JFrame {
    private final JTextPane chatArea; // ����������ʾ����
    private final JTextField inputField; // �����
    private final StyledDocument doc; // �������ݵ���ʽ�ĵ�
    private final Client client; // �ͻ���ͨ���߼�����
    private final String targetUser; // ˽�ĵ�Ŀ���û�
    private final Runnable onCloseCallback; // ���ڹر�ʱ�Ļص�����

    /**
     * ���캯������ʼ��˽�Ĵ���
     * @param client �ͻ��˶���
     * @param targetUser ˽��Ŀ���û�
     * @param onCloseCallback ���ڹر�ʱ�Ļص�����
     */
    public Client_GUI1(Client client, String targetUser, Runnable onCloseCallback) {
        this.client = client;
        this.targetUser = targetUser; // ����Ŀ���û���㲥��ʶ
        this.onCloseCallback = onCloseCallback;

        setTitle(client.c_name + "�� " + targetUser + " ������"); // ���ô��ڱ���
        setBounds(450, 150, 450, 400); // ���ô��ڴ�С
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ���ùر�ʱ�Ĳ���

        // ��Ӵ��ڹرռ�����
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (onCloseCallback != null) {
                    onCloseCallback.run(); // ִ�йرջص�
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // ���ñ�����ɫ

        // ������������
        chatArea = new JTextPane();
        chatArea.setEditable(false); // ����Ϊ���ɱ༭
        chatArea.setBackground(new Color(245, 245, 245)); // �����������򱳾�ɫ
        chatArea.setFont(new Font("΢���ź�", Font.PLAIN, 14)); // ��������
        doc = chatArea.getStyledDocument();

        JScrollPane chatScrollPane = new JScrollPane(chatArea); // ��ӹ�����
        chatScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // ���ñ߿�
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // ���������ͷ��Ͱ�ť
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(240, 240, 240));

        inputField = new JTextField();
        inputField.setFont(new Font("΢���ź�", Font.PLAIN, 14)); // �������������
        inputField.setBackground(new Color(255, 255, 255)); // ��������򱳾�ɫ
        inputField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // ���ñ߿�
        inputField.setPreferredSize(new Dimension(300, 30)); // ����������С

        JButton sendButton = new JButton("����");
        sendButton.setFont(new Font("΢���ź�", Font.PLAIN, 14));
        sendButton.setBackground(new Color(58, 129, 255)); // ��ť����ɫ
        sendButton.setForeground(Color.WHITE); // ��ť������ɫ
        sendButton.setFocusPainted(false); // ȥ�������
        sendButton.setPreferredSize(new Dimension(100, 30)); // ���ð�ť�ߴ�

        // ��Ӱ�ť����¼�
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
        setVisible(true); // ���ô��ڿɼ�
    }

    /**
     * ����������׷����Ϣ
     * @param username ��Ϣ������
     * @param message ��Ϣ����
     * @param color ��Ϣ��ʾ��ɫ
     */
    public void appendMessage(String username, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // ����������ɫ
            StyleConstants.setBold(style, true); // ���üӴ�����Ч��

            // ��ȡʱ���
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            doc.insertString(doc.getLength(), "[" + timestamp + "] [" + username + "]: " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * ������Ϣ
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

            if (targetUser.startsWith("Broadcast from ")) {
                // ����ǹ㲥��Ϣ���ڣ��ظ����㲥������
                String sender = targetUser.substring("Broadcast from ".length());
                client.sendPrivateMessage(sender, message); // �ظ����㲥������
                appendMessage("��", message, new Color(58, 129, 255)); // ��������Ϣʹ����ɫ
            } else {
                // ��ͨ˽����Ϣ
                client.sendPrivateMessage(targetUser, message); // ����˽����Ϣ
                appendMessage("��", message, new Color(58, 129, 255)); // ��������Ϣʹ����ɫ
            }
            inputField.setText(""); // ��������
        }
    }
}
