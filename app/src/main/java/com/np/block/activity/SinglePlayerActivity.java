package com.np.block.activity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.np.block.R;
import com.np.block.base.BaseGameActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.core.manager.SocketServerManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.util.ConstUtils;
import com.np.block.view.SinglePlayerEnemyView;
import com.np.block.view.SinglePlayerView;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * 单人匹配游戏
 * 对战胜利条件 400分或者另外一方死亡
 * 对战基础设定 速度=700
 * 实现思路 每隔多少毫秒将当前坐标发送到服务器
 *
 * @author fengxin
 */
public class SinglePlayerActivity extends BaseGameActivity {

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
    /**定时任务*/
    private ScheduledFuture<?> scheduledFuture;
    /**
     * 计时器 第一个参数总时间，第二个参数间隔时间。
     */
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
        }
    };
    /**接收handler消息*/
    private Handler mHandler = new Handler(msg -> {
        if (msg.what == ConstUtils.HANDLER_GAME_DATA) {
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
        }
        return false;
    });

    @Override
    public BaseTetrisView getTetrisView() {
        return singlePlayerView;
    }

    @Override
    public void initData() {
        speed = 700;
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
        runOnUiThread(() -> score.setText(String.valueOf(usedScore + addScore)));
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            //如果是退出游戏则自动退出游戏游戏队列
            ThreadPoolManager.getInstance().execute(() -> SocketServerManager.getInstance().gameOver());
            if (scheduledFuture != null) {
                //退出定时任务
                scheduledFuture.cancel(true);
            }
        }
        super.onPause();
    }

    /**
     * 定时任务
     * 发送当前游戏数据到服务器
     */
    private void taskSendCoordinate() {
        scheduledFuture = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Thread(() -> {

        }), 0, 1000, TimeUnit.MILLISECONDS);
    }
}
