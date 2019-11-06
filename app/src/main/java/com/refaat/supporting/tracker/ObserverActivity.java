package com.refaat.supporting.tracker;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class ObserverActivity extends FragmentActivity implements OnMapReadyCallback {

    private Socket mSocket;
    private GoogleMap mMap;
    private ArrayList<LatLng> arrayList=new ArrayList<>();
    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observer);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        try {
            mSocket = IO.socket("http://192.168.1.4:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }




    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    double lat=0;
                    double lang=0;
                    try {
                      lat=data.getDouble("lat");
                      lang=data.getDouble("lang");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mMap.clear();

                    LatLng latLng=new LatLng(lat,lang);
                    arrayList.add(latLng);
                    markerOptions.position(latLng);
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16f));


                    PolylineOptions mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.RED);
                    mPolylineOptions.width(3);
                    mPolylineOptions.addAll(arrayList);
                    mMap.addPolyline(mPolylineOptions);

                    //Toast.makeText(ObserverActivity.this,"", Toast.LENGTH_SHORT).show();

                    // add the message to view
                    //  addMessage(username, message);
                }
            });
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markerOptions=new MarkerOptions();

        Bitmap bitmap = getBitmapFromVectorDrawable(this,R.drawable.ic_car);
        BitmapDescriptor descriptor =BitmapDescriptorFactory.fromBitmap(bitmap);

        markerOptions.icon(descriptor);
        mMap.clear();

        mSocket.connect();

        mSocket.on("drive", onNewMessage);


    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable =  AppCompatResources.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}
