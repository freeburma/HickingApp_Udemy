package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/// Getting Permission
// Ref: https://www.tutorialspoint.com/how-to-request-location-permission-at-run-time-in-android

public class LocationDemo extends AppCompatActivity
{


    LocationManager locationManager;
    LocationListener locationListener;


    /**
     * Checking the permission granded by user such as "Yes" or "No".
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            //// Updating the user location
            if ( ContextCompat.checkSelfPermission(LocationDemo.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }// end if

    }// end onRequestPermissionsResult()

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_demo);

        //// Getting the user location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //// Creating locatilon listener
        locationListener = new LocationListener()
        {

            @Override
            public void onLocationChanged (Location location)
            {
                Log.i( "Location : ", location.toString());

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + location.getLatitude() + "," + location.getLongitude() + "&daddr=55.877526, 26.533898"));
                startActivity(intent);

            }

            @Override
            public void onStatusChanged (String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled (String provider)
            {

            }

            @Override
            public void onProviderDisabled (String provider)
            {

            }
        };

        if ( ContextCompat.checkSelfPermission(LocationDemo.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LocationDemo.this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(LocationDemo.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            else
            {
//                ActivityCompat.requestPermissions(LocationDemo.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }// end if
        }




    }// end onCreate()

}