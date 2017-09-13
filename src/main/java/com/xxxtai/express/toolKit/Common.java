package com.xxxtai.express.toolKit;

import javax.swing.*;

public class Common {

    public static void changePanel(JFrame main, JPanel panel) {
        main.getContentPane().removeAll();
        main.getContentPane().add(panel);
        main.repaint();
        main.validate();
    }

    public static void delay(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
