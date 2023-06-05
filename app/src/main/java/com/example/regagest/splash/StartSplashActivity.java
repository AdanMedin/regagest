package com.example.regagest.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.regagest.activity.LogInActivity;
import com.example.regagest.R;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("CustomSplashScreen")
public class StartSplashActivity extends AppCompatActivity {

    ImageView iconG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_splash);

        iconG = findViewById(R.id.imageViewG);

        Animation a = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotation);

        iconG.startAnimation(a);

        //Timer para el splash
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(StartSplashActivity.this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }
}