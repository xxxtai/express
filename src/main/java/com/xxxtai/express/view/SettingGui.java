package com.xxxtai.express.view;

import com.xxxtai.express.main.Main;
import com.xxxtai.express.toolKit.Common;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class SettingGui extends JPanel implements Gui {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private RoundButton schedulingGuiBtn;
    private RoundButton drawingGuiBtn;

    public SettingGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        schedulingGuiBtn = new RoundButton("调度界面");
        schedulingGuiBtn.setBounds(0, 0, screenSize.width / 3, screenSize.height / 20);

        RoundButton settingGuiBtn = new RoundButton("设置界面");
        settingGuiBtn.setBounds(screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);
        settingGuiBtn.setForeground(new Color(30, 144, 255));
        settingGuiBtn.setBackground(Color.WHITE);

        drawingGuiBtn = new RoundButton("制图界面");
        drawingGuiBtn.setBounds(2 * screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);

        JLabel stateLabel = new JLabel();
        stateLabel.setBounds(0, 22 * screenSize.height / 25, screenSize.width, screenSize.height / 25);
        stateLabel.setFont(new Font("宋体", Font.BOLD, 25));


        this.setLayout(null);
        this.add(schedulingGuiBtn);
        this.add(settingGuiBtn);
        this.add(drawingGuiBtn);
        this.add(stateLabel);


    }

    public void getGuiInstance(JFrame main, JPanel schedulingGui, JPanel drawingGui) {
        schedulingGuiBtn.addActionListener(e -> Common.changePanel(main, schedulingGui));
        drawingGuiBtn.addActionListener(e -> Common.changePanel(main, drawingGui));
    }

}
