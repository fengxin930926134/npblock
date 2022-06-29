package com.npblock.webservice.service.impl;

import com.npblock.webservice.entity.RushRecord;
import com.npblock.webservice.entity.Users;
import com.npblock.webservice.repository.RushRecordRepository;
import com.npblock.webservice.repository.UsersRepository;
import com.npblock.webservice.service.RushRecordService;
import com.npblock.webservice.util.ConstUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RushRecordServiceImpl implements RushRecordService {

    private final @NonNull RushRecordRepository rushRecordRepository;
    private final @NonNull UsersRepository usersRepository;

    @Override
    public boolean updateRush(Integer id, int pass, int num) {
        RushRecord one = rushRecordRepository.getOne(id);
        if (one != null && one.getMaxPass() <= pass && one.getScore() <= num) {
            return true;
        }
        return rushRecordRepository.updateRushRecordById(id, pass, num) > 0;
    }

    @Override
    public RushRecord getRushRecordById(Integer id) {
        return rushRecordRepository.getOne(id);
    }

    @Override
    public List<Users> getRushScoreRank(String token) {
        //定义排序规则
        Sort sort = new Sort(Sort.Direction.DESC, "maxPass", "score", "updateTime");
        //创建分页对象
        Pageable pageable = PageRequest.of(0, ConstUtils.GAME_CLASSIC_RANK_NUMBER, sort);
        Page<RushRecord> page = rushRecordRepository.findAll(pageable);
        Page<Users> usersPage = page.map(record -> {
            //其他的数据库进行查询
            Users users = usersRepository.findFirstByRushId(record.getId());
            users.setPassword("");
            users.setRushPass(record.getMaxPass());
            users.setRushScore(record.getScore());
            //设置result 的值
            return users;
        });
        return usersPage.getContent();
    }
}
