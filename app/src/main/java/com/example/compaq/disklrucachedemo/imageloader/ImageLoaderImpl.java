package com.example.compaq.disklrucachedemo.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.compaq.disklrucachedemo.ImageLoaderManager;
import com.example.compaq.disklrucachedemo.ImageUtils;
import com.example.compaq.disklrucachedemo.cache.ImageCache;
import com.example.compaq.disklrucachedemo.cache.MemoryCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 图片加载器，通过从预先设置的缓存中读取图片，如果没有读取到相应的图片，则从网络上下载图片。
 * @author: qiubing
 * @date: 2017/3/19 20:21
 */
public class ImageLoaderImpl implements ImageLoaderManager.IImageLoader {
    //缓存
    private ImageCache mImageCache = new MemoryCache();

    //线程池，线程数量为CPU的数量
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 加载图片到ImageView中
     * @param imageUrl 图片的url
     * @param imageView 显示图片的ImageView
     */
    @Override
    public void loadImage(String imageUrl, ImageView imageView) {
        //先从缓存中读取，如果缓存中存在，则直接显示出来
        Bitmap bitmap = mImageCache.get(imageUrl);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }
        //缓存中不存在，从网络上加载请求
        submitLoadRequest(imageView, imageUrl);
    }

    /**
     * 注入缓存实现
     * @param cache
     */
    @Override
    public void setImageCache(ImageCache cache){
        mImageCache = cache;
    }

    @Override
    public void setPauseWork(boolean pauseWork) {
        //todo:实现暂停功能
    }

    @Override
    public void setExitTasksEarly(boolean exitTasksEarly) {
        //todo：实现提前退出功能
    }

    /**
     * 从网络上下载图片
     * @param imageUrl
     * @return
     */
    public Bitmap downloadImage(String imageUrl){
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            URL url = new URL(imageUrl);
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            is = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ImageUtils.closeQuietly(is);
        }
        return bitmap;
    }

    private void submitLoadRequest(final ImageView imageView,final String imageUrl){
        //防止错乱
        imageView.setTag(imageUrl);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(imageUrl);
                if (bitmap == null){
                    return;
                }
                if (imageView.getTag().equals(imageUrl)){
                    imageView.setImageBitmap(bitmap);
                }
                mImageCache.put(imageUrl,bitmap);
            }
        });
    }
}
