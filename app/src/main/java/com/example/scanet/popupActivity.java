package com.example.scanet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;

public class popupActivity extends AppCompatActivity {


    WifiManager wifiManager;
    WifiInfo connection;
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;



    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_popup);
        TextView ssid = findViewById(R.id.ssid);
        TextView bssid = findViewById(R.id.bssid);
        TextView ipaddress = findViewById(R.id.ipaddress);
        TextView linkspeed = findViewById(R.id.linkspeed);
        TextView channel = findViewById(R.id.channel);
        TextView frequency = findViewById(R.id.frequency);
        TextView band = findViewById(R.id.band);
        TextView signal = findViewById(R.id.signal);


        //getting wifi info
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connection = wifiManager.getConnectionInfo();
        ssid.setText(connection.getSSID());
        bssid.setText(connection.getBSSID());
        linkspeed.setText(String.valueOf(connection.getLinkSpeed()) + " Mbps");
        signal.setText(String.valueOf(connection.getRssi())+" dBm");
        ipaddress.setText(Formatter.formatIpAddress(connection.getIpAddress()));
        int chan=convertFrequencyToChannel(connection.getFrequency());
        String chann = String.valueOf(chan);
        channel.setText("Ch#"+chann);
        frequency.setText(String.valueOf(connection.getFrequency())+" MHz");
        int f=connection.getFrequency();
        if(f>=5000){
            band.setText("5 GHz");
        }
        if(f>=2000 && f<=3000){
            band.setText("2.4 GHz");
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .45 ));

    }
    public static int convertFrequencyToChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }
}