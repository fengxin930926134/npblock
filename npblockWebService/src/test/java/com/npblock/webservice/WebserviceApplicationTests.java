package com.npblock.webservice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.npblock.webservice.entity.Users;
import com.npblock.webservice.repository.RankRecordRepository;
import com.npblock.webservice.repository.UsersRepository;
import com.npblock.webservice.service.UsersService;
import com.npblock.webservice.util.ConstUtils;
import com.npblock.webservice.util.HttpUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WebserviceApplicationTests {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersService usersService;

    @Autowired
    RankRecordRepository rankRecordRepository;

    boolean flag = true;
    int cnt = 10;

    private int port = 65535;

    @Test
    @Transactional
    public void contextLoads3() throws Exception {
        // 进入队列排队
//        QueueMessage queueMessage = new QueueMessage();
//        queueMessage.setIp("127.0.0.1");
//        queueMessage.setPort(50878);
//        SocketServer.getInstance().addQueue(queueMessage);

        /*
         * 向服务器端发送数据
         */
        // 1.定义服务器的地址、端口号、数据
//        InetAddress address = InetAddress.getByName("localhost");
//        // 3.创建DatagramSocket对象
//        DatagramSocket socket = new DatagramSocket(50878, address);
//
//        String next = "{'test':'这是50878端口用户发的消息'}";
//        byte[] data = next.getBytes();
//        // 2.创建数据报，包含发送的数据信息
//        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//        // 4.向服务器端发送数据报
//        socket.send(packet);
//
//        /*
//         * 接收服务器端响应的数据
//         */
//        while (true) {
//            // 1.创建数据报，用于接收服务器端响应的数据
//            byte[] data2 = new byte[1024];
//
//            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
//            // 2.接收服务器响应的数据
//            socket.receive(packet2);
//            // 3.读取数据
//            String reply = new String(data2, 0, packet2.getLength());
//            System.out.println("服务器说：" + reply);
//        }

        System.out.println(rankRecordRepository.getOne(1));
    }

    @Test
    public void contextLoads() throws IOException {
//        System.out.println(3 * 24 * 60 * 60 * 1000);
//        int a = usersService.updateClassicScoreByToken("d83305c2-489b-4977-bba5-c53f53a27bd3", 12414);
//        List<Users> classicScoreRank = usersService.getClassicScoreRank();
//        System.out.println(classicScoreRank);


//        Users users = new Users();
//        users.setName("哈哈哈");
//        users.setPhone("124124");
//        users.setPassword("124124124");
//        Users register = usersService.register(users);
//        System.out.println(register);
        // 进入队列排队
//        QueueMessage queueMessage = new QueueMessage();
//        queueMessage.setIp("127.0.0.1");
//        queueMessage.setPort(50877);
//        SocketServer.getInstance().addQueue(queueMessage);

        /*
         * 向服务器端发送数据
         */
        // 1.定义服务器的地址、端口号、数据
        InetAddress address = InetAddress.getByName("localhost");
        // 3.创建DatagramSocket对象
        DatagramSocket socket = new DatagramSocket(50877, address);

        String next = "{'test':'这是50877端口用户发的消息'}";
        byte[] data = next.getBytes();
        // 2.创建数据报，包含发送的数据信息
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        // 4.向服务器端发送数据报
        socket.send(packet);

        /*
         * 接收服务器端响应的数据
         */
        // 1.创建数据报，用于接收服务器端响应的数据
        byte[] data2 = new byte[1024];

        DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
        while (true) {
            // 2.接收服务器响应的数据
            socket.receive(packet2);
            // 3.读取数据
            String reply = new String(data2, 0, packet2.getLength());
            System.out.println("服务器说：" + reply);
        }
    }

    @Test
    public void contextLoads2() throws Exception {
//        new Thread(() -> SocketServer.getInstance().startSocketServer(port)).start();
//        QueueMessage queueMessage = new QueueMessage();
//        queueMessage.setIp("127.0.0.1");
//        queueMessage.setPort(50877);
//        SocketServer.getInstance().addQueue(queueMessage);
//        /*
//         * 向服务器端发送数据
//         */
//        // 1.定义服务器的地址、端口号、数据
//        InetAddress address = InetAddress.getByName("localhost");
//        // 3.创建DatagramSocket对象
//        DatagramSocket socket = new DatagramSocket(queueMessage.getPort(), address);
//
//        String next = "{'test':'这是50877端口用户发的消息'}";
//        byte[] data = next.getBytes();
//        // 2.创建数据报，包含发送的数据信息
//        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//
//        new Thread(() -> {
//            // 1.创建数据报，用于接收服务器端响应的数据
//            byte[] data2 = new byte[1024];
//
//            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
//            while (true) {
//                // 2.接收服务器响应的数据
//                try {
//                    socket.receive(packet2);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                // 3.读取数据
//                String reply = new String(data2, 0, packet2.getLength());
//                System.out.println("服务器说：" + reply);
//            }
//        }).start();
//
//        // 4.向服务器端发送数据报
//        socket.send(packet);
//        send2();
    }

    private void send2() throws Exception{
//        QueueMessage queueMessage1 = new QueueMessage();
//        queueMessage1.setIp("127.0.0.1");
//        queueMessage1.setPort(50880);
//        SocketServer.getInstance().addQueue(queueMessage1);
//        /*
//         * 向服务器端发送数据
//         */
//        // 1.定义服务器的地址、端口号、数据
//        InetAddress address = InetAddress.getByName("localhost");
//        // 3.创建DatagramSocket对象
//        DatagramSocket socket = new DatagramSocket(queueMessage1.getPort(), address);
//
//        String next = "{'test':'这是50880端口用户发的消息'}";
//        byte[] data = next.getBytes();
//        // 2.创建数据报，包含发送的数据信息
//        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//        // 4.向服务器端发送数据报
//        socket.send(packet);
//        /*
//         * 接收服务器端响应的数据
//         */
//        while (true) {
//            // 1.创建数据报，用于接收服务器端响应的数据
//            byte[] data2 = new byte[1024];
//
//            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
//            // 2.接收服务器响应的数据
//            socket.receive(packet2);
//            // 3.读取数据
//            String reply = new String(data2, 0, packet2.getLength());
//            System.out.println("服务器说：" + reply);
//        }
//
//

    }
}
