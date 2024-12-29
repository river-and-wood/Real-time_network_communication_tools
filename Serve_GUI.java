package GUI_Socket;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class Serve_GUI extends JFrame {
    private final JTextPane textPane; // ��ʾ��������־
    private final StyledDocument doc;

    public Serve_GUI() {
        setTitle("��������̨");
        setBounds(100, 150, 450, 650); // ���ڴ�С
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel ps = new JLabel("������״̬����");
        ps.setBounds(160, 15, 200, 30);
        ps.setFont(new Font("����", Font.BOLD, 17));
        ps.setForeground(new Color(57, 182, 120));
        panel.add(ps);

        textPane = new JTextPane();
        textPane.setEditable(false); // ��ֹ�û��༭
        textPane.setFocusable(false);
        textPane.setBounds(50, 50, 340, 450);
        doc = textPane.getStyledDocument(); // ��ȡ��ʽ�ĵ�
        panel.add(textPane);

        JButton refresh = new JButton("����");
        refresh.setFocusable(false);
        refresh.setBounds(170, 530, 100, 28);
        refresh.setBackground(new Color(165, 169, 222, 255));
        refresh.setFont(new Font("��������", Font.BOLD, 15));
        refresh.addActionListener(e -> textPane.setText("")); // �����־
        panel.add(refresh);

        JScrollPane scrollPane = new JScrollPane(textPane); // ��ӹ�����
        scrollPane.setBounds(50, 50, 340, 450);
        panel.add(scrollPane);

        add(panel);
        setVisible(true);
    }

    // ������־��Ϣ
    public void appendMessage(String msg, int size, Color color, int alignment) {
        try {
            Style style = textPane.addStyle("Style", null);
            StyleConstants.setForeground(style, color); // ����������ɫ
            StyleConstants.setFontSize(style, size); // ���������С
            doc.insertString(doc.getLength(), msg + "\n", style); // ��������
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
