package com.example.projectdraft3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Highscores extends AppCompatActivity {

    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    @SuppressLint({"ResourceAsColor", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        final Typeface font = Typeface.createFromAsset(getAssets(), "fonts/treasure.ttf");

        final SharedPreferences sharedPrefs = getSharedPreferences("gameData", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        final Gson gson = new Gson();

        final int[] i = {0};

        final Button global = findViewById(R.id.global);
        final Button local = findViewById(R.id.local);
        TextView title = findViewById(R.id.titl);
        global.setTypeface(font);
        local.setTypeface(font);
        title.setTypeface(font);

        final LinearLayout localList = findViewById(R.id.ScoresL);
        final LinearLayout globalList = findViewById(R.id.ScoresG);

        final String[] levels = {"Level One Score", "Level Two Score"};

        for (int j = levels.length - 1; j >= 0; j--){
            int score = sharedPrefs.getInt(levels[j], 0);

            TextView newL = new TextView(this);
            newL.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            newL.setId(i[0]++);
            newL.setTypeface(font); newL.setTextSize(26); newL.setTextColor(Color.BLACK);
            newL.setBackgroundResource(R.drawable.border_high);
            newL.setPadding(12,12,12,12);
            newL.setText(String.format("Your %s : %d", levels[j], score));
            localList.addView(newL, 0);
        }

        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.sL).animate().alpha(0.0f).setDuration(800).start();
                findViewById(R.id.sG).animate().alpha(1.0f).setDuration(800).start();
                global.setEnabled(false);
                local.setEnabled(true);
            }
        });

        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.sG).animate().alpha(0.0f).setDuration(800).start();
                findViewById(R.id.sL).animate().alpha(1.0f).setDuration(800).start();
                local.setEnabled(false);
                global.setEnabled(true);
            }
        });

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    TextView newL = new TextView(getApplicationContext());
                    newL.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    newL.setId(i[0]++);
                    newL.setTypeface(font); newL.setTextSize(22); newL.setTextColor(Color.BLACK);
                    newL.setBackgroundResource(R.drawable.border_high);
                    newL.setPadding(12,12,12,12);
                    newL.setText("The high scores for "+ key +":\r\n");

                    String names = snapshot.child("First").child("Name").getValue(String.class);
                    int score = snapshot.child("First").child("Value").getValue(Integer.class);
                    newL.append("1. " + score +" owned by " + names +"\r\n");
                    names = snapshot.child("Second").child("Name").getValue(String.class);
                    score = snapshot.child("Second").child("Value").getValue(Integer.class);
                    newL.append("2. " + score +" owned by " + names+"\r\n");
                    names = snapshot.child("Third").child("Name").getValue(String.class);
                    score = snapshot.child("Third").child("Value").getValue(Integer.class);
                    newL.append("3. " + score +" owned by " + names);

                    globalList.addView(newL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        for(int k = globalList.getChildCount()-1 ; k >= 0 ; k--)
        {
            View item = globalList.getChildAt(k);
            globalList.removeViewAt(k);
            globalList.addView(item);
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

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Highscores.this, Menu.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
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
