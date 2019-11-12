package com.np.block.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 格式转换工具类
 * @author fengxin
 */
class FormatConversionUtils {

    /**
     * 将map转化为url参数形式
     * 格式:a=1&b=2
     * @param map map集合
     * @return url参数
     */
    static String getUrlParamsByMap(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : map.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //sb.deleteCharAt(sb.lastIndexOf("&"));  貌似去不去最后一个&不影响
        return sb.toString();
    }

    private FormatConversionUtils() {

    }
}
