package com.np.block;

import com.alibaba.fastjson.JSONObject;
import com.np.block.core.model.UnitBlock;
import com.np.block.core.model.Users;
import com.np.block.util.OkHttpUtils;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException, CloneNotSupportedException {
        assertEquals(4, 2 + 2);
//        String response = OkHttpUtils.get("/hello");
//        System.out.println(response);



//        JSONObject jsonObject = new JSONObject();
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject1.put("phone","admin");
//        jsonObject1.put("password",123123);
//        jsonObject.put("params",jsonObject1);
//
//
//        try {
//            String response1 = OkHttpUtils.post("/login/verify", jsonObject.toJSONString());
//            JSONObject parse = JSONObject.parseObject(response1);
//            if (parse.getIntValue("status") == 200) {
//                parse = parse.getJSONObject("body");
//                if (parse.getIntValue("code") > 80000){
//                    System.out.println(parse.getString("msg"));
//                }else {
//                    System.out.println(parse.getString("result"));
//                }
//            }else {
//                System.out.println(parse);
//                System.out.println("网络异常");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("网络异常");
//        }

        //System.out.println(response1);

//        System.out.println(cmpGrade(2, 300));

//        List<UnitBlock> list = new ArrayList<>();
//        list.add(new UnitBlock(1,2,3));
//        List<UnitBlock> test = new ArrayList<>();
//        test.add(list.get(0).clone());
//        test.get(0).setX(777);
//        System.out.println(list.get(0));
//        System.out.println(test.get(0));

//        Users users = new Users();
//        users.setName("dawdd");
//        users.setPhone("18785517851");
//        String jsonObject = JSONObject.toJSONString(users);
//        System.out.println(jsonObject);
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject1.put("params", JSONObject.parse(jsonObject));
//        System.out.println(jsonObject1.toJSONString());
//
//
//
//        JSONObject jsonObject2 = new JSONObject();
//        jsonObject2.put("name", "dawdd");
//        jsonObject2.put("phone","18785517851");
//        JSONObject jsonObject3 = new JSONObject();
//        jsonObject3.put("params2", jsonObject2);
//        System.out.println(jsonObject3.toJSONString());

        String s = "[{\"classicScore\":12314,\"id\":3,\"createDate\":1575697962000},{\"classicScore\":123,\"openId\":\"12412dawd\",\"sex\":1,\"tokenTime\":1241254,\"token\":\"1241512512\",\"gameName\":\"124124\",\"phone\":\"14124\",\"name\":\"13123\",\"id\":1,\"createDate\":1575697957000}]";
        List<Users> users = JSONObject.parseArray(s, Users.class);
        System.out.println(users);
    }

    private int cmpGrade(int grade, int score) {
        double a = score / Math.pow(grade, 2);
        if ((int)a >= 100) {
            return cmpGrade(grade + 1, score);
        } else {
            return grade;
        }
    }
}