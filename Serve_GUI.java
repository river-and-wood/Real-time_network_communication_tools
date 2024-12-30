package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Serve_GUI extends JFrame {
    private final JTextPane textPane; // ��ʾ��������־
    private final StyledDocument doc;
    private final JTextField usernameField; // �û��������
    private final JButton disconnectButton; // �Ͽ���ť

    public Serve_GUI() {
        // ���ô��ڱ���ʹ�С
        setTitle("��������̨");
        setBounds(100, 150, 600, 650); // ���ڴ�С
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ���������
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240)); // ������ɫ

        // ���������ǩ
        JLabel ps = new JLabel("������״̬����");
        ps.setHorizontalAlignment(SwingConstants.CENTER);
        ps.setFont(new Font("΢���ź�", Font.BOLD, 20));  // ���ñ�������
        ps.setForeground(new Color(57, 182, 120));  // ���ñ�����ɫ
        panel.add(ps, BorderLayout.NORTH);

        // ������ʾ��־������
        textPane = new JTextPane();
        textPane.setEditable(false); // ��ֹ�༭
        textPane.setFocusable(false);
        doc = textPane.getStyledDocument(); // ��ȡ��ʽ�ĵ�

        JScrollPane scrollPane = new JScrollPane(textPane); // ��ӹ�����
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));  // ���ù������߿�
        panel.add(scrollPane, BorderLayout.CENTER);

        // ����������ť
        JButton refresh = new JButton("����");
        refresh.setFocusable(false);
        refresh.setPreferredSize(new Dimension(100, 35));
        refresh.setBackground(new Color(165, 169, 222));  // ���ð�ť������ɫ
        refresh.setFont(new Font("΢���ź�", Font.BOLD, 14));  // ��������
        refresh.addActionListener(e -> textPane.setText("")); // �����־
        panel.add(refresh, BorderLayout.SOUTH);

        // �����û��������ͶϿ���ť
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // ���þ��в��ֲ���Ӽ��

        JLabel usernameLabel = new JLabel("�����û�����");
        usernameLabel.setFont(new Font("΢���ź�", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(51, 51, 51));  // ����������ɫ

        usernameField = new JTextField(15); // �����ı�����
        usernameField.setFont(new Font("΢���ź�", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(150, 30));  // �����ı���ߴ�
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));  // ���ñ߿�

        disconnectButton = new JButton("�Ͽ��û�");
        disconnectButton.setBackground(new Color(217, 83, 79));  // ���ð�ť������ɫ
        disconnectButton.setForeground(Color.WHITE);  // ����������ɫ
        disconnectButton.setFont(new Font("΢���ź�", Font.PLAIN, 14));
        disconnectButton.setPreferredSize(new Dimension(120, 35)); // ���ð�ť�ߴ�
        disconnectButton.setFocusPainted(false); // ȥ�������
        disconnectButton.setBorder(BorderFactory.createEmptyBorder());  // ȥ����ť�߿�

        // ��ť����¼����Ͽ�ָ���û���������
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "�������û���", "��ʾ", JOptionPane.WARNING_MESSAGE);
                } else {
                    // ����ĶϿ��������Ը�����Ҫʵ�֣�������һ����������Ͽ�����
                    Serve.disconnectClient(username);
                    appendMessage("�û� " + username + " �ѶϿ����ӡ�\n", 14, Color.RED, StyleConstants.ALIGN_LEFT);
                    usernameField.setText("");  // ����ı���
                }
            }
        });

        // ���ؼ���ӵ��������
        controlPanel.add(usernameLabel);
        controlPanel.add(usernameField);
        controlPanel.add(disconnectButton);

        panel.add(controlPanel, BorderLayout.NORTH); // �ѿؼ������ӵ������

        add(panel);
        setVisible(true);
    }

    // ������־��Ϣ
    public void appendMessage(String msg, int size, Color color, int alignment) {
        try {
            Style style = textPane.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // ����������ɫ
            StyleConstants.setFontSize(style, size); // ���������С
            StyleConstants.setAlignment(style, alignment); // ���ö��뷽ʽ
            doc.insertString(doc.getLength(), msg + "\n", style); // ��������
            // ���ö���Ķ��뷽ʽ
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attrs, alignment);
            doc.setParagraphAttributes(doc.getLength(), 1, attrs, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Serve_GUI();
    }
}
