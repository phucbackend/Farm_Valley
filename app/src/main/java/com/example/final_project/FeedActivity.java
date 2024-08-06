package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

public class FeedActivity extends AppCompatActivity {
    ImageButton close, chicken1, chicken2, chicken3, chicken4, chicken5, cow1, cow2, cow3, cow4,
    cow5, pig1, pig2, pig3, pig4, pig5, sheep1, sheep2, sheep3, sheep4, sheep5;
    MediaPlayer backgroundMusic, closeSound;;
    Handler handler;
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        //
        Intent intent = getIntent() ;
        //
        addControls();
        addEventsUI();
        addEventsFeedChicken();
        addEventsFeedCow();
        addEventsFeedPig();
        addEventsFeedSheep();
        backgroundMusic = MediaPlayer.create(this, R.raw.feed_background_music);
        handler = new Handler();
        playMusic();
    }
    public void addControls()
    {
        close = (ImageButton) findViewById(R.id.btnCloseFeed);
        chicken1 = (ImageButton) findViewById(R.id.btnChicken1);
        chicken2 = (ImageButton) findViewById(R.id.btnChicken2);
        chicken3 = (ImageButton) findViewById(R.id.btnChicken3);
        chicken4 = (ImageButton) findViewById(R.id.btnChicken4);
        chicken5 = (ImageButton) findViewById(R.id.btnChicken5);
        cow1 = (ImageButton) findViewById(R.id.btnCow1);
        cow2 = (ImageButton) findViewById(R.id.btnCow2);
        cow3 = (ImageButton) findViewById(R.id.btnCow3);
        cow4 = (ImageButton) findViewById(R.id.btnCow4);
        cow5 = (ImageButton) findViewById(R.id.btnCow5);
        pig1 = (ImageButton) findViewById(R.id.btnPig1);
        pig2 = (ImageButton) findViewById(R.id.btnPig2);
        pig3 = (ImageButton) findViewById(R.id.btnPig3);
        pig4 = (ImageButton) findViewById(R.id.btnPig4);
        pig5 = (ImageButton) findViewById(R.id.btnPig5);
        sheep1 = (ImageButton) findViewById(R.id.btnSheep1);
        sheep2 = (ImageButton) findViewById(R.id.btnSheep2);
        sheep3 = (ImageButton) findViewById(R.id.btnSheep3);
        sheep4 = (ImageButton) findViewById(R.id.btnSheep4);
        sheep5 = (ImageButton) findViewById(R.id.btnSheep5);
    }
    public void addEventsUI()
    {
        closeSound = MediaPlayer.create(this, R.raw.close_sound);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSound.start();
                Intent intent = new Intent(FeedActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }
    public void addEventsFeedChicken()
    {
        chicken1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        chicken2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        chicken3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        chicken4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        chicken5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void addEventsFeedCow()
    {
        cow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        cow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        cow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        cow4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        cow5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void addEventsFeedPig()
    {
        pig1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        pig2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        pig3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        pig4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        pig5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void addEventsFeedSheep()
    {
        sheep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        sheep2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        sheep3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        sheep4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        sheep5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    // Các hàm xử lý nhạc nền bao gồm: chạy lại nhạc sau 3 giây từ khi kết thúc và giữ trạng thái nhạc khi chuyển đổi giữa các activity
    private void playMusic() {
        backgroundMusic.start();
        backgroundMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                handler.postDelayed(runnable, 1000);
            }
        });
        runnable = new Runnable() {
            @Override
            public void run() {
                if (backgroundMusic != null) {
                    backgroundMusic.seekTo(0);
                    backgroundMusic.start();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            playMusic();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
        handler.removeCallbacks(runnable);
    }
}