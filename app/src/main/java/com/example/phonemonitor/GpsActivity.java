package com.example.phonemonitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class GpsActivity extends AppCompatActivity {

    public static final int FINE_LOCATION_REQUEST = 1;
    public static GpsActivity instance;
    public static GpsActivity getInstance() { return instance; }

    ToggleButton toggleButton;
    TextView longitudeText, latitudeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_gps);
        toggleButton = findViewById(R.id.gpsToggleButton);
        longitudeText = findViewById(R.id.longitudeView);
        latitudeText = findViewById(R.id.latitudeView);

        if (GpsService.getInstance() == null) {
            toggleButton.setChecked(false);
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                    if(isChecked){
                        Intent intent = new Intent();
                        intent.setClass(GpsActivity.this, GpsService.class);
                        startService(intent);
                        longitudeText.setVisibility(View.VISIBLE);
                        latitudeText.setVisibility(View.VISIBLE);
                    }
                    else{
                        Intent intent = new Intent();
                        intent.setClass(GpsActivity.this, GpsService.class);
                        stopService(intent);
                        longitudeText.setVisibility(View.INVISIBLE);
                        latitudeText.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        else {
            showGpsData(GpsService.getInstance().longitude, GpsService.getInstance().latitude);
            toggleButton.setChecked(true);
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked){
                    if(isChecked){
                        Intent intent = new Intent();
                        intent.setClass(GpsActivity.this, GpsService.class);
                        startService(intent);
                        longitudeText.setVisibility(View.VISIBLE);
                        latitudeText.setVisibility(View.VISIBLE);
                    }
                    else{
                        Intent intent = new Intent();
                        intent.setClass(GpsActivity.this, GpsService.class);
                        stopService(intent);
                        longitudeText.setVisibility(View.INVISIBLE);
                        latitudeText.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onPause(){
        super.onPause();

    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    public void showGpsData (double longitude, double latitude) {
        longitudeText.setText("Longitude: " + String.valueOf(longitude));
        latitudeText.setText("Latitude: " + String.valueOf(latitude));
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(GpsActivity.this, "Fine location permission granted", Toast.LENGTH_SHORT).show();
                GpsService.getInstance().locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, GpsService.getInstance().myLocationListener);
            }
            else {
                Toast.makeText(GpsActivity.this, "Fine location permission rejected", Toast.LENGTH_SHORT).show();
                toggleButton.setChecked(false);
            }
        }
    }
}
