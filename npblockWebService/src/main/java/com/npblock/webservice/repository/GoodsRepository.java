package com.npblock.webservice.repository;

import com.npblock.webservice.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods, Integer> {
    /**
     * 获取指定商品信息
     *
     * @param ids ids
     * @return goodsS
     */
    List<Goods> findByIdIn(List<Integer> ids);
}
