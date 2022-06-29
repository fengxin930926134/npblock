package com.npblock.webservice.service;

import com.npblock.webservice.entity.Goods;
import java.util.List;

public interface GoodsService {

    /**
     * 获取商品列表
     * @return list
     */
    List<Goods> getAll();

    /**
     * 购买商品
     * @param userId userId
     * @param goodsId goodsId
     * @return boolean
     */
    boolean buyGoods(int userId, int goodsId) throws Exception;

    /**
     * 获取已经购买的商品 （背包）
     * @param userId userId
     * @return List<Goods>
     */
    List<Goods> getPack(int userId);

    /**
     * 使用商品
     * @param recordId recordId
     * @return boolean
     */
    boolean useGoods(int recordId);
}
