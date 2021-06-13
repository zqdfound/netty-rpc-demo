package com.api.rpcapi.common;

import lombok.Data;

/**
 * 通用响应
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Data
public class RpcResponse {
    //请求ID
    private String requestId;
    //返回
    private Object result;
    //错误信息
    private String error;
}
