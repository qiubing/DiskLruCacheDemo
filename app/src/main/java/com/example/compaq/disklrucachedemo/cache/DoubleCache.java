package com.example.compaq.disklrucachedemo.cache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * @Description: 双缓存，包含内存缓存和SD卡缓存
 * @author: qiubing
 * @date: 2017/3/19 21:15
 */
public class DoubleCache implements ImageCache {
    //内存缓存
    private MemoryCache mMemoryCache;
    //硬盘缓存
    private DiskCache mDiskCache;

    public DoubleCache(Context context){
        mMemoryCache = new MemoryCache();
        mDiskCache = new DiskCache(context);
    }

    /**
     * 先从内存缓存中读取，如果不存在，则从硬盘缓存中读取
     * @param key
     * @return
     */
    @Override
    public Bitmap get(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap == null){
            bitmap = mDiskCache.get(key);
            //如果内存缓存中没有，但是硬盘中有缓存，则更新内存缓存。
            if (bitmap != null){
                mMemoryCache.put(key,bitmap);
            }
        }
        return bitmap;
    }

    /**
     * 同时存入内存缓存和硬盘缓存中
     * @param key
     * @param value
     */
    @Override
    public void put(String key, Bitmap value) {
        mMemoryCache.put(key,value);
        mDiskCache.put(key,value);
    }
}
