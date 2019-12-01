package com.np.block.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.np.block.R;
import com.np.block.base.BaseActivity;

/**
 * 主界面
 * @author fengxin
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public void init() {
        Button button = findViewById(R.id.classic_block);
        button.setOnClickListener(this);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.classic_block) {
            startActivity(new Intent(MainActivity.this, ClassicBlockActivity.class));
        }
    }
}
