package com.zzx.ranklist.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzx.ranklist.user.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    List<User> getTopRankList(Integer limit);

    String addUsers(List<String> userNames);

    String likeUser(String userId);
}
