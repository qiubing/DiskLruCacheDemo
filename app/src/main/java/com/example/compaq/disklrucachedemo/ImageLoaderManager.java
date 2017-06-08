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
         * ����imageUrl��Ӧ��ͼƬ����ʾ��ImageView��
         * @param imageUrl
         * @param imageView
         */
        public void loadImage(String imageUrl,ImageView imageView);

        /**
         * ��ͣ��ǰͼƬ���ز���
         * @param pauseWork ���Ϊtrue������ͣ��ǰ���ز���
         */
        public void setPauseWork(boolean pauseWork);

        /**
         * ��ǰ�˳���ǰͼƬ���ز���
         * @param exitTasksEarly
         */
        public void setExitTasksEarly(boolean exitTasksEarly);

        /**
         * ע��ͼƬ����ʵ��
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
