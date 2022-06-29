package com.npblock.webservice.util;

/**
 * 字符串工具类
 * 对字符串的转换等保存下来下次方便使用
 *
 * @author fengxin
 */
public class StringUtils {

    /**
     * 将http直接换成https
     *
     * @param url url
     * @return 转换过后的https url
     */
    public static String HttpToHttps(String url) {
        return url.replaceFirst("http", "https");
    }

    private StringUtils(){}
}
