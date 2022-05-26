package com.example.scanet;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.scanet.wlanscanner.dialogs.DialogPermissions;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    //variables
    private static int SPLASH_TIME_OUT=6000;
    Animation topAnim , bottomAnim;
    ImageView logo, word_logo;
    private final static String[] permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };
    private final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    Intent locationIntent = new Intent(MainActivity.this,location_activity.class);
                    startActivity(locationIntent);
                    finish();
                }
                else{
                Intent homeIntent = new Intent(MainActivity.this,homeActivity.class);
                startActivity(homeIntent);
                finish();}
            }
        },SPLASH_TIME_OUT);

        //app full screen


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Animations
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.botton_animation);

        //hooks
        logo = findViewById(R.id.logo);
        word_logo = findViewById(R.id.logo_word);

        logo.setAnimation(topAnim);
        word_logo.setAnimation(bottomAnim);

        handlePermissions();


    }

    public void handlePermissions() {
        if (android.os.Build.VERSION.SDK_INT < 23 ) {
            return;
        }

        List<String> permissionsToRequest = new ArrayList<String>();

        for (int i = 0; i < permissions.length; i++) {
            String p = permissions[i];
            if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(p);
            }
        }

        if (! permissionsToRequest.isEmpty()) {
            new DialogPermissions(this, permissionsToRequest).show();
        }
    }

    public void requestPermissions(String[] permissions) {
        requestPermissions(permissions, 111);
    }



}