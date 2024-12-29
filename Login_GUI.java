package GUI_Socket;

import javax.swing.*;
import java.awt.*;

public class Login_GUI extends JFrame {
    public Login_GUI() {
        setTitle("用户登录");
        setBounds(400, 200, 350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel label = new JLabel("请输入用户名：");
        label.setFont(new Font("宋体", Font.PLAIN, 16));
        label.setBounds(30, 40, 120, 25);
        panel.add(label);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 40, 150, 25);
        panel.add(usernameField);

        JButton confirmButton = new JButton("确认");
        confirmButton.setBounds(80, 100, 80, 30);
        panel.add(confirmButton);

        JButton exitButton = new JButton("退出");
        exitButton.setBounds(180, 100, 80, 30);
        panel.add(exitButton);

        confirmButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            } else {
                Client.c_name = username; // 设置用户名
                Client client = new Client(); // 初始化客户端
                if (client.connected) { // 如果成功连接
                    new Client_GUI(client); // 进入主聊天界面
                    dispose(); // 关闭登录窗口
                }
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login_GUI::new); // 程序从登录界面启动
    }
}
