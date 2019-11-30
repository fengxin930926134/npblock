package com.np.block.util;

import java.util.Random;

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

    private RandomUtils(){}
}
