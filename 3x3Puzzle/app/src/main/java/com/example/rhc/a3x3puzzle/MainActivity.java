package com.example.rhc.a3x3puzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private GamePintuLayout mGamePintuLayout;
    private TextView mLevel ;
    private TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTime = (TextView) findViewById(R.id.id_time);
        mLevel = (TextView) findViewById(R.id.id_level);

        mGamePintuLayout = (GamePintuLayout) findViewById(R.id.id_gamepintu);
        // 这个这个
        mGamePintuLayout.setTimeEnabled(true);
        // 哇，这个是我们自己写的。
        mGamePintuLayout.setOnGamePintuListener(new GamePintuLayout.GamePintuListener()
        {
            @Override
            public void timechanged(int currentTime)
            {
                // 转为字符串的形式
                mTime.setText(""+currentTime);
            }

            @Override
            public void nextLevel(final int nextLevel)
            {
                // 到下一关的时候弹出dialog框让用户选择是否进行下一关。
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Game Info").setMessage("LEVEL UP !!!")
                        .setPositiveButton("NEXT LEVEL", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                mGamePintuLayout.nextLevel();
                                // 转为字符串的形式。
                                mLevel.setText(""+nextLevel);
                            }
                        }).show();
            }

            @Override
            public void gameover()
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Game Info").setMessage("Game over !!!")
                        .setPositiveButton("RESTART", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                mGamePintuLayout.restart();
                            }
                        }).setNegativeButton("QUIT",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                }).show();
            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();

        mGamePintuLayout.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mGamePintuLayout.resume();
    }

}