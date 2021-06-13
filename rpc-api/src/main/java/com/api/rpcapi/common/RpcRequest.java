package com.api.rpcapi.common;

import lombok.Data;

/**
 * 通用请求类
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Data
public class RpcRequest {

    //请求对象ID
    private String requestId;
    //类名
    private String className;
    //方法名
    private String methodName;
    //参数类型
    private Class<?>[] paramTypes;
    //入参
    private Object[] params;
}
