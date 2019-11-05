package com.np.block.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.np.block.R;
import com.np.block.base.BaseActivity;

/**
 * 首页
 * @author fengxin
 */
public class WelcomeActivity extends BaseActivity implements Animation.AnimationListener {

    ImageView welcomeImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeImage = findViewById(R.id.welcome_image_view);
        // 配置动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_anim);
        // 启动Fill保持
        animation.setFillEnabled(true);
        // 动画执行完后是否停留在执行完的状态 (设置动画的最后一帧是保留在view上的)
        animation.setFillAfter(true);
        animation.setAnimationListener(this);
        welcomeImage.setAnimation(animation);
    }

    /**
     * 动画执行的开始
     */
    @Override
    public void onAnimationStart(Animation animation) {

    }

    /**
     * 动画执行完之后
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        // 跳转至更新界面
        startActivity(new Intent(this, UpdateActivity.class));
        this.finish();
    }

    /**
     * 动画重复执行的过程中
     */
    @Override
    public void onAnimationRepeat(Animation animation) {
        // 事先加载需要加载的数据
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
