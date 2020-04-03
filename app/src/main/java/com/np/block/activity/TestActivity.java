package com.np.block.activity;

import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.np.block.NpBlockApplication;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.base.BaseGameActivity;
import com.np.block.base.BaseTetrisView;
import com.np.block.util.LoggerUtils;
import com.np.block.view.SinglePlayerEnemyView;
import com.np.block.view.SinglePlayerView;
import butterknife.BindView;


/**
 * 用做测试使用 代码不必提交
 *
 * @author fengxin
 */
public class TestActivity extends BaseActivity {

    @BindView(R.id.test)
    Button button;
    int i = 0;


    @Override
    public void init() {
        button.setOnClickListener(v -> {
            NpBlockApplication.getInstance().mHandler.sendEmptyMessage(i);
            i++;
        });
    }

    @Override
    public int getContentView() {
        return R.layout.test_layout;
    }
}
