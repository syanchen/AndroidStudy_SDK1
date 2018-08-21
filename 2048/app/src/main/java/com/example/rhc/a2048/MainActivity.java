package com.example.rhc.a2048;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener{
    public TextView tvScrore;//计分的
    public TextView tvBestScore;//最高分
    public int score = 0;
    private int bestScores;//历史最高成绩
    private Button bt;

    private static MainActivity mainActivity = null;
    public MainActivity(){
        mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inital();

    }

    @SuppressLint("SetTextI18n")
    public void inital() {
        tvBestScore = (TextView) findViewById(R.id.maxSorce);
        tvScrore = (TextView) findViewById(R.id.tvSorce);
        bt = (Button)findViewById(R.id.bt_cx);
        bt.setOnClickListener(this);
        BestScore bestScore = new BestScore(this);
        bestScores = bestScore.getBestScore();
        tvBestScore.setText(bestScores+"");
    }

    @Override
    public void onClick(View v) {
        GameView.getGameView().startGame();
    }

    public void clearScore(){
        score = 0;
        showScore();
    }

    public void showScore(){
        tvScrore.setText(score+"");
    }

    public void addScore(int s){
        score+=s;
        showScore();
        if (score > bestScores){
            bestScores = score;
            BestScore bs = new BestScore(this);
            bs.setBestScore(bestScores);
            tvBestScore.setText(bestScores+"");
        }
    }

    /**
     * 菜单、返回键响应
     */
    private long exitTime = 0;

    @SuppressLint("WrongConstant")
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出哈",1000).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}