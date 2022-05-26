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

import com.stealthcopter.networktools.PortScan;

import java.util.ArrayList;

public class PreOpActivity extends AppCompatActivity {

    Button btn_op;
    EditText op_ip;
    ScrollView svv;
    TextView open_ports;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_pre_op);

        btn_op = findViewById(R.id.btn_FOP);
        op_ip = findViewById(R.id.FOP_ip);
        open_ports = findViewById(R.id.open_ports);
        svv = findViewById(R.id.scrollView4);

        Intent intent = this.getIntent();

        if (intent != null){
            String ip_d = intent.getStringExtra("ip");

            op_ip.setText(ip_d);
        }

        btn_op.setOnClickListener(view -> {
            try {
                doPortScan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setEnabled(final View view, final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.setEnabled(enabled);
                }
            }
        });
    }

    private void appendResultsText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                open_ports.append(text + "\n");
                svv.post(new Runnable() {
                    @Override
                    public void run() {
                        svv.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    private void doPortScan() throws Exception {
        String ipAddress = op_ip.getText().toString();

        if (TextUtils.isEmpty(ipAddress)) {
            appendResultsText("Invalid Ip Address");
            setEnabled(btn_op, true);
            return;
        }

        setEnabled(btn_op, false);

        // Perform synchronous port scan
        appendResultsText("PortScanning IP: " + ipAddress);
        ArrayList<Integer> openPorts = PortScan.onAddress(ipAddress).setPort(21).setMethodTCP().doScan();

        final long startTimeMillis = System.currentTimeMillis();

        // Perform an asynchronous port scan
        PortScan portScan = PortScan.onAddress(ipAddress).setPortsAll().setMethodTCP().doScan(new PortScan.PortListener() {
            @Override
            public void onResult(int portNo, boolean open) {
                if (open) appendResultsText("Open: " + portNo);
            }

            @Override
            public void onFinished(ArrayList<Integer> openPorts) {
                appendResultsText("Open Ports: " + openPorts.size());
                appendResultsText("Time Taken: " + ((System.currentTimeMillis() - startTimeMillis)/1000.0f));
                setEnabled(btn_op, true);
            }
        });

        // Below is example of how to cancel a running scan
        // portScan.cancel();
    }
}