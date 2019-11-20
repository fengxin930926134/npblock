package com.np.block.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LogUtils;
import com.np.block.util.OkHttpUtils;
import com.np.block.util.VerificationUtils;
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
    /**腾讯服务*/
    private Tencent mTencent;
    private IUiListener qqLoginListener;
    /**判断是否QQ登陆*/
    private boolean isQQLogin;
    /**获取验证码按钮*/
    private Button requestCodeBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void init() {
        //初始化控件
        initFind();
        //初始化背景
        initVideo();
        //Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        mTencent = Tencent.createInstance(ConstUtils.APP_ID, this.getApplicationContext());
        qqLoginListener = new BaseUiListener();
        // 注册mod回调监听接口
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        });
        //检查本地token
    }

    /**
     * 初始化find和click(this)
     */
    private void initFind() {
        //加载视频资源控件
        videoview = findViewById(R.id.video_view);
        //用户名
        userName = findViewById(R.id.user_name);
        qqLogin = findViewById(R.id.qq_login);
        phoneLogin = findViewById(R.id.phone_login);
        districtService = findViewById(R.id.district_service);
        //注销
        cancellation = findViewById(R.id.cancellation);
        //开始游戏
        beginGame = findViewById(R.id.begin_game);
        phoneLogin.setOnClickListener(this);
        cancellation.setOnClickListener(this);
        qqLogin.setOnClickListener(this);
        beginGame.setOnClickListener(this);
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
        if (isQQLogin){
            mTencent.logout(this);
        }
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
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.begin_game:
                //开始游戏
                startActivity(new Intent(context, MainActivity.class));
                break;
            case R.id.phone_login:
                //手机登陆
                loginForPhone();
                break;
            case R.id.cancellation:
                //注销按钮
                loginOut();
                break;
            case R.id.qq_login:
                //QQ登陆
                loginForQQ();
                break;
                default:
                    Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * QQ登陆启动
     */
    private void loginForQQ(){
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
        final AlertDialog loginDialog = DialogUtils.showDialogDefault(context);
        final View view = View.inflate(context, R.layout.alert_dialog_login, null);
        //finish按钮
        view.findViewById(R.id.alert_login_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.cancel();
            }
        });
        //注册按钮
        view.findViewById(R.id.alert_login_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.cancel();
                phoneRegister();
            }
        });
        //登陆按钮
        view.findViewById(R.id.alert_login_btn).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                EditText password = view.findViewById(R.id.password);
                EditText phone = view.findViewById(R.id.phone_number);
                //验证手机号和密码
                if (VerificationUtils.phoneValidate(phone.getText().toString())){
                    //关闭弹窗
                    loginDialog.cancel();
                    //打开小圆圈 请求服务器
                    alertDialog = DialogUtils.showDialog(context);
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("phone",phone.getText().toString());
                    jsonObject.put("password",password.getText().toString());
                    ThreadPoolManager.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject params = new JSONObject();
                                params.put("params", jsonObject);
                                //请求服务器
                                String response = OkHttpUtils.post("/login/verify",params.toJSONString());
                                JSONObject data = JSONObject.parseObject(response);
                                //解析返回数据
                                if (data.getIntValue(ConstUtils.STATUS) == ConstUtils.STATUS_SUCCESS) {
                                    data = data.getJSONObject("body");
                                    if (data.getIntValue(ConstUtils.CODE) > ConstUtils.CODE_SUCCESS){
                                        Looper.prepare();
                                        Toast.makeText(context, data.getString("msg"), Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }else {
                                        JSONObject rest = JSONObject.parseObject(data.getString("result"));
                                        updateUiAfterLogin(rest.getString("name"));
                                        isQQLogin = false;
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
                } else {
                    Toast.makeText(context, "请输入正确手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginDialog.setContentView(view);
    }

    /**
     * 手机注册
     */
    private void phoneRegister() {
        AlertDialog registerDialog = DialogUtils.showDialogDefault(context);
        //初始化注册布局
        View view = initSendCode();
        //设置弹窗视图
        registerDialog.setContentView(view);
    }

    /**
     * 给注册布局初始化验证码的全部事件
     *
     * @return 注册布局
     */
    private View initSendCode() {
        View view = View.inflate(context, R.layout.alert_dialog_register, null);
        //发送验证码按钮
        requestCodeBtn = view.findViewById(R.id.register_request_code_btn);
        final TextView register_input_phone = view.findViewById(R.id.register_input_phone);
        final TextView inputCode = view.findViewById(R.id.register_input_code);
        //提交验证码
        view.findViewById(R.id.register_commit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = register_input_phone.getText().toString();
                //将收到的验证码和手机号提交再次核对
                SMSSDK.submitVerificationCode("86", phoneNum, inputCode
                        .getText().toString());
            }
        });

        //发送验证码
        requestCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = register_input_phone.getText().toString().trim();
                LogUtils.i(TAG, phoneNum);
                // 1. 通过规则判断手机号
                if (!VerificationUtils.phoneValidate(phoneNum)) {
                    Toast.makeText(context, "手机号错误, 请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "发送验证码"+phoneNum, Toast.LENGTH_SHORT).show();
                    // 2. 通过sdk请求验证码
                    SMSSDK.getVerificationCode("86", phoneNum);
                }
            }
        });
        return view;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                } else {
                    //  处理错误的结果
                    ((Throwable) data).printStackTrace();
                }
            } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理验证码验证通过的结果
                } else {
                    // 处理错误的结果
                    ((Throwable) data).printStackTrace();
                }
            }
        }
    };

    /**
     * activity回调
     * @param requestCode 请求码
     * @param resultCode 返回码
     * @param data 返回数据
     */
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
                    isQQLogin = true;
                }

                @Override
                public void onError(UiError uiError) {
                    //失败
                }

                @Override
                public void onCancel() {
                    //取消
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
