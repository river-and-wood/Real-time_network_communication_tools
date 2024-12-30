package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ChatRoom���ǻ���Swing�������ҽ��棬����չʾ�������ݺ������û��б�
 */
public class ChatRoom extends JFrame {

    // ������������
    private final JTextPane chatArea; // ������ʾ�������ݵ��ı�����
    private final DefaultListModel<String> userListModel; // �����û��б��ģ��
    private final StyledDocument doc; // �����������ʽ�ĵ�

    /**
     * ���캯������ʼ�������ҽ���
     */
    public ChatRoom() {
        setTitle("ʵʱ����ͨѶ����"); // ���ô��ڱ���
        setBounds(300, 100, 600, 500); // ���ô��ڴ�С��λ��
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ���ùرղ���

        // ʹ��BorderLayout����
        JPanel mainPanel = new JPanel(new BorderLayout());

        // ������������
        chatArea = new JTextPane(); // ���������ı�����
        chatArea.setEditable(false); // ����Ϊ���ɱ༭
        doc = chatArea.getStyledDocument(); // ��ȡ��ʽ�ĵ�
        JScrollPane chatScrollPane = new JScrollPane(chatArea); // ��ӹ�����
        mainPanel.add(chatScrollPane, BorderLayout.CENTER); // ������������ӵ�����������

        // �û��б�����
        userListModel = new DefaultListModel<>(); // ��ʼ���û��б�ģ��
        JList<String> userList = new JList<>(userListModel); // �����û��б�
        JScrollPane userScrollPane = new JScrollPane(userList); // ��ӹ�����
        userScrollPane.setPreferredSize(new Dimension(150, 0)); // ���ÿ��
        mainPanel.add(userScrollPane, BorderLayout.EAST); // ���û��б���ӵ�������Ҳ�

        // ��������ұ���
        JLabel roomTitleLabel = new JLabel("�����ң�ʵʱ����ͨѶ����", JLabel.CENTER); // ���������ǩ
        roomTitleLabel.setFont(new Font("Arial", Font.BOLD, 16)); // ��������
        mainPanel.add(roomTitleLabel, BorderLayout.NORTH); // ��ӱ��⵽����嶥��

        // �ײ���������
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField(); // ���������
        JButton sendButton = new JButton("����"); // �������Ͱ�ť
        inputPanel.add(inputField, BorderLayout.CENTER); // �����������м�
        inputPanel.add(sendButton, BorderLayout.EAST); // ����ť�����Ҳ�
        mainPanel.add(inputPanel, BorderLayout.SOUTH); // ������������ӵ��ײ�

        // ���������Ļس����¼�
        inputField.addActionListener(e -> {
            String message = inputField.getText().trim(); // ��ȡ�������ı�
            if (!message.isEmpty()) {
                appendMessage("��", message, Color.BLUE); // ����Ϣ��ʾ����������
                inputField.setText(""); // ��������
            }
        });

        // �������Ͱ�ť�ĵ���¼�
        sendButton.addActionListener(e -> {
            String message = inputField.getText().trim(); // ��ȡ�������ı�
            if (!message.isEmpty()) {
                appendMessage("��", message, Color.BLUE); // ����Ϣ��ʾ����������
                inputField.setText(""); // ��������
            }
        });

        add(mainPanel); // �������嵽����
        setVisible(true); // ���ô��ڿɼ�
    }

    /**
     * ������û����û��б�
     * @param username �û���
     */
    public void addUser(String username) {
        if (!userListModel.contains(username)) {
            userListModel.addElement(username); // ���û�����ӵ��б�
        }
    }

    /**
     * ���������������Ϣ������ʱ���
     * @param sender ������
     * @param message ��Ϣ����
     * @param color ��ʾ����ɫ
     */
    public void appendMessage(String sender, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // ����������ɫ

            // ��ȡ��ǰʱ���
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

            // ��ʽ����Ϣ��ʾ
            doc.insertString(doc.getLength(), "[" + timestamp + "] [" + sender + "]: " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * ���û��б����Ƴ��û�
     * @param username �û���
     */
    public void removeUser(String username) {
        if (userListModel.contains(username)) {
            userListModel.removeElement(username); // ���б����Ƴ��û���
        }
    }

    /**
     * ��������¼
     */
    public void clearChat() {
        chatArea.setText(""); // �����������
    }

    /**
     * ���������ҵı���
     * @param roomName �µ�����������
     */
    public void updateRoomTitle(String roomName) {
        JLabel roomTitleLabel = (JLabel) getContentPane().getComponent(0); // ��ȡ�����ǩ
        roomTitleLabel.setText("�����ң�" + roomName); // ���±�������
    }
}
