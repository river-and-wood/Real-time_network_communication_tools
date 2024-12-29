package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class Serve_GUI extends JFrame {
    private final JTextPane textPane; // 显示服务器日志
    private final StyledDocument doc;

    public Serve_GUI() {
        setTitle("服务器后台");
        setBounds(100, 150, 450, 650); // 窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel ps = new JLabel("服务器状态监视");
        ps.setBounds(160, 15, 200, 30);
        ps.setFont(new Font("宋体", Font.BOLD, 17));
        ps.setForeground(new Color(57, 182, 120));
        panel.add(ps);

        textPane = new JTextPane();
        textPane.setEditable(false); // 禁止用户编辑
        textPane.setFocusable(false);
        textPane.setBounds(50, 50, 340, 450);
        doc = textPane.getStyledDocument(); // 获取样式文档
        panel.add(textPane);

        JButton refresh = new JButton("清屏");
        refresh.setFocusable(false);
        refresh.setBounds(170, 530, 100, 28);
        refresh.setBackground(new Color(165, 169, 222, 255));
        refresh.setFont(new Font("华文宋体", Font.BOLD, 15));
        refresh.addActionListener(e -> textPane.setText("")); // 清空日志
        panel.add(refresh);

        JScrollPane scrollPane = new JScrollPane(textPane); // 添加滚动条
        scrollPane.setBounds(50, 50, 340, 450);
        panel.add(scrollPane);

        add(panel);
        setVisible(true);
    }

    // 更新日志信息
    public void appendMessage(String msg, int size, Color color, int alignment) {
        try {
            Style style = textPane.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // 设置文字颜色
            StyleConstants.setFontSize(style, size); // 设置字体大小
            doc.insertString(doc.getLength(), msg + "\n", style); // 插入文字
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
