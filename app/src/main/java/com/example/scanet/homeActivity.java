package com.example.scanet;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;


public class homeActivity extends AppCompatActivity {


    WifiManager wifiManager;
    WifiInfo connection;
    JSONObject list;
    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_home);

        Button btnscan = findViewById(R.id.btn_scan);
        Button btnwifidetails = findViewById(R.id.btn_wifi_details);
        TextView wifiname = findViewById(R.id.wifi_name);
        TextView connected_to = findViewById(R.id.connected);
        TextView location = findViewById(R.id.location);
        TextView providername = findViewById(R.id.provider_name);
        Button btn_testspeed = findViewById(R.id.btn_testspeed);


        //getting wifi info
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        connection = wifiManager.getConnectionInfo();
        System.out.println("----------------------------"+connection.getSSID());
        wifiname.setText(connection.getSSID());
        connected_to.setText("Connected to "+ connection.getBSSID());



       // ISP();
        //Get location and ISP name

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                    IP_Check obj = new IP_Check();

                list = obj.details();


                    System.out.println(list);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        try {

                            String isp = list.get("isp")+"";
                            String loc = list.get("city")+", "+list.get("country_name");
                            providername.setText(isp);
                            location.setText(loc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        thread.start();

        //Initialize and assign variables
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tools:
                        startActivity(new Intent(getApplicationContext()
                                , toolsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext()
                                , accountActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                }
                return false;
            }
        });

        btnscan.setOnClickListener(v -> {
            Intent DevicelistIntent = new Intent(homeActivity.this,DeviceListActivity.class);
            startActivity(DevicelistIntent);
        });

        btnwifidetails.setOnClickListener(view -> startActivity( new Intent(homeActivity.this,popupActivity.class)));

        btn_testspeed.setOnClickListener(view -> {
            Intent testspeedIntent = new Intent(homeActivity.this,speedtestActivity.class);
            startActivity(testspeedIntent);
        });

    }

}