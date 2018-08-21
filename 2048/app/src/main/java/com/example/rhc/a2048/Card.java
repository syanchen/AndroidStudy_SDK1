package com.example.rhc.a2048;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by rhc on 2018/8/16.
 */

public class Card extends FrameLayout{
    private TextView label;  //呈现的文字
    private int num = 0;     //绑定的编号
    // 设置背景色
    private int defaultBackColor = 0x338B8B00;

    public Card(Context context) {
        super(context);
        label = new TextView(getContext());   //显示下
        label.setTextSize(32);
        label.setGravity(Gravity.CENTER);
        label.setBackgroundColor(0x33ffffff);
        LayoutParams lp = new LayoutParams(-1,-1);  //创建个布局，填充满整个父局容器
        lp.setMargins(15,15,0,0);
        addView(label,lp);   //然后扔进去
        setNum(0);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        label.setBackgroundColor(getBackground(num));//不同的数字背景颜色不同
        if (this.num<= 0)
        {
            label.setText("");

        }else {
            label.setText(num + "");
        }

    }

    private int getBackground(int num) {
        int bgcolor = defaultBackColor;
        switch (num) {
            case 0:
                bgcolor = 0xffCCC0B3;
                break;
            case 2:
                bgcolor = 0xffEEE4DA;
                break;
            case 4:
                bgcolor = 0xffEDE0C8;
                break;
            case 8:
                bgcolor = 0xffF2B179;// #F2B179
                break;
            case 16:
                bgcolor = 0xffF49563;
                break;
            case 32:
                bgcolor = 0xffF5794D;
                break;
            case 64:
                bgcolor = 0xffF55D37;
                break;
            case 128:
                bgcolor = 0xffEEE863;
                break;
            case 256:
                bgcolor = 0xffEDB04D;
                break;
            case 512:
                bgcolor = 0xffECB04D;
                break;
            case 1024:
                bgcolor = 0xffEB9437;
                break;
            case 2048:
                bgcolor = 0xffEA7821;
                break;
            default:
                bgcolor = 0xffEA7821;
                break;
        }
        return bgcolor;
    }


    /**
     * 判断卡片的数字是否相同？？？？？？？？？
     * @param
     * @return
     */
    public boolean equals(Card o) {
        return getNum()==o.getNum();
    }
}