package com.example.projectdraft3;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Level_3 extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_3);

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/treasure.ttf");

        ImageView middle = findViewById(R.id.middle_bi);
        ImageView right = findViewById(R.id.right_bi);
        ImageView left = findViewById(R.id.left_bi);

        ObjectAnimator middleOff = ObjectAnimator.ofFloat(middle, "alpha", 1, 0);
        middleOff.setDuration(1000);
        ObjectAnimator middleOn = ObjectAnimator.ofFloat(middle, "alpha", 0, 1);
        middleOn.setDuration(1000);
        ObjectAnimator rightOff = ObjectAnimator.ofFloat(right, "alpha", 1, 0);
        rightOff.setDuration(1000);
        ObjectAnimator rightOn = ObjectAnimator.ofFloat(right, "alpha", 0, 1);
        rightOn.setDuration(1000);
        ObjectAnimator leftOff = ObjectAnimator.ofFloat(left, "alpha", 1, 0);
        leftOff.setDuration(1000);
        ObjectAnimator leftOn = ObjectAnimator.ofFloat(left, "alpha", 0, 1);
        leftOn.setDuration(1000);
        final AnimatorSet backn = new AnimatorSet();
        final AnimatorSet forthn = new AnimatorSet();
        final AnimatorSet nback = new AnimatorSet();
        final AnimatorSet nforth = new AnimatorSet();
        backn.play(leftOff).with(middleOn);
        forthn.play(middleOff).with(rightOn);
        nforth.play(rightOff).with(middleOn);
        nback.play(middleOff).with(leftOn);
        backn.addListener(new AnimatorListenerAdapter() {

            public void onAnimationEnd(Animator animation) {
                forthn.start();
            }
        });
        forthn.addListener(new AnimatorListenerAdapter() {

            public void onAnimationEnd(Animator animation) {
                nforth.start();
            }
        });
        nforth.addListener(new AnimatorListenerAdapter() {

            public void onAnimationEnd(Animator animation) {
                nback.start();
            }
        });
        nback.addListener(new AnimatorListenerAdapter() {

            public void onAnimationEnd(Animator animation) {
                backn.start();
            }
        });
        backn.start();

        Button back = findViewById(R.id.go_back);
        back.setTypeface(font);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Level_3.this, LevelSelect.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        believe.cht.fadeintextview.TextView text = findViewById(R.id.sadly);
        text.setLetterDuration(60);
        text.setTypeface(font);
        text.setText("We are searching for the next scavenger map... come back later matey");
    }

    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if(mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
