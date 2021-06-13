package com.provider.rpcprovider.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 暴露服务接口
 * @author zhuangqingdian
 * @date 2021/6/12
 */
@Target(ElementType.TYPE)//作用于类
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
}
