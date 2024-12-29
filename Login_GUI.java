package GUI_Socket;

import javax.swing.*;
import java.awt.*;

/**
 * Login_GUI 类是用户登录界面，用于输入用户名并连接到服务器。
 */
public class Login_GUI extends JFrame {
    public Login_GUI() {
        // 设置窗口标题和大小
        setTitle("用户登录");
        setBounds(400, 200, 350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建面板
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 240, 240)); // 设置背景颜色

        // 设置标题标签
        JLabel label = new JLabel("请输入用户名：");
        label.setFont(new Font("微软雅黑", Font.PLAIN, 16)); // 设置字体
        label.setBounds(30, 40, 120, 25);
        label.setForeground(new Color(51, 51, 51)); // 设置字体颜色
        panel.add(label);

        // 设置输入框
        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 40, 150, 25);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // 设置边框
        panel.add(usernameField);

        // 设置确认按钮
        JButton confirmButton = new JButton("确认");
        confirmButton.setBounds(50, 100, 80, 30);
        confirmButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        confirmButton.setBackground(new Color(30, 144, 255)); // 设置按钮背景色
        confirmButton.setForeground(Color.WHITE); // 设置文字颜色
        confirmButton.setFocusPainted(false); // 去除焦点框
        confirmButton.setBorder(BorderFactory.createEmptyBorder()); // 去除边框
        confirmButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // 设置鼠标样式
        confirmButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            } else {
                Client.c_name = username; // 设置用户名
                Client client = new Client(); // 初始化客户端
                if (client.connected) { // 如果成功连接
                    new Client_GUI(client); // 进入主聊天界面
                    client.sendUsername(); // 发送用户名
                    dispose(); // 关闭登录窗口
                }
            }
        });
        panel.add(confirmButton);

        // 设置退出按钮
        JButton exitButton = new JButton("退出");
        exitButton.setBounds(180, 100, 80, 30);
        exitButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitButton.setBackground(new Color(255, 69, 0)); // 设置退出按钮颜色
        exitButton.setForeground(Color.WHITE); // 设置文字颜色
        exitButton.setFocusPainted(false); // 去除焦点框
        exitButton.setBorder(BorderFactory.createEmptyBorder()); // 去除边框
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // 设置鼠标样式
        exitButton.addActionListener(e -> System.exit(0));
        panel.add(exitButton);

        // 设置窗口居中显示
        setContentPane(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login_GUI::new); // 程序从登录界面启动
    }
}
