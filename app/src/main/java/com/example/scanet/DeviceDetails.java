package com.example.scanet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.scanet.databinding.ActivityDeviceDetailsBinding;


public class DeviceDetails extends AppCompatActivity {
     ActivityDeviceDetailsBinding binding;

     String ipg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        binding = ActivityDeviceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView btn7 = findViewById(R.id.btn7);
        ImageView btn8 = findViewById(R.id.btn8);
        ImageView btn9 = findViewById(R.id.btn9);
        ImageView btn10 = findViewById(R.id.btn10);

        Intent intent = this.getIntent();

        if (intent != null){
            ipg = intent.getStringExtra("ip");
            String hostnameg = intent.getStringExtra("hostname");
            String macg = intent.getStringExtra("mac");
            String vendorg = intent.getStringExtra("vendor");

            binding.ip2.setText(ipg);
            binding.hostname2.setText(hostnameg);
            binding.mac2.setText(macg);
            binding.vendor2.setText(vendorg);

        }
        btn7.setOnClickListener(v -> {
            Intent intent2 = new Intent(DeviceDetails.this,PrePingActivity.class);
            intent2.putExtra("ip",ipg);
            startActivity(intent2);
        });
        btn9.setOnClickListener(v -> {
            Intent intent2 = new Intent(DeviceDetails.this,PreTracerouteActivity.class);
            intent2.putExtra("ip",ipg);
            startActivity(intent2);
        });
        btn8.setOnClickListener(v -> {
            Intent intent2 = new Intent(DeviceDetails.this,PreOpActivity.class);
            intent2.putExtra("ip",ipg);
            startActivity(intent2);
        });
        btn10.setOnClickListener(v -> {
            Intent intent2 = new Intent(DeviceDetails.this,WOLActivity.class);
            intent2.putExtra("ip",ipg);
            startActivity(intent2);
        });
    }
}