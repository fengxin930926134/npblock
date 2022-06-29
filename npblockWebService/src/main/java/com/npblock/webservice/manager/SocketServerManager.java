package com.npblock.webservice.manager;

import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.Message;
import com.npblock.webservice.entity.RoomItem;
import com.npblock.webservice.entity.Users;
import com.npblock.webservice.entity.enums.GameOverTypeEnum;
import com.npblock.webservice.entity.enums.MessageTypeEnum;
import com.npblock.webservice.util.ConstUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * UDP socket服务管理类
 *
 * @author fengxin
 */
public class SocketServerManager {
    /**重发送key的延迟时间*/
    private static final long DELAY_TIME = 1000;
    /**最大重发送次数*/
    private static final int MAX_SEND = 11;
    /**设置缓冲区大小 10M*/
    private static final int BUF_SIZE = 10 * 1024 * 1024;
    /**接收服务器数据数组大小*/
    private static final int RECEIVER_SIZE = 10 * 1024;
    /**队列的用户 id 用户信息*/
    private static final Map<String, Message> matchQueue = new Hashtable<>();
    /**游戏房间列表 房间id 用户信息Set*/
    private static final Map<Integer, Set<Message>> roomMap = new Hashtable<>();
    /**游戏房间map 方便查询*/
    private static final Map<Integer, RoomItem> roomList = new Hashtable<>();
    /**游戏房间准备人数*/
    private static final Map<Integer, Integer> prepareMap = new Hashtable<>();
    /**游戏队列 key, 队伍信息*/
    private static final Map<String, Set<Message>> gameQueue = new Hashtable<>();
    /**游戏队列 id, confirm*/
    private static final Map<String, Boolean> confirmMap = new Hashtable<>();
    /**socket对象*/
    private static DatagramSocket socket;
    /**接收数据*/
    private static final byte[] receiveData = new byte[RECEIVER_SIZE];
    /**接收数据包，装数据*/
    private static final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    /**房间号*/
    private static Integer roomNum = 1;
    private final Logger logger;

    /**
     * 获得唯一单例
     *
     * @return instance
     */
    public static SocketServerManager getInstance() {
        return SocketServerManager.LazyHolder.INSTANCE;
    }

    /**
     * 打印当前socket状态日志
     */
    public void log() {
        logger.info("【匹配队列】 当前人数 --> " + matchQueue.size());
        logger.info("【游戏队列】 当前人数 --> " + gameQueue.size());
        logger.info("【roomMap】 当前数量 --> " + roomMap.size());
        logger.info("【roomList】 当前数量 --> " + roomList.size());
        logger.info("【prepareMap】 当前数量 --> " + prepareMap.size());
    }

