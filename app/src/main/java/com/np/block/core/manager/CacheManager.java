package com.np.block.core.manager;

import java.util.HashMap;

/**
 * 缓存管理类
 * 保存一些需要缓存的东西
 * @author fengxin
 */
public class CacheManager {

    /**私有化内部类 第一次加载类时初始化CacheManager*/
    private static class Inner {
        private static CacheManager instance = new CacheManager();
    }

    /**提供一个共有的可以返回类对象的方法*/
    public static CacheManager getInstance(){
        return Inner.instance;
    }

    /**缓存map*/
    private static HashMap<String, Object> mMap;
      
    public void put(String key, Object value){
        mMap.put(key, value);
    }  
      
    public Object get(String key)  
    {  
        return mMap.get(key);  
    }

    /**私有化构造方法*/
    private CacheManager() {
        mMap = new HashMap<>();
    }
} 