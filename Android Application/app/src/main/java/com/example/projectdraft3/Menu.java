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
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Menu extends AppCompatActivity {

    boolean visibleInfo = false;
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
                Scon,Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if(mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.getSharedPreferences("gameData", 0).edit().clear().apply();

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        TextView title1 = findViewById(R.id.menuTitle);
        TextView title2 = findViewById(R.id.menuTitle2);
        TextView title3 = findViewById(R.id.menuTitle3);
        TextView intro = findViewById(R.id.info);
        TextView levels = findViewById(R.id.levels);
        TextView exit = findViewById(R.id.exit);
        TextView high = findViewById(R.id.high);
        View skull = findViewById(R.id.skull);

        final Animation shake = AnimationUtils.loadAnimation(Menu.this, R.anim.shake);
        intro.startAnimation(shake);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/treasure.ttf");
        intro.setTypeface(font);
        title1.setTypeface(font);
        title2.setTypeface(font);
        title3.setTypeface(font);
        levels.setTypeface(font);
        exit.setTypeface(font);
        high.setTypeface(font);

        ObjectAnimator scale_skull_x = ObjectAnimator.ofFloat(skull, "scaleX", 1, 1.3f);
        scale_skull_x.setDuration(1500);
        ObjectAnimator scale_skull_rx = ObjectAnimator.ofFloat(skull, "scaleX", 1.3f, 1);
        scale_skull_rx.setDuration(1500);
        ObjectAnimator scale_skull_y = ObjectAnimator.ofFloat(skull, "scaleY", 1, 1.3f);
        scale_skull_y.setDuration(1500);
        ObjectAnimator scale_skull_ry = ObjectAnimator.ofFloat(skull, "scaleY", 1.3f, 1);
        scale_skull_ry.setDuration(1500);
        ObjectAnimator rotate_black = ObjectAnimator.ofFloat(title2, "rotation", -15, 15);
        rotate_black.setDuration(1500);
        ObjectAnimator scale_blackX = ObjectAnimator.ofFloat(title2, "scaleX", 1, 1.5f);
        scale_blackX.setDuration(1500);
        ObjectAnimator scale_blackY = ObjectAnimator.ofFloat(title2, "scaleY", 1, 1.5f);
        scale_blackY.setDuration(1500);
        ObjectAnimator rotate_orange = ObjectAnimator.ofFloat(title3, "rotation", 15, -15);
        rotate_orange.setDuration(1500);
        ObjectAnimator scale_orangeX = ObjectAnimator.ofFloat(title3, "scaleX", 1, 1.5f);
        scale_orangeX.setDuration(1500);
        ObjectAnimator scale_orangeY = ObjectAnimator.ofFloat(title3, "scaleY", 1, 1.5f);
        scale_orangeY.setDuration(1500);
        ObjectAnimator scale_whiteX = ObjectAnimator.ofFloat(title1, "scaleX", 1, 1.5f);
        scale_whiteX.setDuration(1500);
        ObjectAnimator scale_whiteY = ObjectAnimator.ofFloat(title1, "scaleY", 1, 1.5f);
        scale_whiteY.setDuration(1500);
        ObjectAnimator rotate_black_r = ObjectAnimator.ofFloat(title2, "rotation", 15, -15);
        rotate_black_r.setDuration(1500);
        ObjectAnimator scale_blackX_r = ObjectAnimator.ofFloat(title2, "scaleX", 1.5f, 1);
        scale_blackX_r.setDuration(1500);
        ObjectAnimator scale_blackY_r = ObjectAnimator.ofFloat(title2, "scaleY", 1.5f, 1);
        scale_blackY_r.setDuration(1500);
        ObjectAnimator rotate_orange_r = ObjectAnimator.ofFloat(title3, "rotation", -15, 15);
        rotate_orange_r.setDuration(1500);
        ObjectAnimator scale_orangeX_r = ObjectAnimator.ofFloat(title3, "scaleX", 1.5f, 1);
        scale_orangeX_r.setDuration(1500);
        ObjectAnimator scale_orangeY_r = ObjectAnimator.ofFloat(title3, "scaleY", 1.5f, 1);
        scale_orangeY_r.setDuration(1500);
        ObjectAnimator scale_whiteX_r = ObjectAnimator.ofFloat(title1, "scaleX", 1.5f, 1);
        scale_whiteX_r.setDuration(1500);
        ObjectAnimator scale_whiteY_r = ObjectAnimator.ofFloat(title1, "scaleY", 1.5f, 1);
        scale_whiteY_r.setDuration(1500);
        AnimatorSet s = new AnimatorSet();
        s.play(rotate_black).with(scale_skull_x).with(scale_skull_y).with(scale_blackX).with(scale_blackY).with(rotate_orange).with(scale_orangeX)
                .with(scale_orangeY).with(scale_whiteX).with(scale_whiteY).before(rotate_black_r).before(scale_blackX_r)
                .before(scale_blackY_r).before(rotate_orange_r).before(scale_orangeX_r).before(scale_orangeY_r)
                .before(scale_whiteX_r).before(scale_whiteY_r).before(scale_skull_rx).before(scale_skull_ry).after(0);
        s.addListener(new AnimatorListenerAdapter() {

            public void onAnimationEnd(Animator animation) {
                animation.start();
            }
        });
        s.start();

        FloatingActionButton fab = findViewById(R.id.introductionGo);
        ImageButton goLevel = findViewById(R.id.map);
        ImageButton goExit = findViewById(R.id.exitButton);
        ImageButton goHigh = findViewById(R.id.high_scores);

        goLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, LevelSelect.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        goHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, Highscores.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        goExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, Introduction2.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView intro = findViewById(R.id.info);
                if(!visibleInfo){
                    intro.animate().alpha(1.0f).setDuration(1000).start();
                    visibleInfo = !visibleInfo;
                }
                else {
                    intro.animate().alpha(0.0f).setDuration(1000).start();
                    visibleInfo = !visibleInfo;
                }
                return false;
            }
        });

        HomeWatcher mHomeWatcher;
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();
    }

   @Override
    public void onBackPressed() { }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
    }
}
