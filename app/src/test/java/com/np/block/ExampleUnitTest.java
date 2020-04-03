package com.np.block;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.enums.TetrisTypeEnum;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Tetris;
import com.np.block.core.model.UnitBlock;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit litepal.xml, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws IOException, CloneNotSupportedException {
        assertEquals(4, 2 + 2);
//        String response = OkHttpUtils.get("/hello");
//        System.out.println(response);

        Map<String, Integer> map= new Hashtable<>();
        map.put("test", 2);
        Integer test = map.get("test");
        System.out.println( test);
        test = test + 1;
        map.put("test", test);
        System.out.println(test);
        System.out.println(map.get("test"));
//        JSONObject param = new JSONObject();
//        param.put("key", "27298457-92f9-41b9-b3b8-9f3a88063f2b");
//        try {
//            JSONObject response = OkHttpUtils.post("/match/getMsg", param.toString());
//            System.out.println(response.toJSONString());
//            if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS) {
//                JSONArray objects = JSONObject.parseArray(response.getString(ConstUtils.RESULT));
//                if (objects != null) {
//                    //用户信息
//                    List<Users> users = objects.toJavaList(Users.class);
//                    System.out.println(users.toString());
//                    //保存到缓存中
//                    CacheManager.getInstance().putUsers(ConstUtils.CACHE_USER_BATTLE_INFO, users);
//                } else {
//                    System.out.println("空数据");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("获取对局用户信息错误：" + e.getMessage());
//        }

        long l = System.currentTimeMillis();
        long l2 = System.currentTimeMillis();
        long i = l2-l;
        System.out.println(i);
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

//        int notOccupyPort = OkHttpUtils.getNotOccupyPort();
//        DatagramSocket socket = new DatagramSocket(notOccupyPort);
//        System.out.println("使用"+notOccupyPort+"成功");
//        System.out.println(getIpAddressString());
//        String s = "[{\"classicScore\":12314,\"id\":3,\"createDate\":1575697962000},{\"classicScore\":123,\"openId\":\"12412dawd\",\"sex\":1,\"tokenTime\":1241254,\"token\":\"1241512512\",\"gameName\":\"124124\",\"phone\":\"14124\",\"name\":\"13123\",\"id\":1,\"createDate\":1575697957000}]";
//        List<Users> users = JSONObject.parseArray(s, Users.class);
//        System.out.println(users);
    }


    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
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