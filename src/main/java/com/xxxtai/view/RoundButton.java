package com.xxxtai.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class RoundButton extends JButton {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public RoundButton(String label) {
        super(label);
        Dimension size = getPreferredSize();//Dimension 类封装单个对象中组件的宽度和高度（精确到整数
//        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);

        // This call causes the JButton not to paint the background.
        // This allows us to paint a round background.
        setContentAreaFilled(false);
        this.setBackground(new Color(30, 144, 255));
        this.setFocusPainted(false);
        this.setFont(new Font("宋体", Font.BOLD, 30));
        this.setForeground(Color.WHITE);
    }

    // Paint the round background and label.
    protected void paintComponent(Graphics g) {
	if (getModel().isArmed()) {
            // You might want to make the highlight color
            // a property of the RoundButton class.
            g.setColor(Color.white);
        } else {
            g.setColor(getBackground());
        }
	g.fillRect(0, 0, getSize().width-1, getSize().height-1);

        // This call will paint the label and the focus rectangle.
	super.paintComponent(g);
    }

    // Paint the border of the button using a simple stroke.
    protected void paintBorder(Graphics g) {
        //g.setColor(getForeground());
        //g.drawRect(0, 0, getSize().width-1, getSize().height-1);
    }

    // Hit detection.
    Shape shape;
    public boolean contains(int x, int y) {
        // If the button has changed size, make a new shape object.
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        return shape.contains(x, y);
    }

}