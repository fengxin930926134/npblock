package com.npblock.webservice.service;

import com.npblock.webservice.entity.ClassicRecord;
import com.npblock.webservice.entity.Users;

import java.util.List;

public interface ClassicRecordService {

    /**
     * 根据token更新经典模式成绩
     *
     * @param token token
     * @param classicScore 最新成绩
     * @return int
     */
    boolean updateClassicScoreByToken(String token, Integer classicScore);

    /**
     * 获取经典模式排行榜
     *
     * @param token 用户token
     * @return List
     */
    List<Users> getClassicScoreRank(String token);

    /**
     * 获取ClassicRecord记录
     * @param id id
     * @return ClassicRecord
     */
    ClassicRecord getClassicRecordById(Integer id);
}
