package com.np.block.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.enums.MessageTypeEnum;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Message;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;
import com.np.block.util.RandomUtils;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
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
    @BindView(R.id.edit_text)
    EditText editText;
    private Message message = new Message();
    /**设置超时为3秒*/
    private static final int TIMEOUT = 3000;
    /**最大重发次数5次*/
    private static final int MAXTRIES = 5;
    final byte[] receiverData = new byte[2048];
    /**创建数据报套接字并将其绑定到本地主机上的指定端口*/
    DatagramSocket socket;
    DatagramPacket receiverPacket = new DatagramPacket(receiverData, receiverData.length);
    InetAddress sendAddress;
    private int sendPort = 65535;
    private int receiverPort;
    /**判断是否退出队列或游戏*/
    private boolean isExit = false;
    /**确认收到key 防止服务器多个同样消息*/
    private boolean confirmReceiptKey = false;
    /**确认点击ok*/
    private boolean confirmOk = false;
    AlertDialog dialog = null;
    TextView content;
    private boolean isGame = false;
    /**匹配等待时间*/
    private long matchWaitTime = 10 * 1000;
    @Override
    public void init() {
        receiverPort = OkHttpUtils.getNotOccupyPort();
        try {
            sendAddress = InetAddress.getByName("192.144.128.184");
        } catch (IOException e) {
            LoggerUtils.i(e.getMessage());
        }
        message.setId(RandomUtils.getUUID());
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setOnClickListener(v -> {
            //countDownTimer.start();
        });
        //进入队列
        button.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() ->{
            try {
                if (!reqSocket()) {
                    return;
                }
                LoggerUtils.i("已进入队列");
                /*
                 * 接收服务器端响应的数据 （包括匹配，和游戏中）
                 */
                isExit = false;
                confirmReceiptKey = false;
                socket = new DatagramSocket(receiverPort);
                while (true) {
                    LoggerUtils.i("已经准备好接收数据");
                    // 2.接收服务器响应的数据
                    socket.receive(receiverPacket);
                    if (isExit) {
                        break;
                    }
                    // 检查来源
                    if (!receiverPacket.getAddress().equals(sendAddress)) {
                        LoggerUtils.e("收到未知来源消息");
                        continue;
                    }
                    // 3.读取数据
                    final String reply = new String(receiverData, 0, receiverPacket.getLength(), StandardCharsets.UTF_8);
                    LoggerUtils.i("服务器发来的信息是：" + reply);
                    Message receiveMsg;
                    try {
                        receiveMsg = JSONObject.toJavaObject(JSONObject.parseObject(reply), Message.class);
                    }catch (Exception e) {
                        LoggerUtils.e("{接收到的数据格式有误, 转换失败！info = [" + reply + "]}");
                        continue;
                    }
                    switch (receiveMsg.getMessageType()) {
                        case GAME_MESSAGE_TYPE:{
                            runOnUiThread(() -> textView.setText(String.valueOf(textView.getText()).concat(receiveMsg.getMsg() +"\n")));
                            break;
                        }
                        case SEND_KEY_TYPE:{
                            this.message.setConfirmNum(receiveMsg.getConfirmNum());
                            if (!confirmReceiptKey) {
                                confirmReceiptKey = true;
                                this.message.setKey(receiveMsg.getKey());
                                //弹起确认弹窗
                                runOnUiThread(() -> {
                                    showConfirmDialog();
                                    countDownTimer.start();
                                });
                            } else {
                                if (this.message.getConfirmNum() == 2 && !isGame) {
                                    isGame = true;
                                    //3.人数足够则进入游戏
                                    runOnUiThread(() -> {
                                        countDownTimer.cancel();
                                        if (dialog != null) {
                                            dialog.cancel();
                                        }
                                        button.setText("游戏中");
                                        button3.setEnabled(true);
                                        button2.setEnabled(true);
                                        button4.setEnabled(false);
                                        button.setEnabled(false);
                                        textView.setText(String.valueOf(textView.getText()).concat("进入游戏\n"));
                                    });
                                }
                            }
                            break;
                        }
                        default:LoggerUtils.e("未实现类型");
                    }
                }
            } catch (Exception e) {
                LoggerUtils.e(e.getMessage());
            }
        }));
        //发消息
        button2.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() ->{
            try {
                /*
                 * 向服务器端发送游戏数据
                 */
                // 1.定义服务器的地址、端口号、数据
                String s = editText.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    message.setMsg(s);
                } else {
                    message.setMsg("这是测试消息");
                }
                message.setMessageType(MessageTypeEnum.GAME_MESSAGE_TYPE);
                byte[] data = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
                // 2.创建数据报，包含发送的数据信息
                DatagramPacket packet = new DatagramPacket(data, data.length, sendAddress, sendPort);
                // 4.向服务器端发送数据报
                socket.send(packet);
                LoggerUtils.i("发送...");
            } catch (Exception e) {
                LoggerUtils.i(e.getMessage());
            }
        }));
        //退出游戏
        button3.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() -> {
            try {
                JSONObject post = OkHttpUtils.post("/match/remove", JSONObject.toJSONString(message));
                LoggerUtils.toJson(post.toJSONString());
                exitSocket();
                runOnUiThread(() -> {
                    Toast.makeText(context, "退出成功", Toast.LENGTH_SHORT).show();
                    textView.setText("退出成功");
                    button.setEnabled(true);
                    button2.setEnabled(false);
                    button3.setEnabled(false);
                    button.setText("开始匹配");
                    button4.setEnabled(false);
                });
                confirmReceiptKey = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        //退出队列
        button4.setOnClickListener(v -> ThreadPoolManager.getInstance().execute(() -> {
            try {
                JSONObject post = OkHttpUtils.post("/match/signOut", JSONObject.toJSONString(message));
                LoggerUtils.toJson(post.toJSONString());
                if (post.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS) {
                    exitSocket();
                    runOnUiThread(() -> {
                        Toast.makeText(context, "退出成功", Toast.LENGTH_SHORT).show();
                        button.setEnabled(true);
                        button2.setEnabled(false);
                        button3.setEnabled(false);
                        button.setText("开始匹配");
                        button4.setEnabled(false);
                    });
                } else {
                    Toast.makeText(context, "退出失败", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * 弹出确认进入游戏弹窗
     */
    private void showConfirmDialog() {
        dialog = DialogUtils.showDialogDefault(context);
        dialog.setCancelable(false);
        View inflate = View.inflate(context, R.layout.alert_dialog_select, null);
        dialog.setContentView(inflate);
        TextView title = inflate.findViewById(R.id.tv_alert_title);
        title.setText("匹配成功");
        content = inflate.findViewById(R.id.tv_alert_content);
        content.setText("当前确认人数: 0 人\n倒计时: 10 秒");
        Button alertOk = inflate.findViewById(R.id.btn_alert_ok);
        Button alertCancel = inflate.findViewById(R.id.btn_alert_cancel);
        alertOk.setText("确认");
        alertOk.setOnClickListener(v1 -> {
            //1.向服务器发TCP数据包确认收到
            Message message = new Message();
            message.setId(this.message.getId());
            alertOk.setTextColor(Color.GRAY);
            alertOk.setEnabled(false);
            alertCancel.setEnabled(false);
            //确定 发送http去确认
            ThreadPoolManager.getInstance().execute(() -> {
                try {
                    JSONObject post = OkHttpUtils.post("/match/confirm", JSONObject.toJSONString(message));
                    LoggerUtils.toJson(post.toJSONString());
                    confirmOk = true;
                } catch (Exception e) {
                    LoggerUtils.e(e.getMessage());
                }
            });
        });
        alertCancel.setText("拒绝");
        alertCancel.setOnClickListener(v12 -> {
            // 使其他键变黑 等待时间结束
            alertOk.setEnabled(false);
            alertCancel.setEnabled(false);
            confirmOk = false;
            alertOk.setTextColor(Color.GRAY);
        });
    }

    /**
     * 第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后10秒之后会回调onFinish方法
     */
    private CountDownTimer countDownTimer = new CountDownTimer(matchWaitTime, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            long second = millisUntilFinished / 1000;
            String s2 = "倒计时 " + second + " 秒";
            String s1 = "当前确认人数: " + message.getConfirmNum() + "人\n".concat(s2);
            content.setText(s1);
            LoggerUtils.i("执行了onTick");
        }

        @Override
        public void onFinish() {
            LoggerUtils.i("执行了onFinish");
            if (dialog != null) {
                dialog.cancel();
                //进入游戏失败 重置状态
                ThreadPoolManager.getInstance().execute(() -> {
                    //延迟重置状态 防止服务器重复消息
                    try {
                        Thread.sleep(1000);
                        confirmReceiptKey = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                //检查是否确认
                if (!confirmOk) {
                    exitSocket();
                    Toast.makeText(context, "退出成功", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    button2.setEnabled(false);
                    button3.setEnabled(false);
                    button.setText("开始匹配");
                    button4.setEnabled(false);
                } else {
                    confirmOk = false;
                }
            }
        }
    };

    /**
     * 退出队列
     */
    private synchronized void exitSocket() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            socket.disconnect();
            isExit = true;
            isGame = false;
        }
    }

    /*******************发送本机ip和端口到服务器***********************/
    private synchronized boolean reqSocket()  {
        boolean receivedResponse = false;
        message.setMessageType(MessageTypeEnum.MATCH_MESSAGE_TYPE);
        message.setMsg("npblock");
        int tries = 0;
        try (DatagramSocket socket = new DatagramSocket(receiverPort)){
            //1.构造数据包（加入游戏类型, 消息类型，请求端口和ip）
            byte[] bytes = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(bytes,
                    bytes.length, sendAddress, sendPort);
            // 设置阻塞时间
            socket.setSoTimeout(TIMEOUT);
            do {
                //3.从此套接字发送数据报包。
                socket.send(sendPacket);
                try {
                    socket.receive(receiverPacket);
                    // 检查来源
                    if (!receiverPacket.getAddress().equals(sendAddress)) {
                        throw new IOException("未知来源");
                    }
                    // 处理接收到的消息
                    final String reply = new String(receiverData, 0, receiverPacket.getLength(), StandardCharsets.UTF_8);
                    Message receiveMsg = JSONObject.toJavaObject(JSONObject.parseObject(reply), Message.class);
                    //减一秒防止提前结束弹窗
                    this.matchWaitTime = receiveMsg.getMatchWaitTime();
                    // 进入队列
                    runOnUiThread(() -> {
                        Toast.makeText(context, "加入成功", Toast.LENGTH_SHORT).show();
                        button.setText("队列中");
                        button.setEnabled(false);
                        button4.setEnabled(true);
                    });
                    receivedResponse = true;
                } catch (InterruptedIOException e) {
                    tries += 1;
                    LoggerUtils.i("Timed out, " + (MAXTRIES - tries) + " more tries...");
                }
                tries++;
            } while (!receivedResponse && tries < MAXTRIES);
        } catch (IOException e) {
            LoggerUtils.e(e.getMessage());
        }
        return receivedResponse;
    }

    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
