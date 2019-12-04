package com.np.block.core.manager;

import java.util.HashMap;

public class XclSingleton {
    //单例模式实例  
    private static XclSingleton instance = null;  
      
    //synchronized 用于线程安全，防止多线程同时创建实例  
    public synchronized static XclSingleton getInstance(){  
        if(instance == null){  
            instance = new XclSingleton();  
        }     
        return instance;  
    }     
      
    final HashMap<String, Object> mMap;
    private XclSingleton()  
    {  
        mMap = new HashMap<String,Object>();  
    }  
      
    public void put(String key,Object value){  
        mMap.put(key,value);  
    }  
      
    public Object get(String key)  
    {  
        return mMap.get(key);  
    }  
      
} 