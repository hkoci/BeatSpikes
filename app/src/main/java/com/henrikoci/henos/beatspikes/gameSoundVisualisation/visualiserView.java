package com.henrikoci.henos.beatspikes.gameSoundVisualisation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
