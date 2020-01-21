package com.np.block.core.manager;

import com.np.block.core.model.Users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static Map<String, Object> mMap;
      
    public void put(String key, Object value){
        mMap.put(key, value);
    }  
      
    public Object get(String key)  
    {  
        return mMap.get(key);  
    }

    public void remove(String key) {
        mMap.remove(key);
    }

    public boolean containsKey(String key) {
        return mMap.containsKey(key);
    }

    /**缓存usersList**/
    private static Map<String, List<Users>> usersMap;

    public void putUsers(String key, List<Users> value){
        usersMap.put(key, value);
    }

    public List<Users> getUsers(String key)
    {
        return usersMap.get(key);
    }

    public boolean containsUsers(String key) {
        return usersMap.containsKey(key);
    }

    public void removeUsers(String key) {
        usersMap.remove(key);
    }

    /**私有化构造方法*/
    private CacheManager() {
        mMap = new HashMap<>();
        usersMap = new HashMap<>();
    }
} 