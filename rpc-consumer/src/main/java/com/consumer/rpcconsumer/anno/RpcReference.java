package com.consumer.rpcconsumer.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 引用代理类
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Target(ElementType.FIELD)//作用于字段
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
}
