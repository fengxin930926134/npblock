package com.np.block.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LogUtils;
import com.np.block.util.OkHttpUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

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
    private Tencent mTencent;
    private IUiListener qqLoginListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void init() {
        //加载视频资源控件
        videoview = findViewById(R.id.video_view);
        initVideo();
        //Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        mTencent = Tencent.createInstance(ConstUtils.APP_ID, this.getApplicationContext());
        qqLoginListener = new BaseUiListener();
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
//        mTencent.logout(this);
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
                qqLogin();
                break;
                default:
                    Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }

    private void qqLogin(){
        mTencent = Tencent.createInstance(ConstUtils.APP_ID, this.getApplicationContext());
        if (!mTencent.isSessionValid())
        {
            mTencent.login(this, "get_simple_userinfo", qqLoginListener);
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
            @RequiresApi(api = Build.VERSION_CODES.O)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 回调
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode,resultCode,data,qqLoginListener);
    }

    /**
     *  实现回调 IUiListener
     *  调用SDK已经封装好的接口时，例如：登录、快速支付登录、应用分享、应用邀请等接口，需传入该回调的实例。
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Toast.makeText(context, "onComplete"+o.toString(), Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = (JSONObject) o;
            LogUtils.i(TAG, jsonObject.toJSONString());
            String token;
            String expiresIn;
            String uniqueCode;
            uniqueCode = jsonObject.getString("openid");//QQ的openid
            token = jsonObject.getString("access_token");
            expiresIn = jsonObject.getString("expires_in");
            //获取QQ返回的用户信息
            QQToken qqtoken = mTencent.getQQToken();
            mTencent.setOpenId(uniqueCode);
            mTencent.setAccessToken(token, expiresIn);
            UserInfo info = new UserInfo(getApplicationContext(), qqtoken);
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    JSONObject jsonObject = (JSONObject) o;
                    LogUtils.i(TAG, jsonObject.toJSONString());
                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(context, "onError:"+ "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancel() {
            Toast.makeText(context, "取消登陆", Toast.LENGTH_SHORT).show();
        }
    }
}
