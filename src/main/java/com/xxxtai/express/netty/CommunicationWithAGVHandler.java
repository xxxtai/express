package com.xxxtai.express.netty;

import com.xxxtai.express.constant.Constant;
import com.xxxtai.express.constant.State;
import com.xxxtai.express.model.AGVCar;
import com.xxxtai.express.model.Car;
import com.xxxtai.express.model.Graph;
import com.xxxtai.express.model.Node;
import com.xxxtai.express.view.SchedulingGui;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.awt.*;

@Slf4j(topic = "develop")
public class CommunicationWithAGVHandler extends ChannelInboundHandlerAdapter {
    private Car car;
    private SocketChannel socketChannel;
    @Resource
    private Graph graph;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        String msg = (String) object;
        log.info(msg);
        if (this.car == null && this.socketChannel == null) {
            setup(ctx, msg);
            return;
        }
        log.info(this.car.getAGVNum() + "AGV netty rec:" + msg);
        this.car.setLastCommunicationTime(System.currentTimeMillis());
        String content = Constant.getContent(msg);
        String[] c = content.split(Constant.SPLIT);
        if (msg.startsWith(Constant.CARD_PREFIX)) {
            int cardNum;
            if (Constant.USE_SERIAL) {
                if (c[0].length() % 2 == 0) {
                    cardNum = graph.getSerialNumMap().get(c[0]);
                } else {
                    cardNum = graph.getSerialNumMap().get("0" + c[0]);
                }
            } else {
                cardNum = Integer.parseInt(c[0]);
            }

            if (cardNum != 0) {
                try {
                    this.car.setReceiveCardNum(cardNum);
                } catch (Exception e) {
                    log.error("Exception:" , e);
                }
            }
        } else if (msg.startsWith(Constant.STATE_PREFIX)) {
            int stateValue = Integer.parseInt(c[0], 16);
            if (stateValue == State.FORWARD.getValue()) {
                this.car.setState(State.FORWARD);
            } else if (stateValue == State.STOP.getValue() && this.car.getAtEdge() != null) {
                Node n = graph.getNodeMap().get(this.car.getAtEdge().cardNum);
                ((AGVCar)this.car).setPosition(new Point(n.x, n.y));
                this.car.setState(State.STOP);
            } else if (stateValue == State.UNLOADED.getValue()) {
                if (this.car.getReadCardNum() == this.car.getStopCardNum()) {
                    ((AGVCar)this.car).setOnDuty(false);
                    ((AGVCar)this.car).setDestination(null);
                }
            } else if (stateValue == State.COLLIED.getValue()) {
                this.car.setState(State.COLLIED);
            }
        }
    }

    private void setup(ChannelHandlerContext ctx, String msg){
        StringBuilder builder = new StringBuilder();
        builder.append(this.hashCode()).append(" CommunicationWithAGV Runnable start\n");
        builder.append("receive msg:").append(msg);
        if (msg.startsWith(Constant.HEART_PREFIX)) {
            int AGVNum = Integer.parseInt(Constant.getContent(msg), 16);
            for (Car car : SchedulingGui.AGVArray) {
                if (car.getAGVNum() == AGVNum) {
                    this.car = car;
                    this.socketChannel = (SocketChannel) ctx.channel();
                    this.car.setSocketChannel(this.socketChannel);
                    this.car.setLastCommunicationTime(System.currentTimeMillis());
                    builder.append(" confirmed AGVNum :").append(AGVNum).append("号AGV");
                    log.info(builder.toString());
                    break;
                }
            }
        }
    }
}
