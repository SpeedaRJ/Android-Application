package com.example.projectdraft3;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        SharedPreferences sharedPrefs = getSharedPreferences("gameData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String completedLevels = gson.toJson(new ArrayList<>(Arrays.asList(false, false, false, false, false, false)));
        String unlocked = gson.toJson(new ArrayList<>(Arrays.asList(true, false, false, false, false, false)));
        String level_ids = gson.toJson(new ArrayList<>(Arrays.asList("level1", "level2", "level3", "level4", "level5", "level6")));
        String level_act = gson.toJson(new ArrayList<>(Arrays.asList("com.example.projectdraft3.Level_1","com.example.projectdraft3.Level_2", "com.example.projectdraft3.Level_3",
                "com.example.projectdraft3.Level_4", "com.example.projectdraft3.Level_5", "com.example.projectdraft3.Level_6")));

        if(!sharedPrefs.contains("completedLevels"))
            editor.putString("completedLevels", completedLevels);
        if(!sharedPrefs.contains("unlocked"))
            editor.putString("unlocked" ,unlocked);
        if(!sharedPrefs.contains("level_ids"))
            editor.putString("level_ids", level_ids);
        if(!sharedPrefs.contains("level_activities"))
            editor.putString("level_activities", level_act);
        editor.apply();

        ImageView ship = findViewById(R.id.ship);
        ObjectAnimator scale_ship_x = ObjectAnimator.ofFloat(ship, "scaleX", 1, 1.15f);
        scale_ship_x.setDuration(3000);
        ObjectAnimator scale_ship_rx = ObjectAnimator.ofFloat(ship, "scaleX", 1.15f, 1);
        scale_ship_rx.setDuration(3000);
        ObjectAnimator scale_ship_y = ObjectAnimator.ofFloat(ship, "scaleY", 1, 1.15f);
        scale_ship_y.setDuration(3000);
        ObjectAnimator scale_ship_ry = ObjectAnimator.ofFloat(ship, "scaleY", 1.15f, 1);
        scale_ship_ry.setDuration(3000);

        AnimatorSet s = new AnimatorSet();
        s.play(scale_ship_x).with(scale_ship_y).before(scale_ship_rx).before(scale_ship_ry);
        s.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                animation.start();
            }
        });
        s.start();

        TextView completed = findViewById(R.id.textLEVELFINISH);
        TextView all = findViewById(R.id.textLEVELCOUNT);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/treasure.ttf");
        completed.setTypeface(font);
        all.setTypeface(font);
    }

    public int getCompleted() {
        int vales = 0;
        SharedPreferences sharedPrefs = getSharedPreferences("gameData",MODE_PRIVATE);
        Gson gson = new Gson();
        String completedL = sharedPrefs.getString("completedLevels", "");
        ArrayList<Boolean> completed = gson.fromJson(completedL, new TypeToken<List<Boolean>>(){}.getType());
        assert completed != null;
        for(boolean comp : completed) if(comp) vales++;
        return vales;
    }

    public void updateScreen() {
        int completedL = getCompleted();
        ProgressBar bar = findViewById(R.id.completed);
        TextView completed = findViewById(R.id.textLEVELFINISH);
        completed.setText(String.valueOf(completedL));
        bar.getProgressDrawable().setColorFilter( Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
        bar.setMax(6);
        bar.setProgress(completedL);
    }

    public void updateButtons() {
        SharedPreferences sharedPrefs = getSharedPreferences("gameData",MODE_PRIVATE);
        Gson gson = new Gson();
        String ids = sharedPrefs.getString("level_ids", "");
        ArrayList<String> buttons = gson.fromJson(ids, new TypeToken<List<String>>(){}.getType());
        String unlockedL = sharedPrefs.getString("unlocked", "");
        ArrayList<Boolean> unlocked = gson.fromJson(unlockedL, new TypeToken<List<Boolean>>(){}.getType());
        String activitiesL = sharedPrefs.getString("level_activities", "");
        final ArrayList<String> activities = gson.fromJson(activitiesL, new TypeToken<List<String>>(){}.getType());
        assert buttons != null;
        for(int i = 0; i < buttons.size(); i++) {
            int resourceId = this.getResources().
                    getIdentifier(buttons.get(i), "id", this.getPackageName());
            Button but = findViewById(resourceId);
            assert unlocked != null;
            but.setEnabled(unlocked.get(i));
            final int finalI = i;
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        assert activities != null;
                        finish();
                        startActivity(new Intent(LevelSelect.this, Class.forName(activities.get(finalI))));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            if(unlocked.get(i)){
                but.setAlpha(1);
            }
        }

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
    public void onResume() {
        super.onResume();

        updateScreen();
        updateButtons();

        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(LevelSelect.this, Menu.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
