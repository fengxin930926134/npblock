package com.np.block.util;

import android.net.ConnectivityManager;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OkHttp框架封装工具类
 * @author fengxin
 */
public class OkHttpUtils {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client  = new OkHttpClient().newBuilder().connectTimeout(50000, TimeUnit.MILLISECONDS)
            .readTimeout(50000, TimeUnit.MILLISECONDS)
            .build();
    /**
     * Post请求
     *
     * @param url url
     * @param json 请求数据
     * @return json
     * @throws Exception Exception
     */
    public synchronized static JSONObject post(String url, String json) throws Exception {
        LoggerUtils.toJson(json);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(ConstUtils.URL + url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() != ConstUtils.STATUS_SUCCESS) {
                throw new Exception("请求服务器失败");
            }
            return JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
        }
    }

    /**
     * get请求
     *
     * @param url url
     * @return json
     * @throws Exception Exception
     */
    public synchronized static JSONObject get(String url) throws Exception {
        Request request = new Request.Builder()
                .url(ConstUtils.URL + url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() != ConstUtils.STATUS_SUCCESS) {
                throw new Exception("请求服务器失败");
            }
            return JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
        }
    }

    /**
     * 获取有效端口
     * 查询到一个未被使用端口则返回
     * @return int
     */
    public synchronized static int getNotOccupyPort() {
        final int portBegin = 8900;
        final int portEnd = 9000;
        for (int i = portBegin; i < portEnd; i++) {
            try {
                DatagramSocket socket = new DatagramSocket(i);
                socket.close();
                return i;
            } catch (SocketException e) {
                LoggerUtils.i("检查到" + i + "被占用，继续遍历。");
            }
        }
        return 9001;
    }
}
