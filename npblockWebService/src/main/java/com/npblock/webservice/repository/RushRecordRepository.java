package com.npblock.webservice.repository;

import com.npblock.webservice.entity.RushRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RushRecordRepository extends JpaRepository<RushRecord, Integer> {

    /**
     * 更新挑战模式记录
     * @param id id
     * @param pass 关卡
     * @return int
     */
    @Transactional
    @Modifying
    @Query("update RushRecord r set r.maxPass = ?2, r.score = ?3 where r.id = ?1")
    int updateRushRecordById(Integer id, Integer pass, Integer num);
}
