package com.npblock.webservice.service;

import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.RankRecord;
import com.npblock.webservice.entity.Users;

import java.util.List;

public interface RankRecordService {
    /**
     * 根据对应用户记录id更新排位记录
     *
     * @param recordId 排位记录对象Id
     * @param win win
     * @return int 变化排位分数
     */
    JSONObject updateRank(int recordId, boolean win);

    /**
     * 获取排位记录
     * @param recordId recordId
     * @return RankRecord
     */
    RankRecord getRankRecordByRecordId(int recordId);

    /**
     * 获取排位模式排行榜
     *
     * @param token 用户token
     * @return List
     */
    List<Users> getRankScoreRank(String token);
}
