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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
    @BindView(R.id.test_6)
    Button button5;
    private QueueMessage queueMessage = new QueueMessage();
    private static final int TIMEOUT = 3000;   // 设置超时为3秒
    private static final int MAXTRIES = 5;     // 最大重发次数5次
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
        button5.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() -> {
            try {
                JSONObject jsonObject = OkHttpUtils.get("/hello");
                LoggerUtils.toJson(jsonObject.toJSONString());

//                /*******************发送数据***********************/
//                byte[] bytes = "这是测试消息".getBytes(StandardCharsets.UTF_8);
//                InetAddress address = InetAddress.getByName("192.144.128.184");
//                //1.构造数据包
//                DatagramPacket packet = new DatagramPacket(bytes,
//                        bytes.length, address, 65535);
//                //2.创建数据报套接字并将其绑定到本地主机上的指定端口。
//                DatagramSocket socket = new DatagramSocket();
//                //3.从此套接字发送数据报包。
//                socket.send(packet);
//                /*******************接收数据***********************/
//                //1.构造 DatagramPacket，用来接收长度为 length 的数据包。
//                final byte[] bytes1 = new byte[1024];
//                DatagramPacket receiverPacket = new DatagramPacket(bytes1, bytes1.length);
//                socket.receive(receiverPacket);
//                final String reply = new String(bytes1, 0, receiverPacket.getLength());
//                LoggerUtils.i("服务器发来的信息是：" + reply);
//                runOnUiThread(() -> Toast.makeText(context, "服务器发来的信息是：" + reply, Toast.LENGTH_SHORT).show());
//                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        button.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() ->{
            try {
                if (!reqSocket("test")) {
                    return;
                }
                /*
                 * 接收服务器端响应的数据 指定端口
                 */
                DatagramSocket socket = new DatagramSocket(9000);
                while (true) {
                    LoggerUtils.i("已经准备好接收数据");
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
                JSONObject post = OkHttpUtils.post("/match/remove", JSONObject.toJSONString(queueMessage));
                LoggerUtils.toJson(post.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }));

        button4.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() -> {
            try {
                JSONObject post = OkHttpUtils.post("/match/signOut", JSONObject.toJSONString(queueMessage));
                LoggerUtils.toJson(post.toJSONString());
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

    /*******************发送本机ip和端口到服务器***********************/
    private boolean reqSocket(String gameType) throws IOException {
        boolean receivedResponse = false;
        int tries = 0;
        //1.构造数据包（加入游戏类型, 消息类型，请求端口和ip）
        byte[] bytes = gameType.getBytes(StandardCharsets.UTF_8);
        InetAddress address = InetAddress.getByName("192.144.128.184");
        DatagramPacket sendPacket = new DatagramPacket(bytes,
                bytes.length, address, 65535);
        final byte[] bytes1 = new byte[1024];
        DatagramPacket receiverPacket = new DatagramPacket(bytes1, bytes1.length);
        //2.创建数据报套接字并将其绑定到本地主机上的指定端口。
        DatagramSocket socket = new DatagramSocket(9000);
        // 设置阻塞时间
        socket.setSoTimeout(TIMEOUT);
        do {
            //3.从此套接字发送数据报包。
            socket.send(sendPacket);
            try {
                socket.receive(receiverPacket);
                // 检查来源
                if (!receiverPacket.getAddress().equals(address)) {
                    throw new IOException("未知来源");
                }
                receivedResponse = true;
                // 处理接收到的消息
                runOnUiThread(() -> {
                    Toast.makeText(context, "加入成功", Toast.LENGTH_SHORT).show();
                    button.setText("队列中");
                    button.setEnabled(false);
                    button4.setEnabled(true);
                });
            } catch (InterruptedIOException e) {
                tries += 1;
                LoggerUtils.i("Timed out, " + (MAXTRIES - tries) + " more tries...");
            }
            tries++;
        } while (!receivedResponse && tries < MAXTRIES);
        socket.close();
        return receivedResponse;
    }

    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
