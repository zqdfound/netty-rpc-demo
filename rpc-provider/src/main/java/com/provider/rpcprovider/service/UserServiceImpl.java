package com.provider.rpcprovider.service;

import com.consumer.rpcconsumer.domain.User;
import com.consumer.rpcconsumer.service.IUserService;
import com.provider.rpcprovider.anno.RpcService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuangqingdian
 * @date 2021/6/13
 */
@RpcService
@Service
public class UserServiceImpl implements IUserService {

    Map<Object,User> map = new HashMap<>();

    @Override
    public User getUserById(int id) {
        if(map.size() == 0){
            User user = new User();
            user.setId(1);
            user.setName("张三");
            map.put(user.getId(),user);
            User user1 = new User();
            user1.setId(2);
            user1.setName("李四");
            map.put(user1.getId(),user1);
        }
        return map.get(id);
    }
}
