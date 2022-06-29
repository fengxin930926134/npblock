package com.npblock.webservice.util;

/**
 * 常量工具类
 *
 * @author fengxin
 */
public class ConstUtils {
    public final static int SOCKET_PORT = 65535;
    /**
     * 请求成功
     */
    public final static int REST_OK = 80000;

    /**
     * 数据库操作未按预期完成
     */
    public final static int REST_DB_ERROR = 80001;

    /**
     * 请求失败
     */
    public final static int REST_ERROR = 80007;

    /**
     * 经典模式排行榜名额
     */
    public final static int GAME_CLASSIC_RANK_NUMBER = 50;

    /**
     * 查询用户的条数
     */
    public final static int SELECT_USER_NUMBER = 100;

    /**
     * 环信请求Url
     */
    public final static String IM_URL = "http://a1.easemob.com/1128190927148345/npblock";

    /**
     * 环信Client ID
     */
    final static String IM_CLIENT_ID = "YXA6fiUmuUBtQxeZjFdFUXojlQ";

    /**
     * 环信Client Secret
     */
    final static String IM_CLIENT_SECRET = "YXA69oxCrKRyVa3fh7y2EG4rV6kdmLw";


    private ConstUtils(){}
}
