package com.npblock.webservice.repository;

import com.npblock.webservice.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    /**
     * 查询数据库是否有这个游戏名字
     *
     * @param gameName 游戏名字
     * @return boolean
     */
    Users findUsersByGameName(String gameName);

    /**
     * 查询数据库指定手机号码和密码的用户
     *
     * @param phone 手机号码
     * @param password 密码
     * @return 满足条件的用户
     */
    Users findFirstByPhoneAndPassword(String phone, String password);

    /**
     * 根据openId查询用户
     *
     * @param openId qqOpen
     * @return Users
     */
    Users findByOpenId(String openId);

    /**
     * 根据token查询用户
     *
     * @param token token
     * @return Users
     */
    Users findByToken(String token);

    /**
     * 根据classicId 查询用户
     *
     * @param classicId 经典成绩id
     * @return user
     */
    Users findFirstByClassicId(Integer classicId);

    /**
     * 根据rushId 查询用户
     *
     * @param rushId 挑战成绩id
     * @return user
     */
    Users findFirstByRushId(Integer rushId);

    /**
     * 根据rankId 查询用户
     *
     * @param rankId rankId
     * @return user
     */
    Users findFirstByRankId(Integer rankId);

    /**
     * 根据phone查询用户
     *
     * @param phone phone
     * @return Users
     */
    Users findByPhone(String phone);

    /**
     * 根据用户id更新token过期时间
     *
     * @param id 用户id
     * @param tokenTime token过期时间
     * @return int
     */
    @Transactional
    @Modifying
    @Query("update Users u set u.tokenTime = ?2 where u.id = ?1")
    int updateTokenTimeById(Integer id, Long tokenTime);

    /**
     * 根据用户token更新游戏名称
     * @param token token
     * @param gameName 游戏名字
     * @return int
     */
    @Transactional
    @Modifying
    @Query("update Users u set u.gameName = ?2 where u.token = ?1")
    int updateGameNameByToken(String token, String gameName);

    /**
     * 根据用户id增加方块币
     * @param id userID
     * @param block 增加的方块币
     * @return int
     */
    @Transactional
    @Modifying
    @Query("update Users u set u.walletBlock = u.walletBlock + ?2 where u.id = ?1")
    int updateBlockById(int id, int block);

    /**
     * 按照分页规则获取符合条件的全部记录
     *
     * @param pageable spring封装的分页实现类
     * @return 分页查询User
     */
    Page<Users> findByGameNameLikeAndSex(Pageable pageable, String gameName, int sex);

    /**
     * 查询指定ids用户
     * @param ids ids
     * @return list
     */
    List<Users> findByIdIn(List<Integer> ids);
}
