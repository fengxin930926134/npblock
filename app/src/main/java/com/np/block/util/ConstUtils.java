package com.np.block.util;

/**
 * 常量工具类
 * @author fengxin
 */
public class ConstUtils {

    /**
     * 再按一次返回退出的触发时间
     */
    public static final int BACK_TIME = 2000;

    /**
     * 字符编码
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 请求url
     */
    public static final String URL = "http://192.168.10.228:8083";
//    public static final String URL = "http://192.168.2.112:8083";

    private ConstUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
}