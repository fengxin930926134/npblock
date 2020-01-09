package com.np.block.activity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.SocketServerManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.core.model.Tetris;
import com.np.block.util.ConstUtils;
import com.np.block.view.NextTetrisView;
import com.np.block.view.SinglePlayerEnemyView;
import butterknife.BindView;

/**
 * 单人匹配游戏
 * 对战胜利条件 400分或者另外一方死亡
 * 对战基础设定 速度=700
 *
 * @author fengxin
 */
public class SinglePlayerActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.single_player_tetris)
    SinglePlayerEnemyView singlePlayerEnemyView;
    /**下一个俄罗斯方块视图*/
    @BindView(R.id.next_tetris_view)
    NextTetrisView nextTetris;
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
        }
    };
    /**接收handler消息*/
    private Handler mHandler = new Handler(msg -> {
        if (msg.what == ConstUtils.HANDLER_GAME_DATA) {
            //处理游戏消息
        }
        return false;
    });

    @Override
    public void init() {
        //初始化接收匹配队列消息的Handler
        SocketServerManager.getInstance().setHandler(mHandler);
        singlePlayerEnemyView.setFatherActivity(this);
        //启动倒计时操作
        countDownTimer.start();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_single_player;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                throw new IllegalStateException("未实现: " + v.getId());
        }
    }

    /**
     * 从下一个方块视图里获取方块
     */
    public Tetris getNextTetris() {
        return nextTetris.getNextTetris();
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            //如果是退出游戏则自动退出游戏游戏队列
            ThreadPoolManager.getInstance().execute(() -> SocketServerManager.getInstance().gameOver());
        }
        super.onPause();
    }
}
