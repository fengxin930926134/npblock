package com.np.block.activity;

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

public class SinglePlayerActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.exit_message)
    Button exit;
    @BindView(R.id.send_message)
    Button send;
    @BindView(R.id.edit_message)
    EditText editText;
    @BindView(R.id.message_text)
    TextView text;
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
        //后续增加一个倒计时操作
        Toast.makeText(context, "倒计时结束", Toast.LENGTH_SHORT).show();
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
                ThreadPoolManager.getInstance().execute(() -> {
                    if (SocketServerManager.getInstance().gameOver()) {
                        finish();
                        ActivityManager.getInstance().removeActivity(SinglePlayerActivity.this);
                    } else {
                        runOnUiThread(() -> Toast.makeText(context, "退出失败", Toast.LENGTH_SHORT).show());
                    }
                });
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            //如果是游戏中退出游戏则自动退出游戏
            ThreadPoolManager.getInstance().execute(() -> SocketServerManager.getInstance().gameOver());
        }
        super.onPause();
    }
}
