package com.np.block.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;
import com.np.block.R;
import com.np.block.base.BaseActivity;

/**
 * 登陆和更新
 * @author fengxin
 */
public class LoginActivity extends BaseActivity {

    /**创建播放视频的控件对象*/
    private VideoView videoview;

    @Override
    public void init() {
        //加载视频资源控件
        videoview = findViewById(R.id.video_view);
        initVideo();
        TextView userName = findViewById(R.id.user_name);
        Button cancellation = findViewById(R.id.cancellation);
        Button beginGame = findViewById(R.id.begin_game);

        userName.setText("封测者");
        userName.setVisibility(View.VISIBLE);
        cancellation.setVisibility(View.VISIBLE);
        beginGame.setVisibility(View.VISIBLE);
        beginGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    private void initVideo() {
        //设置播放加载路径
        videoview.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.login_bg);
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
    }

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void onRestart() {
        //返回重启加载
        initVideo();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        //防止锁屏或者切出的时候，音乐在播放
        videoview.stopPlayback();
        super.onStop();
    }
}
