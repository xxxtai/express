package com.xxxtai.express.model;

import com.xxxtai.express.constant.Command;
import com.xxxtai.express.constant.State;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;
@Slf4j(topic = "develop")
public class ExeCommandTask extends TimerTask {
    private Car car;
    private int count_1s;
    private boolean firstlyExecutiveCommand = true;
    public ExeCommandTask(Car car){
        this.car = car;
    }
    @Override
    public void run() {
        if (this.count_1s == 80) {
            this.count_1s = 0;
            if (this.car.getExecutiveCommand() != null && this.car.getExecutiveCommand().getValue() != this.car.getState().getValue()
                    && !this.car.getState().equals(State.COLLIED) && !this.car.getState().equals(State.INFRARED_ANOMALY) && !this.firstlyExecutiveCommand) {
                this.car.sendMessageToAGV(this.car.getExecutiveCommand().getCommand());
                log.info("--------------------------------------------  让" + this.car.getAGVNum() + "AGV" + this.car.getExecutiveCommand().getDescription());
            }
        } else {
            this.count_1s++;
        }

        if (this.car.getExecutiveCommand() != null && this.car.getExecutiveCommand().getValue() != this.car.getState().getValue()
                && !this.car.getState().equals(State.COLLIED) && !this.car.getState().equals(State.INFRARED_ANOMALY)
                && !this.car.getState().equals(State.UNLOADED) && this.firstlyExecutiveCommand) {
            State state = this.car.getState();
            Command command = this.car.getExecutiveCommand();
            if (((state.equals(State.FORWARD) || state.equals(State.UNLOADED)) && command.equals(Command.STOP)) ||
                    (state.equals(State.STOP) && command.equals(Command.FORWARD))){
                this.car.sendMessageToAGV(this.car.getExecutiveCommand().getCommand());
                log.info("让" + this.car.getAGVNum() + "AGV " + this.car.getExecutiveCommand().getDescription());
            }
        } else if (this.car.getExecutiveCommand() != null && this.car.getExecutiveCommand().getValue() == this.car.getState().getValue()
                && !this.firstlyExecutiveCommand) {
            this.firstlyExecutiveCommand = true;
        }
    }
}
