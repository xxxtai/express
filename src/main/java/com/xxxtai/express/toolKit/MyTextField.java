package com.xxxtai.express.toolKit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyTextField extends JTextField implements MouseListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean first;

    public MyTextField(String string) {
        this.setText(string);
        first = true;
        this.setFont(new Font("ו", Font.BOLD, 30));
        this.setForeground(Color.LIGHT_GRAY);
        this.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (first) {
                first = false;
                this.setText("");
                this.setForeground(Color.BLACK);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }


}
