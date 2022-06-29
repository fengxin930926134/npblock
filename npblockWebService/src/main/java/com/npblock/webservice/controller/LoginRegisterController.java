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
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;

/**
 * 用户控制器
 * @author fengxin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class LoginRegisterController {

    private final @NonNull UsersService usersService;

    /**
     * 用户登陆
     *
     * @param data 用户信息
     * @return 用户信息
     */
    @PostMapping(value = "/login")
    public HttpEntity<?> login(@NotNull @RequestBody JSONObject data) {
        Users params = data.toJavaObject(Users.class);
        //判断是什么登陆
        Users user;
        if (params.getToken() != null) {
            user = usersService.loginByToken(params.getToken(), params.getTokenTime());
            //处理返回值
            if (user != null) {
                if (user.getId() != null) {
                    return ResponseEntity.ok(new Result(user));
                }
            } else {
                return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "token过期"));
            }
        } else if (params.getOpenId() != null) {
            user = usersService.loginByQQ(params);
            //处理返回值
            if (user != null) {
                return ResponseEntity.ok(new Result(user));
            }
        } else {
            user = usersService.loginByPhoneAndPassword(params.getPhone(), params.getPassword());
            //处理返回值
            if (user != null) {
                if (user.getId() != null) {
                    return ResponseEntity.ok(new Result(user));
                }
            } else {
                return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "帐号或密码错误"));
            }
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "请求服务器超时"));
    }

    /**
     * 用户注册
     *
     * @param data 注册信息
     * @return 用户信息
     */
    @PostMapping(value = "/register")
    public HttpEntity<?> register(@NotNull @RequestBody JSONObject data) {
        Users params = data.toJavaObject(Users.class);
        if (usersService.expiredPhone(params.getPhone())) {
            Users users = usersService.register(params);
            if (users != null) {
                return ResponseEntity.ok(new Result(users));
            } else {
                return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "请求服务器超时"));
            }
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "手机号已经被注册"));
    }

    /**
     * 更新游戏名称
     *
     * @param data 用户信息
     * @return 更改状态
     */
    @PostMapping(value = "/gameName")
    public HttpEntity<?> gameName(@NotNull @RequestBody JSONObject data) {
        Users params = data.toJavaObject(Users.class);
        if (!usersService.expiredGameName(params.getGameName())) {
            if (usersService.updateGameName(params.getToken(), params.getGameName())) {
                return ResponseEntity.ok(new Result());
            } else {
                return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "请求服务器超时"));
            }
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "游戏名称已经被注册"));
    }
}
