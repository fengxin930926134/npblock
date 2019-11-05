package com.np.block.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.util.ConstUtils;

/**
 * 更新页
 * @author fengxin
 */
public class UpdateActivity extends BaseActivity {

    private long tempTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
    }

    /**
     * 对这个函数的复写 实现2次返回退出游戏
     * 后面改成弹窗
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if((keyCode == KeyEvent.KEYCODE_BACK)&&(event.getAction() == KeyEvent.ACTION_DOWN))
        {
            // 2s内再次选择back键有效
            if(System.currentTimeMillis() - tempTime > ConstUtils.BACK_TIME)
            {
                Toast.makeText(this, "再按一次返回退出", Toast.LENGTH_LONG).show();
                tempTime = System.currentTimeMillis();
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
