package com.example.compaq.disklrucachedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.compaq.disklrucachedemo.cache.DoubleCache;
import com.example.compaq.disklrucachedemo.imageloader.ImageLoaderAsyncTask;

/**
 * @Description: ${todo}(这里用一句话描述这个类的作用)
 * @author: qiubing
 * @date: 2017/3/19 17:22
 */
public class PhotoWallAdapter extends ArrayAdapter<String>{
    //加载图片的类
    private ImageLoaderManager manager;
    //记录每个子项的高度
    private int mItemHeight;
    public PhotoWallAdapter(Context context,int resourceId,String[] objects){
        super(context,resourceId,objects);
        manager = ImageLoaderManager.getInstance();
        //设置加载图片的实现类
        manager.setImageLoader(new ImageLoaderAsyncTask());
        //设置加载图片的缓存策略
        manager.setImageCache(new DoubleCache(context));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取下载链接
        final String url = getItem(position);
        View view;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.photo_layout,null);
        }else {
            view = convertView;
        }
        final ImageView imageView = (ImageView) view.findViewById(R.id.photo);
        if (imageView.getLayoutParams().height != mItemHeight){
            imageView.getLayoutParams().height = mItemHeight;
        }
        imageView.setTag(url);
        imageView.setImageResource(R.mipmap.empty_photo);
        manager.loadImage(url, imageView);
        return view;
    }

    /**
     * 设置Item项的高度
     * @param height
     */
    public void setItemHeight(int height){
        if (height == mItemHeight){
            return;
        }
        mItemHeight = height;
        notifyDataSetChanged();
    }

}
