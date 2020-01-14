package com.example.projectdraft3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import believe.cht.fadeintextview.TextViewListener;

public class Introduction2 extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        final String[] instructions = {"Welcome to the scavenger hunt pirate",
                "Are you ready for a bit of an adventure",
                "Here's how this game is played",
                "I give you a hint",
                "And you have to find the website that im thinking of",
                "Just search in the in game browser until you find the correct website",
                "Your overall level progress will be displayed in the bottom right",
                "And your time to the left of that",
                "After completing all of the objectives you beat the level",
                "Your time will be tracked the first time you play a level",
                "But you only have 5 minutes to solve a level",
                "Your first time score will be displayed in the high scores tab with global rankings",
                "Sounds simple doesn't it",
                "But it can get a bit complicated so don't get to comfortable",
                "Anyway I wish you the best of luck OFF YOU GO"};
        final int[] showing_index = {0};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction2);

        TextView background = findViewById(R.id.background);
        background.animate().alpha(1.0f).setDuration(8000).start();
        ImageView sunset = findViewById(R.id.sunset);
        sunset.animate().alpha(0.5f).setDuration(10000).start();
        final TextView menu = findViewById(R.id.next_menu);

        final Button next_text = findViewById(R.id.continue_text);
        next_text.setEnabled(false);

        View head = findViewById(R.id.pirate_head);
        Animation head_move = AnimationUtils.loadAnimation(Introduction2.this, R.anim.pirate_anim);
        head.startAnimation(head_move);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/treasure.ttf");
        final believe.cht.fadeintextview.TextView intro = findViewById(R.id.intro);
        intro.setTypeface(font);
        intro.setLetterDuration(25);
        intro.setText(instructions[showing_index[0]]);
        menu.setTypeface(font);

        final Animation shake = AnimationUtils.loadAnimation(Introduction2.this, R.anim.shake);
        next_text.startAnimation(shake);
        menu.startAnimation(shake);
        next_text.animate().alpha(0.5f).setDuration(1000).start();

        next_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showing_index[0] == 14) {
                    startActivity(new Intent(Introduction2.this, Menu.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                showing_index[0]++;
                next_text.setEnabled(false);
                next_text.setAlpha(0.5f);
                intro.setText(instructions[showing_index[0]]);
            }
        });
        intro.setListener(new TextViewListener() {
            @Override
            public void onTextStart() { }
            @Override
            public void onTextFinish() {
                next_text.setEnabled(true);
                next_text.setAlpha(1f);
                if (showing_index[0] == 14) {
                    menu.animate().alpha(1.0f).setDuration(1000).start();
                }
            }
        });
    }
    @Override
    public void onBackPressed() { }

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

    void doUnbindService()
    {
        if(mIsBound)
        {
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
}
