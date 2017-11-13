package com.xxxtai.express;

import com.xxxtai.express.controller.DispatchingAGV;
import com.xxxtai.express.netty.NettyServerBootstrap;
import com.xxxtai.express.view.DrawingGui;
import com.xxxtai.express.view.SchedulingGui;
import com.xxxtai.express.view.SettingGui;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
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

    private static Map<Integer, SocketChannel> nettyChannelMap;

    public Main() {
        super("AGV快递分拣系统");
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

    public static Map<Integer, SocketChannel> getNettyChannelMap() {
        return nettyChannelMap;
    }

    public static void setNettyChannelMap(Map<Integer, SocketChannel> nettyChannelMap) {
        Main.nettyChannelMap = nettyChannelMap;
    }

    private void init() {
        graphingGui.getGuiInstance(Main.this, schedulingGui, settingGui);
        settingGui.getGuiInstance(Main.this, schedulingGui, graphingGui);
        schedulingGui.getGuiInstance(Main.this, settingGui, graphingGui);
        this.getContentPane().add(schedulingGui);
        this.repaint();
        this.validate();
        new Thread(dispatchingAGV).start();
        nettyServerBootstrap.bind(8899);
    }

    private void exit() {
        Object[] option = {"exit", "cancel"};
        JOptionPane pane = new JOptionPane(" ", JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, option, option[1]);
        JDialog dialog = pane.createDialog(this, "  ");
        dialog.setVisible(true);
        Object result = pane.getValue();
        if (result == null || result == option[1]) {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        } else if (result == option[0]) {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/beans.xml");
        Main main = context.getBean(Main.class);
        nettyChannelMap = new ConcurrentHashMap<>();
        main.init();
    }
}
