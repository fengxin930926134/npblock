package com.np.block.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.util.DialogUtils;
import com.np.block.util.LogUtils;
import com.np.block.util.OkHttpUtils;
import com.np.block.util.HttpRequestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 登陆和更新
 * @author fengxin
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /**创建播放视频的控件对象*/
    private VideoView videoview;
    private AlertDialog alertDialog;
    private TextView userName;
    private Button cancellation;
    private Button beginGame;
    private Button phoneLogin;
    private Button qqLogin;

    @Override
    public void init() {
        alertDialog = DialogUtils.showDialog(LoginActivity.this);
        //加载视频资源控件
        videoview = findViewById(R.id.video_view);
        initVideo();
        //用户名
        userName = findViewById(R.id.user_name);
        qqLogin = findViewById(R.id.qq_login);
        phoneLogin = findViewById(R.id.phone_login);
        phoneLogin.setOnClickListener(this);
        //注销
        cancellation = findViewById(R.id.cancellation);
        cancellation.setOnClickListener(this);
        beginGame = findViewById(R.id.begin_game);
        beginGame.setOnClickListener(this);
        //检查本地token 无
        showLogin();
        alertDialog.cancel();
    }

    private void showLogin(){
        qqLogin.setVisibility(View.VISIBLE);
        phoneLogin.setVisibility(View.VISIBLE);
        userName.setVisibility(View.INVISIBLE);
        cancellation.setVisibility(View.INVISIBLE);
        beginGame.setVisibility(View.INVISIBLE);
    }

    private void show(final String name){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userName.setText(name);
                userName.setVisibility(View.VISIBLE);
                cancellation.setVisibility(View.VISIBLE);
                beginGame.setVisibility(View.VISIBLE);
                qqLogin.setVisibility(View.INVISIBLE);
                phoneLogin.setVisibility(View.INVISIBLE);
                alertDialog.cancel();
            }
        });
    }

    private void initVideo() {
        //设置播放加载路径
        videoview.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.login_bg);
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void onRestart() {
        //返回重启加载
        initVideo();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        //防止锁屏或者切出的时候，音乐在播放
        videoview.stopPlayback();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.begin_game:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                break;
            case R.id.phone_login:
                alertDialog = DialogUtils.showDialog(LoginActivity.this);
                loginV2();
                break;
            case R.id.cancellation:
                showLogin();
                break;
                default:
                    Toast.makeText(this, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginV2(){
        final com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        com.alibaba.fastjson.JSONObject jsonObject1 = new com.alibaba.fastjson.JSONObject();
        jsonObject1.put("phone","admin");
        jsonObject1.put("password",123123);
        jsonObject.put("params",jsonObject1);
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = OkHttpUtils.post("/login/verify", jsonObject.toJSONString());
                    com.alibaba.fastjson.JSONObject parse = com.alibaba.fastjson.JSONObject.parseObject(response);
                    if (parse.getIntValue("status") == 200) {
                        parse = parse.getJSONObject("body");
                        if (parse.getIntValue("code") > 80000){
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this, parse.getString("msg"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else {
                            com.alibaba.fastjson.JSONObject rest = com.alibaba.fastjson.JSONObject.parseObject(parse.getString("result"));
                            show(rest.getString("name"));
                        }
                    }else {
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }catch (Exception e){
                    LogUtils.e(TAG, e.getMessage());
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }finally {
                    alertDialog.cancel();
                }
            }
        });
    }

    private void login() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> jsonObject = new HashMap<>(2);
                jsonObject.put("phone","admin");
                jsonObject.put("password",123123);
                String result = HttpRequestUtils.sendPostRequest("/login/verify",jsonObject,null,false);
                if (result != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(result);
                        int code = jsonObject1.getInt("code");
                        if (code > 80000) {
                            alertDialog.cancel();
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this, jsonObject1.getString("msg"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else {
                            JSONObject rest = jsonObject1.getJSONObject("result");
                            show(rest.getString("name"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
