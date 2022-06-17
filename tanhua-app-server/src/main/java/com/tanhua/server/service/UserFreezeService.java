package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class UserFreezeService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 判断用户是否被冻结，已被冻结，抛出异常
     *  参数：冻结范围，用户id
     *
     *  检测登录：
     *     checkUserStatus（“1”，106）
     */
    public void checkUserStatus(String state,Long userId) {
        //1、拼接key，从redis中查询数据
        String key = Constants.FREEZE_USER + userId;
        String value = redisTemplate.opsForValue().get(key);
        //2、如果数据存在，且冻结范围一致，抛出异常
        if(!StringUtils.isEmpty(value)) {
            Map map = JSON.parseObject(value, Map.class);
            String freezingRange = (String) map.get("freezingRange");
            if(state.equals(freezingRange)) {
                throw new BusinessException(ErrorResult.builder().errMessage("用户被冻结").build());
            }
        }
     }
}
