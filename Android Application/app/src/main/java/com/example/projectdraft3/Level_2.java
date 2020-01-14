package com.example.projectdraft3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Level_2 extends AppCompatActivity {

    private WebView webView = null;
    int minutes = 0;
    int seconds = 0;
    boolean notCompleted = true;
    boolean putInBase = false;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference level_two = root.child("LevelTwo");
    String names;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_1);

        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final SharedPreferences sharedPrefs = getSharedPreferences("gameData", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        final Gson gson = new Gson();

        final Button quit = findViewById(R.id.quit1);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Level_2.this, LevelSelect.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        final ImageView image = findViewById(R.id.result_img);

        final int[] computing_index = {0};
        final String[] queses = {"Im about as swedish as they come just like the flag im blue and yellow " +
                "And all over the world im know for my furniture. What am I?",
                "The front page of everything people use me to talk about absolutely everything News movies games " +
                        "and everything in between in the end I am a friendly robot. What am I?",
                "Im a green alien robot creature that is used on millions of devices on this planet " +
                        "Everybody likes me and I like to bite apples. What am I?",
                "I am the biggest movie database on the internet Rating actors upcoming movies its all available " +
                        "in black and yellow black and yellow. What am I?"};

        final String[] urls = {"https://www.ikea.com/",
                "https://www.reddit.com/",
                "https://www.android.com/",
                "https://m.imdb.com/"};

        final TextView time = findViewById(R.id.time1);
        final TextView congrats = findViewById(R.id.highscore);
        final EditText name = findViewById(R.id.nameInput);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/treasure.ttf");
        time.setTypeface(font);
        quit.setTypeface(font);
        congrats.setTypeface(font);
        name.setTypeface(font);

        final Button levelSelect = findViewById(R.id.to_level_select);
        levelSelect.setTypeface(font);
        levelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Level_2.this, LevelSelect.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        final Button results = findViewById(R.id.result_button);
        results.setTypeface(font);
        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notCompleted) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else {
                    finish();
                    startActivity(new Intent(Level_2.this, Menu.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

        name.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String input = "";
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    input = name.getText().toString();
                    if(putInBase) {
                        if (names.contains("First")) {
                            if (names.contains("none")) {
                                level_two.child("First").child("Name").setValue(input);
                            } else {
                                level_two.child("First").child("Name").setValue(names.split(" ", 2)[1] + input);
                            }
                        }
                        else if (names.contains("Second")) {
                            if (names.contains("none")) {
                                level_two.child("Second").child("Name").setValue(input);
                            } else {
                                level_two.child("Second").child("Name").setValue(names.split(" ", 2)[1] + input);
                            }
                        }
                        else if (names.contains("Third")) {
                            if (names.contains("none")) {
                                level_two.child("Third").child("Name").setValue(input);
                            } else {
                                level_two.child("Third").child("Name").setValue(names.split(" ", 2)[1] + input);
                            }
                        }
                    }
                    levelSelect.setEnabled(true);
                    results.setEnabled(true);
                    name.setEnabled(false);
                    results.animate().alpha(1.0f).setDuration(1500).start();
                    levelSelect.animate().alpha(1.0f).setDuration(1500).start();
                    congrats.animate().alpha(0.0f).setDuration(1500).start();
                    name.animate().alpha(0.0f).setDuration(1500).start();
                }
                return true;
            }
        });

        final CircularProgressBar progressBar = findViewById(R.id.progress1);
        progressBar.setProgress(0);
        progressBar.setProgressMax(4);

        final believe.cht.fadeintextview.TextView quiz = findViewById(R.id.lvl1inst);
        quiz.setTypeface(font);
        quiz.setLetterDuration(30);
        quiz.setText(queses[computing_index[0]]);
        final TextView result = findViewById(R.id.result);
        result.setTypeface(font);

        final Timer T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        @SuppressLint("DefaultLocale") String timer = String.format("%02d : %02d", minutes, seconds);
                        time.setText(timer);
                        if (++seconds % 60 == 0) {
                            minutes++;
                            seconds = 0;
                            if (minutes == 5) {
                                timer = String.format("%02d : %02d", minutes, seconds);
                                time.setText(timer);
                                levelSelect.animate().alpha(1.0f).setDuration(1500).start();
                                result.setText("You Lose...");
                                result.animate().alpha(1.0f).setDuration(1500).start();
                                quit.setEnabled(false);
                                time.animate().alpha(0.6f).setDuration(1500).start();
                                progressBar.animate().alpha(0.6f).setDuration(1500).start();
                                quit.animate().alpha(0.0f).setDuration(1500).start();
                                webView.animate().alpha(0.0f).setDuration(1500).start();
                                quiz.animate().alpha(0.0f).setDuration(1500).start();
                                image.setBackgroundResource(R.drawable.loosing);
                                image.animate().alpha(1.0f).setDuration(1000).start();
                                levelSelect.setEnabled(true);
                                results.setEnabled(true);
                                results.animate().alpha(1.0f).setDuration(1500).start();
                                results.setText("Retry?");
                                T.cancel();
                                if (!sharedPrefs.contains("Level Two Score")) {
                                    int score = 0;
                                    score += 60 * minutes * 1.5;
                                    score += seconds;
                                    editor.putInt("Level Two Score", 500 - score);
                                    level_two.child("Tmp").setValue(500 - score);
                                }
                                editor.apply();
                            }
                        }
                    }
                });
            }
        }, 1000, 1000);

        webView = findViewById(R.id.web);
        android.webkit.CookieManager.getInstance().removeAllCookie();
        WebStorage.getInstance().deleteAllData();
        WebSettings webSettings = webView.getSettings();
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setSaveFormData(false);
        webSettings.setSupportMultipleWindows(true);
        webView.loadUrl("https://www.google.com");
        webView.setWebViewClient(new WebViewClient() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                String urlData = view.getUrl();
                if (urlData.equals(urls[computing_index[0]])) {
                    computing_index[0]++;
                    if (computing_index[0] == 4) {
                        notCompleted = false;
                        progressBar.setProgress(computing_index[0]);
                        image.setBackgroundResource(R.drawable.winning);
                        image.animate().alpha(1.0f).setDuration(1000).start();
                        String unlockedL = sharedPrefs.getString("unlocked", "");
                        ArrayList<Boolean> unlocked = gson.fromJson(unlockedL, new TypeToken<List<Boolean>>() {
                        }.getType());
                        String completedL = sharedPrefs.getString("completedLevels", "");
                        ArrayList<Boolean> completed = gson.fromJson(completedL, new TypeToken<List<Boolean>>() {
                        }.getType());
                        assert unlocked != null;
                        unlocked.set(2, true);
                        assert completed != null;
                        completed.set(1, true);
                        String completedLevels = gson.toJson(completed);
                        String unlockedLevels = gson.toJson(unlocked);
                        editor.putString("completedLevels", completedLevels);
                        editor.putString("unlocked", unlockedLevels);
                        if (!sharedPrefs.contains("Level Two Score")) {
                            int score = 0;
                            score += 60 * minutes * 1.5;
                            score += seconds;
                            editor.putInt("Level Two Score", 500 - score);
                            level_two.child("Tmp").setValue(500 - score);
                        }
                        editor.apply();
                        webView.stopLoading();
                        T.cancel();
                        result.setText("You Win!!!");
                        result.animate().alpha(1.0f).setDuration(1500).start();
                        quit.setEnabled(false);
                        time.animate().alpha(0.6f).setDuration(1500).start();
                        progressBar.animate().alpha(0.6f).setDuration(1500).start();
                        quit.animate().alpha(0.0f).setDuration(1500).start();
                        webView.animate().alpha(0.0f).setDuration(1500).start();
                        quiz.animate().alpha(0.0f).setDuration(1500).start();
                        levelSelect.animate().alpha(1.0f).setDuration(1500).start();
                        levelSelect.setEnabled(true);
                        results.setEnabled(true);
                        results.animate().alpha(1.0f).setDuration(1500).start();
                        results.setText("To Main Menu");
                    } else {
                        quiz.setText(queses[computing_index[0]]);
                        webView.loadUrl("https://www.google.com");
                        progressBar.setProgress(computing_index[0]);
                    }
                }
            }
        });

        level_two.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int first = dataSnapshot.child("First").child("Value").getValue(int.class);
                int second = dataSnapshot.child("Second").child("Value").getValue(int.class);
                int third = dataSnapshot.child("Third").child("Value").getValue(int.class);
                int tmp = dataSnapshot.child("Tmp").getValue(int.class);
                if (tmp == third && !putInBase) {
                    putInBase = true;
                    names = dataSnapshot.child("Third").child("Name").getValue(String.class);
                    alerT("third",true, tmp);
                    level_two.child("Tmp").setValue(-1);
                }
                else if (third < tmp && tmp <  second && !putInBase) {
                    putInBase = true;
                    names = dataSnapshot.child("Third").child("Name").getValue(String.class);
                    level_two.child("Third").child("Value").setValue(tmp);
                    alerT("third",false, tmp);
                    level_two.child("Tmp").setValue(-1);
                }
                else if (tmp == second && !putInBase) {
                    putInBase = true;
                    names = dataSnapshot.child("Second").child("Name").getValue(String.class);
                    alerT("second",true, tmp);
                    level_two.child("Tmp").setValue(-1);
                }
                else if (second < tmp && tmp < first && !putInBase) {
                    putInBase = true;
                    names = dataSnapshot.child("Second").child("Name").getValue(String.class);
                    level_two.child("Second").child("Value").setValue(tmp);
                    alerT("second",false, tmp);
                    level_two.child("Tmp").setValue(-1);
                }
                else if (tmp == first && !putInBase) {
                    putInBase = true;
                    names = dataSnapshot.child("First").child("Name").getValue(String.class);
                    alerT("first",true, tmp);
                    level_two.child("Tmp").setValue(-1);
                }
                else if (first < tmp && !putInBase) {
                    putInBase = true;
                    names = dataSnapshot.child("First").child("Name").getValue(String.class);
                    level_two.child("First").child("Value").setValue(tmp);
                    alerT("first",false, tmp);
                    level_two.child("Tmp").setValue(-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

    @SuppressLint("SetTextI18n")
    public void alerT(String position, boolean tie, int score) {
        if(seconds > 0) {
            final Button levelSelect = findViewById(R.id.to_level_select);
            final Button results = findViewById(R.id.result_button);
            final TextView congrats = findViewById(R.id.highscore);
            final EditText name = findViewById(R.id.nameInput);
            levelSelect.setEnabled(false);
            results.setEnabled(false);
            name.setEnabled(true);
            results.animate().alpha(0.0f).setDuration(1500).start();
            levelSelect.animate().alpha(0.0f).setDuration(1500).start();
            congrats.animate().alpha(1.0f).setDuration(1500).start();
            name.animate().alpha(1.0f).setDuration(1500).start();
            switch(position){
                case "first":
                    if(tie){
                        congrats.setText("Congratulations, your score of " + score + " is tied for first place.\nPlease input your name below");
                        names = "First " + names;
                    }else{
                        congrats.setText("Congratulations, your score of " + score + " is the best in the world!!!.\nPlease input your name below");
                        names = "First none";
                    }
                    break;
                case "second":
                    if(tie){
                        congrats.setText("Congratulations, your score of " + score + " is tied for second place.\nPlease input your name below");
                        names = "Second " + names;
                    }else{
                        congrats.setText("Congratulations, your score of " + score + " is good enough for second place.\nPlease input your name below");
                        names = "Second none";
                    }
                    break;
                case "third":
                    if(tie){
                        congrats.setText("Congratulations, your score of " + score + " is tied for third place.\nPlease input your name below");
                        names = "Third " + names;
                    }else{
                        congrats.setText("Congratulations, your score of " + score + " just snuck up onto the leader board in third.\nPlease input your name below");
                        names = "Third none";
                    }
                    break;
            }
        }

    }

    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
        }
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