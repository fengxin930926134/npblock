package com.npblock.webservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.*;
import com.npblock.webservice.service.ClassicRecordService;
import com.npblock.webservice.service.RankRecordService;
import com.npblock.webservice.service.RushRecordService;
import com.npblock.webservice.service.UsersService;
import com.npblock.webservice.util.ConstUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 排行榜控制器
 * @author fengxin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
public class RankController {

    private final @NonNull ClassicRecordService classicRecordService;
    private final @NonNull UsersService usersService;
    private final @NonNull RankRecordService rankRecordService;
    private final @NonNull RushRecordService rushRecordService;

    /**
     * 上传用户经典模式成绩v2
     *
     * @param data (classicScore, token)
     * @return 状态信息
     */
    @PostMapping(value = "/uploadClassic")
    public HttpEntity<?> uploadClassic(@NotNull @RequestBody JSONObject data) {
        int classicScore = data.getIntValue("classicScore");
        String token = data.getString("token");
        if (classicRecordService.updateClassicScoreByToken(token, classicScore)) {
            return ResponseEntity.ok(new Result());
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "上传经典模式成绩失败！"));
    }

    /**
     * 上传挑战模式成绩
     * @param data data
     * @return HttpEntity
     */
    @PostMapping(value = "/uploadRush")
    public HttpEntity<?> uploadRush(@NotNull @RequestBody JSONObject data) {
        int pass = data.getIntValue("pass");
        int id = data.getIntValue("id");
        int num = data.getIntValue("num");
        if (rushRecordService.updateRush(id, pass, num)) {
            return ResponseEntity.ok(new Result());
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "上传闯关模式成绩失败！"));
    }

    /**
     * 上传用户排位成绩同时更新方块币
     * 结算规则：
     *  初始值20方块币，胜利方初始方块币翻倍，每游戏进行一分钟加1，最高上限50
     *  排位初始分1000，
     *  每次胜利加20分+连胜场数，
     *  每次失败减16分+连败场数的一半，最低1000分
     *  每次满一百分进行晋级赛，晋级失败则扣20分
     * @param data (gameTime(毫秒), win, recordId, id)
     * @return (排位状态类型,  增加金币, 变化积分)
     */
    @PostMapping(value = "/uploadRank")
    public HttpEntity<?> uploadRank(@NotNull @RequestBody JSONObject data) {
        long gameTime = data.getLongValue("gameTime");
        boolean win = data.getBooleanValue("win");
        int recordId = data.getIntValue("recordId");
        int id = data.getIntValue("id");
        //转换成分钟
        int rankTime = (int) (gameTime / 1000 / 60);
        int block = 20;
        if (win) {
            block = block * 2;
        }
        //计算应该增加的方块币
        block = block + rankTime;
        if (block > 50) {
            block = 50;
        }
        //计算排位分变化
        if (!usersService.updateBlockById(id, block)) {
            return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "更新方块币失败！"));
        }
        JSONObject jsonObject = rankRecordService.updateRank(recordId, win);
        if (!jsonObject.getBooleanValue("update")) {
            return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "更新排位成绩失败！"));
        }
        jsonObject.put("block", block);
        return ResponseEntity.ok(new Result(jsonObject));
    }

    /**
     * 获取全部排行榜 （三种）
     *
     * @return 状态信息
     */
    @PostMapping(value = "/getRank")
    public HttpEntity<?> classic(@NotNull @RequestBody JSONObject data) {
        String token = data.getString("token");
        if (usersService.tokenIsExpired(token)) {
            return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "token失效"));
        }
        List<Users> classicScoreRank = classicRecordService.getClassicScoreRank(token);
        List<Users> rushScoreRank = rushRecordService.getRushScoreRank(token);
        List<Users> rankScoreRank = rankRecordService.getRankScoreRank(token);
        if (classicScoreRank != null && rushScoreRank != null && rankScoreRank != null) {
            JSONObject result = new JSONObject();
            result.put("classicRank", classicScoreRank);
            result.put("rushRank", rushScoreRank);
            result.put("rankRank", rankScoreRank);
            return ResponseEntity.ok(new Result(result.toJSONString()));
        }
        return ResponseEntity.ok(new Result(ConstUtils.REST_DB_ERROR, "获取排行榜失败！"));
    }

    /**
     * 获取某个用户记录
     * @param data data
     * @return json
     */
    @PostMapping(value = "/record")
    public HttpEntity<?> record(@NotNull @RequestBody JSONObject data) {
        int rankRecordId = data.getIntValue("rankRecordId");
        int rushRecordId = data.getIntValue("rushRecordId");
        int classicRecordId = data.getIntValue("classicRecordId");
        RankRecord rankRecordByRecordId = rankRecordService.getRankRecordByRecordId(rankRecordId);
        ClassicRecord classicRecordById = classicRecordService.getClassicRecordById(classicRecordId);
        RushRecord rushRecordById = rushRecordService.getRushRecordById(rushRecordId);
        JSONObject result = new JSONObject();
        result.put("rankRecord", rankRecordByRecordId);
        result.put("classicRecord", classicRecordById);
        result.put("rushRecord", rushRecordById);
        return ResponseEntity.ok(new Result(result.toJSONString()));
    }
}
