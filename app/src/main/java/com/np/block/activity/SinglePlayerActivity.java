package com.np.block.activity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseGameActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.manager.ActivityManager;
import com.np.block.core.manager.SocketServerManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import com.np.block.util.LoggerUtils;
import com.np.block.view.SinglePlayerEnemyView;
import com.np.block.view.SinglePlayerView;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;

/**
 * 单人匹配游戏
 * 对战胜利条件 350分或者另外一方死亡
 * 对战基础设定 速度=700
 * 实现思路 每隔多少毫秒将当前坐标发送到服务器
 *
 * @author fengxin
 */
public class SinglePlayerActivity extends BaseGameActivity {

    /** 胜利条件*/
    public static final int WIN_CONDITION = 350;
    /** 游戏速度*/
    public static final int GAME_SPEED = 700;
    /** 游戏延迟*/
    public static final int GAME_DELAY = 150;
    /**俄罗斯方块视图*/
    @BindView(R.id.single_player_tetris)
    SinglePlayerView singlePlayerView;
    /**俄罗斯方块敌人视图*/
    @BindView(R.id.single_player_tetris_enemy)
    SinglePlayerEnemyView singlePlayerEnemyView;
    /**成绩*/
    @BindView(R.id.score)
    TextView score;
    /**敌人成绩*/
    @BindView(R.id.enemy_score)
    TextView scoreEnemy;
    /**胜利时间 不处理时区*/
    private long winTime;
    /**定时任务*/
    private ScheduledFuture<?> scheduledFuture;
    /** 开局倒计时 计时器 第一个参数总时间，第二个参数间隔时间*/
    private CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            long second = millisUntilFinished / 1000;
            String text = "倒计时 " + second + " 秒\n后续改弹窗防止瞎点";
            //后续改弹窗
            Toast.makeText(SinglePlayerActivity.this, text, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish() {
            //启动游戏
            startDownThread();
            //启动定时发送数据线程
            taskSendCoordinate();
        }
    };
    /** 完成胜利条件 向服务器发送完成信息 计时器 第一个参数，第二个参数间隔时间*/
    private CountDownTimer winDownTimer = new CountDownTimer(2000, 500) {

        @Override
        public void onTick(long millisUntilFinished) {
            //将当前完成胜利条件的时间发送给服务器
            SocketServerManager.getInstance().sendGameWinMessage(winTime);
        }

        @Override
        public void onFinish() {
            LoggerUtils.e("发送胜利时间完毕...还未收到服务器消息");
        }
    };
    /** 完成胜利条件 向服务器发送完成信息 计时器 第一个参数，第二个参数间隔时间*/
    private CountDownTimer defeatDownTimer = new CountDownTimer(2000, 500) {

        @Override
        public void onTick(long millisUntilFinished) {
            //将失败的消息发送给服务器
            SocketServerManager.getInstance().sendGameOverMessage();
        }

        @Override
        public void onFinish() {
            LoggerUtils.e("发送失败消息完毕...还未收到服务器消息");
        }
    };
    /**接收handler消息*/
    private Handler mHandler = new Handler(msg -> {
        switch (msg.what) {
            case ConstUtils.HANDLER_GAME_DATA:{
                //处理服务器游戏消息
                String data = (String) msg.obj;
                JSONObject parseObject = JSONObject.parseObject(data);
                //设置敌人成绩
                String score = parseObject.getString(ConstUtils.JSON_KEY_ENEMY_SCORE);
                if (!scoreEnemy.getText().toString().equals(score)) {
                    scoreEnemy.setText(score);
                }
                //更新敌人界面
                singlePlayerEnemyView.updateView(parseObject);
                break;
            }
            case ConstUtils.HANDLER_GAME_WIN:{
                beginGame = false;
                //关闭定时发送数据
                scheduledFuture.cancel(true);
                //游戏胜利
                DialogUtils.showTextDialog(context, "游戏胜利", "不错不错，不要骄傲", (dialog, which) -> {
                    dialog.cancel();
                    ActivityManager.getInstance().removeActivity(this);
                });
                break;
            }
            case ConstUtils.HANDLER_LOSE_GAME:{
                beginGame = false;
                //关闭定时发送数据
                scheduledFuture.cancel(true);
                //游戏失败
                DialogUtils.showTextDialog(context, "你输了", "菜鸡，别人比你强", (dialog, which) -> {
                    dialog.cancel();
                    ActivityManager.getInstance().removeActivity(this);
                });
                break;
            }
            case ConstUtils.HANDLER_ESCAPE_GAME:{
                beginGame = false;
                //关闭定时发送数据
                scheduledFuture.cancel(true);
                //对方逃跑
                DialogUtils.showTextDialog(context, "游戏胜利", "对方屈服于您的淫威, 逃跑了", (dialog, which) -> {
                    dialog.cancel();
                    ActivityManager.getInstance().removeActivity(this);
                });
                break;
            }
            default: LoggerUtils.e("未知handler消息");
        }
        return false;
    });

