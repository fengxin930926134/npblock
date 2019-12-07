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

    /**游戏分数 sp文件名*/
    private static final String SP_SCORE = "np_score";

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
        edit.putInt(ConstUtils.SP_SCORE, score);
        return !edit.commit();
    }

    /**
     * 读取token信息
     *
     * @return map
     */
    public static int readScore() {
        sp = NpBlockApplication.getInstance().getApplicationContext()
                .getSharedPreferences(SP_SCORE, Context.MODE_PRIVATE);
        return sp.getInt(ConstUtils.SP_SCORE, 0);
    }

    /**私有化构造方法*/
    private SharedPreferencesUtils(){
    }

}
