package com.pouch.ui;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.pouch.R;


public class IntroActivity extends AppCompatActivity {
    AnimationDrawable rocketAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler handler = new Handler();

        /**
         * 2초뒤에 MainActivity가 그려지도록 이벤트 발생.
         */
        ImageView rocketImg = (ImageView) findViewById(R.id.rocket_image);
        rocketImg.setBackgroundResource(R.drawable.rocket);

        rocketAnimation = (AnimationDrawable) rocketImg.getBackground();
        rocketAnimation.start();

        

        handler.postDelayed(new Runnable(){
            public void run(){

                Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                startActivity(intent);
                /** 뒤로가기 버튼을 눌렀을때 메모리에는 IntroActivity가 살아있으므로
                 * finish를 호출해준다.
                 */
                finish();


            }

        },6000);

    }
}
