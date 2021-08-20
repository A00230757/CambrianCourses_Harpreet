package com.example.cambriancourses_A00230757;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//to make activity ful screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//4 seconds sleep in splash screen , after that opens main interface
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(getApplicationContext(),MainInterface.class));
            }
        }).start();



    }
}
