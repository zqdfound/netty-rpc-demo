package com.consumer.rpcconsumer.service;

import com.consumer.rpcconsumer.domain.User;
import org.springframework.stereotype.Component;

/**
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@Component
public interface IUserService {

    User getUserById(int id);
}
