package com.npblock.webservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.Goods;
import com.npblock.webservice.entity.Result;
import com.npblock.webservice.service.GoodsService;
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
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 商业
 * @author fengxin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class BusinessController {

    private final @NonNull GoodsService goodsService;
    private final @NonNull UsersService usersService;

    /**
     * 获取商品列表
     * @param data data
     * @return all
     */
    @PostMapping("/goods")
    public HttpEntity<?> goods(@NotNull @RequestBody JSONObject data) {
        String token = data.getString("token");
        if (usersService.tokenIsExpired(token)) {
            return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "token失效"));
        }
        List<Goods> all = goodsService.getAll();
        if (all != null) {
            return ResponseEntity.ok(new Result(all));
        }
        return ResponseEntity.ok(new Result(new ArrayList<>()));
    }

    /**
     * 购买商品
     * @param data data
     * @return boolean
     */
    @PostMapping("/buy")
    public HttpEntity<?> buy(@NotNull @RequestBody JSONObject data) {
        int userId = data.getIntValue("userId");
        int goodsId = data.getIntValue("goodsId");
        try {
            if (goodsService.buyGoods(userId, goodsId)) {
                return ResponseEntity.ok(new Result());
            }
        } catch (Exception e) {
            System.out.println("[购买商品失败] " + e.getMessage());
            return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "购买商品失败"));
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "方块币不足"));
    }

    /**
     * 查看背包
     * @param data data
     * @return all
     */
    @PostMapping("/pack")
    public HttpEntity<?> pack(@NotNull @RequestBody JSONObject data) {
        int userId = data.getIntValue("userId");
        List<Goods> pack = goodsService.getPack(userId);
        if (pack != null) {
            return ResponseEntity.ok(new Result(pack));
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "查询失败"));
    }

    /**
     * 使用商品
     * @param data data
     * @return all
     */
    @PostMapping("/useGoods")
    public HttpEntity<?> useGoods(@NotNull @RequestBody JSONObject data) {
        int recordId = data.getIntValue("recordId");
        if (goodsService.useGoods(recordId)) {
            return ResponseEntity.ok(new Result());
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_ERROR, "使用商品失败"));
    }
}
