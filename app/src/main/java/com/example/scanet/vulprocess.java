package com.example.scanet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import com.stealthcopter.networktools.PortScan;

public class vulprocess extends AppCompatActivity {

    int counter = 0;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        pb = findViewById(R.id.pb);

        setContentView(R.layout.activity_vulprocess);

        prog();


    }

    public void prog(){
        pb = (ProgressBar)findViewById(R.id.pb);

        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run()
            {
                counter++;
                pb.setProgress(counter);

                if(counter == 100) {
                    t.cancel();
                    Intent vul_result = new Intent(vulprocess.this, vul_result.class);
                    startActivity(vul_result);
                }       }
        };

        t.schedule(tt,0,30);

    }


}