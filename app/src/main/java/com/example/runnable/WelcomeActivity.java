package com.example.runnable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;


public class WelcomeActivity extends AppCompatActivity {


    //ET - create variable "SPLASH_TIME_OUT" to display and set it for 3 seconds
    private static int SPLASH_TIME_OUT = 3000;

    //ET - Intent loginIntent used to start the login screen, named 'MainActivity'
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent loginIntent = new Intent(WelcomeActivity.this, VideoActivity.class);
                startActivity(loginIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}