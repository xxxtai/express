package com.xxxtai.express.model;

import com.xxxtai.express.Main;
import com.xxxtai.express.constant.Command;
import com.xxxtai.express.constant.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.TimerTask;
@Slf4j(topic = "develop")
@Component
public class ExeCommandTask implements Runnable {
    @Override
    public void run() {
        for (Car car : Main.AGVArray) {
            State state = car.getState();
            Command command = car.getExecutiveCommand();
            if (command != null) {
                if (((state.equals(State.FORWARD) || state.equals(State.UNLOADED)) && command.equals(Command.STOP)) ||
                        (state.equals(State.STOP) && command.equals(Command.FORWARD))){
                    log.info("让" + car.getAGVNum() + "AGV " + car.getExecutiveCommand().getDescription());
                    car.sendMessageToAGV(car.getExecutiveCommand().getCommand());
                }
            }
        }
    }
}
