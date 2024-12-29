package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class Serve_GUI extends JFrame {
    private final JTextPane textPane; // 显示服务器日志
    private final StyledDocument doc;

    public Serve_GUI() {
        setTitle("服务器后台");
        setBounds(100, 150, 600, 650); // 窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel ps = new JLabel("服务器状态监视");
        ps.setHorizontalAlignment(SwingConstants.CENTER);
        ps.setFont(new Font("宋体", Font.BOLD, 17));
        ps.setForeground(new Color(57, 182, 120));
        panel.add(ps, BorderLayout.NORTH);

        textPane = new JTextPane();
        textPane.setEditable(false); // 禁止用户编辑
        textPane.setFocusable(false);
        doc = textPane.getStyledDocument(); // 获取样式文档

        JScrollPane scrollPane = new JScrollPane(textPane); // 添加滚动条
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refresh = new JButton("清屏");
        refresh.setFocusable(false);
        refresh.setPreferredSize(new Dimension(100, 30));
        refresh.setBackground(new Color(165, 169, 222, 255));
        refresh.setFont(new Font("华文宋体", Font.BOLD, 15));
        refresh.addActionListener(e -> textPane.setText("")); // 清空日志
        panel.add(refresh, BorderLayout.SOUTH);

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
}
