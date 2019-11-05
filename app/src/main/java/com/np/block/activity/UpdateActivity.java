package com.np.block.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.np.block.R;
import com.np.block.base.BaseActivity;

/**
 * 更新页
 * @author fengxin
 */
public class UpdateActivity extends BaseActivity {

    private long temptime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)//主要是对这个函数的复写
    {
        if((keyCode == KeyEvent.KEYCODE_BACK)&&(event.getAction() == KeyEvent.ACTION_DOWN))
        {
            // 2s内再次选择back键有效
            if(System.currentTimeMillis() - temptime > 2000)
            {
                System.out.println(Toast.LENGTH_LONG);
                Toast.makeText(this, "请在按一次返回退出", Toast.LENGTH_LONG).show();
                temptime = System.currentTimeMillis();
            }
            else {
                finish();
                //凡是非零都表示异常退出!0表示正常退出!
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
