package com.example.rhc.a3x3puzzle;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by rhc on 2018/8/17.
 * 主要过程：根据传进来的Image进行分割成图片序列后随机打乱，添加index和tag等属性，根据获取到的屏幕像素初始化每个小图片
 * 为每个图片设置点击事件，调用JDK的TranslateAnimation来实现动画效果
 * 时刻注意判断玩家的输赢，胜利到达下一关要修改图片数量以及修改时间
 */

public class GamePintuLayout extends RelativeLayout implements View.OnClickListener
{
    // 默认3*3的碎片
    private int mColumn = 3;

    // 容器的内边距
    private int mPadding;

    // 每张小图之间的距离，横纵。dp
    private int mMargin = 3;

    // 每一张小图位置上的那个view
    private ImageView[] mGamePintuItems;

    // 每一张小图的宽度。
    private int mItemWidth;

    // 游戏的图片。
    private Bitmap mBitmap;

    // 存放所有的图片碎片的list。
    private List<ImagePiece> mItemBitmaps;

    // 操作一次的标识。
    private boolean once;

    // 游戏面板的宽度，容器的宽度
    private int mWidth;

    private boolean isGameSuccess;
    private boolean isGameOver;

    // 设置接口通知MainActivity进行回调。这三个函数是在MainActivity中进行实现的。这里提供的只是一个接口。
    public interface GamePintuListener
    {
        void nextLevel(int nextLevel);

        void timechanged(int currentTime);

        void gameover();
    }

    // 接口成员变量。
    public GamePintuListener mListener;

    /**
     * 设置接口回调
     *
     * @param mListener
     */
    public void setOnGamePintuListener(GamePintuListener mListener)
    {
        this.mListener = mListener;
    }

    private int mLevel = 1;
    private static final int TIME_CHANGED = 0x110;//？？？？？？？？？这样设置的原因
    private static final int NEXT_LEVEL = 0x111;

