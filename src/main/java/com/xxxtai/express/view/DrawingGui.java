package com.xxxtai.express.view;

import com.xxxtai.express.toolKit.Common;
import com.xxxtai.express.toolKit.MyTextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Slf4j(topic = "develop")
public class DrawingGui extends JPanel{
    private static final long serialVersionUID = 1L;
    private RoundButton schedulingGuiBtn;
    private RoundButton settingGuiBtn;
    private RoundButton drawingGuiBtn;
    private MyTextField rowField;
    private MyTextField columnField;
    private MyTextField realDistanceField;

    public DrawingGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        schedulingGuiBtn = new RoundButton("调度界面");
        schedulingGuiBtn.setBounds(0, 0, screenSize.width / 3, screenSize.height / 20);

        settingGuiBtn = new RoundButton("设置界面");
        settingGuiBtn.setBounds(screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);

        drawingGuiBtn = new RoundButton("制图界面");
        drawingGuiBtn.setBounds(2 * screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);
        drawingGuiBtn.setForeground(new Color(30, 144, 255));
        drawingGuiBtn.setBackground(Color.WHITE);

        rowField = new MyTextField("        行数");
        rowField.setBounds(5 * screenSize.width / 12, 3 * screenSize.height / 15, screenSize.width / 6, screenSize.height / 20);
        columnField = new MyTextField("        列数");
        columnField.setBounds(5 * screenSize.width / 12, 4 * screenSize.height / 15, screenSize.width / 6, screenSize.height / 20);
        realDistanceField = new MyTextField("        距离");
        realDistanceField.setBounds(5 * screenSize.width / 12, 5 * screenSize.height / 15, screenSize.width / 6, screenSize.height / 20);

        this.setLayout(null);
        this.add(schedulingGuiBtn);
        this.add(settingGuiBtn);
        this.add(drawingGuiBtn);
        this.add(columnField);
        this.add(realDistanceField);
        this.add(rowField);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

    }

    public void getGuiInstance(JFrame main, JPanel schedulingGui, JPanel settingGui) {
        schedulingGuiBtn.addActionListener(e -> Common.changePanel(main, schedulingGui));
        settingGuiBtn.addActionListener(e -> Common.changePanel(main, settingGui));
    }
}
