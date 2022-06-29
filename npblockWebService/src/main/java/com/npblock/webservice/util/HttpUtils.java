package com.npblock.webservice.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * http请求工具类
 *
 * @author fengxin
 */
public class HttpUtils {

    /**
     * 权限token
     */
    private static String accessToken = null;

    /**
     * 权限token过期时间
     */
    private static long tokenExpire = 0;

    /**
     * http请求
     *
     * @param url 请求url
     * @param body 参数
     * @param access 是否需要权限
     * @param method 请求方式
     * @return JSON
     */
    public static JSONObject http(String url, JSONObject body, boolean access, HttpMethod method){
        //使用RestTemplate来发送HTTP请求
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头  ContentType为APPLICATION_JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 是否需要权限
        if (access) {
            if (tokenExpire <= System.currentTimeMillis()) {
                // 过期刷新
                refreshAccessToken();
            }
            headers.set("Authorization", "Bearer ".concat(accessToken));
        }

        // 请求体，包括请求数据 body 和 请求头 headers
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(body, headers);
        try {
            //开始请求
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, method, httpEntity, JSONObject.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                return responseEntity.getBody();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取有效端口
     * 查询到一个未被使用端口则返回
     * @return int
     */
    public static int getNotOccupyPort() {
        final int portBegin = 65535;
        final int portEnd = 65530;
        for (int i = portBegin; i > portEnd; i--) {
            try {
                DatagramSocket socket = new DatagramSocket(i);
                socket.close();
                return i;
            } catch (SocketException e) {
                System.out.println("检查到" + i + "被占用，继续遍历。");
            }
        }
        return 9001;
    }

    /**
     * 构建获取access_token的Json对象
     *
     * @return JSONObject
     */
    private static JSONObject getAccessTokenJSON() {
        JSONObject body = new JSONObject();
        body.put("grant_type", "client_credentials");
        body.put("client_id", ConstUtils.IM_CLIENT_ID);
        body.put("client_secret", ConstUtils.IM_CLIENT_SECRET);
        return body;
    }

    /**
     * 刷新权限token
     */
    private static void refreshAccessToken() {
        JSONObject post = http(ConstUtils.IM_URL.concat("/token"), getAccessTokenJSON(), false, HttpMethod.POST);
        if (post != null) {
            accessToken = post.getString("access_token");
            // 减去2分钟防止莫名其妙情况
            tokenExpire = post.getLong("expires_in") + System.currentTimeMillis() - 120000;
        }
    }

    private HttpUtils(){}
}
