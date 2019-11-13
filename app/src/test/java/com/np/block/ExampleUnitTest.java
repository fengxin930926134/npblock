package com.np.block;

import com.alibaba.fastjson.JSONObject;
import com.np.block.util.OkHttpUtils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException {
        assertEquals(4, 2 + 2);
//        String response = OkHttpUtils.get("/hello");
//        System.out.println(response);



        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("phone","admin");
        jsonObject1.put("password",123123);
        jsonObject.put("params",jsonObject1);


        try {
            String response1 = OkHttpUtils.post("/login/verify", jsonObject.toJSONString());
            JSONObject parse = JSONObject.parseObject(response1);
            if (parse.getIntValue("status") == 200) {
                parse = parse.getJSONObject("body");
                if (parse.getIntValue("code") > 80000){
                    System.out.println(parse.getString("msg"));
                }else {
                    System.out.println(parse.getString("result"));
                }
            }else {
                System.out.println(parse);
                System.out.println("网络异常");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("网络异常");
        }

        //System.out.println(response1);



    }
}