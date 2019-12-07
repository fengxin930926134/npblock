package com.np.block;

import android.app.Application;

/**
 * 主Application
 * @author fengxin
 */
public class NpBlockApplication extends Application {

    private static NpBlockApplication app;

    public static NpBlockApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        // 进行一些全局的初始化操作
    }

}
