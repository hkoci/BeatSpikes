package com.henrikoci.henos.beatspikes.gameSoundVisualisation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.henrikoci.henos.beatspikes.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class visualiserView extends AppCompatActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiser_view);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new PlayerVisualiserView(this));
    }

}