package com.example.rhc.a2048;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rhc on 2018/8/16.
 */

public class BestScore {

    private SharedPreferences s;
    BestScore(Context context){
        s = context.getSharedPreferences("bestscode",context.MODE_PRIVATE);

    }

    public int getBestScore(){
        int bestscode = s.getInt("bestscode",0);
        return bestscode;
    }
    public void setBestScore(int bestScode){
        SharedPreferences.Editor editor = s.edit();
        editor.putInt("bestscode",bestScode);
        editor.commit();
    }

}
