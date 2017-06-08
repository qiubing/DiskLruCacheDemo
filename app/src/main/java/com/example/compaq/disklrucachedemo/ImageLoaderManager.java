package com.example.compaq.disklrucachedemo;

import android.widget.ImageView;

import com.example.compaq.disklrucachedemo.cache.ImageCache;

/**
 * Description:
 * Author: qiubing
 * Date: 2017-06-06 20:03
 */
public class ImageLoaderManager {

    private IImageLoader imageLoader;
    private static ImageLoaderManager sInstance;
    private ImageLoaderManager(){

    }
    public static ImageLoaderManager getInstance(){
        if (sInstance == null){
            synchronized (ImageLoaderManager.class){
                if (sInstance == null){
                    sInstance = new ImageLoaderManager();
                }
            }
        }
        return sInstance;
    }

    public void loadImage(String url,ImageView imageView){
        imageLoader.loadImage(url, imageView);
    }

    public void setPauseWork(boolean pauseWork){
        imageLoader.setPauseWork(pauseWork);
    }

    public void setExitTaskEarly(boolean exitTaskEarly){
        imageLoader.setExitTasksEarly(exitTaskEarly);
    }

    public static interface IImageLoader{
        /**
         * 加载imageUrl对应的图片，显示到ImageView上
         * @param imageUrl
         * @param imageView
         */
        public void loadImage(String imageUrl,ImageView imageView);

        /**
         * 暂停当前图片加载操作
         * @param pauseWork 如果为true，则暂停当前加载操作
         */
        public void setPauseWork(boolean pauseWork);

        /**
         * 提前退出当前图片加载操作
         * @param exitTasksEarly
         */
        public void setExitTasksEarly(boolean exitTasksEarly);

        /**
         * 注入图片缓存实现
         * @param cache
         */
        public void setImageCache(ImageCache cache);
    }

    public void setImageLoader(IImageLoader imageLoader){
        this.imageLoader = imageLoader;
    }

    public void setImageCache(ImageCache cache){
        imageLoader.setImageCache(cache);
    }
}
