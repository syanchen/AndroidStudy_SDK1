package com.example.rhc.volleydemo;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by rhc on 2018/8/10.
 */

public class BitmapCache implements ImageLoader.ImageCache
{

    private LruCache<String,Bitmap> mCache;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public BitmapCache() {
        int maxSize = 10* 1024 *1024;//10m
        mCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url,bitmap);
    }
}
