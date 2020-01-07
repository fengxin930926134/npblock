package com.np.block.core.manager;

import android.os.Handler;

import com.alibaba.fastjson.JSONObject;
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

public class SocketServerManager {

    /**最大重发次数5次*/
    private static final int MAX_TRIES = 5;
    /**设置超时为0.5秒*/
    private static final int TIMEOUT = 500;
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
    /**匹配成功等待时间*/
    private static long matchWaitTime;
    /**当前调用的activity中的handler*/
    private static Handler handler;
    /**是否停止接收消息*/
    private static boolean receiveStop = false;
    /**确认收到key 防止服务器多个同样消息*/
    private boolean confirmReceiptKey = false;
    private static DatagramSocket socket;
    private boolean isGame = false;
    /**提供一个共有的可以返回类对象的方法*/
    public static SocketServerManager getInstance(){
        return Inner.instance;
    }

    public static void setHandler(Handler handler) {
        SocketServerManager.handler = handler;
    }

    public synchronized void startSocketReceiverServer() {
        try {
            socket = new DatagramSocket(receiverPort);
            LoggerUtils.i("SocketReceiverServer启动完成...");
            while (true) {
                // 2.接收服务器响应的数据
                socket.receive(receiverPacket);
                if (receiveStop) {
                    break;
                }
                // 检查来源
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
                switch (receiveMsg.getMessageType()) {
                    case GAME_MESSAGE_TYPE:{
                        //发送游戏数据
                        android.os.Message msg = new android.os.Message();
                        msg.what = ConstUtils.HANDLER_GAME_DATA;
                        msg.obj = receiveMsg;
                        handler.sendMessage(msg);
                        break;
                    }
                    case SEND_KEY_TYPE:{
                        message.setConfirmNum(receiveMsg.getConfirmNum());
                        if (!confirmReceiptKey) {
                            confirmReceiptKey = true;
                            message.setKey(receiveMsg.getKey());
                            // 发送handler消息弹起确认弹窗

                        } else {
                            if (message.getConfirmNum() == 2 && !isGame) {
                                isGame = true;
                                //进入游戏
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
                    // 进入队列
                    receivedResponse = true;
                } catch (InterruptedIOException e) {
                    tries ++;
                    LoggerUtils.i("进入匹配队列超时, 倒数第" + (MAX_TRIES - tries) + "次尝试...");
                }
            } while (!receivedResponse && tries < MAX_TRIES);
        } catch (IOException e) {
            LoggerUtils.e(e.getMessage());
        }
        return receivedResponse;
    }

    /**
     * 退出游戏队列
     *
     * @return 是否成功
     */
    public synchronized boolean signOutMatchQueue() {
        try {
            JSONObject response = OkHttpUtils.post("/match/signOut", JSONObject.toJSONString(message));
            //打印回调
            if (response.getIntValue(ConstUtils.CODE) != ConstUtils.CODE_SUCCESS) {
                LoggerUtils.i(response.getString("msg"));
            }
            //停止接收服务器消息
            stopSocketReceive();
            //只要正常请求都返回true
            return true;
        } catch (Exception e) {
            LoggerUtils.e(e.getMessage());
            return false;
        }
    }

    /**
     * 停止Socket接收消息
     */
    private void stopSocketReceive() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            socket.disconnect();
            receiveStop = true;
        }
    }

    /**私有化内部类 第一次加载类时初始化ActivityManager*/
    private static class Inner {
        private static SocketServerManager instance = new SocketServerManager();
    }

    private SocketServerManager() {
        try {
            sendAddress = InetAddress.getByName(ConstUtils.HOST);
        } catch (IOException e) {
            LoggerUtils.i(e.getMessage());
        }
        message = new Message();
        message.setId(RandomUtils.getUUID());
        receiverData = new byte[2048];
        receiverPort = OkHttpUtils.getNotOccupyPort();
        receiverPacket = new DatagramPacket(receiverData, receiverData.length);
    }
}
