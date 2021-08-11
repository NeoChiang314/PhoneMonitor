package com.example.phonemonitor;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GpsService extends Service {

    public static GpsService instance;
    public static GpsService getInstance() {
        return instance;
    }

    public double longitude, latitude;

    LocationManager locationManager;
    MyLocationListener myLocationListener;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        myLocationListener = new MyLocationListener();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GpsActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GpsActivity.FINE_LOCATION_REQUEST);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, myLocationListener);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        instance = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged (Location loc){
            getLocationInfo(loc);
        }
    }

    public void getLocationInfo (Location loc) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(myLocationListener);
            ActivityCompat.requestPermissions(GpsActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GpsActivity.FINE_LOCATION_REQUEST);
        }
        else{
            longitude = loc.getLongitude();
            latitude = loc.getLatitude();

            if (GpsActivity.getInstance() != null) {
                GpsActivity.getInstance().showGpsData(longitude, latitude);
            }
        }
    }
}
