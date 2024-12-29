package GUI_Socket;

import javax.swing.*;
import java.awt.*;

public class Login_GUI extends JFrame {
    public Login_GUI() {
        setTitle("�û���¼");
        setBounds(400, 200, 350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel label = new JLabel("�������û�����");
        label.setFont(new Font("����", Font.PLAIN, 16));
        label.setBounds(30, 40, 120, 25);
        panel.add(label);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 40, 150, 25);
        panel.add(usernameField);

        JButton confirmButton = new JButton("ȷ��");
        confirmButton.setBounds(80, 100, 80, 30);
        panel.add(confirmButton);

        JButton exitButton = new JButton("�˳�");
        exitButton.setBounds(180, 100, 80, 30);
        panel.add(exitButton);

        confirmButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "�û�������Ϊ�գ�", "����", JOptionPane.ERROR_MESSAGE);
            } else {
                Client.c_name = username; // �����û���
                Client client = new Client(); // ��ʼ���ͻ���
                if (client.connected) { // ����ɹ�����
                    new Client_GUI(client); // �������������
                    dispose(); // �رյ�¼����
                }
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login_GUI::new); // ����ӵ�¼��������
    }
}
