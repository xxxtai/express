package com.xxxtai.express;

import com.xxxtai.express.controller.DispatchingAGV;
import com.xxxtai.express.controller.ResolveDeadLock;
import com.xxxtai.express.model.Car;
import com.xxxtai.express.model.ExeCommandTask;
import com.xxxtai.express.model.Graph;
import com.xxxtai.express.netty.NettyServerBootstrap;
import com.xxxtai.express.view.DrawingGui;
import com.xxxtai.express.view.SchedulingGui;
import com.xxxtai.express.view.SettingGui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j(topic = "develop")
public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    public static final ArrayList<Car> AGVArray = new ArrayList<>();
    @Resource
    private Graph graph;
    @Resource
    private SchedulingGui schedulingGui;
    @Resource
    private SettingGui settingGui;
    @Resource
    private DrawingGui graphingGui;
    @Resource
    private DispatchingAGV dispatchingAGV;
    @Resource
    private NettyServerBootstrap nettyServerBootstrap;
    @Resource
    private ResolveDeadLock resolveDeadLock;
    @Resource
    private ApplicationContext context;
    @Resource
    private ExeCommandTask exeCommandTask;

    public Main() {
        super("多AGV物流分拣系统");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
    }

    private void start() {
        for (int i = 0; i < graph.getAGVSPosition().size(); i++) {
            Car car = context.getBean(Car.class);
            car.init(i + 1, 0);
            AGVArray.add(car);
        }
        graphingGui.getGuiInstance(Main.this, schedulingGui, settingGui);
        settingGui.getGuiInstance(Main.this, schedulingGui, graphingGui);
        schedulingGui.getGuiInstance(Main.this, settingGui, graphingGui);
        this.getContentPane().add(schedulingGui);
        this.repaint();
        this.validate();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
        executorService.scheduleAtFixedRate(exeCommandTask, 0, 50, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(dispatchingAGV, 0, 3000, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(resolveDeadLock, 1000, 1000, TimeUnit.MILLISECONDS);
        nettyServerBootstrap.bind(8899);
    }

    private void exit() {
        Object[] option = {"确认", "取消"};
        JOptionPane pane = new JOptionPane("退出？", JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, option, option[1]);
        JDialog dialog = pane.createDialog(this, "  ");
        dialog.setVisible(true);
        Object result = pane.getValue();
        if (result == null || result == option[1]) {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        } else if (result == option[0]) {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            dispatchingAGV.saveSorting();
            log.info("系统退出，并保存数据");
        }
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/beans.xml");
        Main main = context.getBean(Main.class);
        main.start();
    }
}
