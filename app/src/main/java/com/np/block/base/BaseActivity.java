package com.np.block.base;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.np.block.core.manager.ActivityManager;
import com.np.block.util.ConstUtils;
import com.np.block.util.DialogUtils;

/**
 * 基类activity
 * @author fengxin
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * log TAG
     */
    public static String TAG;
    /**退出时间*/
    private long tempTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getName();
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
            //判断sdk版本是否大于等于19
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                 DialogUtils.showDialog(this,
                        "提示",
                        "是否退出游戏？",
                        "取消",
                        "确定",
                        false,
                        false,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                exitApp();
                            }
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
    private void exitApp() {
        ActivityManager.getInstance().finishAll();
    }
}
