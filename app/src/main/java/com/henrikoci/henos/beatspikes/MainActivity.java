package com.henrikoci.henos.beatspikes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.henrikoci.henos.beatspikes.gameInternals.GameCanvasView;
import com.henrikoci.henos.beatspikes.gameSoundVisualisation.gameActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    // onCreate is created by default when creating a new activity, this sets the current view to its respective activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // create a new instance of 'super'
        setContentView(R.layout.activity_main); // sets content to the splash activity
    }
    //end of auto-generated method by creating activity, the view is set to the splash activity

    // When the onclick of the playButton has been called, this method is called below
    public void gotoPlayGame(View view) {
        Intent intent = new Intent(this, gameActivity.class); // Sets a new intent variable to launch the gameActivity class
        startActivity(intent); // Launches the activity of the intent described above
    }

    // When the onclick of the characterButton has been called, this method is called below
    public void gotoCharacterDesign(View view) {
        Intent intent = new Intent(this, characterDesign.class); // Sets a new intent variable to launch the characterDesign class
        startActivity(intent); // Launches the activity of the intent described above
    }

    // When the onclick of the settingsButton has been called, this method is called below
    public void gotoSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class); // Sets a new intent variable to launch the SettingsActivity class
        startActivity(intent); // Launches the activity of the intent described above
    }

    public void gotoDev(View view) {
        Intent intent = new Intent(this, GameCanvasView.class); // Sets a new intent variable to launch the SettingsActivity class
        startActivity(intent); // Launches the activity of the intent described above
    }

    // When the onclick of the quitButton has been called, this method is called below
    public void gotoQuit(View view) {
        this.finish(); // The application will exit using the finish call which effectively destroys the current activity
    }

}
