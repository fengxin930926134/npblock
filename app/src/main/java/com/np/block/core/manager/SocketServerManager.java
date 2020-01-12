package com.np.block.core.manager;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.np.block.core.enums.GameOverTypeEnum;
import com.np.block.core.enums.GameTypeEnum;
import com.np.block.core.enums.MessageTypeEnum;
import com.np.block.core.model.Message;
import com.np.block.util.ConstUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;
import com.np.block.util.RandomUtils;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * 对战模式Socket服务管理类
 * @author fengxin
 */
public class SocketServerManager {
    /**最大重发次数5次*/
    private static final int MAX_TRIES = 5;
    /**设置超时为0.5秒*/
    private static final int TIMEOUT = 500;
    /**设置缓冲区大小*/
    private static final int BUF_SIZE = 5 * 1024 * 1024;
    /**接收服务器数据数组大小*/
    private static final int RECEIVER_SIZE = 10 * 1024;
    /**消息体*/
    private static Message message;
    /**接收服务器消息端口*/
    private static int receiverPort;
    /**服务器主机*/
    private static InetAddress sendAddress;
    /**接收服务器数据*/
    private static byte[] receiverData;
    /**接收服务器数据包*/
    private static DatagramPacket receiverPacket;
    /**当前调用的activity中的handler*/
    private static Handler handler;
    /**是否停止接收消息*/
    private static boolean receiveStop = false;
    /**接收Socket*/
    private static DatagramSocket socket;
    /**发送Socket*/
    private static DatagramSocket sendSocket;
    /**判断是否进入游戏*/
    private boolean enterGame = false;
    /**确认收到key 防止服务器多个同样消息*/
    private boolean confirmReceiptKey = false;
    /**匹配等待时间*/
    private static long matchWaitTime = 11 * 1000;
    /**
     * 第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后10秒之后会回调onFinish方法
     */
    private CountDownTimer countDownTimer = new CountDownTimer(matchWaitTime, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            long second = millisUntilFinished / 1000 - 1;
            String s2 = "倒计时 " + second + " 秒";
            String s1 = "当前确认人数: " + message.getConfirmNum() + "人\n".concat(s2);
            //发送s1
            android.os.Message msg = new android.os.Message();
            msg.what = ConstUtils.HANDLER_TIME_STRING;
            msg.obj = s1;
            handler.sendMessage(msg);
        }

