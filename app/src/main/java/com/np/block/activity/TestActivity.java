package com.np.block.activity;

import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.QueueMessage;
import com.np.block.util.ConstUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;
import com.np.block.util.RandomUtils;

import org.litepal.util.Const;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import butterknife.BindView;

/**
 * 用做测试使用 代码不必提交
 *
 * @author fengxin
 */
public class TestActivity extends BaseActivity {

    @BindView(R.id.test_1)
    Button button;
    @BindView(R.id.test_2)
    Button button2;
    @BindView(R.id.test_3)
    TextView textView;
    @BindView(R.id.test_4)
    Button button3;
    @BindView(R.id.test_5)
    Button button4;
    private QueueMessage queueMessage;
    @Override
    public void init() {
//        String s = "[{\"classicScore\":12314,\"id\":3,\"createDate\":1575697962000},{\"classicScore\":123,\"openId\":\"12412dawd\",\"sex\":1,\"tokenTime\":1241254,\"token\":\"1241512512\",\"gameName\":\"124124\",\"phone\":\"14124\",\"name\":\"13123\",\"id\":1,\"createDate\":1575697957000}]";
//        LoggerUtils.toJson(s);
        // litePal测试
//        DefaultDataBase.generateBasicDatabase();
//        Stage test1 = LitePal
//                .where("stageType = ?", StageTypeEnum.FIRST_PASS.getCode())
//                .findLast(Stage.class);
//        LoggerUtils.d("[测试] test="+ test1.toString());
//        List<Tetris> allTetris = test1.getAllTetris();
//        LoggerUtils.d("[测试] test="+ allTetris.toString());
//        List<UnitBlock> allUnitBlock = allTetris.get(0).getAllUnitBlock();
//        LoggerUtils.d("[测试] test="+ allUnitBlock.toString());
        button3.setEnabled(false);
        button4.setEnabled(false);
        button.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() ->{
            try {
                int port = OkHttpUtils.getNotOccupyPort();
                String ipAddressString = OkHttpUtils.getIpAddressString();
                queueMessage = new QueueMessage(RandomUtils.getUUID(),ipAddressString , port);
                JSONObject post = OkHttpUtils.post("/match/add", JSONObject.toJSONString(queueMessage));
                LoggerUtils.toJson(post.toJSONString());
                runOnUiThread(() -> {
                    Toast.makeText(context, "加入成功", Toast.LENGTH_SHORT).show();
                    button.setText("队列中");
                    button.setEnabled(false);
                    button4.setEnabled(true);
                });

                /*
                 * 接收服务器端响应的数据
                 */
                DatagramSocket socket = new DatagramSocket(port);
                while (true) {
                    // 1.创建数据报，用于接收服务器端响应的数据
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    // 2.接收服务器响应的数据
                    socket.receive(packet);
                    // 3.读取数据 （2种情况）
                    final String reply = new String(data, 0, packet.getLength());
                    LoggerUtils.i("服务器发来的信息是：" + reply);
                    JSONObject jsonObject = JSONObject.parseObject(reply);
                    if (jsonObject.containsKey("key")) {
                        // 说明是刚加入游戏队列
                        runOnUiThread(() -> {
                            button.setText("游戏中");
                            button3.setEnabled(true);
                            button.setEnabled(false);
                            textView.setText(String.valueOf(textView.getText()).concat("进入游戏\n"));
                        });
                        queueMessage.setKey(jsonObject.getString("key"));
                    } else if (jsonObject.containsKey("params")){
                        QueueMessage params = jsonObject.getObject("params", QueueMessage.class);
                        runOnUiThread(()->textView.setText(String.valueOf(textView.getText()).concat( params.getMsg() +"\n")));
                    } else {
                        runOnUiThread(() -> {
                            button.setEnabled(true);
                            button.setText("开始匹配");
                            textView.setText(String.valueOf(textView.getText()).concat( jsonObject.toJSONString() +"\n"));
                        });
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        button2.setOnClickListener(v -> {
            ThreadPoolManager.getInstance().execute(() ->{
                int port = OkHttpUtils.getNotOccupyPort();
                try (DatagramSocket socket = new DatagramSocket(port)){
                    /*
                     * 向服务器端发送数据
                     */
                    // 1.定义服务器的地址、端口号、数据
                    InetAddress address = InetAddress.getByName("npblock.cn");
                    queueMessage.setMsg("这是测试消息");
                    byte[] data = JSONObject.toJSONString(queueMessage).getBytes(StandardCharsets.UTF_8);
                    // 2.创建数据报，包含发送的数据信息
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, 65535);
                    // 4.向服务器端发送数据报
                    socket.send(packet);
                    socket.close();
                    LoggerUtils.i("发送了一条消息");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        button3.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() -> {
            try {
                OkHttpUtils.post("/match/remove", JSONObject.toJSONString(queueMessage));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }));

        button4.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() -> {
            try {
                OkHttpUtils.post("/match/signOut", JSONObject.toJSONString(queueMessage));
                runOnUiThread(() -> {
                    button.setEnabled(true);
                    button.setText("开始匹配");
                    button4.setEnabled(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
