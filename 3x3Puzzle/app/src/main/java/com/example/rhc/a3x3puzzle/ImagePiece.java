package com.example.rhc.a3x3puzzle;

import android.graphics.Bitmap;

/**
 * Created by rhc on 2018/8/17.
 * 用于处理划分后的每一块小图片的属性，例如index和tag
 */

public class ImagePiece {

    private int index ; // 当前第几块
    private Bitmap bitmap ; // 指向当前图片

    public ImagePiece()
    {
    }

    public ImagePiece(int index, Bitmap bitmap)
    {
        this.index = index;
        this.bitmap = bitmap;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    /*
     * 这个是每个类里可以重写的。
     * */
    @Override
    public String toString()
    {
        return "ImagePiece [index=" + index + ", bitmap=" + bitmap + "]";
    }



}
