package com.example.compaq.disklrucachedemo.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.compaq.disklrucachedemo.ImageUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import libcore.io.DiskLruCache;

/**
 * @Description: 硬盘缓存,使用DiskLruCache实现
 * @author: qiubing
 * @date: 2017/3/19 20:33
 */
public class DiskCache implements ImageCache {
    //设置硬盘缓存大小为10MB
    private static final int MAX_DISK_CACHE_SIZE = 10 * 1024 * 1024;
    private static final String CACHE_DIR_NAME = "thumb";
    private DiskLruCache mDiskLruCache;

    public DiskCache(Context context){
        File cacheDir = ImageUtils.getDiskCacheDir(context, CACHE_DIR_NAME);
        if (!cacheDir.exists()){
            cacheDir.mkdir();
        }
        int appVersion = ImageUtils.getAppVersion(context);
        try {
            mDiskLruCache = DiskLruCache.open(cacheDir,appVersion,1,MAX_DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从硬盘缓存中读取图片
     * @param key
     * @return
     */
    @Override
    public Bitmap get(String key) {
        String hashKey = ImageUtils.hashKeyForDisk(key);
        FileInputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hashKey);
            if (snapshot != null){
                inputStream = (FileInputStream) snapshot.getInputStream(0);
                if (inputStream != null){
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 将文件写入硬盘缓存中
     * @param key
     * @param value
     */
    @Override
    public void put(String key, Bitmap value) {
        String hashKey = ImageUtils.hashKeyForDisk(key);
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hashKey);
            //已经在缓存了，直接返回
            if (snapshot != null){
                return;
            }
            //不在缓存中，将文件存入缓存中
            DiskLruCache.Editor editor = mDiskLruCache.edit(hashKey);
            if (editor != null){
                outputStream = editor.newOutputStream(0);
                if (writeBitmapToOutputStream(value,outputStream)){
                    editor.commit();
                }else {
                    editor.abort();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ImageUtils.closeQuietly(in);
            ImageUtils.closeQuietly(inputStream);
            ImageUtils.closeQuietly(out);
            ImageUtils.closeQuietly(outputStream);
        }
    }

    private boolean writeBitmapToOutputStream(Bitmap bitmap,OutputStream outputStream){
        InputStream inputStream = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            inputStream = ImageUtils.BitmapToInputStream(bitmap);
            in = new BufferedInputStream(inputStream,8*1024);
            out = new BufferedOutputStream(outputStream,8*1024);
            int b;
            while ((b = in.read()) != -1){
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ImageUtils.closeQuietly(in);
            ImageUtils.closeQuietly(out);
            ImageUtils.closeQuietly(inputStream);
        }
        return false;
    }




}
