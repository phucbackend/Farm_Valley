package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
    ImageView wheat1, wheat2, wheat3, wheat4;
    FrameLayout progressFrame, noteFrame;
    ProgressBar pgbLoading;
    TextView tvLoading;
    Handler handler, progressHandler, finishHandler;
    Timer progressTimer;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        addControls();
        addEvents();
    }
    public void addControls()
    {
        pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);
        tvLoading = (TextView) findViewById(R.id.tvLoading);
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        noteFrame = (FrameLayout) findViewById(R.id.noteFrame);
        wheat1 = (ImageView) findViewById(R.id.imgWheat1);
        wheat2 = (ImageView) findViewById(R.id.imgWheat2);
        wheat3 = (ImageView) findViewById(R.id.imgWheat3);
        wheat4 = (ImageView) findViewById(R.id.imgWheat4);
    }
    public void addEvents()
    {
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.flywheat1);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.flywheat2);
        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.flywheat3);
        Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.flywheat4);
        mediaPlayer = MediaPlayer.create(StartActivity.this, R.raw.intro_background_music);
        int max = 100;
        progressHandler = new Handler();
        finishHandler = new Handler();
        progressTimer = new Timer();
        pgbLoading.setVisibility(View.INVISIBLE);
        progressFrame.setVisibility(View.INVISIBLE);
        noteFrame.setVisibility(View.INVISIBLE);
        tvLoading.setVisibility(View.INVISIBLE);
        wheat1.setVisibility(View.INVISIBLE);
        wheat2.setVisibility(View.INVISIBLE);
        wheat3.setVisibility(View.INVISIBLE);
        wheat4.setVisibility(View.INVISIBLE);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.start();
                pgbLoading.setVisibility(View.VISIBLE);
                progressFrame.setVisibility(View.VISIBLE);
                noteFrame.setVisibility(View.VISIBLE);
                tvLoading.setVisibility(View.VISIBLE);
                wheat1.setVisibility(View.VISIBLE);
                wheat2.setVisibility(View.VISIBLE);
                wheat3.setVisibility(View.VISIBLE);
                wheat4.setVisibility(View.VISIBLE);
                wheat1.startAnimation(animation1);
                wheat2.startAnimation(animation2);
                wheat3.startAnimation(animation3);
                wheat4.startAnimation(animation4);
                progressTimer.schedule(new TimerTask() {
                    int current = 0;
                    @Override
                    public void run() {
                        progressHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (current <= max) {
                                    pgbLoading.setProgress(current);
                                    tvLoading.setText(String.valueOf(current) + '%');
                                    current++;
                                } else {
                                    progressTimer.cancel();
                                    finishHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mediaPlayer.stop();
                                            Intent intent = new Intent(StartActivity.this, GameActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 2000);
                                }
                            }
                        });
                    }
                }, 0, 50);
            }
        }, 3000);
    }
}