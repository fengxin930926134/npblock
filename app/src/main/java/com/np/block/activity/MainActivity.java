package com.np.block.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.NetUtils;
import com.np.block.NpBlockApplication;
import com.np.block.R;
import com.np.block.adapter.AddFriendAdapter;
import com.np.block.adapter.ClassicRankAdapter;
import com.np.block.adapter.FriendApplyAdapter;
import com.np.block.adapter.FriendManageAdapter;
import com.np.block.base.BaseActivity;
import com.np.block.core.enums.GameTypeEnum;
import com.np.block.core.enums.SexTypeEnum;
import com.np.block.core.enums.StageTypeEnum;
import com.np.block.core.manager.CacheManager;
import com.np.block.core.manager.MessageManager;
import com.np.block.core.manager.SocketServerManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Users;
import com.np.block.fragment.ClassicRankFragment;
import com.np.block.fragment.RankingRankFragment;
import com.np.block.fragment.RushRankFragment;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.util.OkHttpUtils;
import com.np.block.util.ResolutionUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * 主界面
 * @author fengxin
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    /**登陆的用户基本信息*/
    private static Users users = null;
    /**头像*/
    @BindView(R.id.head_img)
    ImageView headImg;
    /**性别*/
    @BindView(R.id.sex)
    ImageView sexImg;
    /**主页的用户游戏名字*/
    @BindView(R.id.user_name)
    TextView userName;
    /**经典模式按钮*/
    @BindView(R.id.classic_block)
    Button classic;
    /**对战模式按钮*/
    @BindView(R.id.battle_block)
    Button battle;
    /**趣味模式按钮*/
    @BindView(R.id.interest_block)
    Button interest;
    /**查看排行榜按钮*/
    @BindView(R.id.view_leaderboards)
    Button viewLeaderboards;
    /**社交*/
    @BindView(R.id.social)
    TextView social;
    @BindView(R.id.social_notification)
    View socialNotification;
    /**聊天弹窗*/
    @BindView(R.id.talk)
    ImageButton talk;
    /**成就*/
    @BindView(R.id.attainment)
    TextView attainment;
    /**左边的排行榜整体*/
    @BindView(R.id.left_linear_rank)
    RelativeLayout leftLinearRank;
    /**方块币*/
    @BindView(R.id.block_num)
    TextView blockNum;
    /**段位*/
    @BindView(R.id.rankText)
    TextView rankText;
    /**计时器*/
    private Chronometer gameChronometer;
    /**经典模式排行榜适配器*/
    public ClassicRankAdapter classicRankAdapter = null;
    /**是否进入匹配 防止多次点击*/
    private boolean enterSuccess = false;
    /**确认进入匹配弹窗*/
    private AlertDialog matchDialog = null;
    /**确认进入匹配弹窗点击ok按钮*/
    private boolean confirmOk = false;
    /**确认进入匹配弹窗的内容*/
    private TextView content;
    /**社交弹窗*/
    private AlertDialog socialDialog = null;
    /**好友管理通知*/
    private View managementNotification;
    /**好友管理适配器数据*/
    private List<Users> friendManageItems = new ArrayList<>();
    /**设置好友管理适配器*/
    private FriendManageAdapter friendManageAdapter = new FriendManageAdapter(R.layout.friend_manage_item, friendManageItems);
    /**Handler接收来自匹配队列消息*/
    public Handler mHandler = new Handler(msg -> {
        Object obj = msg.obj;
        switch (msg.what) {
            case ConstUtils.HANDLER_MATCH_SUCCESS: {
                //匹配成功 暂停匹配计时
                gameChronometer.stop();
                FloatWindow.get().hide();
                //弹出弹窗
                showConfirmDialog((long) obj);
                break;
            }
            case ConstUtils.HANDLER_TIME_STRING: {
                //更新弹窗文字
                content.setText((String) obj);
                break;
            }
            case ConstUtils.HANDLER_MATCH_FAILURE: {
                //取消弹窗
                if (matchDialog != null) {
                    matchDialog.dismiss();
                    matchDialog = null;
                }
                //检查是否点击确认
                if (confirmOk) {
                    //重新进入计时
                    FloatWindow.get().show();
                    gameChronometer.start();
                } else {
                    //退出队列
                    ThreadPoolManager.getInstance().execute(() -> {
                        if (SocketServerManager.getInstance().signOutMatchQueue()) {
                            Toast.makeText(context, "退出队列", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // 重置状态
                    enterSuccess = false;
                }
                break;
            }
            case ConstUtils.HANDLER_ENTER_THE_GAME: {
                //进入游戏
                content.setText((String) obj);
                //取消弹窗
                if (matchDialog != null) {
                    matchDialog.dismiss();
                    matchDialog = null;
                }
                // 重置状态
                enterSuccess = false;
                startActivityForResult(new Intent(this, SinglePlayerActivity.class), 1);
                break;
            }
            case ConstUtils.HANDLER_CHAT_WINDOW: {
                //关闭社交管理 弹出聊天弹窗
                if (socialDialog != null) {
                    socialDialog.dismiss();
                    socialDialog = null;
                }
                Integer toChatUsername = msg.arg1;
                MessageManager.getInstance().showMessageDialog(this, toChatUsername.toString());
                break;
            }
            default:
                Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
        return false;
    });
    /**环信消息监听*/
    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            MessageManager.getInstance().addMessage(messages);
            MessageManager.getInstance().refreshMessage(messages);
            LoggerUtils.i("收到消息=" + messages.toString());
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            LoggerUtils.i("收到透传消息");
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            LoggerUtils.i("收到已读回执");
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            LoggerUtils.i("收到已送达回执");
        }
        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            LoggerUtils.i("消息被撤回");
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            LoggerUtils.i("消息状态变动");
        }
    };

    @Override
    public void init() {
        refreshBlockAndRank();
        // 初始化性别图标
        initSexImg(users.getSex());
        // 初始化用户名
        userName.setText(users.getGameName() != null ? users.getGameName(): getResources().getString(R.string.app_name));
        // 初始化点击事件
        classic.setOnClickListener(this);
        battle.setOnClickListener(this);
        interest.setOnClickListener(this);
        viewLeaderboards.setOnClickListener(this);
        social.setOnClickListener(this);
        talk.setOnClickListener(this);
        attainment.setOnClickListener(this);
        // 加载头像
        loadHeadImg();
        //排行榜
        initRank();
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new EmClientConnectionListener());
        //初始化接收匹配队列消息的Handler
        SocketServerManager.getInstance().setHandler(mHandler);
        //初始化悬浮窗
        initFloatWindow();
        //初始化好友
        initFriendCache();
        //检查消息
        checkNotification();
    }

    /**
     * 检查是否存在通知，存在则打开
     */
    public void checkNotification() {
        if (NpBlockApplication.getInstance().receiveApplication ||
                NpBlockApplication.getInstance().applicationAgree) {
            socialNotification.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.classic_block:
                startActivity(new Intent(MainActivity.this, ClassicBlockActivity.class));
                break;
            case R.id.battle_block:
                battleDialog();
                break;
            case R.id.interest_block:
                interestDialog();
                break;
            case R.id.view_leaderboards:
                rankAnimEvent();
                break;
            case R.id.game_chronometer_close:
                initGameChronometerCloseEvent();
                break;
            //社交
            case R.id.social:
                initSocialEvent();
                break;
            case R.id.talk:{
                MessageManager.getInstance().showMessageDialog(this, null);
                break;
            }
            case R.id.attainment: {
                startActivity(new Intent(context, GameOverActivity.class));
                break;
            }
            default: Toast.makeText(context, "尚未实现", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 匹配计时器关闭事件
     */
    private void initGameChronometerCloseEvent() {
        if (!enterSuccess) {
            //防止重复点击
            return;
        }
        //退出队列
        ThreadPoolManager.getInstance().execute(() -> {
            if (SocketServerManager.getInstance().signOutMatchQueue()) {
                runOnUiThread(() -> {
                    gameChronometer.stop();
                    FloatWindow.get().hide();
                    Toast.makeText(context, "退出队列", Toast.LENGTH_SHORT).show();
                });
                enterSuccess = false;
            }
        });
    }

    /**
     * 社交按钮事件
     */
    private void initSocialEvent() {
        socialDialog = DialogUtils.showDialogFull(context, false, true);
        View inflate = View.inflate(context, R.layout.alert_dialog_social, null);
        //加载好友管理通知
        managementNotification = inflate.findViewById(R.id.friend_management_notification);
        if (NpBlockApplication.getInstance().applicationAgree ||
                NpBlockApplication.getInstance().receiveApplication) {
            //收到申请或者申请被同意则打开通知
            managementNotification.setVisibility(View.VISIBLE);
        }
        LinearLayout body = inflate.findViewById(R.id.body);
        TextView add = inflate.findViewById(R.id.add_friend);
        TextView management = inflate.findViewById(R.id.friend_management);
        //back
        inflate.findViewById(R.id.back).setOnClickListener(v1 -> socialDialog.cancel());
        //默认首页是添加好友布局
        body.addView(initSearch());
        add.setBackgroundColor(Color.BLACK);
        add.setEnabled(false);
        //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
        if (socialDialog.getWindow() != null) {
            socialDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        management.setBackgroundColor(Color.TRANSPARENT);
        management.setEnabled(true);
        //添加好友
        add.setOnClickListener(v12 -> {
            body.removeAllViews();
            body.addView(initSearch());
            add.setBackgroundColor(Color.BLACK);
            add.setEnabled(false);
            management.setBackgroundColor(Color.TRANSPARENT);
            management.setEnabled(true);
        });
        //好友管理
        management.setOnClickListener(v14 -> {
            //点击后关闭通知
            if (NpBlockApplication.getInstance().applicationAgree) {
                managementNotification.setVisibility(View.INVISIBLE);
                socialNotification.setVisibility(View.INVISIBLE);
                NpBlockApplication.getInstance().applicationAgree = false;
            }
            //将view添加进入body
            body.removeAllViews();
            body.addView(initFriendManage());
            add.setBackgroundColor(Color.TRANSPARENT);
            add.setEnabled(true);
            management.setBackgroundColor(Color.BLACK);
            management.setEnabled(false);
        });
        socialDialog.setContentView(inflate);
    }

    /**
     * 初始化搜索好友的布局 (动画效果)
     *          * 渐显 ALPHAIN
     *          * 缩放 SCALEIN
     *          * 从下到上 SLIDEIN_BOTTOM
     *          * 从左到右 SLIDEIN_LEFT
     *          * 从右到左 SLIDEIN_RIGHT
     * @return view
     */
    private View initSearch() {
        LinearLayout view = (LinearLayout) View.inflate(context, R.layout.social_search, null);
        List<Users> usersItems = new ArrayList<>();
        //获取RecyclerView
        RecyclerView userList = view.findViewById(R.id.user_recycler_view);
        // 设置布局管理器
        userList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        // 设置adapter适配器
        AddFriendAdapter mAdapter = new AddFriendAdapter(R.layout.add_item, usersItems);
        //开启动画效果
        mAdapter.openLoadAnimation();
        //设置动画效果
        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        //添加分割线
        userList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        userList.setAdapter(mAdapter);
        //设置权值为1
        view.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        EditText edit = view.findViewById(R.id.edit_search);
        RadioGroup radGroup = view.findViewById(R.id.radioGroup);
        //设置查询事件
        view.findViewById(R.id.search).setOnClickListener(v13 -> {
            String name = edit.getText().toString();
            Users users = new Users();
            users.setToken(MainActivity.users.getToken());
            //获取姓名
            if (!TextUtils.isEmpty(name)) {
                users.setName(name);
            }
            //获取性别
            for (int i = 1; i <= radGroup.getChildCount(); i++) {
                RadioButton childAt = (RadioButton) radGroup.getChildAt(i - 1);
                if (childAt.isChecked()) {
                    users.setSex(i);
                    break;
                }
            }
            //查询数据
            ThreadPoolManager.getInstance().execute(() -> {
                try {
                    JSONObject response = OkHttpUtils.post("/social/select", JSONObject.toJSONString(users));
                    if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS) {
                        JSONArray objects = JSONObject.parseArray(response.getString(ConstUtils.RESULT));
                        if (objects != null) {
                            //适配器数据
                            List<Users> users1 = objects.toJavaList(Users.class);
                            usersItems.clear();
                            usersItems.addAll(users1);
                            //更新
                            runOnUiThread(mAdapter::notifyDataSetChanged);
                        }
                    }
                } catch (Exception e) {
                    LoggerUtils.e("查询失败：" + e.getMessage());
                    Toast.makeText(context, "查询失败", Toast.LENGTH_SHORT).show();
                }
            });
        });
        return view;
    }

    /**
     * 初始化好友管理布局
     * @return view
     */
    private View initFriendManage(){
        LinearLayout view = (LinearLayout) View.inflate(context, R.layout.social_manage, null);

        View applyNotification = view.findViewById(R.id.apply_notification);
        if (NpBlockApplication.getInstance().receiveApplication) {
            applyNotification.setVisibility(View.VISIBLE);
        }
        RecyclerView userList = view.findViewById(R.id.user_recycler_view);
        // 设置布局管理器
        userList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //开启动画效果
        friendManageAdapter.openLoadAnimation();
        //设置动画效果
        friendManageAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        //添加分割线
        userList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        userList.setAdapter(friendManageAdapter);
        //获取好友数据
        if (CacheManager.getInstance().containsUsers(ConstUtils.CACHE_USER_FRIEND_INFO)) {
            List<Users> users = CacheManager.getInstance().getUsers(ConstUtils.CACHE_USER_FRIEND_INFO);
            friendManageItems.clear();
            friendManageItems.addAll(users);
            //更新
            friendManageAdapter.notifyDataSetChanged();
        }else {
            //查询
            initFriendCache();
        }
        //好友申请按钮
        TextView friendApply = view.findViewById(R.id.friend_apply);
        friendApply.setOnClickListener(v -> {
            initFriendApplyDialog();
            if (NpBlockApplication.getInstance().receiveApplication) {
                managementNotification.setVisibility(View.INVISIBLE);
                socialNotification.setVisibility(View.INVISIBLE);
                applyNotification.setVisibility(View.INVISIBLE);
                NpBlockApplication.getInstance().receiveApplication = false;
            }
        });
        //设置权值为1
        view.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        return view;
    }

    /**
     * 查看好友申请弹窗
     */
    private void initFriendApplyDialog() {
        AlertDialog friendApplyDialog = DialogUtils.showDialogDefault(context);
        View view = View.inflate(context, R.layout.alert_dialog_friend_application, null);
        //finish按钮
        view.findViewById(R.id.alert_finish).setOnClickListener(v -> {
            friendApplyDialog.cancel();
            initFriendCache();
        });
        //标题
        TextView title = view.findViewById(R.id.tv_alert_title);
        title.setText("好友申请");
        //配置recyclerView
        RecyclerView recyclerView = view.findViewById(R.id.friend_apply_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        //adapter
        FriendApplyAdapter applyAdapter = new FriendApplyAdapter(R.layout.friend_apply_item, NpBlockApplication.getInstance().receiveApplyData);
        //开启动画效果
        applyAdapter.openLoadAnimation();
        //设置动画效果
        applyAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(applyAdapter);
        //刷新
        runOnUiThread(applyAdapter::notifyDataSetChanged);
        friendApplyDialog.setContentView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            LoggerUtils.i("初始化接收匹配队列消息的Handler");
            //初始化接收匹配队列消息的Handler
            SocketServerManager.getInstance().setHandler(mHandler);
            if (resultCode == RESULT_OK) {
                AlertDialog alertDialog = DialogUtils.showDialog(context);
                ThreadPoolManager.getInstance().execute(() -> {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        LoggerUtils.e(e.getMessage());
                    }
                    alertDialog.cancel();
                    //再来一局
                    startSinglePlayer();
                });
            }
            refreshBlockAndRank();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁消息监听
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    /**
     * 弹出确认进入游戏弹窗
     */
    private void showConfirmDialog(long matchWaitTime) {
        confirmOk = false;
        matchDialog = DialogUtils.showDialogDefault(context);
        matchDialog.setCancelable(false);
        View inflate = View.inflate(context, R.layout.alert_dialog_select, null);
        matchDialog.setContentView(inflate);
        TextView title = inflate.findViewById(R.id.tv_alert_title);
        title.setText("匹配成功");
        content = inflate.findViewById(R.id.tv_alert_content);
        String matchWaitTimeText = "当前确认人数: 0 人\n倒计时: " + (matchWaitTime / 1000 - 1) + " 秒";
        content.setText(matchWaitTimeText);
        Button alertOk = inflate.findViewById(R.id.btn_alert_ok);
        Button alertCancel = inflate.findViewById(R.id.btn_alert_cancel);
        alertOk.setText("确认");
        alertOk.setOnClickListener(v1 -> {
            //1.向服务器发TCP数据包确认收到
            alertOk.setTextColor(Color.GRAY);
            alertOk.setEnabled(false);
            alertCancel.setEnabled(false);
            //确定 发送http去确认
            ThreadPoolManager.getInstance().execute(() ->
                    confirmOk = SocketServerManager.getInstance().confirmMatch());
        });
        alertCancel.setText("拒绝");
        alertCancel.setOnClickListener(v12 -> {
            // 使其他键变黑 等待时间结束
            alertOk.setEnabled(false);
            alertCancel.setEnabled(false);
            confirmOk = false;
            alertOk.setTextColor(Color.GRAY);
        });
    }

    /**
     * 排行榜动画事件
     */
    private void rankAnimEvent() {
        Drawable background = viewLeaderboards.getBackground();
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.rank_left, null);
        if (drawable == null || drawable.getConstantState() == null) {
            return;
        }
        //判断是否为未点击过状态
        if (!Objects.equals(background.getConstantState(), drawable.getConstantState())) {
            final int left = leftLinearRank.getLeft();
            final ValueAnimator rankAnimator = ValueAnimator.ofInt(1, ResolutionUtils.dp2Px(context, 300));
            rankAnimator.setDuration(250);
            rankAnimator.setInterpolator(new DecelerateInterpolator());
            rankAnimator.addUpdateListener(animation -> {
                int current = (int) rankAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) leftLinearRank.getLayoutParams();
                layoutParams.leftMargin = left + current;
                leftLinearRank.setLayoutParams(layoutParams);
            });
            rankAnimator.start();
            //设置为点击过后状态
            viewLeaderboards.setBackground(drawable);
        } else {
            final int left = leftLinearRank.getLeft();
            final ValueAnimator rankAnimator = ValueAnimator.ofInt(1, ResolutionUtils.dp2Px(context, 300));
            rankAnimator.setDuration(250);
            rankAnimator.setInterpolator(new LinearInterpolator());
            rankAnimator.addUpdateListener(animation -> {
                int current = (int) rankAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) leftLinearRank.getLayoutParams();
                layoutParams.leftMargin = left - current;
                leftLinearRank.setLayoutParams(layoutParams);
            });
            rankAnimator.start();
            //设置为未点击状态
            viewLeaderboards.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.rank_right, null));
        }
    }

    /**
     * 趣味模式弹窗
     */
    private void interestDialog() {
        AlertDialog dialog = DialogUtils.showDialogDefault(context);
        View view = View.inflate(context, R.layout.alert_dialog_interest, null);
        view.findViewById(R.id.alert_interest_finish).setOnClickListener(v -> dialog.cancel());
        dialog.setContentView(view);
        Button rushMode = view.findViewById(R.id.rush_mode_button);
        rushMode.setOnClickListener(v -> {
            dialog.cancel();
            // 缓存选择的关卡
            CacheManager.getInstance().put(ConstUtils.CACHE_RUSH_STAGE_TYPE, StageTypeEnum.FIRST_PASS.getCode());
            startActivity(new Intent(MainActivity.this, RushBlockActivity.class));
        });
    }

    /**
     * 对战模式弹窗
     */
    private void battleDialog() {
        final AlertDialog dialog = DialogUtils.showDialogDefault(context);
        View view = View.inflate(context, R.layout.alert_dialog_battle, null);
        //设置取消按钮
        view.findViewById(R.id.alert_battle_finish).setOnClickListener(v -> dialog.cancel());
        //设置单人按钮
        view.findViewById(R.id.single_player_game).setOnClickListener(v -> {
            dialog.cancel();
            startSinglePlayer();
        });
        //设置双人游戏
        dialog.setContentView(view);
    }

    /**
     * 开始单人排位匹配
     */
    private void startSinglePlayer() {
        int version = 23;
        if (Build.VERSION.SDK_INT >= version) {
            if (!Settings.canDrawOverlays(this)) {
                //若未授权则请求权限
                Toast.makeText(context, "需要悬浮窗权限", Toast.LENGTH_SHORT).show();
                getOverlayPermission();
            }
        }
        if (enterSuccess) {
            return;
        }
        enterSuccess = true;
        //进入匹配
        ThreadPoolManager.getInstance().execute(() -> {
            if (SocketServerManager.getInstance().enterMatchQueue(GameTypeEnum.SINGLE_PLAYER_GAME)) {
                runOnUiThread(() -> {
                    FloatWindow.get().show();
                    gameChronometer.setBase(SystemClock.elapsedRealtime());
                    //开始计时
                    gameChronometer.start();
                    Toast.makeText(context, "进入队列", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(context, "请检查网络连接是否正常", Toast.LENGTH_SHORT).show());
                enterSuccess = false;
            }
        });
    }

    @Override
    protected void onPause() {
        if (enterSuccess && isFinishing()) {
            //如果是队列中退出游戏则自动退出队列
            ThreadPoolManager.getInstance().execute(() -> SocketServerManager.getInstance().signOutMatchQueue());
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 判断是否存在未上传成绩
        if (CacheManager.getInstance().containsKey(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE)) {
            final int score = (int) CacheManager.getInstance().get(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE);
            ThreadPoolManager.getInstance().execute(() -> {
                try {
                    JSONObject params = new JSONObject();
                    params.put("classicScore", score);
                    params.put("token", users.getToken());
                    JSONObject response = OkHttpUtils.post("/rank/uploadClassic", params.toJSONString());
                    //解析返回数据
                    if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                        refreshRankData();
                        // 清缓存
                        CacheManager.getInstance().remove(ConstUtils.CACHE_WAIT_UPLOAD_CLASSIC_SCORE);
                    } else {
                        throw new Exception(response.getString(ConstUtils.MSG));
                    }
                } catch (Exception e) {
                    LoggerUtils.e(e.getMessage());
                }
            });
        }
        super.onResume();
        if (!enterSuccess && FloatWindow.get() != null) {
            FloatWindow.get().hide();
        }
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    /**
     * 刷新排行榜数据 TODO 存在bug
     */
    private void refreshRankData(){
        ThreadPoolManager.getInstance().execute(() -> {
            // 获取最新排行榜数据
            try {
                Users user = new Users();
                user.setToken(users.getToken());
                JSONObject response = OkHttpUtils.post("/rank/classic", JSONObject.toJSONString(user));
                if (response.getIntValue(ConstUtils.CODE) == ConstUtils.CODE_SUCCESS){
                    List<Users> usersList = JSONObject.parseArray(response.getString("result"), Users.class);
                    CacheManager.getInstance().put(ConstUtils.CACHE_RANK_CLASSICAL_MODE, usersList);
                    runOnUiThread(() -> {
                        // 刷新数据
                        if (classicRankAdapter != null) {
                            classicRankAdapter.notifyDataSetChanged();
                            LoggerUtils.i("执行刷新了");
                        }
                    });
                }else {
                    //获取失败
                    LoggerUtils.toJson(response.toJSONString());
                }
            } catch (Exception e) {
                LoggerUtils.e(e.getMessage());
            }
        });
    }

    /**
     * 加载头像图片
     */
    private void loadHeadImg() {
        RequestOptions options = new RequestOptions()
                //加载成功之前占位图
                .placeholder(R.mipmap.np_block_launcher)
                //加载错误之后的错误图
                .error(R.mipmap.np_block_launcher)
                //指定图片的尺寸
                .override(100,100)
                //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
                .fitCenter()
                //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
                .centerCrop();
        Glide.with(this)
                .load(users.getHeadSculpture())
                .apply(options)
                .into(headImg);

        headImg.setOnClickListener(v -> {
            //测试
            Toast.makeText(context, "没实现东西", Toast.LENGTH_SHORT).show();
            LoggerUtils.i(String.valueOf(classicRankAdapter == null));
        });
    }

    /**
     * 实现ConnectionListener接口
     */
    private class EmClientConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }
        @Override
        public void onDisconnected(final int error) {
            runOnUiThread( ()-> {
                if(error == EMError.USER_REMOVED){
                    // 显示帐号已经被移除
                    Toast.makeText(context, "垃圾东西，滚", Toast.LENGTH_SHORT).show();
                }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录 做弹窗强制下线
                    Toast.makeText(context, "其他地方登陆，马上给我滚", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetUtils.hasNetwork(MainActivity.this)){
                        //连接不到聊天服务器
                        LoggerUtils.i("正在连接聊天服务器");
                        Toast.makeText(context, "无法连接聊天服务器，请退出重试", Toast.LENGTH_LONG).show();
                    }else {
                        //当前网络不可用，请检查网络设置
                        Toast.makeText(context, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 初始化排行榜
     */
    private void initRank() {
        //创建标签组
        ViewGroup tab = findViewById(R.id.tab);
        //添加标签组的子视图
        tab.addView(LayoutInflater.from(this).inflate(R.layout.rank_title_effect, tab, false));

        //设置页
        ViewPager viewPager = findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);

        //循环生成每一个Fragment
        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of(getString(R.string.classic_block_text), ClassicRankFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.battle_block_text), RankingRankFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.interest_block_text), RushRankFragment.class));

        //创建页面切换组件的适配器（pages类似于list，类似页面的集合）
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);

        //设置页面切换组件的适配器
        viewPager.setAdapter(adapter);
        //设置tab组件关联的viewPage
        viewPagerTab.setViewPager(viewPager);
    }

    /**
     * 初始化悬浮窗
     */
    private void initFloatWindow() {
        if (FloatWindow.get() == null) {
            @SuppressLint("InflateParams")
            View gameChronometerLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.game_chronometer_layout, null);
            gameChronometer = gameChronometerLayout.findViewById(R.id.game_chronometer);
            //计时器关闭按钮
            gameChronometerLayout.findViewById(R.id.game_chronometer_close).setOnClickListener(this);
            //获取当前界面宽度和view宽度
            Rect outSize = new Rect();
            getWindowManager().getDefaultDisplay().getRectSize(outSize);
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            gameChronometerLayout.measure(w, h);
            int width = gameChronometerLayout.getMeasuredWidth();
            //获取要设置的坐标位置
            int right = outSize.right - width;
            FloatWindow
                    .with(getApplication())
                    .setView(gameChronometerLayout)
                    //设置控件初始位置
                    .setX(right)
                    .setY(100)
                    //桌面是否显示
                    .setDesktopShow(false)
                    .setMoveType(MoveType.slide)
                    //app中显示的页面
                    .setFilter(true, MainActivity.class)
                    .build();
            FloatWindow.get().hide();
        }
    }

    /**
     * 申请悬浮窗权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void getOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }

    /**
     * 初始化好友缓存
     */
    private void initFriendCache() {
        if (CacheManager.getInstance().containsUsers(ConstUtils.CACHE_USER_FRIEND_INFO)) {
            return;
        }
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
                        friendManageItems.clear();
                        friendManageItems.addAll(users);
                        //更新
                        runOnUiThread(() -> friendManageAdapter.notifyDataSetChanged());
                    }
                }
            } catch (Exception e) {
                LoggerUtils.e("好友缓存失败：" + e.getMessage());
            }
        });
    }

    /**
     * 初始化性别图标
     * @param sex 性别
     */
    private void initSexImg(Integer sex){
        //默认男图标 判断是否性别女
        if (sex != null && SexTypeEnum.getEnumByCode(sex).equals(SexTypeEnum.FEMALE)) {
            sexImg.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.girl, null));
        }
    }

    /**
     * 刷新方块币和排位成绩
     */
    private void refreshBlockAndRank() {
        users = (Users) CacheManager.getInstance().get(ConstUtils.CACHE_USER_INFO);
        blockNum.setText(users.getWalletBlock() != null ? users.getWalletBlock().toString(): "0");
        rankText.setText(ConstUtils.getRankName(users.getRankScore()));
    }
}
