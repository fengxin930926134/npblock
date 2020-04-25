package com.np.block.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.np.block.NpBlockApplication;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Users;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
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
import java.util.List;
import java.util.Map;

/**
 * 登陆和更新
 * @author fengxin
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /**gif背景图*/
    @BindView(R.id.background_gif)
    ImageView backgroundImg;
    /**登录后名字*/
    @BindView(R.id.user_name)
    TextView userName;
    /**注销按钮*/
    @BindView(R.id.cancellation)
    Button cancellation;
    /**开始游戏按钮*/
    @BindView(R.id.begin_game)
    Button beginGame;
    /**手机登录按钮*/
    @BindView(R.id.phone_login)
    ImageView phoneLogin;
    /**QQ登录按钮*/
    @BindView(R.id.qq_login)
    ImageView qqLogin;
    /** 区服 */
    @BindView(R.id.district_service)
    TextView districtService;
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
    private static Integer gender;
    /**是否取消登录 false取消*/
    private static boolean flag = true;
    /**注册弹窗*/
    private static AlertDialog registerDialog;
    /**用户信息*/
    private Users users;
    /**圆圈弹窗*/
    private AlertDialog alertDialog;

    @Override
    public void init() {
        //加载gif背景资源
        Glide.with(this).load(R.mipmap.bg_gif).apply(new RequestOptions().fitCenter()).into(backgroundImg);
        //初始化控件
        initFind();
        //Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        mTencent = Tencent.createInstance(ConstUtils.APP_ID, NpBlockApplication.getInstance().getApplicationContext());
        qqLoginListener = new BaseUiListener();
        tokenLogin();
    }

    /**
     * token登陆
     */
    private void tokenLogin() {
        //检查本地token
        Map<String, Object> map = SharedPreferencesUtils.readToken();
        if (map != null) {
            final Object o = map.get(ConstUtils.SP_TOKEN_TIME);
            if (o instanceof Long){
                final Object token = map.get(ConstUtils.SP_TOKEN);
                //执行token登陆
                alertDialog = DialogUtils.showDialog(context);
                ThreadPoolManager.getInstance().execute(() -> {
                    Users users = new Users();
                    users.setToken((String) token);
                    users.setTokenTime((Long) o);
                    try {
                        JSONObject response = OkHttpUtils.post("/user/login", JSONObject.toJSONString(users));
                        //解析返回数据
                        if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                            loginSuccess(response);
                        }else {
                            runOnUiThread(() -> Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show());
                        }
                    }catch (Exception e){
                        LoggerUtils.e("[token登陆]" + e.getMessage());
                    }finally {
                        runOnUiThread(() -> {
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                                alertDialog = null;
                            }
                        });
                    }
                });
            }
        }
    }

    /**
     * 初始化find和click(this)
     */
    private void initFind() {
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
        if (!SharedPreferencesUtils.clearToken()){
            LoggerUtils.i("[SP] token清理失败");
        }
        //退出环信
        EMClient.getInstance().logout(true);
    }

    /**
     * 更新登陆后ui
     * @param name 用户名
     */
    private void updateUiAfterLogin(final String name){
        runOnUiThread(() -> {
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
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.begin_game:
                //进入游戏
                enterGame();
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
     * 加载进入游戏前需要加载的数据之类的
     */
    private void enterGame(){
        alertDialog = DialogUtils.showDialog(context);
        ThreadPoolManager.getInstance().execute(() -> {
            // 获取最新排行榜数据
            try {
                Users user = new Users();
                user.setToken(users.getToken());
                JSONObject response = OkHttpUtils.post("/rank/getRank", JSONObject.toJSONString(user));
                if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                    JSONObject result = response.getJSONObject("result");
                    List<Users> classicRank = JSONObject.parseArray(result.getString("classicRank"), Users.class);
                    List<Users> rushRank = JSONObject.parseArray(result.getString("rushRank"), Users.class);
                    List<Users> rankRank = JSONObject.parseArray(result.getString("rankRank"), Users.class);
                    //保存进缓存中
                    CacheManager.getInstance().putUsers(ConstUtils.CACHE_RANK_CLASSICAL_MODE, classicRank);
                    CacheManager.getInstance().putUsers(ConstUtils.CACHE_RANK_RANKING_MODE, rankRank);
                    CacheManager.getInstance().putUsers(ConstUtils.CACHE_RANK_BREAKTHROUGH_MODE, rushRank);
                    //判断是新用户还是老用户
                    if (!TextUtils.isEmpty(this.users.getGameName())) {
                        startActivity(new Intent(context, MainActivity.class));
                    } else {
                        startActivity(new Intent(context, InputNameActivity.class));
                    }
                }else {
                    throw new Exception(response.getString(ConstUtils.MSG));
                }
            } catch (Exception e) {
                LoggerUtils.e(e.getMessage());
                runOnUiThread(() -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                runOnUiThread(() -> {
                    if (alertDialog != null) {
                        alertDialog.cancel();
                        alertDialog = null;
                    }
                });
            }
        });
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
        view.findViewById(R.id.alert_login_finish).setOnClickListener(v -> loginDialog.cancel());
        //注册按钮
        view.findViewById(R.id.alert_login_register).setOnClickListener(v -> {
            loginDialog.cancel();
            phoneRegister();
        });
        //登陆按钮
        view.findViewById(R.id.alert_login_btn).setOnClickListener(v -> {
            EditText password = view.findViewById(R.id.password);
            EditText phone = view.findViewById(R.id.phone_number);
            final String phoneText = phone.getText().toString();
            //验证手机号和密码
            if (VerificationUtils.validatePhone(phoneText)){
                //关闭弹窗
                loginDialog.cancel();
                //打开小圆圈 请求服务器
                alertDialog = DialogUtils.showDialog(context);
                final String passwordText = password.getText().toString();
                ThreadPoolManager.getInstance().execute(() -> {
                    Users users = new Users();
                    users.setPhone(phoneText);
                    users.setPassword(passwordText);
                    try {
                        //请求服务器
                        JSONObject response = OkHttpUtils.post("/user/login", JSONObject.toJSONString(users));
                        //解析返回数据
                        if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                            loginSuccess(response);
                        }else {
                            throw new Exception(response.getString("msg"));
                        }
                    }catch (Exception e){
                        LoggerUtils.e(e.getMessage());
                        runOnUiThread(() -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }finally {
                        runOnUiThread(() -> {
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                                alertDialog = null;
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(context, "请输入正确手机号", Toast.LENGTH_SHORT).show();
            }
        });
        loginDialog.setContentView(view);
    }

    /**
     * 手机注册
     */
    private void phoneRegister() {
        registerDialog = DialogUtils.showDialogDefault(context);
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
        //初始化注册布局
        View view = initSendCode();
        //finish按钮
        view.findViewById(R.id.alert_register_finish).setOnClickListener(v -> {
            registerDialog.cancel();
            //注销SMSSDK
            SMSSDK.unregisterAllEventHandler();
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
        //发送验证码按钮
        requestCodeBtn = view.findViewById(R.id.register_request_code_btn);
        final EditText registerInputName = view.findViewById(R.id.register_input_name);
        final EditText registerInputPhone = view.findViewById(R.id.register_input_phone);
        final EditText inputCode = view.findViewById(R.id.register_input_code);
        final EditText registerInputPassword = view.findViewById(R.id.register_input_password);
        //提交验证码
        view.findViewById(R.id.register_commit_btn).setOnClickListener(v -> {
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
        });
        //发送验证码
        requestCodeBtn.setOnClickListener(v -> {
            String phoneNum = registerInputPhone.getText().toString().trim();
            // 1. 通过规则判断手机号
            if (!VerificationUtils.validatePhone(phoneNum)) {
                Toast.makeText(context, "手机号错误, 请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                // 2. 通过sdk请求验证码
                SMSSDK.getVerificationCode("86", phoneNum);
                requestCodeBtn.setEnabled(false);
                //30秒后再请求发送验证码
                ThreadPoolManager.getInstance().execute(() -> {
                    //第一次设置
                    for (int i = ConstUtils.SHORT_MESSAGE_TIME; i > 0; i--) {
                        final String text =  "重新发送("+i+")";
                        mHandler.post(() -> requestCodeBtn.setText(text));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    final String text =  "获取验证码";
                    mHandler.post(() -> {
                        requestCodeBtn.setText(text);
                        requestCodeBtn.setEnabled(true);
                    });
                });
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
                ThreadPoolManager.getInstance().execute(() -> {
                    int number = 60;
                    while (name == null) {
                        number --;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            LoggerUtils.i("获取用户name失败：" + e.getMessage());
                        }
                        if (number <= 0){
                            break;
                        }
                    }
                    if (number > 0){
                        Users users = new Users();
                        users.setOpenId(openId);
                        users.setName(name);
                        users.setHeadSculpture(figureUrl);
                        users.setSex(gender != null? gender: 1);
                        LoggerUtils.toJson(JSON.toJSONString(users));
                        try {
                            JSONObject response = OkHttpUtils.post("/user/login", JSONObject.toJSONString(users));
                            //解析返回数据
                            if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                                loginSuccess(response);
                            }else {
                                throw new Exception(response.getString("msg"));
                            }
                        }catch (Exception e){
                            LoggerUtils.e(e.getMessage());
                            runOnUiThread(() -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
                        }finally {
                            runOnUiThread(() -> {
                                if (alertDialog != null) {
                                    alertDialog.dismiss();
                                    alertDialog = null;
                                }
                            });
                        }
                    }else {
                        runOnUiThread(() -> {
                            if (alertDialog != null) {
                                alertDialog.dismiss();
                                alertDialog = null;
                            }
                            Toast.makeText(context, "获取用户信息失败，请重试", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        }else {
            Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
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
                        LoggerUtils.i("验证码发送成功");
                    } else {
                        //  处理错误的结果
                        Toast.makeText(activity, "验证码发送失败", Toast.LENGTH_SHORT).show();
                        LoggerUtils.i(((Throwable) data).getMessage());
                    }
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // 处理验证码验证通过的结果  此时手机号正确 密码格式正确 验证码正确 弹出请求小圆圈
                        final AlertDialog alertDialog = DialogUtils.showDialog(activity);
                        //请求注册的接口
                        ThreadPoolManager.getInstance().execute(() -> {
                            Users users = new Users();
                            users.setName(name);
                            users.setPhone(phone);
                            users.setPassword(password);
                            try {
                                JSONObject response = OkHttpUtils.post("/user/register", JSONObject.toJSONString(users));
                                //解析返回数据
                                if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                                    //正确结果
                                    activity.runOnUiThread(() -> {
                                        registerDialog.dismiss();
                                        Toast.makeText(activity, "注册成功", Toast.LENGTH_SHORT).show();
                                    });
                                }else {
                                    throw new Exception(response.getString("msg"));
                                }
                            }catch (Exception e){
                                LoggerUtils.e(e.getMessage());
                                activity.runOnUiThread(() -> Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show());
                            } finally {
                                activity.runOnUiThread(() -> {
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        // 处理错误的结果
                        Toast.makeText(activity, "验证码错误", Toast.LENGTH_SHORT).show();
                        LoggerUtils.i(((Throwable) data).getMessage());
                    }
                }
            } else {
                LoggerUtils.i("LoginActivity已经被回收...");
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
                        LoggerUtils.toJson(jsonObject.toJSONString());
                        name = jsonObject.getString("nickname");
                        figureUrl = jsonObject.getString("figureurl_qq_2");
                        gender = jsonObject.getInteger("gender_type");
                    }

                    @Override
                    public void onError(UiError uiError) {
                        //失败
                        flag = false;
                        LoggerUtils.i("[QQ]"+uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        //失败
                        flag = false;
                        LoggerUtils.i("[QQ]取消");
                    }
                });
            }catch (Exception e){
                LoggerUtils.i("[QQ]"+e.getMessage());
            }finally {
                mTencent.logout(context);
            }
        }

        @Override
        public void onError(UiError e) {
            flag = false;
            Toast.makeText(context, "登录错误", Toast.LENGTH_SHORT).show();
            LoggerUtils.i("[QQ] 登录出错 onError:"+ "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }
        @Override
        public void onCancel() {
            flag = false;
            LoggerUtils.i("[QQ]取消登录");
        }
    }

    /**
     * 登录成功处理数据
     * @param data 数据
     */
    private void loginSuccess(JSONObject data) throws Exception {
        Users usersResult = data.getObject("result", Users.class);
        //登陆环信
        loginEmClient(usersResult.getId(), usersResult.getToken());
        // 保存token
        if (SharedPreferencesUtils.saveToken(usersResult.getToken(), usersResult.getTokenTime())){
            LoggerUtils.i("[SP] token保存失败");
        }
        // 更新ui
        updateUiAfterLogin(usersResult.getName());
        // 缓存用户数据
        JSONObject param = new JSONObject();
        param.put("rankRecordId", usersResult.getRankId());
        param.put("rushRecordId", usersResult.getRushId());
        param.put("classicRecordId", usersResult.getClassicId());
        JSONObject post = OkHttpUtils.post("/rank/record", param.toJSONString());
        if (post.getIntValue(ConstUtils.CODE) != ConstUtils.CODE_SUCCESS) {
            throw new Exception(post.getString(ConstUtils.MSG));
        }
        //result 包括排位 挑战 经典 三种模式的成绩
        JSONObject result = post.getJSONObject(ConstUtils.RESULT);
        saveRushAndClassic(result);
        JSONObject rankRecord = result.getJSONObject("rankRecord");
        int rankScore = rankRecord.getIntValue("rankScore");
        boolean riseInRank = rankRecord.getBoolean("riseInRank");
        usersResult.setRankScore(rankScore);
        usersResult.setRiseInRank(riseInRank);
        users = usersResult;
        CacheManager.getInstance().put(ConstUtils.CACHE_USER_INFO, usersResult);
        LoggerUtils.i(users.toString());
        // 缓存用户好友信息
        ThreadPoolManager.getInstance().execute(() -> {
            try {
                JSONObject params = new JSONObject();
                params.put("id", users.getId());
                JSONObject response = OkHttpUtils.post("/social/friend", params.toJSONString());
                if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS) {
                    JSONArray objects = JSONObject.parseArray(response.getString(ConstUtils.RESULT));
                    if (objects != null) {
                        LoggerUtils.toJson(objects.toJSONString());
                        //适配器数据
                        List<Users> users = objects.toJavaList(Users.class);
                        //加入缓存
                        CacheManager.getInstance().putUsers(ConstUtils.CACHE_USER_FRIEND_INFO, users);
                    }
                }
            } catch (Exception e) {
                LoggerUtils.e("好友缓存失败：" + e.getMessage());
            }
        });
    }

    /**
     * 保存挑战模式和经典模式成绩
     * @param object json
     */
    private void saveRushAndClassic(JSONObject object) {
        JSONObject classicRecord = object.getJSONObject("classicRecord");
        JSONObject rushRecord = object.getJSONObject("rushRecord");
        int maxPass = rushRecord.getIntValue("maxPass");
        int maxScore = classicRecord.getIntValue("maxScore");
        if (SharedPreferencesUtils.saveScore(maxScore)) {
            LoggerUtils.e("[SP] 保存成绩失败");
        }
        if (SharedPreferencesUtils.savePass(maxPass)) {
            LoggerUtils.e("[SP] 保存关卡信息失败");
        }
    }

    /**
     * 登陆环信
     */
    private void loginEmClient(Integer id, String token) {
        EMClient.getInstance().login(id.toString(), token, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                LoggerUtils.d("登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                LoggerUtils.e("登录聊天服务器失败！\n" + "id=" + id + " token=" + token);
                LoggerUtils.e("失败原因 code=" + code +" msg=" + message);
            }
        });
    }
}
