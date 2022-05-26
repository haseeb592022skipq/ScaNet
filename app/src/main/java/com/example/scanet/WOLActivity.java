package com.example.scanet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stealthcopter.networktools.ARPInfo;
import com.stealthcopter.networktools.WakeOnLan;


import java.io.IOException;

public class WOLActivity extends AppCompatActivity {

    ScrollView sv;
    TextView message;
    Button send;
    EditText wolip;
    TextView macfip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_wolactivity);
        wolip= findViewById(R.id.WOL_ip);
        send = findViewById(R.id.btn_send);
        message = findViewById(R.id.message);
        macfip = findViewById(R.id.macfip);

        Intent intent = this.getIntent();

        if (intent != null){
            String ip_d = intent.getStringExtra("ip");

            wolip.setText(ip_d);
        }

        send.setOnClickListener(view -> {
            try {
                doWakeOnLan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void setEnabled(final View view, final boolean enabled) {
        runOnUiThread(() -> {
            if (view != null) {
                view.setEnabled(enabled);
            }
        });
    }

    private void doWakeOnLan() throws IllegalArgumentException {
        String WOL_ip = wolip.getText().toString();

        if (TextUtils.isEmpty(WOL_ip)) {
            message.setText("Invalid Ip Address");
            message.setTextColor(Color.parseColor("#FF0000"));
            return;
        }

        setEnabled(send, false);

        message.setText("IP address: " + WOL_ip);

        // Get mac address from IP (using arp cache)
        String macAddress = ARPInfo.getMACFromIPAddress(WOL_ip);

        if (macAddress == null) {
            message.setText("MAC address not for this IP address, cannot send WOL packet without it.");
            message.setTextColor(Color.parseColor("#FF0000"));
            setEnabled(send, true);
            return;
        }

        macfip.setText("MAC address: " + macAddress);
        message.setText("Wake On LAN Packet sent");
        message.setTextColor(Color.parseColor("#006400"));

        // Send Wake on lan packed to ip/mac
        try {
            WakeOnLan.sendWakeOnLan(WOL_ip, macAddress);

        } catch (IOException e) {
            message.setText(e.getMessage());
            e.printStackTrace();
        } finally {
            setEnabled(send, true);
        }
    }

}