    /**
     * 改变准备状态
     * @param roomNum 房间号
     * @param prepare 准备状态
     * @param userId userId
     */
    public void prepare(int roomNum, boolean prepare, Integer userId) {
        if (roomMap.containsKey(roomNum) && prepareMap.containsKey(roomNum)) {
            Integer num = prepareMap.get(roomNum);
            Set<Message> messages = roomMap.get(roomNum);
            if (prepare) {
                if (num == 1) {
                    //开始游戏
                    for (Message message: messages) {
                        Message sendMessage = new Message();
                        sendMessage.setKey(roomNum + "room");
                        sendMessage.setMessageType(MessageTypeEnum.START_CUSTOMIZATION_TYPE);
                        sendMessage(socket, sendMessage, message.getAddress());
                    }
                    //加入游戏队列
                    try {
                        addGameQueue(messages, roomNum + "room");
                        removeRoom(roomNum);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                } else {
                    prepareMap.put(roomNum, 1);
                    //发送消息通知已准备
                    for (Message message: messages) {
                        if (!message.getId().equals(String.valueOf(userId))) {
                            Message sendMessage = new Message();
                            sendMessage.setMessageType(MessageTypeEnum.PREPARE_TYPE);
                            sendMessage(socket, sendMessage, message.getAddress());
                        }
                    }
                }
            } else {
                prepareMap.put(roomNum, 0);
                //发送消息通知已取消
                for (Message message: messages) {
                    if (!message.getId().equals(String.valueOf(userId))) {
                        Message sendMessage = new Message();
                        sendMessage.setMessageType(MessageTypeEnum.CANCEL_TYPE);
                        sendMessage(socket, sendMessage, message.getAddress());
                    }
                }
            }
        }
    }

    /**
     * 退出房间
     * @param roomNum 房间号
     * @param userId 用户id
     */
    public void exitRoom(int roomNum, int userId) {
        if (roomMap.containsKey(roomNum) && prepareMap.containsKey(roomNum)) {
            Set<Message> messages = roomMap.get(roomNum);
            Message remove = null;
            for (Message message: messages) {
                if (message != null && !message.getId().equals(String.valueOf(userId))) {
                    //发送消息通知此人 另外一人退出房间
                    Message sendMessage = new Message();
                    sendMessage.setMessageType(MessageTypeEnum.EXIT_ROOM_TYPE);
                    sendMessage(socket, sendMessage, message.getAddress());
                } else {
                    remove = message;
                }
            }
            messages.remove(remove);
            if (roomMap.get(roomNum).size() == 0) {
                removeRoom(roomNum);
            }
        }
    }

    /**
     * 判断用户是否已经加入匹配队列
     *
     * @param id 用户id
     * @return boolean
     */
    public boolean containsMatchId(String id) {
        return matchQueue.containsKey(id);
    }

    /**
     * 判断用户是否已经确认
     *
     * @param id 用户id
     * @return boolean
     */
    public boolean containsConfirm(String id) {
        return confirmMap.containsKey(id);
    }

    /**
     * 根据游戏key获取用户信息list
     * @param key key
     * @return list
     */
    public List<Integer> getUserIdListByKey(String key) {
        List<Integer> ids = new ArrayList<>();
        Set<Message> messages = gameQueue.get(key);
        if (messages != null) {
            for (Message message :messages) {
                ids.add(Integer.parseInt(message.getId()));
            }
            return ids;
        }
        return new ArrayList<>();
    }

    /**
     * 用户确认进入游戏
     *
     * @param id 用户id
     */
    public void confirm(String id) {
        if (containsConfirm(id)) {
            confirmMap.put(id, true);
        }
    }

    /**
     * 加入匹配队列
     *
     * @param message 加入队列的成员信息
     */
    private void addMatchQueue(Message message) {
        if (!containsMatchId(message.getId())) {
            matchQueue.put(message.getId(), message);
        }
        SocketServerManager.getInstance().log();
    }

    /**
     * 退出匹配队列
     *
     * @param id 加入队列的用户id
     */
    public synchronized void removeMatchQueue(String id) {
        if (containsMatchId(id)) {
            matchQueue.remove(id);
        }
    }

    /**
     * 启动游戏Socket服务 接收客户端发送的数据
     */
    public void startGameSocketServer() {
        logger.info("********** UDPSocket游戏服务已启动 *********");
        while (true) {
            try {
                // 1.接收消息
                socket.receive(receivePacket);
                // 2.分析消息(获取端口ip, 消息类型)
                String info = new String(receiveData, 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                Message receiveMsg;
                try {
                    receiveMsg = JSONObject.toJavaObject(JSONObject.parseObject(info), Message.class);
                }catch (Exception e) {
                    logger.error("{接收到的数据格式有误, 转换失败！info = [" + info + "]}");
                    continue;
                }
                // 3.处理数据 响应客户端
                handle(receiveMsg, receivePacket.getSocketAddress());
            } catch (IOException e) {
                logger.error("{socket出错} 错误信息 = " + e.getMessage());
            }
        }
    }

    /**
     * 判断游戏队列是否存在队列key
     *
     * @param key 队列唯一标识
     * @return boolean
     */
    public boolean containsGameKey(String key) {
        return gameQueue.containsKey(key);
    }

    /**
     * 退出游戏队列
     *
     * @param key key
     */
    private synchronized void removeGameKey(String key) {
        if (containsGameKey(key)) {
            Set<Message> remove = gameQueue.remove(key);
            //检查确认map是否移除
            for (Message message:
                 remove) {
                if (containsConfirm(message.getId())) {
                    confirmMap.remove(key);
                }
            }
        }
    }

    /**
     * 处理客户端发送数据, 并根据类型响应
     *
     * @param message message
     * @param socketAddress socketAddress
     * @throws IOException IOException
     */
    private void handle(Message message, SocketAddress socketAddress) throws IOException {
        switch (message.getMessageType()) {
            //游戏消息
            case GAME_MESSAGE_TYPE:{
                // 1.获取key
                String key = message.getKey();
                // 2.验证key是否有效
                if (containsGameKey(key)) {
                    // 3.获取游戏信息
                    String msg = message.getMsg();
                    // 3.5.打包数据
                    Message sendMessage = new Message();
                    sendMessage.setMessageType(MessageTypeEnum.GAME_MESSAGE_TYPE);
                    sendMessage.setMsg(msg);
                    byte[] bytes = JSONObject.toJSONString(sendMessage).getBytes(StandardCharsets.UTF_8);
                    // 4.发送游戏信息
                    Set<Message> messages = gameQueue.get(key);
                    for (Message userMessage : messages) {
                        // 检查是否本人
                        if (message.getId().equals(userMessage.getId())){
                            continue;
                        }
                        // 创建数据报，包含响应的数据信息
                        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, userMessage.getAddress());
                        // 响应客户端
                        socket.send(packet);
                    }
                }
                break;
            }
            case MATCH_MESSAGE_TYPE:{
                // 1.验证msg消息是否正确 暂时瞎几把写
                if (!message.getMsg().equals("npblock")) {
                    throw new IOException("信息非法");
                }
                // 2.保存进入匹配队列的对象
                message.setAddress(socketAddress);
                if (!containsMatchId(message.getId())) {
                    addMatchQueue(message);
                }
                // 3.打包数据
                Message sendMessage = new Message();
                sendMessage.setMessageType(MessageTypeEnum.ADD_MATCH_SUCCESS_TYPE);
                sendMessage.setMsg("Receive message");
                sendMessage.setMatchWaitTime(MAX_SEND * DELAY_TIME);
                // 4.响应客户端
                sendMessage(socket, sendMessage, socketAddress);
                break;
            }
            case GAME_WIN_MESSAGE_TYPE:{
                //判断胜利方
                //暂时直接判断
                // 1.获取key
                String key = message.getKey();
                // 2.验证key是否有效
                if (containsGameKey(key)) {
                    // 3.打包数据
                    Message sendMessage = new Message();
                    sendMessage.setMessageType(MessageTypeEnum.GAME_WIN_MESSAGE_TYPE);
                    // 4.发送游戏信息
                    Set<Message> messages = gameQueue.get(key);
                    for (Message userMessage : messages) {
                        // 检查是否本人
                        if (message.getId().equals(userMessage.getId())){
                            sendMessage.setMsg(GameOverTypeEnum.GAME_OVER_WIN_TYPE.getCode());
                        } else {
                            sendMessage.setMsg(GameOverTypeEnum.GAME_OVER_LOSE_TYPE.getCode());
                        }
                        sendMessage(socket, sendMessage, socketAddress);
                    }
                    //将key移除游戏队列
                    removeGameKey(key);
                }
                break;
            }
            case GAME_DEFEAT_MESSAGE_TYPE:{
                // 1.获取key
                String key = message.getKey();
                // 2.验证key是否有效
                if (containsGameKey(key)) {
                    // 3.打包数据
                    Message sendMessage = new Message();
                    sendMessage.setMessageType(MessageTypeEnum.GAME_WIN_MESSAGE_TYPE);
                    // 4.发送游戏信息
                    Set<Message> messages = gameQueue.get(key);
                    for (Message userMessage : messages) {
                        // 检查是否本人
                        if (message.getId().equals(userMessage.getId())){
                            sendMessage.setMsg(GameOverTypeEnum.GAME_OVER_LOSE_TYPE.getCode());
                        } else {
                            sendMessage.setMsg(GameOverTypeEnum.GAME_OVER_WIN_TYPE.getCode());
                        }
                        sendMessage(socket, sendMessage, userMessage.getAddress());
                    }
                    //将key移除游戏队列
                    removeGameKey(key);
                }
                break;
            }
            //创建房间
            case CREATE_ROOM_TYPE: {
                //创建
                while (roomMap.containsKey(roomNum)) {
                    roomNum ++;
                }
                Set<Message> room = new HashSet<>();
                message.setAddress(socketAddress);
                room.add(message);
                //保存
                roomMap.put(roomNum, room);
                prepareMap.put(roomNum, 0);
                JSONObject jsonObject = JSONObject.parseObject(message.getMsg());
                RoomItem roomMsg = jsonObject.getObject("roomMsg", RoomItem.class);
                roomMsg.setRoomId(roomNum);
                roomList.put(roomNum, roomMsg);
                //返回房间号
                Message send = new Message();
                send.setMessageType(MessageTypeEnum.SEND_KEY_TYPE);
                send.setKey(roomNum.toString());
                sendMessage(socket, send, socketAddress);
                log();
                break;
            }
            case JOIN_ROOM_TYPE: {
                JSONObject jsonObject = JSONObject.parseObject(message.getMsg());
                if (jsonObject != null) {
                    Integer roomNum = jsonObject.getInteger("roomNum");
                    //自己的信息
                    Users userMsg = jsonObject.getObject("userMsg", Users.class);
                    if (roomMap.containsKey(roomNum)) {
                        Set<Message> messages = roomMap.get(roomNum);
                        //获取创建房间的用户信息
                        for (Message userMessage: messages) {
                            if (userMessage != null && !userMessage.getId().equals(String.valueOf(userMsg.getId()))) {
                                //将此人的信息发给加入房间的人
                                Message sendMessage = new Message();
                                Users thisMsg = JSONObject.parseObject(userMessage.getMsg())
                                        .getObject("userMsg", Users.class);
                                sendMessage.setMsg(JSONObject.toJSONString(thisMsg));
                                sendMessage(socket, sendMessage, socketAddress);
                                //将自己的信息发给此人
                                Message sendMessage1 = new Message();
                                sendMessage1.setMessageType(MessageTypeEnum.JOIN_ROOM_TYPE);
                                sendMessage1.setMsg(JSONObject.toJSONString(userMsg));
                                sendMessage(socket, sendMessage1, userMessage.getAddress());
                            }
                        }
                        //保存自己信息
                        message.setAddress(socketAddress);
                        messages.add(message);
                    }
                }
                break;
            }
            default: {
                logger.error("收到未知类型消息");
            }
        }
    }

    /**
     * 匹配是否有适合队伍则开始一个游戏队列
     */
    public void existRightnessTeamBeginGame() {
        if (matchQueue.size() < 2) {
            return;
        }
        Set<Message> wait = new HashSet<>(matchQueue.values());
        Set<Message> success = new HashSet<>();
        Set<Message> defeated = new HashSet<>();
        // 1. 从匹配队列中拿出来
        for (Message queueMessage : wait) {
            removeMatchQueue(queueMessage.getId());
        }
        // 2. 匹配计算
        if (wait.size() >= 2) {
            //暂时瞎几把匹配一波 匹配成功！！返回信息 取两个人
            int i = 0;
            for (Message message : wait) {
                if (i >= 2) {
                    defeated.add(message);
                    continue;
                }
                String id = message.getId();
                success.add(message);
                confirmMap.put(id, false);
                i ++;
            }
            //多的还回去
            for (Message message : defeated) {
                addMatchQueue(message);
            }
            // 3. 成功,发送KEY
            if (success.size() == 2) {
                int confirmNum;
                int sendNum = 0;
                // 1.生成唯一标识key
                String key = UUID.randomUUID().toString();
                try {
                    logger.info("[匹配成功] 发送key");
                    // 2.生成send数据
                    Message sendMessage = new Message();
                    sendMessage.setKey(key);
                    sendMessage.setMsg("npblock");
                    sendMessage.setMessageType(MessageTypeEnum.SEND_KEY_TYPE);
                    do {
                        //获取当前确认人数
                        confirmNum = getConfirmNum(success);
                        sendMessage.setConfirmNum(confirmNum);
                        byte[] data = JSONObject.toJSONString(sendMessage).getBytes(StandardCharsets.UTF_8);
                        // 3.循环发送给每人
                        for (Message message : success) {
                            // 创建数据报，包含响应的数据信息
                            DatagramPacket packet = new DatagramPacket(data, data.length, message.getAddress());
                            // 响应客户端
                            socket.send(packet);
                        }
                        sendNum ++;
                        Thread.sleep(DELAY_TIME);
                    } while (confirmNum != success.size() && MAX_SEND > sendNum);
                    // 发送超时 存在用户未点确认
                    if (sendNum >= MAX_SEND) {
                        // 将确认用户重新加入队列
                        for (Message message : success) {
                            String id = message.getId();
                            if (confirmMap.get(id)) {
                                //重新加入队列
                                addMatchQueue(message);
                            }
                            // 移除确认状态 无论是否确认
                            confirmMap.remove(id);
                        }
                        throw new Exception("存在用户未确认，结束配对，已确认用户重新加入队列");
                    }
                    // 5.加入游戏队列
                    addGameQueue(success, key);
                } catch (Exception e) {
                    logger.info("[发送key失败] " + e.getMessage());
                }
            }
        } else {
            //匹配不成功 将其重新加入匹配队列
            for (Message message : wait) {
                addMatchQueue(message);
            }
        }
    }

    /**
     * 发送游戏结束的消息
     */
    public void gameOver(Message message) {
        // 打包数据
        Message sendMessage = new Message();
        sendMessage.setMessageType(MessageTypeEnum.GAME_WIN_MESSAGE_TYPE);
        sendMessage.setMsg(GameOverTypeEnum.GAME_OVER_ESCAPE_TYPE.getCode());
        // 发送信息
        Set<Message> messages = gameQueue.get(message.getKey());
        for (Message userMessage : messages) {
            // 检查是否本人
            if (!message.getId().equals(userMessage.getId())) {
                byte[] bytes = JSONObject.toJSONString(sendMessage).getBytes(StandardCharsets.UTF_8);
                // 创建数据报，包含响应的数据信息
                final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, userMessage.getAddress());
                // 响应客户端
                ThreadPoolManager.getInstance().execute(() -> {
                    try {
                        //循环发送多次防止未收到
                        for (int i = 0; i < 4; i++) {
                            socket.send(packet);
                            Thread.sleep(300);
                        }
                    } catch (IOException | InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                });
            }
        }
        //退出游戏队列
        removeGameKey(message.getKey());
    }

    /**
     * 发送单个消息
     * @param socket socket
     * @param message message
     * @param socketAddress socketAddress
     */
    private void sendMessage(DatagramSocket socket, Message message, SocketAddress socketAddress) {
        try {
            // 1.创建数据
            byte[] data = JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8);
            // 2.创建数据包，包含响应的数据信息
            DatagramPacket packet = new DatagramPacket(data, data.length, socketAddress);
            // 3.发送数据包
            socket.send(packet);
        } catch (IOException e) {
            logger.error("sendMessage:" + e.getMessage() + "\nMessage:" + message);
        }
    }

