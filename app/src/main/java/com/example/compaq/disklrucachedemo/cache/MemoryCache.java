package com.example.compaq.disklrucachedemo.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.LruCache;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description: 内存缓存,使用LruCache实现
 * @author: qiubing
 * @date: 2017/3/19 20:29
 */
public class MemoryCache implements ImageCache {
    private LruCache<String,Bitmap> mLruCache;

    /**
     * Bitmap内存复用，利用BitmapFactory.Options.inBitmap属性，复用目标对象内存
     */
    private Set<SoftReference<Bitmap>> mReuseableBitmaps;
    public MemoryCache(){
        //最大堆大小
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //1/8的堆大小
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }


            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                /**
                 * entryRemoved方法是把最近最少使用的对象在缓存值到达预设值之前，从内存中移除时回调的方法。
                 * 其中oldValue是即将要从LruCache中移除的对象。
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                    //将最后一张Bitmap从LruCache中移除，加入到软引用集合中，作为内存复用的源对象
                    mReuseableBitmaps.add(new SoftReference<Bitmap>(oldValue));
                }

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mReuseableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }
    }

    @Override
    public Bitmap get(String key) {
        return mLruCache.get(key);
    }

    @Override
    public void put(String key, Bitmap value) {
        if (key == null || value == null){
            return;
        }
        mLruCache.put(key,value);
    }

    public Bitmap decodeSampledBitmapFromStream(InputStream is,BitmapFactory.Options options){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            addInBitmapOptions(options);
        }
        return BitmapFactory.decodeStream(is, null, options);
    }

    private void addInBitmapOptions(BitmapFactory.Options options){
        options.inMutable = true;
        Bitmap inBitmap = getBitmapFromReusableSet(options);
        if (inBitmap != null){
            options.inBitmap = inBitmap;
        }
    }

    public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options){
        Bitmap bitmap = null;
        if (mReuseableBitmaps != null && !mReuseableBitmaps.isEmpty()){
            final Iterator<SoftReference<Bitmap>> iterator = mReuseableBitmaps.iterator();
            Bitmap item;
            while (iterator.hasNext()){
                item = iterator.next().get();
                if (null != item && item.isMutable()){
                    if (canUseForInBitmap(item, options)){
                        bitmap = item;
                        iterator.remove();
                        break;
                    }
                }else {
                    iterator.remove();
                }
            }
        }
        return bitmap;
    }

    private boolean canUseForInBitmap(Bitmap candidate,BitmapFactory.Options options){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            return candidate.getWidth() == options.outHeight &&
                    candidate.getHeight() == options.outHeight &&
                    options.inSampleSize == 1;
        }
        int width = options.outWidth / options.inSampleSize;
        int height = options.outHeight / options.inSampleSize;
        int byteCount = width * height * 4;
        return byteCount <= candidate.getAllocationByteCount();
    }
}
