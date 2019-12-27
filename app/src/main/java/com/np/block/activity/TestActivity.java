package com.np.block.activity;

import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.util.ConstUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;

import org.litepal.util.Const;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

        button.setOnClickListener(v -> {
            ThreadPoolManager.getInstance().execute(() ->{
                try {
                    OkHttpUtils.get("/match/add");
                    runOnUiThread(()->Toast.makeText(context, "加入成功", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        button2.setOnClickListener(v -> {
            ThreadPoolManager.getInstance().execute(() ->{
                try {

                    /*
                     * 向服务器端发送数据
                     */
                    // 1.定义服务器的地址、端口号、数据
                    InetAddress address = InetAddress.getByName("npblock.cn");
                    // 3.创建DatagramSocket对象
                    DatagramSocket socket = new DatagramSocket();
                    socket.getPort();

                    String next = "{'test':'this is test'}";
                    runOnUiThread(()->textView.setText(String.valueOf(textView.getText()).concat( "发送了一条消息\n")));
                    byte[] data = next.getBytes();
                    // 2.创建数据报，包含发送的数据信息
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, 65535);
                    // 4.向服务器端发送数据报
                    socket.send(packet);

                    /*
                     * 接收服务器端响应的数据
                     */
                    while (true) {
                        // 1.创建数据报，用于接收服务器端响应的数据
                        byte[] data2 = new byte[1024];

                        DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
                        // 2.接收服务器响应的数据
                        socket.receive(packet2);
                        // 3.读取数据
                        final String reply = new String(data2, 0, packet2.getLength());
                        LoggerUtils.i("服务器说：" + reply);
                        runOnUiThread(()->textView.setText(String.valueOf(textView.getText()).concat( reply +"\n")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
