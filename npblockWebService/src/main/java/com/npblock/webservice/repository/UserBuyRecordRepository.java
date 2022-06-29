package com.npblock.webservice.repository;

import com.npblock.webservice.entity.UserBuyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBuyRecordRepository extends JpaRepository<UserBuyRecord, Integer> {
    /**
     * 查询指定记录
     * @param userId userId
     * @param alreadyUse alreadyUse
     * @return UserBuyRecord
     */
    List<UserBuyRecord> findByUserIdAndAlreadyUse(Integer userId, Boolean alreadyUse);
}
