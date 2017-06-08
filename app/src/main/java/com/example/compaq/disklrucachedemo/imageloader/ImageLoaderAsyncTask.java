package com.example.compaq.disklrucachedemo.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.compaq.disklrucachedemo.ImageLoaderManager;
import com.example.compaq.disklrucachedemo.ImageUtils;
import com.example.compaq.disklrucachedemo.cache.ImageCache;
import com.example.compaq.disklrucachedemo.cache.MemoryCache;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:使用AsyncTask加载图片的类，支持暂停和提起退出的功能
 * Author: qiubing
 * Date: 2017-06-06 20:32
 */
public class ImageLoaderAsyncTask implements ImageLoaderManager.IImageLoader {
    private boolean mExitTaskEarly = false;
    protected boolean mPauseWork = false;

    private final Object mPauseWorkLock = new Object();

    private ImageCache mImageCache = new MemoryCache();

    /**
     * 记录所有正在下载或者等待下载的任务
     */
    private Set<BitmapLoadTask> taskCollection = new HashSet<BitmapLoadTask>();


    @Override
    public void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl == null){
            return;
        }

        Bitmap bitmap = mImageCache.get(imageUrl);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else {
            //否则从网络上下载
            imageView.setTag(imageUrl);
            final BitmapLoadTask task = new BitmapLoadTask(imageUrl,imageView);
            taskCollection.add(task);
            task.execute();
        }


    }

    private class BitmapLoadTask extends AsyncTask<Void,Void,Bitmap>{
        private String mUrl;
        private final WeakReference<ImageView> imageViewWeakReference;
        public BitmapLoadTask(String imageUrl,ImageView imageView){
            mUrl = imageUrl;
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            synchronized (mPauseWorkLock){
                while (mPauseWork && !isCancelled()){
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (bitmap == null && !isCancelled()
                    && imageViewWeakReference.get() != null && !mExitTaskEarly){
                bitmap = downloadImage(mUrl);
            }
            if (bitmap != null){
                //保存到缓存中
                mImageCache.put(mUrl,bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (isCancelled() || mExitTaskEarly){
                result = null;
            }
            ImageView imageView = imageViewWeakReference.get();
            if (result != null && imageView != null && imageView.getTag().equals(mUrl)){
                imageView.setImageBitmap(result);
            }
            //移除任务
            taskCollection.remove(this);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            synchronized (mPauseWorkLock){
                mPauseWorkLock.notifyAll();
            }
        }
    }


    public void cancelAllTasks(){
        if (taskCollection != null){
            for (BitmapLoadTask task : taskCollection){
                task.cancel(false);
            }
        }
    }

    @Override
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock){
            mPauseWork = pauseWork;
            if (!pauseWork){
                mPauseWorkLock.notifyAll();
            }
        }
    }

    @Override
    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTaskEarly = exitTasksEarly;
        setPauseWork(false);
    }

    @Override
    public void setImageCache(ImageCache cache){
        mImageCache = cache;
    }

    private Bitmap downloadImage(String imageUrl){
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        try {
            final URL url = new URL(imageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = urlConnection.getInputStream();
            //return ImageUtils.decodeSampledBitmapFromStream(in,240,240);
            return decodeSampledBitmapFromStream(in,null);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            ImageUtils.closeQuietly(in);
        }
        return null;
    }

    public Bitmap decodeSampledBitmapFromStream(InputStream is,BitmapFactory.Options options){
        return BitmapFactory.decodeStream(is,null,options);
    }
}
