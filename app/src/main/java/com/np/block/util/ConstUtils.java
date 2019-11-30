package com.np.block.util;

import android.graphics.Color;

/**
 * 常量工具类
 * @author fengxin
 */
public class ConstUtils {

    /**
     * 腾讯appID
     */
    public static final String APP_ID = "101825468";

    /**
     * 再按一次返回退出的触发时间
     */
    public static final int BACK_TIME = 2000;

    /**
     * 执行接口成功
     */
    public static final int CODE_SUCCESS = 80000;

    /**
     * 状态码 成功
     */
    public static final int STATUS_SUCCESS = 200;

    /**
     * 请求服务器返回的状态码
     */
    public static final String STATUS = "status";

    /**
     * 接口返回的状态
     */
    public static final String CODE = "code";

    /**
     * 字符编码
     */
    public static final String CHARSET = "UTF-8";

    /**
     * sp 保存token的key值
     */
    public static final String SP_TOKEN = "spToken";

    /**
     * sp 保存token超时时间的key值
     */
    public static final String SP_TOKEN_TIME = "spTokenTime";

    /**
     * 需要用到的颜色的数组
     */
    public static final int[] COLOR = {Color.parseColor("#FF6600"), Color.BLUE, Color.RED, Color.GREEN, Color.BLACK};

    /**
     * 再次发送短信的秒数
     */
    public static final int SHORT_MESSAGE_TIME = 30;

    /**
     * 请求url
     */
    static final String URL = "https://npblock.cn";

    private ConstUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
}