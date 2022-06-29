package com.npblock.webservice.service;

import com.npblock.webservice.entity.Users;

import java.util.List;

public interface UsersService {
    /**
     * 手机号和密码登陆
     * @param phone 手机号
     * @param password 密码
     * @return 用户信息(含token)
     */
    Users loginByPhoneAndPassword(String phone, String password);

    /**
     * 注册用户
     * @param users 用户设置的基本信息
     * @return 创建的用户(含token)
     */
    Users register(Users users);

    /**
     * QQ登陆
     * @param users qq用户信息
     * @return 用户信息(含token)
     */
    Users loginByQQ(Users users);

    /**
     * token登陆
     *
     * @param token token
     * @param tokenTime 超时时间
     * @return 信息
     */
    Users loginByToken(String token, long tokenTime);

    /**
     * 判断手机号是否被注册
     *
     * @param phone 手机号
     * @return boolean
     */
    boolean expiredPhone(String phone);

    /**
     * 判断游戏名字是否被注册
     *
     * @param gameName 游戏名字
     * @return boolean
     */
    boolean expiredGameName(String gameName);

    /**
     * 更新游戏名字
     *
     * @param token token
     * @param gameName 游戏名字
     * @return int
     */
    boolean updateGameName(String token, String gameName);

    /**
     * 更新方块币
     *
     * @param id id
     * @param block 方块b
     * @return int
     */
    boolean updateBlockById(Integer id, Integer block);

    /**
     * 查询用户
     * @param name 姓名
     * @param sex 性别 1男 2女
     * @param token token
     * @return list
     */
    List<Users> selectUserBySexAndName(String name, Integer sex, String token);

    /**
     * 查询用户的好友列表
     * @return list
     */
    List<Users> selectFriendById(int id);

    /**
     * 查询指定ids用户
     * @param ids ids
     * @return list
     */
    List<Users> findByIdIn(List<Integer> ids);

    /**
     * 判断token是否过期
     * @param token token
     * @return boolean
     */
    boolean tokenIsExpired(String token);
}
