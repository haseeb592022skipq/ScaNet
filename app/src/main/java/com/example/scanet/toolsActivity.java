package com.example.scanet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.scanet.wlanscanner.wifi_scanner;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class toolsActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_tools);

          ImageView btn1 = findViewById(R.id.btn1);
          ImageView btn2 = findViewById(R.id.btn2);
          ImageView btn3 = findViewById(R.id.btn3);
          ImageView btn4 = findViewById(R.id.btn4);
          ImageView   btn5 = findViewById(R.id.btn5);
          ImageView btn6 = findViewById(R.id.btn6);

        //Initialize and assign variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set home selected
        bottomNavigationView.setSelectedItemId(R.id.tools);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.tools:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext()
                                ,homeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext()
                                ,accountActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                }
                return false;
            }
        });

        btn1.setOnClickListener(v -> {
            Intent PrePingIntent = new Intent(toolsActivity.this,PrePingActivity.class);
            startActivity(PrePingIntent);
        });
        btn2.setOnClickListener(view -> {
            Intent WOLIntent = new Intent(toolsActivity.this,WOLActivity.class);
            startActivity(WOLIntent);
        });
        btn5.setOnClickListener(v -> {
            Intent PreTracerouteIntent = new Intent(toolsActivity.this,PreTracerouteActivity.class);
            startActivity(PreTracerouteIntent);
        });
        btn4.setOnClickListener(v -> {
            Intent PreFOPIntent = new Intent(toolsActivity.this,PreOpActivity.class);
            startActivity(PreFOPIntent);
        });
        btn3.setOnClickListener(view -> {
            if ( ActivityCompat.checkSelfPermission(toolsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != (PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(toolsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
            else {
                Intent wifi_scannerIntent = new Intent(toolsActivity.this, WifiScannerActivity.class);
                startActivity(wifi_scannerIntent);
            }


        });
        btn6.setOnClickListener(v -> {
            Intent vul = new Intent(toolsActivity.this,Vulnerability.class);
            startActivity(vul);
        });
    }
}