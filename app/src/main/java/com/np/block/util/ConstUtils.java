package com.np.block.util;

import android.graphics.Color;

/**
 * 常量工具类
 * 如果特别多了再考虑分类
 * @author fengxin
 */
public class ConstUtils {
    /**
     * 群号：这个方块贼牛皮(753857139) 的 key 为： kB_Ss8F5X29JHJ1DJf-Khc_Z0RvbkhCw
     */
    public static final String QQ_GROUP = "kB_Ss8F5X29JHJ1DJf-Khc_Z0RvbkhCw";

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

//    /**
//     * 执行接口成功 但是要达成的条件失败
//     */
//    public static final int CODE_ERROR = 80007;

    /**
     * 游戏进行的时长
     */
    public static final String GAME_TIME = "gameTime";

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
     * 缓存 对战信息
     */
    public static final String CACHE_USER_BATTLE_INFO = "cache_user_battle_info";

    /**
     * 请求url
     */
    public static final String URL = "https://npblock.cn";

    /**
     * 服务器主机地址
     */
    public static final String HOST = "192.144.128.184";

    /**
     * socket通信使用的发送端口
     */
    public static final int SOCKET_SEND_PORT = 65535;

    /**
     * Handler 消息类型 开始游戏
     */
    public static final int HANDLER_START_CUSTOMIZATION = 0x5;

    /**
     * Handler 消息类型 有人加入房间
     */
    public static final int HANDLER_JOIN_ROOM = 0x6;

    /**
     * Handler 消息类型 退出房间
     */
    public static final int HANDLER_EXIT_ROOM = 0x7;

    /**
     * Handler 消息类型 取消准备
     */
    public static final int HANDLER_PREPARE_CANCEL = 0x8;

    /**
     * Handler 消息类型 准备
     */
    public static final int HANDLER_PREPARE = 0x9;

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

//    /**
//     * Handler 消息类型 离线弹窗
//     */
//    public static final int HANDLER_OFFLINE_WINDOW = 0x19;
//    /**
//     * Handler 消息类型 邀请好友游戏
//     */
//    public static final int HANDLER_INVITE_GAME = 0x20;

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

    /**
     * 游戏类型
     */
    public static final String GAME_TYPE = "game_type";

    /**
     * 游戏是否胜利
     */
    public static final String GAME_WIN = "game_win";

    /**
     * 方块币
     */
    public static final String GAME_BLOCK = "block";

    /**
     * rank状态类型
     */
    public static final String GAME_RANK_STATE = "rankStateType";

    /**
     * rank变化分数
     */
    public static final String GAME_RANK = "rank";

    /**
     * 获取当前段位名称
     *
     * @param rankGrade rank分
     */
    public static String getRankName(int rankGrade, boolean isPromotion) {
        int rank = rankGrade;
        //是否晋级赛
        int initRank = 1000;
        if (rank > initRank) {
            if (isPromotion) {
                rank = rank - 1;
            }
        }
        rank = rank - rank % 100;
        switch (rank) {
            case 1000: return "英勇黄铜Ⅲ";
            case 1100: return "英勇青铜Ⅱ";
            case 1200: return "英勇青铜Ⅰ";
            case 1300: return "不屈白银Ⅲ";
            case 1400: return "不屈白银Ⅱ";
            case 1500: return "不屈白银Ⅰ";
            case 1600: return "荣耀黄金Ⅲ";
            case 1700: return "荣耀黄金Ⅱ";
            case 1800: return "荣耀黄金Ⅰ";
            case 1900: return "华贵白金Ⅲ";
            case 2000: return "华贵白金Ⅱ";
            case 2100: return "华贵白金Ⅰ";
            case 2200: return "璀璨钻石Ⅲ";
            case 2300: return "璀璨钻石Ⅱ";
            case 2400: return "璀璨钻石Ⅰ";
            default: {
                //此时超出限制分数 用逗号分割记录超过的分数
                return "最强王者，".concat(Integer.valueOf(rankGrade - 2500).toString());
            }
        }
    }

    private ConstUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
}