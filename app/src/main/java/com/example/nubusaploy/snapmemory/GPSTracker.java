package com.example.nubusaploy.snapmemory;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by HP on 08-10-2016.
 */

    public class GPSTracker extends Service implements LocationListener {

        private final Context context;
        boolean isGPSEnabled = false;
        boolean isNetWorkEnabled = false;
        boolean canGetLocation = false;

        Location location;
        double latitude;
        double longitude;

        private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

        protected LocationManager locationManager;

        public GPSTracker(Context context) {
            this.context = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetWorkEnabled) {

                } else {
                    this.canGetLocation = true;

                    if (isNetWorkEnabled) {
                        String str = LocationManager.NETWORK_PROVIDER;
                       try{
                           locationManager.requestLocationUpdates(str, 0, 0, this);
                       }catch (SecurityException s){
                           Toast.makeText(this,"network error",Toast.LENGTH_SHORT).show();
                       }

                    }
                    if (locationManager != null) {
                        try {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                        catch (SecurityException s){
                            Toast.makeText(this,"network error",Toast.LENGTH_SHORT).show();
                        }


                    }

                    if (isGPSEnabled) {

                        if (location == null) {
                            try {

                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                            } catch (SecurityException s){
                                Toast.makeText(this,"network error",Toast.LENGTH_SHORT).show();
                            }

                        }

                        if (locationManager != null) {
                            try{
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }catch (SecurityException s){
                                Toast.makeText(this,"network error",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return location;
        }

        public void stopUsingGPS() {
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates(GPSTracker.this);

            }

        }

        public double getLatitude(){
            if(location!=null){
                latitude = location.getLatitude();
            }
            return latitude;
        }

        public double getLongitude(){
            if(location!=null){
                longitude = location.getLongitude();
            }
            return longitude;
        }

        public boolean canGetLocation(){
            return this.canGetLocation;
        }

        public void showSettingsAlert(){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setTitle("GPS is settings");

            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


