package com.example.scanet;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import java.net.UnknownHostException;


public class PrePingActivity extends AppCompatActivity {

    EditText ping_ip;
    TextView result;
    Button btn_ping;
    ScrollView scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pre_ping);

        ping_ip = findViewById(R.id.ping_ip);
        result = findViewById(R.id.ping_result);
        btn_ping = findViewById(R.id.btn_ping);
        scrollview = findViewById(R.id.scrollView2);

        Intent intent = this.getIntent();

        if (intent != null){
            String ip_d = intent.getStringExtra("ip");

            ping_ip.setText(ip_d);
        }

        btn_ping.setOnClickListener(v -> {

            try {
                doPing();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void appendResultsText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                result.append(text + "\n");
                result.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.fullScroll(View.FOCUS_DOWN);
                    }
                });
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

    private void doPing() throws Exception {
        String ipAddress = ping_ip.getText().toString();

        if (TextUtils.isEmpty(ipAddress)) {
            appendResultsText("Invalid Ip Address");
            return;
        }

        setEnabled(btn_ping, false);

        // Perform a single synchronous ping
        PingResult pingResult = null;
        try {
            pingResult = Ping.onAddress(ipAddress).setTimeOutMillis(1000).doPing();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            appendResultsText(e.getMessage());
            setEnabled(btn_ping, true);
            return;
        }


        appendResultsText("Pinging Address: " + pingResult.getAddress().getHostAddress());


        appendResultsText(String.format("%.2f ms", pingResult.getTimeTaken()));


        // Perform an asynchronous ping
        Ping.onAddress(ipAddress).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
            @Override
            public void onResult(PingResult pingResult) {
                if (pingResult.isReachable) {
                    appendResultsText(String.format("%.2f ms", pingResult.getTimeTaken()));
                } else {
                    appendResultsText(getString(R.string.timeout));
                }
            }

            @Override
            public void onFinished(PingStats pingStats) {
                appendResultsText(String.format("Pings: %d, Packets lost: %d",
                        pingStats.getNoPings(), pingStats.getPacketsLost()));
                appendResultsText(String.format("Min/Avg/Max Time: %.2f/%.2f/%.2f ms",
                        pingStats.getMinTimeTaken(), pingStats.getAverageTimeTaken(), pingStats.getMaxTimeTaken()));
                setEnabled(btn_ping, true);
            }

            @Override
            public void onError(Exception e) {
                // TODO: STUB METHOD
                setEnabled(btn_ping, true);
            }
        });

    }
}