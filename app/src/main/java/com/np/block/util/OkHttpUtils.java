package com.np.block.util;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
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

    public static String post(String url, String json) throws IOException {
        JSONObject params = new JSONObject();
        params.put("params", JSONObject.parse(json));
        RequestBody body = RequestBody.create(params.toJSONString(), JSON);
        Request request = new Request.Builder()
                .url(ConstUtils.URL + url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return "{'status':"+response.code()+",'body':"+ Objects.requireNonNull(response.body()).string()+"}";
        }
    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(ConstUtils.URL + url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return "{'status':"+response.code()+",'body':"+ Objects.requireNonNull(response.body()).string()+"}";
        }
    }
}
