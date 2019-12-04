package com.np.block.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
import com.np.block.util.SharedPreferencesUtils;
import com.np.block.util.VerificationUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.jetbrains.annotations.NotNull;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 登陆和更新
 * @author fengxin
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /**创建播放视频的控件对象*/
    private VideoView videoview;
    /**圆圈弹窗*/
    private AlertDialog alertDialog;
    /**登录后名字*/
    private TextView userName;
    /**注销按钮*/
    private Button cancellation;
    /**开始游戏按钮*/
    private Button beginGame;
    /**手机登录按钮*/
    private Button phoneLogin;
    /**QQ登录按钮*/
    private Button qqLogin;
    /** 区服 */
    private TextView districtService;
    /**腾讯服务*/
    private Tencent mTencent;
    private IUiListener qqLoginListener;
    /**获取验证码按钮*/
    private Button requestCodeBtn;
    /**传递Message对象*/
    private final MyHandler mHandler = new MyHandler(this);
    /**暂存手机号*/
    private static String phone;
    /**暂存密码*/
    private static String password;
    /**获取QQ登录的name || 手机注册name暂存*/
    private static String name = null;
    /**获取QQ头像*/
    private static String figureUrl;
    /**获取QQ登录的openId*/
    private static String openId;
    /**是否取消登录 false取消*/
    private static boolean flag = true;
    /**注册弹窗*/
    private static AlertDialog registerDialog;

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
        tokenLogin();
    }

    /**
     * token登陆
     */
    private void tokenLogin() {
        //检查本地token
        Map<String, Object> map = SharedPreferencesUtils.readToken(context);
        if (map != null) {
            final Object o = map.get(ConstUtils.SP_TOKEN_TIME);
            if (o instanceof Long){
                final Object token = map.get(ConstUtils.SP_TOKEN);
                //执行token登陆
                alertDialog = DialogUtils.showDialog(context);
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("token", token);
                        jsonObject.put("tokenTime", o);
                        try {
                            String response = OkHttpUtils.post("/user/login", jsonObject);
                            JSONObject data = JSONObject.parseObject(response);
                            //解析返回数据
                            if (data.getIntValue(ConstUtils.STATUS) == ConstUtils.STATUS_SUCCESS) {
                                data = data.getJSONObject("body");
                                if (data.getIntValue(ConstUtils.CODE) > ConstUtils.CODE_SUCCESS){
                                    Toast.makeText(context, data.getString("msg"), Toast.LENGTH_SHORT).show();
                                }else {
                                    //正确结果
                                    JSONObject rest = JSONObject.parseObject(data.getString("result"));
                                    //保存token
                                    if (SharedPreferencesUtils.saveToken(context, rest.getString("token"), rest.getLongValue("tokenTime"))){
                                        LogUtils.i(TAG, "[SP] token保存失败");
                                    }
                                    //更新ui
                                    updateUiAfterLogin(rest.getString("name"));
                                }
                            }else {
                                Toast.makeText(context, "请检查网络后重试", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            LogUtils.e(TAG,"[token登陆]" + e.getMessage());
                        }finally {
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                                alertDialog = null;
                            }
                        }
                    }
                });
            }
        }
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
        //清除token
        if (!SharedPreferencesUtils.clearToken(context)){
            LogUtils.i(TAG, "[SP] token清理失败");
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
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
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
                loginforqq();
                break;
                default:
                    Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * QQ登陆启动
     */
    private void loginforqq(){
        name = null;
        flag = true;
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
                if (VerificationUtils.validatePhone(phone.getText().toString())){
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
                                //请求服务器
                                String response = OkHttpUtils.post("/user/login",jsonObject);
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
                                        //保存token
                                        if (SharedPreferencesUtils.saveToken(context, rest.getString("token"), rest.getLongValue("tokenTime"))){
                                            LogUtils.i(TAG, "[SP] token保存失败");
                                        }
                                    }
                                }else {
                                    Looper.prepare();
                                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }catch (Exception e){
                                LogUtils.e(TAG, e.getMessage());
                                Looper.prepare();
                                Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }finally {
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                    alertDialog = null;
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
        registerDialog = DialogUtils.showDialogDefault(context);
        //初始化注册布局
        View view = initSendCode();
        //finish按钮
        view.findViewById(R.id.alert_register_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDialog.cancel();
                //注销SMSSDK
                SMSSDK.unregisterAllEventHandler();
            }
        });
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
        // 注册mod回调监听接口
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        });
        //发送验证码按钮
        requestCodeBtn = view.findViewById(R.id.register_request_code_btn);
        final EditText registerInputName = view.findViewById(R.id.register_input_name);
        final EditText registerInputPhone = view.findViewById(R.id.register_input_phone);
        final EditText inputCode = view.findViewById(R.id.register_input_code);
        final EditText registerInputPassword = view.findViewById(R.id.register_input_password);
        //提交验证码
        view.findViewById(R.id.register_commit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = registerInputPhone.getText().toString().trim();
                String passWord = registerInputPassword.getText().toString().trim();
                String nameText = registerInputName.getText().toString();
                if (!VerificationUtils.validateName(nameText)){
                    Toast.makeText(context, "昵称格式不正确", Toast.LENGTH_SHORT).show();
                }else if (!VerificationUtils.validatePhone(phoneNum)) {
                    Toast.makeText(context, "手机号错误, 请重新输入", Toast.LENGTH_SHORT).show();
                } else if (!VerificationUtils.validatePwd(passWord)) {
                    // 判断密码正确性
                    Toast.makeText(context, "密码格式错误（6-16位字母或数字）", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(inputCode.getText().toString())){
                    Toast.makeText(context, "验证码不能为空", Toast.LENGTH_SHORT).show();
                }
                else {
                    //将收到的验证码和手机号提交再次核对
                    SMSSDK.submitVerificationCode("86", phoneNum, inputCode
                            .getText().toString());
                    //暂存
                    name =  nameText;
                    phone = phoneNum;
                    password = passWord;
                }
            }
        });

        //发送验证码
        requestCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = registerInputPhone.getText().toString().trim();
                LogUtils.i(TAG, phoneNum);
                // 1. 通过规则判断手机号
                if (!VerificationUtils.validatePhone(phoneNum)) {
                    Toast.makeText(context, "手机号错误, 请重新输入", Toast.LENGTH_SHORT).show();
                } else {
                    // 2. 通过sdk请求验证码
                    SMSSDK.getVerificationCode("86", phoneNum);
                    requestCodeBtn.setEnabled(false);
                    //30秒后再请求发送验证码
                    ThreadPoolManager.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            //第一次设置
                            for (int i = ConstUtils.SHORT_MESSAGE_TIME; i > 0; i--) {
                                final String text =  "重新发送("+i+")";
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        requestCodeBtn.setText(text);
                                    }
                                });
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            final String text =  "获取验证码";
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    requestCodeBtn.setText(text);
                                    requestCodeBtn.setEnabled(true);
                                }
                            });
                        }
                    });
                }
            }
        });
        return view;
    }

    /**
     * activity回调
     * @param requestCode 请求码
     * @param resultCode 返回码
     * @param data 返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 回调
        if (Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginListener)){
            if (flag){
                alertDialog = DialogUtils.showDialog(context);
                //请求login接口  通过线程休眠来保证能获取到name 但并不是一个好的解决办法
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        int number = 40;
                        while (name == null) {
                            number --;
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (number <= 0){
                                break;
                            }
                        }
                        if (number > 0){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("openId", openId);
                            jsonObject.put("name", name);
                            jsonObject.put("headSculpture", figureUrl);
                            try {
                                String response = OkHttpUtils.post("/user/login", jsonObject);
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
                                        //保存token
                                        if (SharedPreferencesUtils.saveToken(context, rest.getString("token"), rest.getLongValue("tokenTime"))){
                                            LogUtils.i(TAG, "[SP] token保存失败");
                                        }
                                        updateUiAfterLogin(name);
                                    }
                                }else {
                                    Looper.prepare();
                                    Toast.makeText(context, "请检查网络后重试", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }catch (Exception e){
                                LogUtils.e(TAG, e.getMessage());
                            }finally {
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                    alertDialog = null;
                                }
                            }
                        }else {
                            Looper.prepare();
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                                alertDialog = null;
                            }
                            Toast.makeText(context, "请求服务器超时", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
            }
        }else {
            Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
            if (alertDialog != null) {
                alertDialog.cancel();
                alertDialog = null;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Handler静态内部类+实例化弱引用
     * 接收message
     */
    private static class MyHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NotNull Message msg) {
            final LoginActivity activity = mActivity.get();
            if (activity != null) {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // 处理成功得到验证码的结果
                        LogUtils.i(TAG, "验证码发送成功");
                    } else {
                        //  处理错误的结果
                        Toast.makeText(activity, "验证码发送失败", Toast.LENGTH_SHORT).show();
                        LogUtils.i(TAG, ((Throwable) data).getMessage());
                    }
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // 处理验证码验证通过的结果  此时手机号正确 密码格式正确 验证码正确 弹出请求小圆圈
                        final AlertDialog alertDialog = DialogUtils.showDialog(activity);
                        //请求注册的接口
                        ThreadPoolManager.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", name);
                                jsonObject.put("phone", phone);
                                jsonObject.put("password", password);
                                try {
                                    String response = OkHttpUtils.post("/user/register", jsonObject);
                                    JSONObject data = JSONObject.parseObject(response);
                                    //解析返回数据
                                    if (data.getIntValue(ConstUtils.STATUS) == ConstUtils.STATUS_SUCCESS) {
                                        data = data.getJSONObject("body");
                                        if (data.getIntValue(ConstUtils.CODE) > ConstUtils.CODE_SUCCESS){
                                            Looper.prepare();
                                            Toast.makeText(activity, data.getString("msg"), Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }else {
                                            //正确结果
                                            Looper.prepare();
                                            registerDialog.dismiss();
                                            Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    }else {
                                        Looper.prepare();
                                        Toast.makeText(activity, "请检查网络后重试", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }catch (Exception e){
                                    LogUtils.e(TAG, e.getMessage());
                                    Looper.prepare();
                                    Toast.makeText(activity, "网络异常", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }finally {
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                }
                            }
                        });
                    } else {
                        // 处理错误的结果
                        Toast.makeText(activity, "验证码错误", Toast.LENGTH_SHORT).show();
                        LogUtils.i(TAG, ((Throwable) data).getMessage());
                    }
                }
            } else {
                LogUtils.i(TAG, "LoginActivity已经被回收...");
            }
        }
    }

    /**
     *  实现回调 IUiListener
     *  调用SDK已经封装好的接口时，例如：登录、快速支付登录、应用分享、应用邀请等接口，需传入该回调的实例。
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.parse(o.toString()).toString());
                openId = jsonObject.getString("openid");//QQ的openid
                final String token = jsonObject.getString("access_token");
                String expiresIn = jsonObject.getString("expires_in");
                //授权成功
                QQToken qqtoken = mTencent.getQQToken();
                mTencent.setOpenId(openId);
                mTencent.setAccessToken(token, expiresIn);
                UserInfo info = new UserInfo(getApplicationContext(), qqtoken);
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        //获取用户信息
                        JSONObject jsonObject = JSONObject.parseObject(JSONObject.parse(o.toString()).toString());
                        name = jsonObject.getString("nickname");
                        figureUrl = jsonObject.getString("figureurl_qq_2");
                    }

                    @Override
                    public void onError(UiError uiError) {
                        //失败
                        flag = false;
                        LogUtils.i(TAG, "[QQ]"+uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        //失败
                        flag = false;
                        LogUtils.i(TAG, "[QQ]取消");
                    }
                });
            }catch (Exception e){
                LogUtils.i(TAG, "[QQ]"+e.getMessage());
            }finally {
                mTencent.logout(context);
            }
        }

        @Override
        public void onError(UiError e) {
            flag = false;
            Toast.makeText(context, "登录错误", Toast.LENGTH_SHORT).show();
            LogUtils.i(TAG, "[QQ] 登录出错 onError:"+ "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }
        @Override
        public void onCancel() {
            flag = false;
            LogUtils.i(TAG, "[QQ]取消登录");
        }
    }
}
