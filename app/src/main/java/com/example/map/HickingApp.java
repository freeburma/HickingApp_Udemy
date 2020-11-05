package com.example.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class HickingApp extends AppCompatActivity
{

    //// Ref: https://www.youtube.com/watch?v=4eWoXPSpA5Y
    //// Background Image Credit: https://pixabay.com/photos/milky-way-galaxy-stars-night-sky-4416194/

    private String TAG = "";
    LocationManager locationManager;
    LocationListener locationListener;

    int LOCATION_REQUEST_CODE = 10001;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    TextView latitudeTextView;
    TextView longitudeTextView;
    TextView accuracyTextView;
    TextView altitudeTextView;
    TextView addressTextView;

    LocationCallback locationCallback = new LocationCallback()
    {

        @Override
        public void onLocationResult (LocationResult locationResult)
        {

            if (locationResult == null)
            {
                return;
            }// end if

            for (Location location : locationResult.getLocations())
            {
                TAG = "locationCallback";
                Log.d(TAG, "onLocationResult: " + location.toString());

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try
                {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    //// Formatting the Float 4 decimal places
                    DecimalFormat df = new DecimalFormat("#.####");

                    if (listAddresses != null && listAddresses.size() > 0)
                    {
                        if (listAddresses.get(0).hasLatitude())
                        {
                            latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
                            latitudeTextView.setText( "Latitude : " + df.format(listAddresses.get(0).getLatitude()));

                        }// end if

                        if (listAddresses.get(0).hasLongitude())
                        {
                            longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
                            longitudeTextView.setText( "Longitude : " + df.format(listAddresses.get(0).getLongitude()));

                        }// end if


                        if (location.hasAccuracy())
                        {
                            accuracyTextView = (TextView) findViewById(R.id.accuracyTextView);
                            accuracyTextView.setText( "Accuracy : " + df.format(location.getAccuracy()));

                        }// end if

                        if (location.hasAltitude())
                        {
                            altitudeTextView = (TextView) findViewById(R.id.altitudeTextView);
                            altitudeTextView.setText( "Altitude : " + df.format(location.getAltitude()));

                        }// end if

                        if (listAddresses.get(0).getSubThoroughfare() != null)
                        {
                            addressTextView = (TextView) findViewById(R.id.addressTextView);
                            addressTextView.setText( "Address : \r\n" + listAddresses.get(0).getAddressLine(0));

                        }// end if


                    }// end if
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }// end for

        }// end onLocationResult()
    };


    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        //        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        //        {
        //
        //            startListening();
        //
        //        }// end if

        if (requestCode == LOCATION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //// Permission Granted
                checkSettingsAndStartLocationUpdates();
            }
            else
            {
                //// Permission not granted

            }// end if

        }// end if


    }// end onRequestPermissionsResult()

    public void startListening ( )
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            TAG = "StartListening";
            Log.i(TAG, "startListening");

        }// end if

    }// end startListening()

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hicking_app);

        TAG = "OnCreate";
        Log.i(TAG, TAG);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }// end onCreate()

    private void checkSettingsAndStartLocationUpdates ( )
    {

        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>()
        {

            @Override
            public void onSuccess (LocationSettingsResponse locationSettingsResponse)
            {
                /// Settings of device are satisfied and we can start location update
                startLocationUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener()
        {

            @Override
            public void onFailure (@NonNull Exception e)
            {

                if (e instanceof ResolvableApiException)
                {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try
                    {
                        apiException.startResolutionForResult(HickingApp.this, 1001);
                    }
                    catch (IntentSender.SendIntentException sendIntentException)
                    {
                        sendIntentException.printStackTrace();
                    }// end cath

                }// end if

            }// end onFailure()

        });

    }// end checkSettingsAndStartLocationUpdates()

    private void startLocationUpdates ( )
    {
        // locationCallback : are declared as property
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }// end if

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }// end startLocationUpdates()

    private void stopLocationUpdates()
    {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }// end stopLocationUpdates()


    @Override
    protected void onStart ( )
    {

        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
//            getLastLocation();
            checkSettingsAndStartLocationUpdates();
        }
        else
        {
            askLocationPermission();
        }// end if

    }// end onStart()

    @Override
    protected void onStop ( )
    {
        super.onStop();

        stopLocationUpdates();

    }// end onStop ( )

    private void askLocationPermission ( )
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                TAG = "askLocationPermission";
                Log.d(TAG, "askLocationPermission: alert diaglog.");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }// end if
        }// end if

    }// end askLocationPermission()



    }