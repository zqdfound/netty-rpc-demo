package com.provider.rpcprovider.server;

import com.provider.rpcprovider.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务端
 *
 * @author zhuangqingdian
 * @date 2021/6/12
 */
@Component
public class NettyRpcServer implements DisposableBean {

    @Autowired
    NettyServerHandler nettyServerHandler;

    EventLoopGroup bossGroup = null;
    EventLoopGroup workerGroup = null;

    public void run(String host, int port) {
        try {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            //自定义处理器
                            socketChannel.pipeline().addLast(nettyServerHandler);
                        }
                    });

            ChannelFuture channelFuture = b.bind(host, port).sync();
            System.out.println("===========服务端启动=============");
            //监听通道关闭状态
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //关闭资源
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }

    }

    @Override
    public void destroy() throws Exception {
        //关闭资源
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
