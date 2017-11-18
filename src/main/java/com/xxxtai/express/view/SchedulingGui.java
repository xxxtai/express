package com.xxxtai.express.view;

import com.xxxtai.express.Main;
import com.xxxtai.express.constant.State;
import com.xxxtai.express.model.Car;
import com.xxxtai.express.toolKit.Common;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;

@Component
public class SchedulingGui extends JPanel{
    private static final long serialVersionUID = 1L;
    private RoundButton settingGuiBtn;
    private RoundButton drawingGuiBtn;
    private JLabel stateLabel;
    @Resource
    private DrawingGraph drawingGraph;
    private Timer timer;

    public SchedulingGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        RoundButton schedulingGuiBtn = new RoundButton("调度界面");
        schedulingGuiBtn.setBounds(0, 0, screenSize.width / 3, screenSize.height / 20);
        schedulingGuiBtn.setForeground(new Color(30, 144, 255));
        schedulingGuiBtn.setBackground(Color.WHITE);

        settingGuiBtn = new RoundButton("设置界面");
        settingGuiBtn.setBounds(screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);

        drawingGuiBtn = new RoundButton("制图界面");
        drawingGuiBtn.setBounds(2 * screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);

        stateLabel = new JLabel();
        stateLabel.setBounds(0, 24 * screenSize.height / 26, screenSize.width, screenSize.height / 26);
        stateLabel.setFont(new Font("宋体", Font.BOLD, 20));
        stateLabel.setForeground(Color.RED);

        this.setLayout(null);
        this.add(schedulingGuiBtn);
        this.add(settingGuiBtn);
        this.add(drawingGuiBtn);
        this.add(stateLabel);

        timer = new Timer(50, e -> {
            repaint();
            for (Car car : Main.AGVArray) {
                if (car.getState().equals(State.COLLIED)) {
                    if (!stateLabel.getText().contains(car.getAGVNum() + "AGV")) {
                        stateLabel.setText(stateLabel.getText() + car.getAGVNum() + "AGV碰撞！");
                    }
                }
            }
        });
    }

    @PostConstruct
    public void init() {
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawingGraph.drawingMap(g, DrawingGraph.Style.EXPRESS, true);
        drawingGraph.drawingAGV(g, Main.AGVArray, this);
    }

    public void getGuiInstance(JFrame main, JPanel settingGui, JPanel drawingGui) {
        settingGuiBtn.addActionListener(e -> Common.changePanel(main, settingGui));
        drawingGuiBtn.addActionListener(e -> Common.changePanel(main, drawingGui));
    }
}
