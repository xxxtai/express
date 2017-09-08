package com.xxxtai.toolKit;

import com.xxxtai.main.Main;

import javax.swing.*;

public class Common {

    public static void changePanel(Main main, JPanel panel) {
        main.getContentPane().removeAll();
        main.getContentPane().add(panel);
        main.repaint();
        main.validate();
    }
}
