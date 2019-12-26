package com.np.block.util;

import android.graphics.Color;

/**
 * 常量工具类
 * 如果特别多了再考虑分类
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
     * 执行接口成功 但是要达成的条件失败
     */
    public static final int CODE_ERROR = 80007;

    /**
     * 状态码 成功
     */
    static final int STATUS_SUCCESS = 200;

    /**
     * 接口返回的状态
     */
    public static final String CODE = "code";

    /**
     * 字符编码
     */
    static final String CHARSET = "UTF-8";

    /**
     * sp 保存token的key值
     */
    public static final String SP_TOKEN = "spToken";

    /**
     * sp 保存token超时时间的key值
     */
    public static final String SP_TOKEN_TIME = "spTokenTime";

    /**
     * sp 保存经典模式分数的key值
     */
    static final String SP_SCORE = "spScore";

    /**
     * 需要用到的颜色的数组
     */
    public static final int[] COLOR = {Color.parseColor("#FF6600"), Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};

    /**
     * 再次发送短信的秒数
     */
    public static final int SHORT_MESSAGE_TIME = 30;

    /**
     * 缓存 用户信息数据的KEY
     */
    public static final String CACHE_USER_INFO = "userInfo";

    /**
     * 缓存 用户等待上传的经典模式分数的KEY
     */
    public static final String CACHE_WAIT_UPLOAD_CLASSIC_SCORE = "waitUploadClassicScore";

    /**
     * 缓存 经典模式排行榜的key
     */
    public static final String CACHE_RANK_CLASSICAL_MODE = "cache_rank_classical_mode";

    /**
     * 缓存 排位模式排行榜的key
     */
    public static final String CACHE_RANK_RANKING_MODE = "cache_rank_ranking_mode";

    /**
     * 缓存 闯关模式排行榜的key
     */
    public static final String CACHE_RANK_BREAKTHROUGH_MODE = "cache_rank_breakthrough_mode";

    /**
     * 缓存 关卡信息的KEY
     */
    public static final String CACHE_RUSH_STAGE_TYPE = "cache_rush_stage_type";

    /**
     * 请求url
     */
    static final String URL = "https://npblock.cn";

    private ConstUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
}