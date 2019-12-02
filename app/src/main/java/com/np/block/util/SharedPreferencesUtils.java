package com.np.block.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

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
     * @param context 上下文
     * @param token token
     * @param tokenTime token超时时间
     * @return boolean
     */
    public static boolean saveToken(Context context, String token, long tokenTime) {
        sp = context.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConstUtils.SP_TOKEN, token);
        edit.putLong(ConstUtils.SP_TOKEN_TIME, tokenTime);
        return !edit.commit();
    }

    /**
     * 读取token信息
     *
     * @param context 上下文
     * @return map
     */
    public static Map<String, Object> readToken(Context context) {
        Map<String, Object> map = new HashMap<>();
        sp = context.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
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
     * @param context 上下文
     * @return boolean
     */
    public static boolean clearToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        return editor.commit();
    }

    /**
     * 保存分数信息
     *
     * @param context 上下文
     * @param score 分数
     * @return boolean
     */
    public static boolean saveScore(Context context, int score) {
        sp = context.getSharedPreferences(SP_SCORE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(ConstUtils.SP_SCORE, score);
        return !edit.commit();
    }

    /**
     * 读取token信息
     *
     * @param context 上下文
     * @return map
     */
    public static int readScore(Context context) {
        sp = context.getSharedPreferences(SP_SCORE, Context.MODE_PRIVATE);
        return sp.getInt(ConstUtils.SP_SCORE, 0);
    }

    /**私有化构造方法*/
    private SharedPreferencesUtils(){
    }

}
