package com.np.block.util;

import android.util.Log;

public class LogUtils {

    /**
     * 打印log开关
     */
    private static boolean IS_DEBUG = true;

    /**
     * Log.e 打印
     *
     * @param text 需要打印的内容
     */
    public static synchronized void e(String tag,String text) {
        if (IS_DEBUG) {
            Log.e(tag, text);
        }
    }

    /**
     * Log.d 打印
     *
     * @param text 需要打印的内容
     */
    public static synchronized void d(String tag,String text) {
        if (IS_DEBUG) {
            Log.d(tag, text);
        }
    }

    /**
     * Log.w 打印
     *
     * @param text 需要打印的内容
     */
    public static synchronized void w(String tag,String text) {
        if (IS_DEBUG) {
            Log.w(tag, text);
        }
    }

    /**
     * Log.i 打印
     *
     * @param text 需要打印的内容
     */
    public static synchronized void i(String tag,String text) {
        if (IS_DEBUG) {
            Log.i(tag, text);
        }
    }

    private LogUtils(){}
}
