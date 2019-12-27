package com.np.block.util;

import com.alibaba.fastjson.JSONObject;
import java.util.Objects;
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

    private static OkHttpClient client  = new OkHttpClient();

    /**
     * Post请求
     *
     * @param url url
     * @param json 请求数据
     * @return json
     * @throws Exception Exception
     */
    public static JSONObject post(String url, String json) throws Exception {
        JSONObject params = new JSONObject();
        params.put("params", JSONObject.parse(json));
        RequestBody body = RequestBody.create(params.toJSONString(), JSON);
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
    public static JSONObject get(String url) throws Exception {
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
}
