package com.zzx.ranklist.user.controller;

import com.zzx.ranklist.user.entity.User;
import com.zzx.ranklist.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/")
    public String addUsers(@RequestParam(value = "userNames") List<String> userNames) {
        return service.addUsers(userNames);
    }

    @GetMapping("/")
    public List<User> userList() {
        return service.list();
    }

    @GetMapping("/topRankList")
    @ApiOperation("获取用户排行榜，limit参数指获取多少条数据，默认为5")
    public List<User> getUserTopRankList(@RequestParam(name = "limit", defaultValue = "5") Integer limit) {
        return service.getTopRankList(limit);
    }

    @PutMapping("/like/{userId}")
    public String likeUser(@PathVariable("userId") String userId) {
        return service.likeUser(userId);
    }
}