    @Override
    public void gameOver() {
        super.gameOver();
        //通知服务器
        defeatDownTimer.start();
    }

    @Override
    public BaseTetrisView getTetrisView() {
        return singlePlayerView;
    }

    @Override
    public void initData() {
        speed = GAME_SPEED;
        //初始化接收匹配队列消息的Handler
        SocketServerManager.getInstance().setHandler(mHandler);
        //启动倒计时操作
        countDownTimer.start();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_single_player;
    }

    /**
     * 消除时更新游戏数据
     *
     * @param rows 消除掉的行数
     */
    public void updateDataAndUi(int rows) {
        int usedScore = Integer.valueOf(score.getText().toString());
        // 计算需要增加的分数
        int addScore = (int) Math.pow(2, rows - 1) * 10;
        // 当前分值
        int scoreTemporary = usedScore + addScore;
        if (scoreTemporary >= WIN_CONDITION) {
            //保存胜利时间
            winTime = System.currentTimeMillis();
            //胜利
            winDownTimer.start();
            //关闭游戏线程
            beginGame = false;
            //关闭定时发送数据
            scheduledFuture.cancel(true);
        }
        runOnUiThread(() -> score.setText(String.valueOf(scoreTemporary)));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //弹出弹窗 退出游戏则让对方胜利
        boolean back = keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0;
        if(back){
            backDialog();
        }
        return true;
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            //如果是退出游戏则自动退出游戏游戏队列
            if (!beginGame) {
                ThreadPoolManager.getInstance().execute(() -> SocketServerManager.getInstance().gameOver());
            }
            if (scheduledFuture != null) {
                //退出定时任务
                scheduledFuture.cancel(true);
            }
        }
        super.onPause();
    }

    /**
     * 点击返回键的弹窗
     */
    private void backDialog() {
        DialogUtils.showDialog(context, "退出游戏",
                "对方将获得胜利，是否确认退出？", "怎么可能让对方赢", "跑路",
                false, false,
                (dialog, which) -> dialog.cancel(), (dialog, which) -> {
                    ThreadPoolManager.getInstance().execute(() -> SocketServerManager.getInstance().gameOver());
                    dialog.cancel();
                    ActivityManager.getInstance().removeActivity(this);
                }
        );
    }

    /**
     * 定时任务
     * 发送当前游戏数据到服务器
     */
    private void taskSendCoordinate() {
        scheduledFuture = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> {
            // 构建当前游戏数据的消息体
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ConstUtils.JSON_KEY_ALL_BLOCK, singlePlayerView.getAllBlockSmall());
            jsonObject.put(ConstUtils.JSON_KEY_ENEMY_SCORE, score.getText().toString());
            jsonObject.put(ConstUtils.JSON_KEY_TETRIS_BLOCK, singlePlayerView.getTetrisSmall());
            // 发送消息
            SocketServerManager.getInstance().sendGameMessage(jsonObject.toJSONString());
        }, 500, GAME_DELAY, TimeUnit.MILLISECONDS);
    }
}
