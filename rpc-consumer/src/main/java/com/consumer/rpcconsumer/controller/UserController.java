package com.consumer.rpcconsumer.controller;

import com.consumer.rpcconsumer.anno.RpcReference;
import com.consumer.rpcconsumer.domain.User;
import com.consumer.rpcconsumer.service.IUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RpcReference
    IUserService userService;

    @RequestMapping("/getUserById")
    public User getUserById(int id){
        System.out.println();
        userService.getUserById(id);
        return userService.getUserById(id);
    }
}
