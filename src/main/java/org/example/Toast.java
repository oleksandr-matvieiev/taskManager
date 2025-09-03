package org.example;

import javax.swing.*;
import java.awt.*;

public class Toast {
    public static void showToast(String message, int durationMs) {
        JWindow window = new JWindow();

        JLabel label = new JLabel(message);
        label.setOpaque(true);
        label.setBackground(new Color(60, 60, 60));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        window.add(label);
        window.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (screenSize.width - window.getWidth()) / 2;
        int y = (screenSize.height - window.getHeight()) - 50;
        window.setLocation(x, y);


        window.setVisible(true);
        new Timer(durationMs, e -> window.dispose()).start();

    }
}
