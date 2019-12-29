package com.np.block.util;

import java.util.Random;
import java.util.UUID;

/**
 *  随机数工具类
 * @author fengxin
 */
public class RandomUtils {

    /**随机数制造类*/
    private static Random random = new Random(System.currentTimeMillis());

    /**
     * 返回一个随机数 [1 ，number]
     * @param number 参数
     * @return int
     */
    public static int getInt(int number){
        return random.nextInt(number) + 1;
    }

    /**
     * 随机一个uuid
     * @return UUID
     */
    public static String getUUID(){
        return UUID.randomUUID().toString();
    }

    private RandomUtils(){}
}
