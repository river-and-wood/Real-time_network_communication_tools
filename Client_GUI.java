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
 * Client_GUI ���ǿͻ��˵������棬��ʾȺ�����ݡ��û��б���֧�ֹ㲥��˽�Ĺ��ܡ�
 */
public class Client_GUI extends JFrame {
    private final JTextPane chatArea; // ����������ʾȺ����Ϣ
    private final JTextField inputField; // ��Ϣ�����
    private final DefaultListModel<String> userListModel; // �����û��б������ģ��
    private final JList<String> userList; // ��ʾ�����û����б����
    private final StyledDocument doc; // �����������ʽ�ĵ�
    private final Client client; // �ͻ���ͨ���߼�����
    private final Map<String, Client_GUI1> privateChatWindows = new HashMap<>(); // �洢˽�Ĵ��ڵ�Map

    /**
     * ���캯������ʼ���ͻ��˽���
     * @param client �ͻ���ͨ�Ŷ���
     */
    public Client_GUI(Client client) {
        this.client = client;

        // ����ͻ���δ���ӣ��˳�����
        if (!client.connected) {
            System.exit(0);
        }

        setTitle("ʵʱ����ͨѶ���� - �û�: " + Client.c_name); // ���ô��ڱ���
        setBounds(300, 100, 900, 600); // ���ô��ڴ�С��λ��
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // �رմ���ʱ�˳�����

        JPanel mainPanel = new JPanel(new BorderLayout());

        // ������������
        chatArea = new JTextPane(); // ������������
        chatArea.setEditable(false); // ����Ϊ���ɱ༭
        doc = chatArea.getStyledDocument(); // ��ȡ��ʽ�ĵ�
        JScrollPane chatScrollPane = new JScrollPane(chatArea); // ��ӹ�����
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // ������ʾ������
        mainPanel.add(chatScrollPane, BorderLayout.CENTER); // ������������ӵ�������м�

        // �û��б�����
        userListModel = new DefaultListModel<>(); // ��ʼ���û��б�ģ��
        userList = new JList<>(userListModel); // �����û��б�
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // ����Ϊ��ѡ
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // ˫���û��б��е�ĳ���û�
                    String selectedUser = userList.getSelectedValue();
                    if (selectedUser != null && !selectedUser.equals(Client.c_name)) {
                        openOrUpdatePrivateWindow(selectedUser, ""); // ��˽�Ĵ���
                    }
                }
            }
        });
        JScrollPane userScrollPane = new JScrollPane(userList); // ��ӹ�����
        userScrollPane.setPreferredSize(new Dimension(200, 0)); // ���ÿ��
        mainPanel.add(userScrollPane, BorderLayout.EAST); // ���û��б���ӵ��Ҳ�

        // �ײ������Ͱ�ť����
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField(); // ���������
        inputField.setPreferredSize(new Dimension(400, 30)); // ����������С
        JButton sendButton = new JButton("����"); // �������Ͱ�ť
        JButton broadcastButton = new JButton("�㲥"); // �����㲥��ť

        // ��ʽ����
        sendButton.setBackground(new Color(58, 129, 255));
        sendButton.setForeground(Color.WHITE);
        broadcastButton.setBackground(new Color(58, 129, 255));
        broadcastButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // ��ť���
        buttonPanel.add(broadcastButton);
        buttonPanel.add(sendButton);

        inputPanel.add(inputField, BorderLayout.CENTER); // ���������ӵ��м�
        inputPanel.add(buttonPanel, BorderLayout.EAST); // ����ť�����ӵ��Ҳ�
        mainPanel.add(inputPanel, BorderLayout.SOUTH); // ������������ӵ��ײ�

        sendButton.addActionListener(e -> sendMessage()); // ���Ͱ�ť������
        broadcastButton.addActionListener(e -> openBroadcastWindow()); // �㲥��ť������

        add(mainPanel); // �������嵽����
        setVisible(true); // ���ô��ڿɼ�

        // ���������̣߳����ڽ������Է���������Ϣ
        new Thread(() -> {
            try {
                String response;
                while ((response = client.in.readLine()) != null) {
                    if (response.startsWith("USERLIST:")) { // �����û��б�
                        updateUserList(response.substring(9));
                    } else if (response.startsWith("BROADCAST:")) { // ���յ��㲥��Ϣ
                        String[] parts = response.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String message = parts[2];
                            openOrUpdatePrivateWindow("Broadcast from " + sender, message);
                        }
                    } else if (response.startsWith("PRIVATE:")) { // ���յ�˽����Ϣ
                        String[] parts = response.split(":", 3);
                        if (parts.length == 3) {
                            String sender = parts[1];
                            String message = parts[2];
                            openOrUpdatePrivateWindow(sender, message);
                        }
                    } else {
                        appendMessage("������", response, Color.BLACK); // ��ʾ������Ϣ
                    }
                }
            } catch (IOException e) {
                appendMessage("ϵͳ", "���ӵ�������ʱ��������", Color.RED);
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * ����������׷����Ϣ
     * @param sender ������
     * @param message ��Ϣ����
     * @param color ��Ϣ��ʾ����ɫ
     */
    private void appendMessage(String sender, String message, Color color) {
        try {
            Style style = chatArea.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // ����������ɫ

            // ��ȡʱ���
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            doc.insertString(doc.getLength(), "[" + timestamp + "] [" + sender + "]: " + message + "\n", style);
            chatArea.setCaretPosition(doc.getLength()); // �������ײ�
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * ������ͨ��Ϣ
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message); // ���͵�������
            appendMessage("��", message, Color.BLUE); // ��ʾ����������
            inputField.setText(""); // ��������
        }
    }

    /**
     * �򿪹㲥����
     */
    private void openBroadcastWindow() {
        JDialog broadcastDialog = new JDialog(this, "ѡ��㲥�û�", true);
        broadcastDialog.setSize(400, 500);
        broadcastDialog.setLayout(new BorderLayout());

        // ������ѡ���б�
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        Map<String, JCheckBox> checkBoxes = new HashMap<>();
        for (int i = 0; i < userListModel.size(); i++) {
            String user = userListModel.getElementAt(i);
            if (!user.equals(Client.c_name)) { // �ų��Լ�
                JCheckBox checkBox = new JCheckBox(user);
                userPanel.add(checkBox);
                checkBoxes.put(user, checkBox);
            }
        }
        JScrollPane userScrollPane = new JScrollPane(userPanel);
        broadcastDialog.add(userScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel("��Ϣ���ݣ�");
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("����");
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
                targets.setLength(targets.length() - 1); // �Ƴ����һ������
            }
            String message = messageField.getText().trim();
            if (!message.isEmpty() && targets.length() > 0) {
                client.sendBroadcastMessage(targets.toString(), message); // �㲥��Ϣ
                JOptionPane.showMessageDialog(broadcastDialog, "�㲥��Ϣ�ѷ��ͣ�", "�ɹ�", JOptionPane.INFORMATION_MESSAGE);
                broadcastDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(broadcastDialog, "��ѡ���û���������Ϣ��", "����", JOptionPane.ERROR_MESSAGE);
            }
        });

        broadcastDialog.setVisible(true);
    }

    /**
     * �򿪻����˽�Ĵ���
     * @param sender ��Ϣ������
     * @param message ��Ϣ����
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
     * �����û��б�
     * @param users �����û��ַ������ö��ŷָ�
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
