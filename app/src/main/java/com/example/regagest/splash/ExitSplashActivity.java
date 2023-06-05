package com.example.regagest.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.regagest.R;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("CustomSplashScreen")
public class ExitSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_splash);

        //Timer para el splash
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
                System.exit(0);
            }
        }, 3000);
    }
}