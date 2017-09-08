package com.xxxtai.view;

import javax.swing.*;
import java.awt.*;

class FileNameDialog extends JDialog {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private MyTextField inputCityName;
    private FileNameDialogListener dialogListener;

    FileNameDialog(String cityName) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width / 4, screenSize.height / 4);
        this.setLocation(3 * screenSize.width / 8, 3 * screenSize.height / 8);

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        JLabel label = new JLabel(cityName);
        label.setFont(new Font("宋体", Font.BOLD, 30));

        inputCityName = new MyTextField("");
        inputCityName.setFont(new Font("宋体", Font.BOLD, 30));

        RoundButton confirmBtn = new RoundButton("确认");
        RoundButton cancelBtn = new RoundButton("取消");
        confirmBtn.addActionListener(e -> dialogListener.getFileName(inputCityName.getText(), true));

        cancelBtn.addActionListener(e -> dialogListener.getFileName("", false));

        mainPanel.add(label);
        mainPanel.add(inputCityName);
        mainPanel.add(confirmBtn);
        mainPanel.add(cancelBtn);
        this.getContentPane().add(mainPanel);
        this.setVisible(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setAlwaysOnTop(true);
    }

    void setOnDialogListener(FileNameDialogListener listener) {
        this.dialogListener = listener;
    }
}
