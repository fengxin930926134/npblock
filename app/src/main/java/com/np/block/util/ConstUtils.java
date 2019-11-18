package com.np.block.util;

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
     * 短信appKey
     */
    public static final String APPKEY = "101732155b605";

    /**
     * 短信APPSECRETE
     */
    public static final String APPSECRETE = "69d1850f4b74100266ab576b64e6cb16";

    /**
     * 请求url
     */
//    public static final String URL = "http://192.168.10.228:8083";
    public static final String URL = "http://192.168.2.112:8083";

    private ConstUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
}