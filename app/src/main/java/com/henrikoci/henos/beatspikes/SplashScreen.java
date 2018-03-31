package com.henrikoci.henos.beatspikes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

public class SplashScreen extends Activity {

    @Override
    // onCreate is created by default when creating a new activity, this sets the current view to its resepective activity
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState); // create a new instance of 'super'
        setContentView(R.layout.splash); // sets content to the splash activity
        //end of auto-generated method by creating activity, the view is set to the splash activity

        // timerThread below will run for 3 seconds to delay the activity of the MainActivity from starting
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000); // waits 3 seconds
                }catch(InterruptedException e){
                    e.printStackTrace(); // logs if the thread has been killed due to a operation that kills the process
                }finally{
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class); //Sets the intent variable of the MainActivity after the 3 second sleep
                    startActivity(intent); // launches the intent
                }
            }
        };
        timerThread.start(); // starts the timed delay of the splash screen
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}