    /*
     * 进行UI操作。
     * */
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case TIME_CHANGED:
                    // 如果游戏成功、失败、停止，停止计时。
                    if (isGameSuccess || isGameOver || isPause)
                        return;
                    if (mListener != null)
                    {
                        mListener.timechanged(mTime);
                    }
                    if (mTime == 0)
                    {
                        isGameOver = true;
                        mListener.gameover();
                        return;
                    }
                    mTime--;
                    // 每一秒发送一次减少。
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);//1000ms

                    break;
                case NEXT_LEVEL:
                    mLevel = mLevel + 1;
                    // 这个是判断用户是否选择进行下一关。
                    if (mListener != null)
                    {
                        mListener.nextLevel(mLevel);
                    } else
                    {
                        nextLevel();
                    }
                    break;

            }
        };
    };

    private boolean isTimeEnabled = false;
    private int mTime;

    /**
     * 设置是否开启时间
     *
     * @param isTimeEnabled
     */
    public void setTimeEnabled(boolean isTimeEnabled)
    {
        this.isTimeEnabled = isTimeEnabled;
    }

    /*
     * 第一个构造方法调用第二个构造方法。
     * */
    public GamePintuLayout(Context context)
    {
        this(context, null);
    }

    /*
     * 第二个构造方法调用第三个构造方法。
     * */
    public GamePintuLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    /*
     * 第三个构造方法进行初始化。
     * */
    public GamePintuLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
		/*
		 * 可以用TypedValue进行单位的转换，将dp转为px，或者将sp转为px
		 * 这样也就把我们3dp转化为3px的值。
		 * 在布局当中尽可能的所有的字体使用sp，所有的merage使用dp，一般不使用px，因为不同的屏幕上分辨率是不同的。
		 * */
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                3, getResources().getDisplayMetrics());
        // 保证上下左右边距相同的，取最小值
        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(),
                getPaddingBottom());
    }

    /*
     * 设置当前布局的大小
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 取容器宽高的最小值。
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

        // 如果once没有发生过。
        if (!once)
        {
            // 进行切图以及随机排序。
            initBitmap();
            // 设置ImageView（Item）的宽高等属性。
            initItem();
            // 判断是否开启时间。
            checkTimeEnable();

            once = true;
        }
        // 重置它占据的位置是一个正方形。
        setMeasuredDimension(mWidth, mWidth);//少了这句即使addView()但是视图也不会显示

    }

    /**
     * 判断是否开启时间。
     * */
    private void checkTimeEnable()
    {
        // 如果开启了，
        if (isTimeEnabled)
        {
            // 根据关卡设置时间长短。
            countTimeBaseLevel();
            // 通知主界面显示时间。
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }

    }

    // 根据关卡设置时间长短。
    private void countTimeBaseLevel()
    {
        // 用pow可以根据游戏难度增加时间，2的mLevel次，指数增长。
        mTime = (int) Math.pow(2, mLevel) * 60;
    }

    /**
     * 进行切图，已经排序。
     */
    private void initBitmap()
    {
        if (mBitmap == null)
        {
            // 获取Bitmap图片对象
            mBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image);
        }
        // 进行切割后返回ImagePiece的List。
        mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);

        // 使用sort乱序排序图片。重写compare方法。
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>()
        {
            @Override
            public int compare(ImagePiece a, ImagePiece b)
            {
                // 使用随机数。
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    /**
     * 设置ImageView(Item)的宽高等属性。
     */
    private void initItem()
    {
        // 获取item宽度。（容器宽度-最左右的空隙-中间图片之间的空隙）/ 一行Item的数目。
        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))
                / mColumn;
        // 这个大小是随程序的mColumn动态创建的。多个ImageView。
        mGamePintuItems = new ImageView[mColumn * mColumn];
        // 生成Item，设置Rule。
        for (int i = 0; i < mGamePintuItems.length; i++)
        {
            // 生产一个一个的ImageView。mItemBitmaps是bitmap的List
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());//item根据打乱顺序后的图片碎片列表用setImage将图片显示出来

            mGamePintuItems[i] = item;
            item.setId(i + 1);

            // 在item的tag中存储了index，index存储的是真正的顺序。
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            /**
             * 排列顺序
             * 0 1 2 3
             * 4 5 6 7
             * 8 9 10 11
             * */
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    mItemWidth, mItemWidth);//用LayoutParams来动态定义界面参数，而不是在xml文件里静态地定义

            // 不是最后一列的列设置右边距，通过rightMargin
            if ((i + 1) % mColumn != 0)
            {
                lp.rightMargin = mMargin;
            }
            // 不是第一列
            if (i % mColumn != 0)
            {
                // 就是设置它是在谁的右边排列，1 rightof 0
                lp.addRule(RelativeLayout.RIGHT_OF,
                        mGamePintuItems[i - 1].getId());
            }
            // 如果不是第一行，就要设置上边距，同时要设置它在谁的下面。
            if ((i + 1) > mColumn)
            {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW,
                        mGamePintuItems[i - mColumn].getId());
            }
            // 给这个item设置它的参数。
            addView(item, lp);
        }// for

    }

    public void restart()
    {
        isGameOver = false;
        mColumn--;
        nextLevel();
    }

    private boolean isPause ;

    public void pause()
    {
        isPause = true ;
        mHandler.removeMessages(TIME_CHANGED);
    }

    public void resume()
    {
        if(isPause)
        {
            isPause = false ;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    /**
     * 下一关要做的事。
     * */
    public void nextLevel()
    {
        // 取消当前容器中的所有view，否则下次加载新的view会提示当前view已存在
        this.removeAllViews();
        // 取消动画层
        mAnimLayout = null;
        // 图片碎片变复杂。
        mColumn++;
        isGameSuccess = false;
        checkTimeEnable();
        // 重新显示新的图片。
        initBitmap();
        initItem();
    }

    /**
     * 取多个参数的最小值。
     */
    private int min(int... params)
    {
        int min = params[0];

        for (int param : params)
        {
            if (param < min)
                min = param;
        }
        return min;
    }

    // 两个被点击的图片。
    private ImageView mFirst;
    private ImageView mSecond;

    //分别处理了3种情况：1、在点击了一张图片的前提下点击该图片两次；2、未点击任何图片的前提下点击第一张；3、已经点击了一张图片的前提下点击另一张。其中只有第3种情况需要进行两幅图片的交换
    @Override
    public void onClick(View v)
    {
        if (isAniming)
            return;

        // 如果两次点击了相同的图片，代表取消第一个点击，也就去掉点中状态。
        if (mFirst == v)//为什么这样就是将一张图片点击了两次？因为下面的if判断语句设置了mFirst为v当玩家第一次点击图片时，如果再次检测到点击动作时mFirst已不为null，表示点击了两次
        {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }
        // 如果第一张null，说明此时点的这张图片是第一张图片，否则是第二张图片。
        if (mFirst == null)
        {
            mFirst = (ImageView) v;
            // 点中后有一个被点中的状态，这里设置透明的红色。55代表透明度，后面是八位颜色。
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        } else
        {
            mSecond = (ImageView) v;
            // 交换item。
            exchangeView();
        }
    }

    /**
     * 动画层
     */
    private RelativeLayout mAnimLayout;
    // 正在动画的时候，就不让用户乱点一通。
    private boolean isAniming;

    /**
     * 交换item
     */
    private void exchangeView()
    {
        // 先去掉点中状态。
        mFirst.setColorFilter(null);
        // 准备动画层
        setUpAnimLayout();

        // 复制我们点中的图片。
        ImageView first = new ImageView(getContext());//只是根据上下文创建一个空壳而已
        // mItemBitmaps是存放所有的图片碎片的list，它是被乱序排过的。
        final Bitmap firstBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        // 要看看是不是RelativeLayout的LayoutParam，或者就是直接写出Relative前缀来。
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        // 减去mPadding是因为，对于底下原本的那层，它的左边是有边距的，
        // 而对于上面的动画层，它的整个大小是没有覆盖下面的边距的，这样它里面的元素也就不需要考虑下面的它说拥有的padding
        lp.leftMargin = mFirst.getLeft() - mPadding;/////////////////////////////////////?????????????????????????????
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        // 加到动画层中。
        mAnimLayout.addView(first);


        //第二张图的处理与第一张差不多

        // 复制我们点中的第二张图片。
        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);



        // 设置动画用这个TranslateAnimation。
//        具体参数表示：2.public　　TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta)
//　　这个是我们最常用的一个构造方法，
//　　float fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
//　　float toXDelta, 这个参数表示动画结束的点离当前View X坐标上的差值；
//　　float fromYDelta, 这个参数表示动画开始的点离当前View Y坐标上的差值；
//　　float toYDelta)这个参数表示动画开始的点离当前View Y坐标上的差值；
//　　如果view在A(x,y)点 那么动画就是从B点(x+fromXDelta, y+fromYDelta)点移动到C 点(x+toXDelta,y+toYDelta)点.
        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft()
                - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        // 动画时间
        anim.setDuration(300);
        // 这个很关键。
        anim.setFillAfter(true);
        // 启动动画
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0,
                -mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop()
                + mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);

        // 监听动画
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                // 先把下面的那两个隐藏。
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);

                isAniming = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {

                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                // 交换bitmap和tag
                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                // 结束的时候再把下面的两个图片显示出来。
                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                // 最后把动画层的东西都消掉
                mAnimLayout.removeAllViews();
                // 判断用户游戏是否成功
                checkSuccess();
                isAniming = false;
            }
        });

    }

    /**
     * 判断用户游戏是否成功
     */
    private void checkSuccess()
    {
        boolean isSuccess = true;

        for (int i = 0; i < mGamePintuItems.length; i++)
        {
            ImageView imageView = mGamePintuItems[i];
            if (getImageIndexByTag((String) imageView.getTag()) != i)
            {
                isSuccess = false;
            }
        }

        if (isSuccess)
        {
            // 当前一次游戏结束以后，把上一次的Handler动作取消掉。
            isGameSuccess = true;
            mHandler.removeMessages(TIME_CHANGED);

            Toast.makeText(getContext(), "Success，level up !!!",
                    Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }

    }

    /**
     * 通过tag获取image的id，也就是当初设置的乱排序以后对应的i值。
     * tag中包含i和index，split[0]就是i。
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag)
    {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    /**
     * 根据tag获取index。
     * */
    public int getImageIndexByTag(String tag)
    {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 构造动画层。
     */
    private void setUpAnimLayout()
    {
        if (mAnimLayout == null)
        {
            mAnimLayout = new RelativeLayout(getContext());
            // 加到面板之中。
            addView(mAnimLayout);
        }
    }

}