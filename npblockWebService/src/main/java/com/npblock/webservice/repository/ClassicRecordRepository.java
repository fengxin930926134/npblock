package com.npblock.webservice.repository;

import com.npblock.webservice.entity.ClassicRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ClassicRecordRepository extends JpaRepository<ClassicRecord, Integer> {

    /**
     * 根据id更新经典模式成绩
     * @param id id
     * @param classicScore 经典模式成绩
     * @return int
     */
    @Transactional
    @Modifying
    @Query("update ClassicRecord c set c.maxScore = ?2, c.uploadCount = c.uploadCount + 1 where c.id = ?1")
    int updateClassicRecordById(Integer id, Integer classicScore);

    /**
     * 按照分页规则获取符合条件的全部记录
     *
     * @param pageable spring封装的分页实现类
     * @return 分页查询User
     */
    Page<ClassicRecord> findAll(Pageable pageable);
}
