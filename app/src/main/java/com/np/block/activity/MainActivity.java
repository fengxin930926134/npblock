package com.np.block.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.manager.ActivityManager;

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

    /**
     * 处理从上一个退出的Activity返回的数据
     * @param requestCode 退出的Activity的code值
     * @param resultCode 退出的Activity设置的resultCode值
     * @param data 传递的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GameActivity.CODE) {
            if (resultCode == RESULT_OK) {
                ActivityManager.getInstance().finishAll();
            }
        } else {
            Toast.makeText(context, "没有实现", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.classic_block) {
            startActivityForResult(new Intent(MainActivity.this, GameActivity.class), GameActivity.CODE);
        } else {
            Toast.makeText(context, "没有实现", Toast.LENGTH_SHORT).show();
        }
    }
}
