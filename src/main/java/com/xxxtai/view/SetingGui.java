package com.xxxtai.view;

import com.xxxtai.main.Main;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class SetingGui extends JPanel implements Gui{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoundButton schedulingGuiBtn;
	private RoundButton drawingGuiBtn;
	private RoundButton setingGuiBtn;

	public SetingGui(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		schedulingGuiBtn = new RoundButton("调度界面");
		schedulingGuiBtn.setBounds(0, 0, screenSize.width/3, screenSize.height/20);
		
		setingGuiBtn = new RoundButton("设置界面");
		setingGuiBtn.setBounds(screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);
		setingGuiBtn.setForeground(new Color(30, 144, 255));
		setingGuiBtn.setBackground(Color.WHITE);
		
		drawingGuiBtn = new RoundButton("制图界面");
		drawingGuiBtn.setBounds(2*screenSize.width/3, 0, screenSize.width/3, screenSize.height/20);

		JLabel stateLabel = new JLabel();
		stateLabel.setBounds(0, 22*screenSize.height/25, screenSize.width, screenSize.height/25);
		stateLabel.setFont(new Font("宋体", Font.BOLD, 25));


		this.setLayout(null);
		this.add(schedulingGuiBtn);
		this.add(setingGuiBtn);
		this.add(drawingGuiBtn);
		this.add(stateLabel);


	
	}
	
	public void getGuiInstance(Main main, SchedulingGui schedulingGui, SetingGui setingGui, DrawingGui drawingGui){
		schedulingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(schedulingGui);
				main.repaint();
				main.validate();
			}
		});
		setingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(setingGui);
				main.repaint();
				main.validate();
			}
		});
		drawingGuiBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				main.getContentPane().removeAll();
				main.getContentPane().add(drawingGui);
				main.repaint();
				main.validate();
			}
		});		
	}

}
