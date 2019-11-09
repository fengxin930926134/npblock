package com.np.block.activity;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
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
    private boolean show = true;

    @Override
    public void init() {
        //加载视频资源控件
        videoview = findViewById(R.id.video_view);
        initVideo();
        Button hide = findViewById(R.id.test_button);
        final Button hh = findViewById(R.id.test_2);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (show){
                    hh.setVisibility(View.VISIBLE);
                    show = false;
                }else {
                    hh.setVisibility(View.INVISIBLE);
                    show = true;
                }
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
