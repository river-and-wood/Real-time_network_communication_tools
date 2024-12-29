package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class Serve_GUI extends JFrame {
    private final JTextPane textPane; // ��ʾ��������־
    private final StyledDocument doc;

    public Serve_GUI() {
        setTitle("��������̨");
        setBounds(100, 150, 600, 650); // ���ڴ�С
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel ps = new JLabel("������״̬����");
        ps.setHorizontalAlignment(SwingConstants.CENTER);
        ps.setFont(new Font("����", Font.BOLD, 17));
        ps.setForeground(new Color(57, 182, 120));
        panel.add(ps, BorderLayout.NORTH);

        textPane = new JTextPane();
        textPane.setEditable(false); // ��ֹ�û��༭
        textPane.setFocusable(false);
        doc = textPane.getStyledDocument(); // ��ȡ��ʽ�ĵ�

        JScrollPane scrollPane = new JScrollPane(textPane); // ��ӹ�����
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refresh = new JButton("����");
        refresh.setFocusable(false);
        refresh.setPreferredSize(new Dimension(100, 30));
        refresh.setBackground(new Color(165, 169, 222, 255));
        refresh.setFont(new Font("��������", Font.BOLD, 15));
        refresh.addActionListener(e -> textPane.setText("")); // �����־
        panel.add(refresh, BorderLayout.SOUTH);

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
}
