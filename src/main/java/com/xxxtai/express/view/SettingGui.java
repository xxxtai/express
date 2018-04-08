package com.xxxtai.express.view;

import com.xxxtai.express.toolKit.Common;
import com.xxxtai.express.toolKit.MyTextField;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class SettingGui extends JPanel{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private RoundButton schedulingGuiBtn;

    public SettingGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        schedulingGuiBtn = new RoundButton("监控界面");
        schedulingGuiBtn.setBounds(0, 0, screenSize.width / 2, screenSize.height / 24);

        RoundButton settingGuiBtn = new RoundButton("设置界面");
        settingGuiBtn.setBounds(screenSize.width / 2, 0, screenSize.width / 2, screenSize.height / 24);
        settingGuiBtn.setForeground(new Color(30, 144, 255));
        settingGuiBtn.setBackground(Color.WHITE);

        JComboBox<String> algorithmType = new JComboBox<String>();
        algorithmType.addItem("A*算法");
        algorithmType.addItem("遗传算法");
        algorithmType.addItem("Dijkstra算法");
        algorithmType.setEditable(false);
        algorithmType.setFont(new Font("宋体", Font.BOLD, 23));
        algorithmType.setBounds(300, 100, 200, 40);
        JLabel algorithmLabel = new JLabel("路径规划算法 ：");
        algorithmLabel.setFont(new Font("宋体", Font.BOLD, 23));
        algorithmLabel.setBounds(100, 100, 200, 40);

        JLabel timeLabel = new JLabel("运行时长:");
        timeLabel.setBounds(100, 200, 200, 40);
        timeLabel.setFont(new Font("宋体", Font.BOLD, 23));
        MyTextField timeField = new MyTextField("100分钟");
        timeField.setBounds(300, 200, 200, 40);

        JLabel countLabel = new JLabel("分拣包裹总数:");
        countLabel.setBounds(100, 300, 200, 40);
        countLabel.setFont(new Font("宋体", Font.BOLD, 23));
        MyTextField countField = new MyTextField("20891个");
        countField.setBounds(300, 300, 200, 40);

        JLabel efficiencyLabel = new JLabel("分拣效率：");
        efficiencyLabel.setBounds(100, 400, 200, 40);
        efficiencyLabel.setFont(new Font("宋体", Font.BOLD, 23));
        MyTextField efficiencyField = new MyTextField("12534个/小时");
        efficiencyField.setBounds(300, 400, 200, 40);

        JComboBox<String> agvType = new JComboBox<String>();
        agvType.addItem("1号");
        agvType.addItem("2号");
        agvType.addItem("3号");
        agvType.setEditable(false);
        agvType.setFont(new Font("宋体", Font.BOLD, 23));
        agvType.setBounds(1200, 100, 200, 40);
        JLabel agvLabel = new JLabel("快递分拣机器人编号：");
        agvLabel.setFont(new Font("宋体", Font.BOLD, 23));
        agvLabel.setBounds(900, 100, 250, 40);

        JLabel infoLabel = new JLabel("快递分拣机器人状态信息：");
        infoLabel.setFont(new Font("宋体", Font.BOLD, 23));
        infoLabel.setBounds(900, 200, 290, 40);
        JTextArea infoArea = new JTextArea("  电量：50%\n  里程：1594m\n  分拣效率：19个\n  运行时长：35分钟\n  充电次数：1次\n");
        infoArea.setFont(new Font("宋体", Font.BOLD, 23));
        infoArea.setBounds(1200, 200, 400, 200);

        JComboBox<String> exitType = new JComboBox<String>();
        exitType.addItem("1号");
        exitType.addItem("2号");
        exitType.addItem("3号");
        exitType.setEditable(false);
        exitType.setFont(new Font("宋体", Font.BOLD, 23));
        exitType.setBounds(1200, 500, 200, 40);
        JLabel exitLabel = new JLabel("分拣出口编号：");
        exitLabel.setFont(new Font("宋体", Font.BOLD, 23));
        exitLabel.setBounds(900, 500, 250, 40);

        JLabel exitInfoLabel = new JLabel("分拣出口信息：");
        exitInfoLabel.setFont(new Font("宋体", Font.BOLD, 23));
        exitInfoLabel.setBounds(900, 600, 290, 40);
        JTextArea exitInfoArea = new JTextArea("  对应地区：广州\n  包裹数量：1083个");
        exitInfoArea.setFont(new Font("宋体", Font.BOLD, 23));
        exitInfoArea.setBounds(1200, 600, 400, 100);

        this.setLayout(null);
        this.add(schedulingGuiBtn);
        this.add(settingGuiBtn);
        this.add(algorithmType);
        this.add(algorithmLabel);
        this.add(timeField);
        this.add(timeLabel);
        this.add(countLabel);
        this.add(countField);
        this.add(efficiencyField);
        this.add(efficiencyLabel);
        this.add(agvType);
        this.add(agvLabel);
        this.add(infoArea);
        this.add(infoLabel);
        this.add(exitInfoArea);
        this.add(exitInfoLabel);
        this.add(exitLabel);
        this.add(exitType);
    }

    public void getGuiInstance(JFrame main, JPanel schedulingGui, JPanel drawingGui) {
        schedulingGuiBtn.addActionListener(e -> Common.changePanel(main, schedulingGui));
    }

}
