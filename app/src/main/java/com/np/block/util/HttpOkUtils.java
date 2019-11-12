package com.np.block.util;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpOkUtils {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(ConstUtils.URL+url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return "{'status':"+response.code()+",'body':"+response.body().string()+"}";
        }
    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(ConstUtils.URL+url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}
