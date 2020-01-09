package com.np.block.activity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ActivityManager;
import com.np.block.core.manager.SocketServerManager;
import com.np.block.core.manager.ThreadPoolManager;
import com.np.block.util.ConstUtils;
import butterknife.BindView;

/**
 * 单人匹配游戏
 * @author fengxin
 */
public class SinglePlayerActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.exit_message)
    Button exit;
    @BindView(R.id.send_message)
    Button send;
    @BindView(R.id.edit_message)
    EditText editText;
    @BindView(R.id.message_text)
    TextView text;
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
            exit.setEnabled(true);
            send.setEnabled(true);
        }
    };
    /**接收handler消息*/
    private Handler mHandler = new Handler(msg -> {
        if (msg.what == ConstUtils.HANDLER_GAME_DATA) {
            String newText = (String) msg.obj;
            text.setText(text.getText().toString().concat("\n").concat(newText));
        }
        return false;
    });

    @Override
    public void init() {
        //初始化接收匹配队列消息的Handler
        SocketServerManager.getInstance().setHandler(mHandler);
        exit.setOnClickListener(this);
        send.setOnClickListener(this);
        exit.setEnabled(false);
        send.setEnabled(false);
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
            case R.id.send_message: {
                String text = editText.getText().toString();
                SocketServerManager.getInstance().sendGameMessage(text);
                break;
            }
            case R.id.exit_message: {
                finish();
                ActivityManager.getInstance().removeActivity(SinglePlayerActivity.this);
                break;
            }
            default:
                throw new IllegalStateException("未实现: " + v.getId());
        }
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
