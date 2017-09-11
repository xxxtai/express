package com.xxxtai.view;

import com.xxxtai.controller.DispatchingAGV;
import com.xxxtai.main.Main;
import com.xxxtai.model.*;
import com.xxxtai.toolKit.Common;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class SchedulingGui extends JPanel implements Gui {
    private static final long serialVersionUID = 1L;
    private RoundButton schedulingGuiBtn;
    private RoundButton settingGuiBtn;
    private RoundButton drawingGuiBtn;
    @Resource
    private Graph graph;
    @Resource
    private DrawingGraph drawingGraph;
    @Resource
    private Runnable monitorServerSocketRunnable;
    @Resource
    private DispatchingAGV dispatchingAGV;
    public static final ArrayList<Car> AGVArray = new ArrayList<>();
    private Timer timer;
    private ExecutorService executors;


    public SchedulingGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        schedulingGuiBtn = new RoundButton("调度界面");
        schedulingGuiBtn.setBounds(0, 0, screenSize.width / 3, screenSize.height / 20);
        schedulingGuiBtn.setForeground(new Color(30, 144, 255));
        schedulingGuiBtn.setBackground(Color.WHITE);

        settingGuiBtn = new RoundButton("设置界面");
        settingGuiBtn.setBounds(screenSize.width / 3, 0, screenSize.width / 3, screenSize.height / 20);

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

        timer = new Timer(50, e -> {
            repaint();
            for (Car car : AGVArray) {
                car.stepByStep();
                car.heartBeat();
            }
        });
        executors = Executors.newFixedThreadPool(2);
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 10; i++) {
            Car car = getCar();
            car.init(i + 1);
            AGVArray.add(car);
        }

        timer.start();
        executors.execute(monitorServerSocketRunnable);
		executors.execute(dispatchingAGV);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawingGraph.drawingMap(g);
        drawingGraph.drawingAGV(g, AGVArray, this);
    }

    public void getGuiInstance(Main main, SettingGui settingGui, DrawingGui drawingGui) {
        schedulingGuiBtn.addActionListener(e -> {
            StringBuilder strBuf = new StringBuilder();
            strBuf.append("/*************************************************\n");
            strBuf.append("边 被 占 用 信 息 ：\n");
            for (Edge edge : graph.getEdgeArray()) {
                if (!edge.waitQueue.isEmpty()) {
                    strBuf.append(edge.cardNum).append("边被");
                    for (Car car : edge.waitQueue)
                        strBuf.append(car.getAGVNum()).append(",");
                    strBuf.append("AGV占用！！");
                    if (!edge.isLocked())
                        strBuf.append(edge.cardNum).append("边的waitQueue不为空，但未被锁住");
                    strBuf.append("\n");
                } else {
                    if (edge.isLocked()) {
                        strBuf.append(edge.cardNum).append("边被锁住，但waitQueue为空");
                        strBuf.append("\n");
                    }
                }

            }
            strBuf.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            strBuf.append("点  被 占 用 信 息 ：\n");
            for (Node node : graph.getNodeArray()) {
                if (!node.waitQueue.isEmpty()) {
                    strBuf.append(node.cardNum).append("点被");
                    for (Car car : node.waitQueue)
                        strBuf.append(car.getAGVNum()).append(",");
                    strBuf.append("AGV占用！！");
                    if (!node.isLocked())
                        strBuf.append(node.cardNum).append("边的waitQueue不为空，但未被锁住");
                    strBuf.append("\n");
                } else {
                    if (node.isLocked()) {
                        strBuf.append(node.cardNum).append("边的waitQueue为空，但被锁住");
                        strBuf.append("\n");
                    }
                }
            }
            strBuf.append("*************************************************/\n");
            System.out.println(strBuf.toString());
        });
        settingGuiBtn.addActionListener(e -> Common.changePanel(main, settingGui));
        drawingGuiBtn.addActionListener(e -> Common.changePanel(main, drawingGui));
    }
    public abstract Car getCar();
}
