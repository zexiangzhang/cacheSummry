package com.zzx.ranklist.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzx.ranklist.constant.RedisKeyConstant;
import com.zzx.ranklist.user.entity.User;
import com.zzx.ranklist.user.mapper.UserMapper;
import com.zzx.ranklist.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<User> getTopRankList(Integer limit) {
        Set<ZSetOperations.TypedTuple<String>> userRankSet = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(RedisKeyConstant.userRankListKey, 1,Integer.MAX_VALUE, 0, limit);
        if (CollectionUtils.isEmpty(userRankSet)) {
            return Collections.emptyList();
        } else {
            List<User> users = new ArrayList<>(userRankSet.size());
            Iterator<ZSetOperations.TypedTuple<String>> iterator = userRankSet.iterator();
            while (iterator.hasNext()){
                ZSetOperations.TypedTuple<String> next = iterator.next();
                User user = baseMapper.selectById(next.getValue());
                if (Objects.nonNull(user)) {
                    user.setLikeCount(next.getScore());
                    users.add(user);
                }
            }
            return users;
        }
    }

    @Override
    public String addUsers(List<String> userNames) {
        if (CollectionUtils.isEmpty(userNames)) {
            return "empty params, just fuck off";
        } else {
            userNames.forEach(userName -> {
                User user = new User();
                user.setId(UUID.randomUUID().toString());
                user.setName(userName);
                baseMapper.insert(user);
            });
        }
        return "insert success";
    }

    @Override
    public String likeUser(String userId) {
        if (Objects.nonNull(baseMapper.selectById(userId))) {
            redisTemplate.opsForZSet().incrementScore(RedisKeyConstant.userRankListKey, userId, 1);
            return "like success";
        }
        return "no such user, just fuck off";
    }
}
