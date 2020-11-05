package com.example.map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void GoToMap_1_Demo (View view)
    {

        Intent intent = new Intent(this, MapDemo_GoogleMap.class);
        startActivity(intent);

    }// end GoToMap_1_Demo()

    public void GoToLocationDemo (View view)
    {
        Intent intent = new Intent(this, LocationDemo.class);
        startActivity(intent);
    }

    public void GoToTrackUserLocationDemo (View view)
    {
        Intent intent = new Intent(this, TrackUserLocation.class);
        startActivity(intent);
    }

    public void GoToHickingApp (View view)
    {
        Intent intent = new Intent(this, HickingApp.class);
        startActivity(intent);
    }

}