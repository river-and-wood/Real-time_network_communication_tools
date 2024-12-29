package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Serve_GUI extends JFrame {
    private final JTextPane textPane; // 显示服务器日志
    private final StyledDocument doc;
    private final JTextField usernameField; // 用户名输入框
    private final JButton disconnectButton; // 断开按钮

    public Serve_GUI() {
        // 设置窗口标题和大小
        setTitle("服务器后台");
        setBounds(100, 150, 600, 650); // 窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建主面板
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240)); // 背景颜色

        // 创建标题标签
        JLabel ps = new JLabel("服务器状态监视");
        ps.setHorizontalAlignment(SwingConstants.CENTER);
        ps.setFont(new Font("微软雅黑", Font.BOLD, 20));  // 设置标题字体
        ps.setForeground(new Color(57, 182, 120));  // 设置标题颜色
        panel.add(ps, BorderLayout.NORTH);

        // 创建显示日志的区域
        textPane = new JTextPane();
        textPane.setEditable(false); // 禁止编辑
        textPane.setFocusable(false);
        doc = textPane.getStyledDocument(); // 获取样式文档

        JScrollPane scrollPane = new JScrollPane(textPane); // 添加滚动条
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));  // 设置滚动面板边框
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建清屏按钮
        JButton refresh = new JButton("清屏");
        refresh.setFocusable(false);
        refresh.setPreferredSize(new Dimension(100, 35));
        refresh.setBackground(new Color(165, 169, 222));  // 设置按钮背景颜色
        refresh.setFont(new Font("微软雅黑", Font.BOLD, 14));  // 设置字体
        refresh.addActionListener(e -> textPane.setText("")); // 清空日志
        panel.add(refresh, BorderLayout.SOUTH);

        // 创建用户名输入框和断开按钮
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); // 设置居中布局并添加间距

        JLabel usernameLabel = new JLabel("输入用户名：");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(51, 51, 51));  // 设置字体颜色

        usernameField = new JTextField(15); // 设置文本框宽度
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(150, 30));  // 设置文本框尺寸
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));  // 设置边框

        disconnectButton = new JButton("断开用户");
        disconnectButton.setBackground(new Color(217, 83, 79));  // 设置按钮背景颜色
        disconnectButton.setForeground(Color.WHITE);  // 设置文字颜色
        disconnectButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        disconnectButton.setPreferredSize(new Dimension(120, 35)); // 设置按钮尺寸
        disconnectButton.setFocusPainted(false); // 去除焦点框
        disconnectButton.setBorder(BorderFactory.createEmptyBorder());  // 去除按钮边框

        // 按钮点击事件，断开指定用户名的连接
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请输入用户名", "提示", JOptionPane.WARNING_MESSAGE);
                } else {
                    // 这里的断开操作可以根据需要实现，假设有一个方法处理断开连接
                    Serve.disconnectClient(username);
                    appendMessage("用户 " + username + " 已断开连接。\n", 14, Color.RED, StyleConstants.ALIGN_LEFT);
                    usernameField.setText("");  // 清空文本框
                }
            }
        });

        // 将控件添加到控制面板
        controlPanel.add(usernameLabel);
        controlPanel.add(usernameField);
        controlPanel.add(disconnectButton);

        panel.add(controlPanel, BorderLayout.NORTH); // 把控件面板添加到主面板

        add(panel);
        setVisible(true);
    }

    // 更新日志信息
    public void appendMessage(String msg, int size, Color color, int alignment) {
        try {
            Style style = textPane.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // 设置文字颜色
            StyleConstants.setFontSize(style, size); // 设置字体大小
            StyleConstants.setAlignment(style, alignment); // 设置对齐方式
            doc.insertString(doc.getLength(), msg + "\n", style); // 插入文字
            // 设置段落的对齐方式
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
