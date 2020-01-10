package com.np.block.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.hyphenate.chat.EMClient;
import com.np.block.core.manager.ActivityManager;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;
import butterknife.ButterKnife;

/**
 * 基类activity
 * @author fengxin
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**退出时间*/
    private long tempTime = 0;
    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        // 隐藏状态栏
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
        setOverridePendingTransition();
        ActivityManager.getInstance().addActivity(this);
        // Butter Knife 初始化
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 设置activity的切换效果
     */
    public void setOverridePendingTransition() {
        // 设置activity的切换效果为无
        overridePendingTransition( 0,0);
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
        //event.getRepeatCount() == 0 避免长按
        if((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0))
        {
            //判断sdk版本是否大于等于19
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                 DialogUtils.showDialog(this,
                        "提示",
                        "是否退出游戏？",
                        "取消",
                        "确定",
                        false,
                        false,
                         (dialog, which) -> dialog.cancel(),
                         (dialog, which) -> {
                             dialog.cancel();
                             exitApp();
                         });
            }else {
                // 2s内再次选择back键有效
                if(System.currentTimeMillis() - tempTime > ConstUtils.BACK_TIME)
                {
                    Toast.makeText(this, "再按一次退出游戏", Toast.LENGTH_LONG).show();
                    tempTime = System.currentTimeMillis();
                }
                else {
                    exitApp();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出app需要做的事
     */
    public void exitApp() {
        //退出环信
        EMClient.getInstance().logout(true);
        ActivityManager.getInstance().finishAll();
    }
}
