package com.example.t.tplanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Typeface tf;
    private TextView logo;

    private static final int PERMISSION_READ_CONTACTS = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestPermissions(new String[]{
                Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);

        tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        logo = (TextView) findViewById(R.id.logo);
        logo.setTypeface(tf);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, Events.class));
                finish();
            }
        }, 2000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_READ_CONTACTS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            }
        }

    }
}