        @Override
        public void onFinish() {
            //发送有人未确认的消息
            handler.sendEmptyMessage(ConstUtils.HANDLER_MATCH_FAILURE);
            ThreadPoolManager.getInstance().execute(() -> {
                //延迟重置状态 防止服务器重复消息
                try {
                    Thread.sleep(1000);
                    resetState();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    };

    /**私有化内部类 第一次加载类时初始化SocketServerManager*/
    private static class Inner {
        private static SocketServerManager instance = new SocketServerManager();
    }

    /**提供一个共有的可以返回类对象的方法*/
    public static SocketServerManager getInstance(){
        return Inner.instance;
    }

    /**
     * 设置handler
     * @param handler handler
     */
    public void setHandler(Handler handler) {
        SocketServerManager.handler = handler;
    }

    /**
     * 向服务器发送游戏消息
     * @param msg msg
     */
    public void sendGameMessage(String msg) {
        ThreadPoolManager.getInstance().execute(() -> {
            if (!TextUtils.isEmpty(msg)) {
                message.setMsg(msg);
            } else {
               return;
            }
            message.setMessageType(MessageTypeEnum.GAME_MESSAGE_TYPE);
            byte[] data = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, sendAddress, ConstUtils.SOCKET_SEND_PORT);
            try {
                //向服务器端发送数据报
                sendSocket.send(packet);
            } catch (Exception e) {
                LoggerUtils.e("sendGameMessage:" + e.getMessage());
            }
        });
    }

    /**
     * 向服务器发送游戏胜利消息
     * @param time 胜利time
     */
    public void sendGameWinMessage(long time) {
        ThreadPoolManager.getInstance().execute(() -> {
            message.setMessageType(MessageTypeEnum.GAME_WIN_MESSAGE_TYPE);
            message.setWinTime(time);
            byte[] data = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, sendAddress, ConstUtils.SOCKET_SEND_PORT);
            try {
                // 向服务器端发送数据报
                sendSocket.send(packet);
            } catch (Exception e) {
                LoggerUtils.e("sendGameWinMessage:" + e.getMessage());
            }
        });
    }

    /**
     * 向服务器发送游戏失败消息
     */
    public void sendGameOverMessage() {
        ThreadPoolManager.getInstance().execute(() -> {
            message.setMessageType(MessageTypeEnum.GAME_DEFEAT_MESSAGE_TYPE);
            byte[] data = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
            try {
                DatagramPacket packet = new DatagramPacket(data, data.length, sendAddress, ConstUtils.SOCKET_SEND_PORT);
                // 向服务器端发送数据报
                sendSocket.send(packet);
            } catch (Exception e) {
                LoggerUtils.e("sendGameOverMessage:" + e.getMessage());
            }
        });
    }

    /**
     * 进入匹配队列
     * 发送本机ip和端口到服务器
     *
     * @param gameTypeEnum 游戏类型
     * @return 是否请求成功
     */
    public synchronized boolean enterMatchQueue(GameTypeEnum gameTypeEnum)  {
        boolean receivedResponse = false;
        //设置消息类型
        message.setMessageType(MessageTypeEnum.MATCH_MESSAGE_TYPE);
        //设置进入的队列类型
        message.setGameType(gameTypeEnum.getCode());
        message.setMsg("npblock");
        int tries = 0;
        try (DatagramSocket socket = new DatagramSocket(receiverPort)){
            //1.构造数据包（加入游戏类型, 消息类型，请求端口和ip）
            byte[] bytes = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(bytes,
                    bytes.length, sendAddress, ConstUtils.SOCKET_SEND_PORT);
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
                    // 获取匹配等待时间
                    matchWaitTime = receiveMsg.getMatchWaitTime();
                    // not stop
                    receiveStop = false;
                    receivedResponse = true;
                } catch (InterruptedIOException e) {
                    tries ++;
                    LoggerUtils.i("进入匹配队列超时, 倒数第" + (MAX_TRIES - tries) + "次尝试...");
                }
            } while (!receivedResponse && tries < MAX_TRIES);
        } catch (IOException e) {
            LoggerUtils.e(e.getMessage());
        }
        if (receivedResponse) {
            // 进入队列 开启接收服务器消息
            ThreadPoolManager.getInstance().execute(this::startSocketReceiverServer);
        }
        return receivedResponse;
    }

    /**
     * 匹配确认
     * 确认进入游戏
     *
     * @return 确认是否成功
     */
    public synchronized boolean confirmMatch() {
        try {
            JSONObject post = OkHttpUtils.post("/match/confirm", JSONObject.toJSONString(message));
            LoggerUtils.toJson(post.toJSONString());
            return true;
        } catch (Exception e) {
            LoggerUtils.e(e.getMessage());
            return false;
        }
    }

    /**
     * 退出匹配队列
     *
     * @return 是否成功
     */
    public synchronized boolean signOutMatchQueue() {
        try {
            JSONObject response = OkHttpUtils.post("/match/signOut", JSONObject.toJSONString(message));
            if (response.getIntValue(ConstUtils.CODE) != ConstUtils.CODE_SUCCESS) {
                //打印回调
                LoggerUtils.i(response.getString("msg"));
            }
            //停止接收服务器消息
            stopSocketServer();
            // 重置状态
            resetState();
            //只要正常请求都返回true
            return true;
        } catch (Exception e) {
            LoggerUtils.e(e.getMessage());
            return false;
        }
    }

    /**
     * 游戏结束
     */
    public synchronized void gameOver() {
        try {
            JSONObject response = OkHttpUtils.post("/match/remove", JSONObject.toJSONString(message));
            if (response.getIntValue(ConstUtils.CODE) != ConstUtils.CODE_SUCCESS) {
                //打印回调
                LoggerUtils.i(response.getString("msg"));
            }
            //停止接收服务器消息
            stopSocketServer();
            // 重置状态
            resetState();
        } catch (Exception e) {
            LoggerUtils.e(e.getMessage());
        }
    }

    /**
     * 重置状态
     */
    private void resetState() {
        //确认接收key
        confirmReceiptKey = false;
        //是否进入游戏
        enterGame = false;
    }

    /**
     * 开启接收服务器匹配消息的SocketServer
     */
    private void startSocketReceiverServer() {
        try {
            socket = new DatagramSocket(receiverPort);
            sendSocket = new DatagramSocket();
            socket.setSendBufferSize(BUF_SIZE);
            socket.setReceiveBufferSize(BUF_SIZE);
            LoggerUtils.i("SocketReceiverServer启动完成...");
            while (true) {
                // 1.接收服务器响应的数据
                socket.receive(receiverPacket);
                if (receiveStop) {
                    break;
                }
                // 2.检查来源
                if (!receiverPacket.getAddress().equals(sendAddress)) {
                    LoggerUtils.e("未知来源消息，已跳过");
                    continue;
                }
                // 3.读取数据
                final String reply = new String(receiverData, 0, receiverPacket.getLength(), StandardCharsets.UTF_8);
                Message receiveMsg;
                try {
                    receiveMsg = JSONObject.toJavaObject(JSONObject.parseObject(reply), Message.class);
                }catch (Exception e) {
                    LoggerUtils.e("{接收到的数据格式有误, 转换失败！info = [" + reply + "]}");
                    continue;
                }
                // 4.处理数据
                switch (receiveMsg.getMessageType()) {
                    case GAME_MESSAGE_TYPE:{
                        //发送游戏数据
                        android.os.Message msg = new android.os.Message();
                        msg.what = ConstUtils.HANDLER_GAME_DATA;
                        msg.obj = receiveMsg.getMsg();
                        handler.sendMessage(msg);
                        break;
                    }
                    case SEND_KEY_TYPE:{
                        message.setConfirmNum(receiveMsg.getConfirmNum());
                        if (!confirmReceiptKey) {
                            confirmReceiptKey = true;
                            message.setKey(receiveMsg.getKey());
                            // 发送handler消息弹起确认弹窗
                            android.os.Message msg = new android.os.Message();
                            msg.what = ConstUtils.HANDLER_MATCH_SUCCESS;
                            msg.obj = matchWaitTime;
                            handler.sendMessage(msg);
                            countDownTimer.start();
                        } else {
                            if (message.getConfirmNum() == 2 && !enterGame) {
                                enterGame = true;
                                //进入游戏
                                countDownTimer.cancel();
                                android.os.Message msg = new android.os.Message();
                                msg.what = ConstUtils.HANDLER_ENTER_THE_GAME;
                                msg.obj = "当前确认人数: " + message.getConfirmNum() + "人\n即将进入游戏";
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case GAME_WIN_MESSAGE_TYPE:{
                        //检查是否发送过
                        if (enterGame) {
                            //停止接收服务器消息
                            stopSocketServer();
                            // 重置状态
                            resetState();
                            //发送游戏结束消息
                            switch (GameOverTypeEnum.getEnumByCode(receiveMsg.getMsg())){
                                case GAME_OVER_WIN_TYPE:{
                                    handler.sendEmptyMessage(ConstUtils.HANDLER_GAME_WIN);
                                    break;
                                }
                                case GAME_OVER_LOSE_TYPE:{
                                    handler.sendEmptyMessage(ConstUtils.HANDLER_LOSE_GAME);
                                    break;
                                }
                                case GAME_OVER_ESCAPE_TYPE:{
                                    handler.sendEmptyMessage(ConstUtils.HANDLER_ESCAPE_GAME);
                                    break;
                                }
                                default: LoggerUtils.e("未知游戏结束类型");
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
    }

    /**
     * 停止Socket服务
     */
    private void stopSocketServer() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            sendSocket.close();
            socket.disconnect();
            receiveStop = true;
        }
    }

    private SocketServerManager() {
        try {
            sendAddress = InetAddress.getByName(ConstUtils.HOST);
        } catch (IOException e) {
            LoggerUtils.i(e.getMessage());
        }
        message = new Message();
        message.setId(RandomUtils.getUUID());
        receiverData = new byte[RECEIVER_SIZE];
        receiverPort = OkHttpUtils.getNotOccupyPort();
        receiverPacket = new DatagramPacket(receiverData, receiverData.length);
    }
}
