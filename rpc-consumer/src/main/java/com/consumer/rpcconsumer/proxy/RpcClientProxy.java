package com.consumer.rpcconsumer.proxy;

import com.alibaba.fastjson.JSON;
import com.api.rpcapi.common.RpcRequest;
import com.api.rpcapi.common.RpcResponse;
import com.consumer.rpcconsumer.client.NettyRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 客户端代理类
 *
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Component
public class RpcClientProxy {

    @Autowired
    NettyRpcClient nettyRpcClient;

    Map<Class, Object> SERVICE_PROXY = new HashMap<>();

    /**
     * 获取代理对象
     *
     * @param serviceClass
     * @return
     */
    public Object getProxy(Class serviceClass) {
        //从缓存中查找
        Object proxy = SERVICE_PROXY.get(serviceClass);
        if (proxy == null) {
            //创建代理对象
            proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(),
                    new Class[]{serviceClass}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            //封装请求
                            RpcRequest request = new RpcRequest();
                            request.setRequestId(UUID.randomUUID().toString());
                            request.setClassName(method.getDeclaringClass().getName());
                            request.setMethodName(method.getName());
                            request.setParamTypes(method.getParameterTypes());
                            request.setParams(args);
                            try {
                                //发送消息
                                Object o = nettyRpcClient.send(JSON.toJSONString(request));
                                //转化消息
                                RpcResponse response = JSON.parseObject(o.toString(), RpcResponse.class);
                                if (response.getError() != null) {
                                    throw new RuntimeException(response.getError());
                                }
                                if (response.getResult() != null) {
                                    return JSON.parseObject(response.getResult().toString(), method.getReturnType());
                                }
                                return null;
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw e;
                            }
                        }
                    });
            SERVICE_PROXY.put(serviceClass, proxy);//放入缓存
        }
        return proxy;
    }
}
