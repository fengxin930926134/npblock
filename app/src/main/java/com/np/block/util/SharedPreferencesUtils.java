package com.np.block.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.np.block.NpBlockApplication;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地SharedPreferences持久化工具类
 *
 * @author fengxin
 */
public class SharedPreferencesUtils {

    /**SharedPreferences*/
    private static SharedPreferences sp;

    /**token sp文件名*/
    private static final String SP_TOKEN = "np_token";

    /**经典分数 sp文件名*/
    private static final String SP_SCORE = "np_score";

    /**挑战关卡 sp文件名*/
    private static final String SP_PASS = "np_pass";

    /**挑战关卡 音乐开关*/
    private static final String SP_MUSIC = "sp_music";

    /**
     * 保存token信息
     *
     * @param token token
     * @param tokenTime token超时时间
     * @return boolean
     */
    public static boolean saveToken(String token, long tokenTime) {
        sp = NpBlockApplication.getInstance().getApplicationContext().
                getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConstUtils.SP_TOKEN, token);
        edit.putLong(ConstUtils.SP_TOKEN_TIME, tokenTime);
        return !edit.commit();
    }

    /**
     * 读取token信息
     *
     * @return map
     */
    public static Map<String, Object> readToken() {
        Map<String, Object> map = new HashMap<>(2);
        sp = NpBlockApplication.getInstance().getApplicationContext().
                getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
        String token = sp.getString(ConstUtils.SP_TOKEN, null);
        long tokenTime = sp.getLong(ConstUtils.SP_TOKEN_TIME, 0);
        if (token != null && tokenTime != 0) {
            map.put(ConstUtils.SP_TOKEN, token);
            map.put(ConstUtils.SP_TOKEN_TIME, tokenTime);
            return map;
        }
        return null;
    }

    /**
     * 清理token
     *
     * @return boolean
     */
    public static boolean clearToken() {
        sp = NpBlockApplication.getInstance().getApplicationContext().
                getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor.commit();
    }

    /**
     * 保存分数信息
     *
     * @param score 分数
     * @return boolean
     */
    public static boolean saveScore(int score) {
        sp = NpBlockApplication.getInstance().getApplicationContext()
                .getSharedPreferences(SP_SCORE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(SP_SCORE, score);
        return !edit.commit();
    }

    /**
     * 读取Score信息
     *
     * @return int
     */
    public static int readScore() {
        sp = NpBlockApplication.getInstance().getApplicationContext()
                .getSharedPreferences(SP_SCORE, Context.MODE_PRIVATE);
        return sp.getInt(SP_SCORE, 0);
    }

    /**
     * 保存关卡信息
     *
     * @param pass 关卡
     * @return boolean
     */
    public static boolean savePass(int pass) {
        sp = NpBlockApplication.getInstance().getApplicationContext()
                .getSharedPreferences(SP_PASS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(SP_PASS, pass);
        return !edit.commit();
    }

    /**
     * 读取PASS信息
     *
     * @return int
     */
    public static int readPass() {
        sp = NpBlockApplication.getInstance().getApplicationContext()
                .getSharedPreferences(SP_PASS, Context.MODE_PRIVATE);
        return sp.getInt(SP_PASS, 0);
    }

    /**
     * 保存背景音乐开关配置
     *
     * @param b 开关
     */
    public static void saveMusic(boolean b) {
        sp = NpBlockApplication.getInstance().getApplicationContext()
                .getSharedPreferences(SP_MUSIC, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(SP_MUSIC, b);
        edit.apply();
    }

    /**
     * 读取背景音乐开关配置
     *
     * @return boolean
     */
    public static boolean readMusic() {
        sp = NpBlockApplication.getInstance().getApplicationContext()
                .getSharedPreferences(SP_MUSIC, Context.MODE_PRIVATE);
        if (!sp.contains(SP_MUSIC)) {
            return true;
        }
        return sp.getBoolean(SP_MUSIC, true);
    }

    /**私有化构造方法*/
    private SharedPreferencesUtils(){
    }

}
