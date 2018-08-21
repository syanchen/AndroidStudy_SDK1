package com.example.rhc.a3x3puzzle;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rhc on 2018/8/17.
 * 传入map，返回分割后的小图片列表，这里还没有进行打乱
 */

public class ImageSplitterUtil {


    /**
     * 传入bitmap，切成piece*piece块，返回的是这个List列表。
     * @param bitmap
     * @param piece
     * @return List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap, int piece)
    {
        List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();

        // 获取图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 图片应该是方的，这是每一块的宽度。
        int pieceWidth = Math.min(width, height) / piece;

        for (int i = 0; i < piece; i++)
        {
            for (int j = 0; j < piece; j++)
            {

                ImagePiece imagePiece = new ImagePiece();
                // i表示行，j表示列，得到的数字就是1，2，3，4，5，6，7。。。
                imagePiece.setIndex(j + i * piece);

                int x = j * pieceWidth;//顶点坐标x
                int y = i * pieceWidth;//顶点坐标y

                // 第二三个参数是顶点坐标，第四五个参数是宽高。
                imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y,
                        pieceWidth, pieceWidth));
                imagePieces.add(imagePiece);
            }
        }

        return imagePieces;
    }



}
