package com.np.block.base;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.np.block.core.manager.ActivityManager;
import com.np.block.util.ConstUtils;

/**
 * 基类activity
 * @author fengxin
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**退出时间*/
    private long tempTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏菜单
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        // 设置界面全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int layoutId = getContentView();
        // 针对强制杀死的情况处理
        if(layoutId == 0) {
            throw new IllegalArgumentException();
        }
        // 设置view
        setContentView(layoutId);
        ActivityManager.getInstance().addActivity(this);
        init();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 初始化view
     * 初始化数据
     * 初始化事件
     */
    public abstract void init ();

    /**
     * 返回子类的视图view
     * @return view
     */
    public abstract int getContentView ();

    /**
     * 退出时的方法
     * @param keyCode 具体操作
     * @param event 触碰屏幕事件
     * @return 是否触发
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_DOWN))
        {
            // 2s内再次选择back键有效
            if(System.currentTimeMillis() - tempTime > ConstUtils.BACK_TIME)
            {
                Toast.makeText(this, "再按一次返回退出", Toast.LENGTH_LONG).show();
                tempTime = System.currentTimeMillis();
            }
            else {
                ActivityManager.getInstance().finishAll();
                //凡是非零都表示异常退出!0表示正常退出!
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
