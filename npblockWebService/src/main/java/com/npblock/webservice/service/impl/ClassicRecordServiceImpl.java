package com.npblock.webservice.service.impl;

import com.npblock.webservice.entity.ClassicRecord;
import com.npblock.webservice.entity.Users;
import com.npblock.webservice.repository.ClassicRecordRepository;
import com.npblock.webservice.repository.UsersRepository;
import com.npblock.webservice.service.ClassicRecordService;
import com.npblock.webservice.util.ConstUtils;
import com.npblock.webservice.util.TokenUtils;
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
public class ClassicRecordServiceImpl implements ClassicRecordService {

    private final @NonNull UsersRepository usersRepository;
    private final @NonNull ClassicRecordRepository classicRecordRepository;

    @Override
    public boolean updateClassicScoreByToken(String token, Integer classicScore) {
        Users byToken = usersRepository.findByToken(token);
        if (byToken != null) {
            //判断token是否有效
            if (TokenUtils.tokenIsNotExpired(byToken.getTokenTime())) {
                if (byToken.getClassicId() != null && byToken.getClassicId() != 0) {
                    return classicRecordRepository.updateClassicRecordById(byToken.getClassicId(), classicScore) > 0;
                } else {
                    // 本地没有记录 创建
                    ClassicRecord classicRecord = new ClassicRecord();
                    classicRecord.setMaxScore(classicScore);
                    classicRecord.setUploadCount(1);
                    ClassicRecord save = classicRecordRepository.save(classicRecord);
                    byToken.setClassicId(save.getId());
                    usersRepository.save(byToken);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Users> getClassicScoreRank(String token) {
        //定义排序规则
        Sort sort = new Sort(Sort.Direction.DESC, "maxScore", "uploadCount");
        //创建分页对象
        Pageable pageable = PageRequest.of(0, ConstUtils.GAME_CLASSIC_RANK_NUMBER, sort);
        Page<ClassicRecord> page = classicRecordRepository.findAll(pageable);
        Page<Users> usersPage = page.map(record -> {
            //其他的数据库进行查询
            Users firstByClassicId = usersRepository.findFirstByClassicId(record.getId());
            firstByClassicId.setPassword("");
            firstByClassicId.setClassicScore(record.getMaxScore());
            //设置result 的值
            return firstByClassicId;
        });
        return usersPage.getContent();
    }

    @Override
    public ClassicRecord getClassicRecordById(Integer id) {
        return classicRecordRepository.getOne(id);
    }
}
