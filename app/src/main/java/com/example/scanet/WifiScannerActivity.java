package com.example.scanet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

public class WifiScannerActivity extends AppCompatActivity {

    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_wifi_scanner);

        wifiList = findViewById(R.id.wifiList);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
                    wifiManager.startScan();
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }
    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(WifiScannerActivity.this, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(WifiScannerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(WifiScannerActivity.this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(WifiScannerActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(WifiScannerActivity.this, "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            Toast.makeText(WifiScannerActivity.this, "scanning", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(WifiScannerActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            } else {
                Toast.makeText(WifiScannerActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            break;
        }
    }
}
