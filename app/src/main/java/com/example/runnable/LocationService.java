package com.example.runnable;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;


public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //JC - declaring variables and calculations
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, lStart, lEnd;
    static double distance = 0;
    double speed;
    double speedmph;

    private final IBinder mBinder = new LocalBinder();


    //JC - bind google API to method
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }
    // JC - Method to assign settings for creating a location request
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        MainActivity.locate.dismiss();
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
            updateUI();
            // JC - calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
            speed = location.getSpeed() * 18 / 5;
        } else
            lEnd = mCurrentLocation;

        //JC - Calling the method below updates the  live values of distance and speed to the TextViews.
        updateUI();
        //JC - calculating the speed with getSpeed method it returns speed in m/s so we are converting it into mph
        speed = location.getSpeed() * 18 / 5;
        speedmph = speed*0.61;

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class LocalBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }
    }

    //JC The live feed of Distance and Speed are being set in the method below .
    private void updateUI() {
        if (MainActivity.p == 0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            MainActivity.endTime = System.currentTimeMillis();
            long hours = MainActivity.endTime - MainActivity.startTime;
            long minutes = MainActivity.endTime - MainActivity.startTime;
            long seconds = MainActivity.endTime - MainActivity.startTime;

            MainActivity.tvDuration.setText(String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(hours) % TimeUnit.HOURS.toMinutes(1), TimeUnit.MILLISECONDS.toMinutes(minutes),
                    TimeUnit.MILLISECONDS.toSeconds(seconds) % TimeUnit.MINUTES.toSeconds(1)));
            if (speed > 0.0)

                MainActivity.tvSpeed.setText(new DecimalFormat("#.##").format(speedmph)  + " mph");
            else
            MainActivity.tvDistance.setText(new DecimalFormat("#.###").format(distance) + " miles");

            lStart = lEnd;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }
}