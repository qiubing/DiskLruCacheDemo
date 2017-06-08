package com.example.compaq.disklrucachedemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.GridView;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    /**
     * 用于展示照片墙的GridView
     */
    private GridView mPhotoWall;

    /**
     * GridView的适配器
     */
    private PhotoWallAdapter mAdapter;

    private int mImageThumbSize;
    private int mImageThumbSpacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        mPhotoWall = (GridView) findViewById(R.id.photo_wall);
        mAdapter = new PhotoWallAdapter(this,0,Images.imageThumbUrls);
        mPhotoWall.setAdapter(mAdapter);
        mPhotoWall.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int numColumns = (int) Math.floor(mPhotoWall.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                if (numColumns > 0) {
                    int columnWidth = (mPhotoWall.getWidth() / numColumns) - mImageThumbSpacing;
                    mAdapter.setItemHeight(columnWidth);
                    mPhotoWall.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        mPhotoWall.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
               /* if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
                    Log.e(TAG,"onScrollStateChanged()...stop the work");
                    ImageLoaderManager.getInstance().setPauseWork(true);
                }else {
                    Log.e(TAG,"onScrollStateChanged()...continue the work");
                    ImageLoaderManager.getInstance().setPauseWork(false);

                }*/
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mAdapter.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出程序时，结束所有的下载任务
        //mAdapter.cancelAllTasks();
    }
}
