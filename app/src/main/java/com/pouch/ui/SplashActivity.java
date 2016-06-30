package com.pouch.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.pouch.R;
import com.pouch.common.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                //TODO:로그인창 하나를 디자인해서 넣어주기.
                finish();
            }

        },500);

    }
}