    /**
     * 获取匹配成功的队伍确认人数
     *
     * @param messages 匹配成功队伍
     * @return int
     */
    private int getConfirmNum(Set<Message> messages) {
        int result = 0;
        for (Message message : messages) {
            String id = message.getId();
            if (containsConfirm(id)) {
                if (confirmMap.get(id)) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * 加入游戏队列
     *
     * @param queues 队列信息
     * @param key key
     */
    private void addGameQueue(Set<Message> queues, String key) throws Exception {
        // 加入游戏队列
        if (!containsGameKey(key)) {
            gameQueue.put(key, queues);
        } else {
            throw new Exception("游戏key已经进入游戏队列。");
            // 后续处理
        }
        log();
    }

    /**
     * 移除指定房间号的房间
     * @param roomNum roomNum
     */
    private void removeRoom(int roomNum) {
        roomMap.remove(roomNum, roomMap.get(roomNum));
        roomList.remove(roomNum, roomList.get(roomNum));
        prepareMap.remove(roomNum, prepareMap.get(roomNum));
        SocketServerManager.roomNum--;
        log();
    }

    /**
     * 获取房间列表
     * @return list
     */
    public List<RoomItem> getRoomList() {
        return new ArrayList<>(roomList.values());
    }

    /**
     * 私有内部类单例
     */
    private static class LazyHolder {
        private static final SocketServerManager INSTANCE = new SocketServerManager();
    }

    /**
     * 私有化构造方法
     */
    private SocketServerManager() {
        logger = LogManager.getLogger(SocketServerManager.class);
        try {
            socket = new DatagramSocket(ConstUtils.SOCKET_PORT);
            //设置缓冲区大小
            socket.setSendBufferSize(BUF_SIZE);
            socket.setReceiveBufferSize(BUF_SIZE);
        } catch (SocketException e) {
            logger.info(e.getMessage());
        }
    }
}
