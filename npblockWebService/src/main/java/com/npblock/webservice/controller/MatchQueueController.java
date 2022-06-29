package com.npblock.webservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.Message;
import com.npblock.webservice.entity.Result;
import com.npblock.webservice.entity.RoomItem;
import com.npblock.webservice.service.UsersService;
import com.npblock.webservice.util.ConstUtils;
import com.npblock.webservice.manager.SocketServerManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 匹配队列控制器
 * @author fengxin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchQueueController {

    private final @NonNull UsersService usersService;

    /**
     * 退出匹配队列
     *
     * @param data 数据
     * @return 成功与否
     */
    @PostMapping("/signOut")
    public HttpEntity<?> signOut(@RequestBody JSONObject data) {
        //TODO 后续增加检查ip是否正确
        Message msg = data.toJavaObject(Message.class);
        String id = msg.getId();
        // 1.检查队列是否存在该用户
        if (SocketServerManager.getInstance().containsMatchId(id)) {
            // 2.将用户移除队列
            SocketServerManager.getInstance().removeMatchQueue(id);
        } else {
            return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "该用户未加入匹配队列"));
        }
        SocketServerManager.getInstance().log();
        return ResponseEntity.ok(new Result());
    }

    /**
     * 确认进入游戏
     *
     * @param data 数据
     * @return 成功与否
     */
    @PostMapping("/confirm")
    public HttpEntity<?> confirm(@RequestBody JSONObject data) {
        //TODO 后续增加检查ip是否正确
        Message msg = data.toJavaObject(Message.class);
        String id = msg.getId();
        // 1.检查队列是否存该待确认条目
        if (SocketServerManager.getInstance().containsConfirm(id)) {
            // 2.确认进入
            SocketServerManager.getInstance().confirm(id);
        } else {
            return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "该用户未加入确认条目"));
        }
        SocketServerManager.getInstance().log();
        return ResponseEntity.ok(new Result());
    }

    /**
     * 退出游戏队列
     *
     * @param data 数据
     * @return 成功与否
     */
    @PostMapping("/remove")
    public HttpEntity<?> remove(@RequestBody JSONObject data) {
        //TODO 后续增加检查ip是否正确
        Message msg = data.toJavaObject(Message.class);
        String key = msg.getKey();
        // 1.检查队列是否存在该用户
        if (SocketServerManager.getInstance().containsGameKey(key)) {
            // 游戏结束
            SocketServerManager.getInstance().gameOver(msg);
        } else {
            return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "该用户未加入游戏队列"));
        }
        SocketServerManager.getInstance().log();
        return ResponseEntity.ok(new Result());
    }

    /**
     * 获取用户信息 根据Key
     *
     * @param data 数据
     * @return 成功与否
     */
    @PostMapping("/getMsg")
    public HttpEntity<?> getMsg(@RequestBody JSONObject data) {
        String key = data.getString("key");
        String resultMsg = "key为空";
        if (key != null) {
            List<Integer> userIdListByKey = SocketServerManager.getInstance().getUserIdListByKey(key);
            if (userIdListByKey != null) {
                return ResponseEntity.ok(new Result(usersService.findByIdIn(userIdListByKey)));
            }
            resultMsg = "根据key获取ids失败";
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, resultMsg));
    }

    /**
     * 准备
     * @param data 数据
     * @return 返回
     */
    @PostMapping("/prepare")
    public HttpEntity<?> prepare(@RequestBody JSONObject data) {
        //获取参数
        int roomNum = data.getIntValue("roomNum");
        Boolean prepare = data.getBoolean("prepare");
        Integer userId = data.getInteger("userId");
        if (roomNum != 0 && prepare != null) {
            SocketServerManager.getInstance().prepare(roomNum, prepare, userId);
        }
        return ResponseEntity.ok(new Result());
    }

    /**
     * 退出房间
     * @param data 数据
     * @return 返回
     */
    @PostMapping("/exitRoom")
    public HttpEntity<?> exitRoom(@RequestBody JSONObject data) {
        int roomNum = data.getIntValue("roomNum");
        int userId = data.getIntValue("userId");
        if (roomNum != 0 && userId != 0) {
            SocketServerManager.getInstance().exitRoom(roomNum, userId);
        }
        return ResponseEntity.ok(new Result());
    }

    /**
     * 获取房间列表
     * @param data 数据
     * @return 返回
     */
    @PostMapping("/getRoomList")
    public HttpEntity<?> getRoomList(@RequestBody JSONObject data) {
        String token = data.getString("token");
        if (token != null) {
            List<RoomItem> roomList = SocketServerManager.getInstance().getRoomList();
            if (roomList != null) {
                return ResponseEntity.ok(new Result(roomList));
            } else {
                return ResponseEntity.ok(new Result(new ArrayList<>()));
            }
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR));
    }
}
