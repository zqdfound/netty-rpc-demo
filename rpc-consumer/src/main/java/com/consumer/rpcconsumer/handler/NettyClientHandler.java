package com.consumer.rpcconsumer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * 客户端处理类
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Component
public class NettyClientHandler extends SimpleChannelInboundHandler<String> implements Callable {

    ChannelHandlerContext channelHandlerContext;
    private String reqMsg;//发送消息
    private String respMsg;//接收消息

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    //通道读取就绪事件--读取服务端消息
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        respMsg = s;
        //唤醒等待线程
        notify();
    }
    //通道连接就绪事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channelHandlerContext = ctx;
    }

    //向服务端发送消息
    @Override
    public synchronized Object call() throws Exception {
        channelHandlerContext.writeAndFlush(reqMsg);
        //将线程处于等待状态--等待服务器消息
        wait();
        return respMsg;
    }
}
