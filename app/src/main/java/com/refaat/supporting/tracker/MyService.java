package com.refaat.supporting.tracker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.LocationSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MyService extends Service {

    private Socket mSocket;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();


        try {
            mSocket = IO.socket("http://192.168.1.4:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect();


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10,
                2, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                       // Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
                        double lat=location.getLatitude();
                        double lang=location.getLongitude();
                        JSONObject object=new JSONObject();
                        try {
                            object.put("lat",lat);
                            object.put("lang",lang);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mSocket.emit("drive", object);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
        return START_STICKY;
    }



}
