package com.xxxtai.express.netty;

import com.xxxtai.express.Main;
import com.xxxtai.express.constant.Constant;
import com.xxxtai.express.constant.State;
import com.xxxtai.express.model.AGVCar;
import com.xxxtai.express.model.Car;
import com.xxxtai.express.model.Graph;
import com.xxxtai.express.model.Node;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.awt.*;

@Slf4j(topic = "develop")
public class CommWithAGVHandler extends ChannelInboundHandlerAdapter {
    private Car car;
    private SocketChannel socketChannel;
    private StringBuilder holdMessage;
    @Resource
    private Graph graph;

    public CommWithAGVHandler(){
        this.holdMessage = new StringBuilder();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error(this.car.getAGVNum() + "AGV channel exceptionCaught！！！！！！！！！！！！！！！！！", cause);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        String msg = (String) object;
        if (msg.contains("Heart") || msg.contains("beat")) {
            return;
        }
        if (this.car == null || this.socketChannel == null) {
            setup(ctx, msg);
            return;
        }
        log.info(this.car.getAGVNum() + "AGV netty rec:" + msg);
        String[] contents;
        if (!msg.endsWith(Constant.SUFFIX)) {
            if (!msg.contains(Constant.SUFFIX)) {
                holdMessage.append(msg);
                log.info(this.car.getAGVNum() + "AGV hold message:" + holdMessage.toString());
                return;
            } else {
                String[] holdContents = msg.split(Constant.SUFFIX);
                int holdLength = holdContents.length;
                contents = new String[holdLength - 1];
                System.arraycopy(holdContents, 0, contents, 0, holdLength - 1);
                holdMessage.append(holdContents[holdLength - 1]);
                log.info(this.car.getAGVNum() + "AGV hold message:" + holdMessage.toString());
            }
        } else {
            if (holdMessage.length() > 0) {
                log.info(this.car.getAGVNum() + "AGV hold message complete:" + holdMessage.toString());
                contents = holdMessage.append(msg).toString().split(Constant.SUFFIX);
                holdMessage.setLength(0);
            } else {
                contents = msg.split(Constant.SUFFIX);
            }
        }
        for (String content : contents){
            String[] c = content.substring(Constant.FIX_LENGTH, content.length()).split(Constant.SPLIT);
            if (content.startsWith(Constant.CARD_PREFIX)) {
                int cardNum = 0;
                if (Constant.USE_SERIAL) {
                    if (graph.getSerialNumMap().containsKey(c[0])) {
                        cardNum = graph.getSerialNumMap().get(c[0]);
                    } else {
//                        log.info(this.car.getAGVNum() + "AGV 识别错误编码标志！！！");
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
            } else if (content.startsWith(Constant.STATE_PREFIX)) {
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
    }

    private void setup(ChannelHandlerContext ctx, String msg){
        StringBuilder builder = new StringBuilder();
        builder.append(this.hashCode()).append(" CommunicationWithAGV Runnable start\n");
        builder.append("receive msg:").append(msg);
        if (msg.startsWith(Constant.HEART_PREFIX)) {
            int AGVNum = Integer.parseInt(Constant.getContent(msg), 16);
            for (Car car : Main.AGVArray) {
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
