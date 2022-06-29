package com.npblock.webservice.service;

import com.npblock.webservice.entity.RushRecord;
import com.npblock.webservice.entity.Users;

import java.util.List;

public interface RushRecordService {

    /**
     * 更新关卡成绩
     * @param id id
     * @param pass pass
     * @param num num
     * @return boolean
     */
    boolean updateRush(Integer id, int pass, int num);

    /**
     * 获取rush记录
     * @param id id
     * @return RushRecord
     */
    RushRecord getRushRecordById(Integer id);

    /**
     * 获取挑战模式排行榜
     *
     * @param token 用户token
     * @return List
     */
    List<Users> getRushScoreRank(String token);
}
