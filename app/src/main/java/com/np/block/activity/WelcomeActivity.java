package com.np.block.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.np.block.R;
import com.np.block.base.BaseActivity;
import com.np.block.core.db.DefaultDataBase;
import com.np.block.core.manager.ActivityManager;

import butterknife.BindView;

/**
 * 首页
 * @author fengxin
 */
public class WelcomeActivity extends BaseActivity implements Animation.AnimationListener {

    @BindView(R.id.welcome_image_view)
    ImageView welcomeImage;

    @Override
    public void init() {
        // 配置动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_anim);
        // 启动Fill保持
        animation.setFillEnabled(true);
        // 动画执行完后是否停留在执行完的状态 (设置动画的最后一帧是保留在view上的)
        animation.setFillAfter(true);
        animation.setAnimationListener(this);
        welcomeImage.setAnimation(animation);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_welcome;
    }

    /**
     * 动画执行的开始
     */
    @Override
    public void onAnimationStart(Animation animation) {
        // 事先加载需要加载的数据
        // 初始化数据库
        DefaultDataBase.generateBasicDatabase();
    }

    /**
     * 动画执行完之后
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        // 跳转至更新界面
        startActivity(new Intent(this, GameUpdateActivity.class));
        ActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 动画重复执行的过程中
     */
    @Override
    public void onAnimationRepeat(Animation animation) {
        // 不会重复执行，因为执行完成后直接跳转了
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
