package com.np.block.util;

import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * http请求工具类
 * @author fengxin
 */
public class HttpRequestUtils {

    private static HttpURLConnection httpURLConnection = null;
    /**
     * 字节输入流
     */
    private static InputStream inputStream = null;
    /**
     * 字节输出流
     */
    private static OutputStream outputStream = null;
    /**
     * 字符输入流
     */
    private static InputStreamReader inputStreamReader = null;
    /**
     * 字符流缓冲区
     */
    private static BufferedReader bufferedReader = null;

    /**
     * POST请求服务器
     * @param httpUrl 请求url
     * @param params 请求参数map集合 JSONObject类型也行（其实现了map）
     * @param oAuth 是否验证oAuth2.0 Null为否
     * @param isJson 服务器接收参数是否为json
     * @return 成功返回json类型的String字符串的请求结果，失败返回错误结果
     */
    public static String sendPostRequest(String httpUrl, Map<String, Object> params, String oAuth, boolean isJson) {
        try{
            //创建url
            URL url = new URL(ConstUtils.URL + httpUrl);
            //打开url连接
            httpURLConnection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            if (oAuth != null) {
                httpURLConnection.setRequestProperty("Authorization", oAuth);
            }
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpURLConnection.connect();

            String data;
            if (!isJson){
                data = FormatConversionUtils.getUrlParamsByMap(params);
            }else {
                data = new JSONObject(params).toString();
            }
            //写入参数
            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            //读取返回数据
            int code = 400;
            if (httpURLConnection.getResponseCode() < code) {
                inputStream = httpURLConnection.getInputStream();
            }else {
                inputStream = httpURLConnection.getErrorStream();
            }
            //获得输入
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            //将bufferReader的值给放到buffer里
            String str;
            StringBuilder strBuf = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                strBuf.append(str);
            }
            return strBuf.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (outputStream != null){
                    outputStream.close();
                }
                if (inputStreamReader != null){
                    inputStreamReader.close();
                }
                if (bufferedReader != null){
                    bufferedReader.close();
                }
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    private HttpRequestUtils(){
    }
}
