package com.np.block.core.manager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import com.np.block.NpBlockApplication;
import com.np.block.R;
import com.np.block.util.LoggerUtils;
import com.np.block.util.SharedPreferencesUtils;
import java.io.IOException;

/**
 * 音效管理类
 * @author fengxin
 */
public class MusicManager {

    /**背景音乐是否停止*/
    private boolean bgmstop = true;
    /**背景音乐*/
    private MediaPlayer mp = null;
    /**背景音乐声音大小*/
    private float volume = 1f;

    //    /**音效*/
//    private SoundPool sp = null;
//    /**音效列表map*/
//    private Map<Integer, Integer> spMap = new Hashtable<>();

    /**
     * 开始播放背景音乐
     */
    public void startBackgroundMusic() {
        if (!bgmstop || !SharedPreferencesUtils.readMusic()) {
            return;
        }
        if (mp != null) {
            mp.start();
        } else {
            mp = MediaPlayer.create(NpBlockApplication.getInstance().getApplicationContext(), R.raw.bgmp3);
            mp.start();
        }
        // 监听音频播放完的代码，实现音频的自动循环播放
        mp.setOnCompletionListener(arg0 -> {
            mp.start();
            mp.setLooping(true);
        });
        mp.setVolume(volume, volume);
        bgmstop = false;
    }

    /**
     * 停止播放背景音乐
     */
    public void stopBackgroundMusic() {
        if (!bgmstop && mp != null) {
            mp.stop();
            try {
                mp.prepare();
            } catch (IOException e) {
                LoggerUtils.e("停止播放背景音乐：" + e.getMessage());
            }
            mp.seekTo(0);
            bgmstop = true;
        }
    }

    /**
     * 调节音量
     * @param volume v
     */
    public void adjustVolume(float volume) {
        this.volume = volume;
        if (!bgmstop) {
            mp.setVolume(volume,volume);
        }
    }

    public float getVolume() {
        return volume;
    }

    /**
     * 配置音乐开关
     * @param b b
     */
    public void configurationSwitch(boolean b) {
        SharedPreferencesUtils.saveMusic(b);
        if (!b) {
            stopBackgroundMusic();
        } else {
            startBackgroundMusic();
        }
    }

    /**提供一个共有的可以返回类对象的方法*/
    public static MusicManager getInstance(){
        return Inner.instance;
    }

    /**私有化内部类 第一次加载类时初始化ActivityManager*/
    private static class Inner {
        private static MusicManager instance = new MusicManager();
    }

    /**私有化构造方法*/
    private MusicManager(){
       if (mp == null) {
           mp = MediaPlayer.create(NpBlockApplication.getInstance().getApplicationContext(), R.raw.bgmp3);
       }
    }
}
