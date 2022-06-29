package com.npblock.webservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.Result;
import com.npblock.webservice.entity.Users;
import com.npblock.webservice.service.UsersService;
import com.npblock.webservice.util.ConstUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理控制层
 * @author fengxin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
public class SocialController {

    private final @NonNull UsersService usersService;

    /**
     * 查询指定条件的用户 100个
     * @param data name, sex
     * @return list
     */
    @PostMapping("/select")
    public HttpEntity<?> select(@NonNull @RequestBody JSONObject data) {
        if (data != null) {
            String name = data.getString("name");
            int sex = data.getIntValue("sex");
            String token = data.getString("token");
            List<Users> users = usersService.selectUserBySexAndName(name, sex, token);
            if (users != null) {
                return ResponseEntity.ok(new Result(users));
            }
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "查询用户失败"));
    }

    /**
     * 查询指定用户好友
     * @param data id
     * @return list
     */
    @PostMapping("/friend")
    public HttpEntity<?> friend(@NonNull  @RequestBody JSONObject data) {
        Integer id = data.getInteger("id");
        if (id != null) {
            List<Users> users = usersService.selectFriendById(id);
            return ResponseEntity.ok(new Result(users));
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "查询好友失败"));
    }

    /**
     * 更新用户
     * @param data id
     * @return list
     */
    @PostMapping("/update")
    public HttpEntity<?> update(@NonNull  @RequestBody JSONObject data) {
        Users params = data.toJavaObject(Users.class);
        if (params != null && params.getGameName() != null) {
            if (usersService.expiredGameName(params.getGameName())) {
                return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "该昵称已存在"));
            }
            if (usersService.updateGameName(params.getToken(), params.getGameName())) {
                return ResponseEntity.ok(new Result());
            }
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "更新失败，请稍后再试"));
    }
}
