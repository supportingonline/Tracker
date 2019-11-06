package com.refaat.supporting.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void drive(View view) {
        startActivity(new Intent(this,DriverActivity.class));
    }



    public void observor(View view) {
        startActivity(new Intent(this, ObserverActivity.class));
    }
}
