package com.example.compaq.disklrucachedemo.cache;

import android.graphics.Bitmap;

/**
 * @Description: 缓存的抽象接口，主要包含读缓存和存入缓存
 * @author: qiubing
 * @date: 2017/3/19 20:25
 */
public interface ImageCache {
    /**
     * 从缓存中读取值
     * @param key
     * @return
     */
    public Bitmap get(String key);

    /**
     * 保持缓存值
     * @param key
     * @param value
     */
    public void put(String key,Bitmap value);

}
