package com.npblock.webservice.service.impl;

import com.npblock.webservice.entity.Goods;
import com.npblock.webservice.entity.UserBuyRecord;
import com.npblock.webservice.entity.Users;
import com.npblock.webservice.repository.GoodsRepository;
import com.npblock.webservice.repository.UserBuyRecordRepository;
import com.npblock.webservice.repository.UsersRepository;
import com.npblock.webservice.service.GoodsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    private final @NonNull UsersRepository usersRepository;
    private final @NonNull GoodsRepository goodsRepository;
    private final @NonNull UserBuyRecordRepository userBuyRecordRepository;

    @Override
    public List<Goods> getAll() {
        List<Goods> all = goodsRepository.findAll();
        if (all != null) {
            return all;
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public boolean buyGoods(int userId, int goodsId) throws Exception {
        Users user = usersRepository.getOne(userId);
        Goods goods = goodsRepository.getOne(goodsId);
        if (user != null && goods != null) {
            if (user.getWalletBlock() >= goods.getGoodsPrice()) {
                user.setWalletBlock(user.getWalletBlock() - goods.getGoodsPrice());
                Users save1 = usersRepository.save(user);
                UserBuyRecord save = userBuyRecordRepository.save(new UserBuyRecord(userId, goodsId, false));
                if (save == null || save1 == null) {
                    throw new Exception("购买失败");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Goods> getPack(int userId) {
        //根据用户id获取未使用过的已购商品list
        List<UserBuyRecord> userBuyRecords = userBuyRecordRepository.findByUserIdAndAlreadyUse(userId, false);
        List<Integer> goodsIdList = new ArrayList<>();
        for (UserBuyRecord userBuyRecord :userBuyRecords) {
            goodsIdList.add(userBuyRecord.getGoodsId());
        }
        //根据goodIds获取商品信息
        List<Goods> byIdIn = goodsRepository.findByIdIn(goodsIdList);
        if (byIdIn == null) {
            return new ArrayList<>();
        }
        List<Goods> result = new ArrayList<>();
        Map<Integer, Goods> goodsMap = new HashMap<>();
        for (Goods goods:byIdIn) {
            goodsMap.put(goods.getId(), goods);
        }
        for (UserBuyRecord userBuyRecord:userBuyRecords) {
            Goods goods = goodsMap.get(userBuyRecord.getGoodsId());
            goods.setRecordId(userBuyRecord.getId());
            result.add(goods);
        }
        return result;
    }

    @Override
    public boolean useGoods(int recordId) {
        UserBuyRecord one = userBuyRecordRepository.getOne(recordId);
        if (one != null) {
            one.setAlreadyUse(true);
        } else {
            return false;
        }
        return userBuyRecordRepository.save(one) != null;
    }
}
