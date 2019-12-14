package com.np.block.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ThreadPoolManager;
import butterknife.BindView;

/**
 * 用作游戏更新
 * @author fengxin
 */
public class GameUpdateActivity extends BaseActivity {

    int progress = 0;
    @BindView(R.id.bottom_left_text)
    TextView left;
    @BindView(R.id.bottom_right_text)
    TextView right;
    @BindView(R.id.update_bar)
    ProgressBar bar;

    @Override
    public void init() {
        left.setText("正在检查更新...");
        //开启线程更新
        ThreadPoolManager.getInstance().execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateUi("开始更新游戏...", "0%", 0);
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress = progress + 10;
                String barProgress = progress + "%";
                updateUi("正在更新游戏...", barProgress, progress);
            } while (progress != 100);
            updateUi("游戏更新完成", "100%", progress);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(GameUpdateActivity.this, LoginActivity.class));
        });
    }

    private void updateUi(final String text1, final String text2, final int progress){
        runOnUiThread(() -> {
            left.setText(text1);
            right.setText(text2);
            bar.setProgress(progress);
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_update;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //在欢迎页面屏蔽BACK键
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return false;
        }
        return false;
    }
}
