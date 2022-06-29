package com.npblock.webservice.util;

import java.util.UUID;

/**
 * Token工具类
 *
 * @author fengxin
 */
public class TokenUtils {

    /**
     * 生成Token
     * @return token
     */
    public static String generateToken(){
        return UUID.randomUUID().toString().concat("np");
    }

    /**
     * 刷新token过期时间
     *
     * @return token过期时间
     */
    public static long refreshTokenTime() {
        return System.currentTimeMillis() + 259200000;
    }


    /**
     * 判断token是否过期
     *
     * @param tokenTime tokenTime
     * @return boolean
     */
    public static boolean tokenIsNotExpired(long tokenTime) {
        return tokenTime > System.currentTimeMillis();
    }

    private TokenUtils(){}
}
