package com.npblock.webservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.RankRecord;
import com.npblock.webservice.entity.Users;
import com.npblock.webservice.entity.enums.RankStateTypeEnum;
import com.npblock.webservice.repository.RankRecordRepository;
import com.npblock.webservice.repository.UsersRepository;
import com.npblock.webservice.service.RankRecordService;
import com.npblock.webservice.util.ConstUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankRecordServiceImpl implements RankRecordService {

    private final @NonNull RankRecordRepository rankRecordRepository;
    private final @NonNull UsersRepository usersRepository;

    /**
     * 排位初始分1000，
     * 每次胜利加20分+连胜场数，
     * 每次失败减16分+连败场数的一半，最低1000分
     * 每次满一百分进行晋级赛，晋级失败则扣20分
     */
    @Override
    public JSONObject updateRank(int recordId, boolean win) {
        JSONObject params = new JSONObject();
        RankStateTypeEnum rankStateTypeEnum = RankStateTypeEnum.DEFAULT;
        RankRecord one = rankRecordRepository.getOne(recordId);
        one.setUpdateTime(new Date());
        //判断是否晋级赛
        if (one.getRiseInRank()) {
            if (win) {
                //晋级成功
                one.setRiseInRank(false);
                if (one.getMaxRankScore() < one.getRankScore()) {
                    one.setMaxRankScore(one.getRankScore());
                }
                //连胜+1
                one.setMaxWinRepeatedly(one.getMaxWinRepeatedly() + 1);
                one.setMaxRepeatedDefeats(0);
                rankStateTypeEnum = RankStateTypeEnum.PROMOTION_SUCCESS_TYPE;
            } else {
                //晋级失败
                one.setRiseInRank(false);
                one.setRankScore(one.getRankScore() - 20);
                params.put("rank", -20);
                //连败+1
                one.setMaxRepeatedDefeats(one.getMaxRepeatedDefeats() + 1);
                one.setMaxWinRepeatedly(0);
                rankStateTypeEnum = RankStateTypeEnum.PROMOTION_DEFEATED_TYPE;
            }
        } else {
            //不是晋级赛
            if (win) {
                //计算增减分情况
                int increase = one.getMaxWinRepeatedly() + 20;
                //排位分
                int rankScore = one.getRankScore();
                //个十位部分
                int rank = rankScore % 100;
                if ((rank + increase) >= 100) {
                    //晋级赛
                    params.put("rank", 100 - rank);
                    rankScore = rankScore - rank + 100;
                    one.setRiseInRank(true);
                    one.setRankScore(rankScore);
                    rankStateTypeEnum = RankStateTypeEnum.GET_INTO_PROMOTION_TYPE;
                } else {
                    params.put("rank", increase);
                    rank = one.getRankScore() + increase;
                    one.setRankScore(rank);
                    if (rank > one.getMaxRankScore()) {
                        one.setMaxRankScore(rank);
                    }
                }
                //连胜+1
                one.setMaxWinRepeatedly(one.getMaxWinRepeatedly() + 1);
                one.setMaxRepeatedDefeats(0);
            } else {
                //计算增减分情况
                int reduce = one.getMaxRepeatedDefeats()/2 + 16;
                if ((one.getRankScore() - reduce) < 1000) {
                    one.setRankScore(1000);
                    params.put("rank", 0);
                } else {
                    one.setRankScore(one.getRankScore() - reduce);
                    params.put("rank", -reduce);
                }
                //连败+1
                one.setMaxRepeatedDefeats(one.getMaxRepeatedDefeats() + 1);
                one.setMaxWinRepeatedly(0);
            }
        }
        //更新数据库
        params.put("update", rankRecordRepository.save(one) != null);
        params.put("rankStateType", rankStateTypeEnum.getCode());
        return params;
    }

    @Override
    public RankRecord getRankRecordByRecordId(int recordId) {
        return rankRecordRepository.getOne(recordId);
    }

    @Override
    public List<Users> getRankScoreRank(String token) {
        //定义排序规则
        Sort sort = new Sort(Sort.Direction.DESC, "rankScore", "updateTime");
        //创建分页对象
        Pageable pageable = PageRequest.of(0, ConstUtils.GAME_CLASSIC_RANK_NUMBER, sort);
        Page<RankRecord> page = rankRecordRepository.findAll(pageable);
        Page<Users> usersPage = page.map(record -> {
            //其他的数据库进行查询
            Users users = usersRepository.findFirstByRankId(record.getId());
            users.setPassword("");
            users.setRankScore(record.getRankScore());
            users.setRiseInRank(record.getRiseInRank());
            //设置result 的值
            return users;
        });
        return usersPage.getContent();
    }
}
