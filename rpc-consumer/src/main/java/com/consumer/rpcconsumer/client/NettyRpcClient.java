package com.consumer.rpcconsumer.client;

import com.consumer.rpcconsumer.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * 客户端
 * 1.连接服务端
 * 2.关闭资源
 * 3.提供发送消息方法
 *
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Component
public class NettyRpcClient implements InitializingBean, DisposableBean {
    @Autowired
    NettyClientHandler nettyClientHandler;
    EventLoopGroup group = null;
    Bootstrap b = null;
    Channel channel = null;

    ExecutorService service = Executors.newCachedThreadPool();

    //bean初始化时连接服务端
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            //创建线程组
            group = new NioEventLoopGroup();
            b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            //自定义处理类
                            socketChannel.pipeline().addLast(nettyClientHandler);
                        }
                    });
            //连接服务
            channel = b.connect("127.0.0.1", 8090).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (channel != null) {
                channel.close();
            }
            if (group != null) {
                group.shutdownGracefully();
            }
        }

    }

    /**
     * 发送消息
     *
     * @param msg
     * @return
     */
    public Object send(String msg) throws ExecutionException, InterruptedException {
        nettyClientHandler.setReqMsg(msg);
        Future submit = service.submit(nettyClientHandler);
        return submit.get();
    }

    @Override
    public void destroy() throws Exception {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
