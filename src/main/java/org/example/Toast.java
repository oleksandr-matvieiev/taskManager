package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
import java.util.Queue;

public class Toast {
    private static final Queue<String> queue = new LinkedList<>();
    private static boolean showing = false;

    public static void showToast(String message, int duration) {
        queue.add(message);
        if (!showing) {
            showNextToast(duration);
        }
    }

    private static void showNextToast(int duration) {
        if (queue.isEmpty()) {
            showing = false;
            return;
        }

        showing = true;
        String message = queue.poll();

        JWindow window = new JWindow();
        window.setLayout(new BorderLayout());
        window.setBackground(new Color(0, 0, 0, 0));

        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton closeButton = new JButton("×");
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        closeButton.setForeground(Color.RED);

        closeButton.addActionListener(e -> {
            window.dispose();
            showNextToast(duration);
        });

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(new Color(75, 68, 68));
        topPanel.setSize(300, 10);
        topPanel.add(closeButton, BorderLayout.EAST);

        JPanel contentPanel = getJPanel();

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(closeButton, BorderLayout.EAST);

        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(label, BorderLayout.CENTER);

        window.add(contentPanel, BorderLayout.CENTER);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        window.pack();
        int x = (screenSize.width - window.getWidth()) ;
        int y = (screenSize.height - window.getHeight()) - 50;
        window.setLocation(x, y);
        window.setVisible(true);

        Timer timer = new Timer(duration, e -> {
            window.dispose();
            showNextToast(duration);
        });

        timer.setRepeats(false);
        timer.start();
    }

    private static JPanel getJPanel() {
        JPanel contentPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60,60,60));
                g2d.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),20,20));
                g2d.dispose();
            }
        };
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        return contentPanel;
    }


}
