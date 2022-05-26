package com.example.scanet;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class PreTracerouteActivity extends AppCompatActivity {

    Button btn_traceroute;
    EditText tr_ip;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pre_traceroute);

        btn_traceroute = findViewById(R.id.btn_traceroute);
        tr_ip = findViewById(R.id.traceroute_ip);
        result = findViewById(R.id.result);

        Intent intent = this.getIntent();

        if (intent != null){
            String ip_d = intent.getStringExtra("ip");

            tr_ip.setText(ip_d);
        }

        btn_traceroute.setOnClickListener(view -> {
            String ip_tr = tr_ip.getText().toString();
            String at = "8.8.8.8";
            tracerouteCmd("tracert "+at);

        });

    }

    public static void tracerouteCmd(String command){

        try{
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );
            String s = "";
            while((s=inputStream.readLine())!=null){
                System.out.println("----------------------------------------------------"+s);
            }
        } catch (IOException e) {

        }

    }


}