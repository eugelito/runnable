package com.example.runnable;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    //JD - Declaration of time out variable (type private static int) in order to indicate how long the splash screen will display - set to 20 seconds
    //private static int SPLASH_TIME_OUT = 20000;

    //JD - Declaration of VideoView variable to enable the display of the mp4 file
    private VideoView mVideoView;
    private Button buttonLogin, buttonRegister;

    @Override

    //JD - onCreate method used to load the display by reading the layout file
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //JD - Intent to redirect the user to the login screen once the splash has timed out (after 20 seconds)
     /*   new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent loginIntent = new Intent(VideoActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
*/
        buttonLogin = (Button) findViewById(R.id.loginBtn);
        buttonRegister = (Button) findViewById(R.id.registerBtn);

        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        //JD - Loading of the videoView by reading the id
        mVideoView = (VideoView) findViewById(R.id.videoView);

        //JD - Creation of URI object to locate the run video to be displayed by reading the id from the raw folder contained within resources
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "//" + R.raw.run);

        //JD - Casting of the videoView to the url variable
        mVideoView.setVideoURI(uri);

        //JD - starting the video
        mVideoView.start();

        //JD -  Use of onPreparedListener to make the video replay (loop) once it has ended
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    private void register(){
        startActivity(new Intent (this, RegisterActivity.class));
    }

    private void login(){
        startActivity(new Intent (this, LoginActivity.class));
    }
    @Override
    public void onClick(View view) {
        //ET - Call the userLogin method when buttonLogin is clicked
        if(view == buttonLogin){
            login();
        }
        //ET - Call the register method when buttonRegister is clicked
        if(view == buttonRegister){
            register();
        }
    }
}

