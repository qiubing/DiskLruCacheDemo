package com.example.compaq.disklrucachedemo.config;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Description:BitmapConfig类实现参数配置
 * Author: qiubing
 * Date: 2017-06-06 21:17
 */
public class BitmapConfig {
    private int mWidth;
    private int mHeight;
    private Bitmap.Config mPreferred;

    public BitmapConfig(int width,int height){
        this(width,height, Bitmap.Config.RGB_565);
    }

    public BitmapConfig(int width,int height,Bitmap.Config config){
        mWidth = width;
        mHeight = height;
        mPreferred = config;
    }

    public BitmapFactory.Options getBitmapOptions(){
        return getBitmapOptions(null);
    }

    public BitmapFactory.Options getBitmapOptions(InputStream is){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        if (is != null){
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is,null,options);
            options.inSampleSize = calculateInSampleSize(options,mWidth,mHeight);
        }
        options.inJustDecodeBounds = false;
        return options;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth){
            final int halfWidth = width/2;
            final int halfHeight = height/2;
            while ((halfWidth /inSampleSize) > reqWidth && (halfHeight/inSampleSize) > reqHeight){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
