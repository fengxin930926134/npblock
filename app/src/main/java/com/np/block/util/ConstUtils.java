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
     * 接口返回的信息
     */
    public static final String MSG = "msg";

    /**
     * 接口返回的数据
     */
    public static final String RESULT = "result";

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
     * 缓存 用户好友信息
     */
    public static final String CACHE_USER_FRIEND_INFO = "cache_user_friend_info";

    /**
     * 请求url
     */
    static final String URL = "https://npblock.cn";

    /**
     * 服务器主机地址
     */
    public static final String HOST = "192.144.128.184";

    /**
     * socket通信使用的发送端口
     */
    public static final int SOCKET_SEND_PORT = 65535;

    /**
     * Handler 消息类型 游戏数据消息
     */
    public static final int HANDLER_GAME_DATA = 0x10;

    /**
     * Handler 消息类型 计时消息
     */
    public static final int HANDLER_TIME_STRING = 0x11;

    /**
     * Handler 消息类型 匹配成功消息
     */
    public static final int HANDLER_MATCH_SUCCESS = 0x12;

    /**
     * Handler 消息类型 匹配失败消息
     */
    public static final int HANDLER_MATCH_FAILURE = 0x13;

    /**
     * Handler 消息类型 进入游戏消息
     */
    public static final int HANDLER_ENTER_THE_GAME = 0x14;

    /**
     * Handler 消息类型 游戏成功消息
     */
    public static final int HANDLER_GAME_WIN = 0x15;

    /**
     * Handler 消息类型 游戏失败消息
     */
    public static final int HANDLER_LOSE_GAME = 0x16;

    /**
     * Handler 消息类型 游戏敌人逃跑消息
     */
    public static final int HANDLER_ESCAPE_GAME = 0x17;

    /**
     * Handler 消息类型 建立聊天窗口消息
     */
    public static final int HANDLER_CHAT_WINDOW = 0x18;

    /**
     * json数据key 所有方块坐标
     */
    public static final String JSON_KEY_ALL_BLOCK = "json_key_all_block";

    /**
     * json数据key 俄罗斯方块坐标
     */
    public static final String JSON_KEY_TETRIS_BLOCK = "json_key_tetris_block";

    /**
     * json数据key 敌人成绩
     */
    public static final String JSON_KEY_ENEMY_SCORE = "json_key_enemy_score";

    private ConstUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
}