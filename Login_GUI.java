package GUI_Socket;

import javax.swing.*;
import java.awt.*;

/**
 * Login_GUI �����û���¼���棬���������û��������ӵ���������
 */
public class Login_GUI extends JFrame {
    public Login_GUI() {
        // ���ô��ڱ���ʹ�С
        setTitle("�û���¼");
        setBounds(400, 200, 350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // �������
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 240, 240)); // ���ñ�����ɫ

        // ���ñ����ǩ
        JLabel label = new JLabel("�������û�����");
        label.setFont(new Font("΢���ź�", Font.PLAIN, 16)); // ��������
        label.setBounds(30, 40, 120, 25);
        label.setForeground(new Color(51, 51, 51)); // ����������ɫ
        panel.add(label);

        // ���������
        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 40, 150, 25);
        usernameField.setFont(new Font("΢���ź�", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // ���ñ߿�
        panel.add(usernameField);

        // ����ȷ�ϰ�ť
        JButton confirmButton = new JButton("ȷ��");
        confirmButton.setBounds(50, 100, 80, 30);
        confirmButton.setFont(new Font("΢���ź�", Font.PLAIN, 14));
        confirmButton.setBackground(new Color(30, 144, 255)); // ���ð�ť����ɫ
        confirmButton.setForeground(Color.WHITE); // ����������ɫ
        confirmButton.setFocusPainted(false); // ȥ�������
        confirmButton.setBorder(BorderFactory.createEmptyBorder()); // ȥ���߿�
        confirmButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // ���������ʽ
        confirmButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "�û�������Ϊ�գ�", "����", JOptionPane.ERROR_MESSAGE);
            } else {
                Client.c_name = username; // �����û���
                Client client = new Client(); // ��ʼ���ͻ���
                if (client.connected) { // ����ɹ�����
                    new Client_GUI(client); // �������������
                    client.sendUsername(); // �����û���
                    dispose(); // �رյ�¼����
                }
            }
        });
        panel.add(confirmButton);

        // �����˳���ť
        JButton exitButton = new JButton("�˳�");
        exitButton.setBounds(180, 100, 80, 30);
        exitButton.setFont(new Font("΢���ź�", Font.PLAIN, 14));
        exitButton.setBackground(new Color(255, 69, 0)); // �����˳���ť��ɫ
        exitButton.setForeground(Color.WHITE); // ����������ɫ
        exitButton.setFocusPainted(false); // ȥ�������
        exitButton.setBorder(BorderFactory.createEmptyBorder()); // ȥ���߿�
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // ���������ʽ
        exitButton.addActionListener(e -> System.exit(0));
        panel.add(exitButton);

        // ���ô��ھ�����ʾ
        setContentPane(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login_GUI::new); // ����ӵ�¼��������
    }
}
