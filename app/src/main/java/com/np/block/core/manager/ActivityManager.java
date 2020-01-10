package com.np.block.core.manager;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理类
 * @author fengxin
 */
public class ActivityManager {

    /**全部activity*/
    private static List<Activity> activities;
    /**私有化内部类 第一次加载类时初始化ActivityManager*/
    private static class Inner {
        private static ActivityManager instance = new ActivityManager();
    }

    /**私有化构造方法*/
    private ActivityManager(){
        activities = new ArrayList<>();
    }

    /**提供一个共有的可以返回类对象的方法*/
    public static ActivityManager getInstance(){
        return Inner.instance;
    }

    /**
     * 添加activity进队列
     * @param activity 需要添加的activity
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 将activity移除队列
     * @param activity 需要删除的activity
     */
    public void removeActivity(Activity activity) {
        if (!activity.isFinishing()){
            activity.finish();
            activities.remove(activity);
        }
    }

    /**
     * 将全部activity移除队列
     */
    public void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }
}
