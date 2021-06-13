package com.consumer.rpcconsumer.processor;

import com.consumer.rpcconsumer.anno.RpcReference;
import com.consumer.rpcconsumer.proxy.RpcClientProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Bean的后置增强
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    RpcClientProxy rpcClientProxy;
    /**
     * 自定义注解注入
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //查看bean的字段中有无对应的注解
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field: declaredFields) {
            //查找字段中是否包含注解
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if(annotation!= null){
                //获取代理对象
                Object proxy = rpcClientProxy.getProxy(field.getType());
                try {
                    //属性注入
                    field.setAccessible(true);
                    field.set(bean,proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
