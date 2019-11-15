package com.np.block.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LogUtils;
import com.np.block.util.OkHttpUtils;

/**
 * 登陆和更新
 * @author fengxin
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /**创建播放视频的控件对象*/
    private VideoView videoview;
    /**圆圈弹窗*/
    private AlertDialog alertDialog;
    /**登陆后名字*/
    private TextView userName;
    /**注销按钮*/
    private Button cancellation;
    /**开始游戏按钮*/
    private Button beginGame;
    /**手机登陆按钮*/
    private Button phoneLogin;
    /**QQ登陆按钮*/
    private Button qqLogin;
    /** 区服 */
    private TextView districtService;


    @Override
    public void init() {
        //加载视频资源控件
        videoview = findViewById(R.id.video_view);
        initVideo();

        //用户名
        userName = findViewById(R.id.user_name);
        qqLogin = findViewById(R.id.qq_login);
        phoneLogin = findViewById(R.id.phone_login);
        districtService = findViewById(R.id.district_service);
        phoneLogin.setOnClickListener(this);
        //注销
        cancellation = findViewById(R.id.cancellation);
        cancellation.setOnClickListener(this);
        qqLogin.setOnClickListener(this);
        beginGame = findViewById(R.id.begin_game);
        beginGame.setOnClickListener(this);
        //检查本地token
    }

    /**
     *  注销登陆
     */
    private void loginOut(){
        qqLogin.setVisibility(View.VISIBLE);
        phoneLogin.setVisibility(View.VISIBLE);
        userName.setVisibility(View.INVISIBLE);
        cancellation.setVisibility(View.INVISIBLE);
        beginGame.setVisibility(View.INVISIBLE);
        districtService.setVisibility(View.INVISIBLE);
    }

    /**
     * 更新登陆后ui
     * @param name 用户名
     */
    private void updateUiAfterLogin(final String name){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userName.setText(name);
                userName.setVisibility(View.VISIBLE);
                cancellation.setVisibility(View.VISIBLE);
                beginGame.setVisibility(View.VISIBLE);
                districtService.setVisibility(View.VISIBLE);
                qqLogin.setVisibility(View.INVISIBLE);
                phoneLogin.setVisibility(View.INVISIBLE);
                alertDialog.cancel();
            }
        });
    }

    /**
     * 初始化背景
     */
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
                startActivity(new Intent(context, MainActivity.class));
                break;
            case R.id.phone_login:
                loginForPhone();
                break;
            case R.id.cancellation:
                loginOut();
                break;
            case R.id.qq_login:
                //login();
                break;
                default:
                    Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 手机登陆启动
     */
    private void loginForPhone(){
        final AlertDialog loginDialog = DialogUtils.showDialogLogin(context);
        final View view = View.inflate(context, R.layout.alert_dialog_login, null);
        //finish按钮
        view.findViewById(R.id.alert_login_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.cancel();
            }
        });
        view.findViewById(R.id.alert_login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.cancel();
                alertDialog = DialogUtils.showDialog(context);
                EditText password = view.findViewById(R.id.password);
                EditText phone = view.findViewById(R.id.phone_number);
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("phone",phone.getText().toString());
                jsonObject.put("password",password.getText().toString());
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject params = new JSONObject();
                            params.put("params", jsonObject);
                            String response = OkHttpUtils.post("/login/verify",params.toJSONString());
                            JSONObject parse = JSONObject.parseObject(response);
                            if (parse.getIntValue(ConstUtils.STATUS) == ConstUtils.STATUS_SUCCESS) {
                                parse = parse.getJSONObject("body");
                                if (parse.getIntValue(ConstUtils.CODE) > ConstUtils.CODE_SUCCESS){
                                    Looper.prepare();
                                    Toast.makeText(context, parse.getString("msg"), Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else {
                                    JSONObject rest = JSONObject.parseObject(parse.getString("result"));
                                    updateUiAfterLogin(rest.getString("name"));
                                }
                            }else {
                                Looper.prepare();
                                Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }catch (Exception e){
                            LogUtils.e(TAG, e.getMessage());
                            if (alertDialog != null) {
                                alertDialog.cancel();
                            }
                            Looper.prepare();
                            Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }finally {
                            if (alertDialog != null) {
                                alertDialog.cancel();
                            }
                        }
                    }
                });
            }
        });
        loginDialog.setContentView(view);
    }

    /**
     * 模拟请求服务器的过程
     */
    public void test() {

    }
}
