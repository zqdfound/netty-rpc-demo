package com.provider.rpcprovider;

import com.provider.rpcprovider.server.NettyRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcProviderApplication implements CommandLineRunner {

    @Autowired
    NettyRpcServer nettyRpcServer;

    public static void main(String[] args) {
        SpringApplication.run(RpcProviderApplication.class, args);
    }

    //启动服务端
    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                nettyRpcServer.run("127.0.0.1",8090);
            }
        }).start();
    }
}
