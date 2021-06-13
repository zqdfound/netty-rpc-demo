package com.provider.rpcprovider.handler;

import com.alibaba.fastjson.JSON;
import com.api.rpcapi.common.RpcRequest;
import com.api.rpcapi.common.RpcResponse;
import com.provider.rpcprovider.anno.RpcService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 自定义业务处理类
 * 1.将标有@RpcService注解的bean进行缓存
 * 2.接收客户端请求
 * 3.根据传递来的beanName从缓存中查找
 * 4.通过反射调用bean的方法
 * 5.响应客户端
 *
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Component
@ChannelHandler.Sharable//设置通道共享 多个客户端共享
public class NettyServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {

    //缓存bean
    static Map<String, Object> SERVICE_INSTANCE_MAP = new HashMap<>();

    /**
     * 读取客户端消息
     *
     * @param ctx
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        RpcRequest request = JSON.parseObject(s, RpcRequest.class);
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        //业务处理
        try {
            response.setResult(handler(request));
        } catch (Exception e) {
            e.printStackTrace();
            response.setError(e.getMessage());
        }
        //响应客户端
        ctx.writeAndFlush(JSON.toJSONString(response));
    }

    /**
     * 获取bean并反射调用方法
     *
     * @param request
     * @return
     */
    private Object handler(RpcRequest request) throws InvocationTargetException {
        Object serviceBean = SERVICE_INSTANCE_MAP.get(request.getClassName());
        if (serviceBean == null) {
            throw new RuntimeException("服务端没有找到该服务");
        }
        FastClass proxy = FastClass.create(serviceBean.getClass());
        FastMethod method = proxy.getMethod(request.getMethodName(), request.getParamTypes());
        return method.invoke(serviceBean,request.getParams());
    }

    /**
     * 将@RpcServer注解类缓存
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //通过注解获取bean的集合
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        //遍历
        Set<Map.Entry<String, Object>> entrySets = serviceMap.entrySet();
        for (Map.Entry<String, Object> entry : entrySets) {
            Object serviceBean = entry.getValue();
            //判断接口是否实现
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new RuntimeException("对外暴露的服务必须实现接口");
            }
            //默认处理第一个作为缓存的名字
            String serviceName = serviceBean.getClass().getInterfaces()[0].getName();
            SERVICE_INSTANCE_MAP.put(serviceName,serviceBean);
        }
    }
}
