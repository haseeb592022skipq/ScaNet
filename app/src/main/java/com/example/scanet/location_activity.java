package com.example.scanet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class location_activity extends AppCompatActivity {
    //variables
    Animation topAnim , bottomAnim;
    ImageView location_icon;
    Button enable_location;
    TextView location_description;
    private LocationRequest locationRequest;
    public static final int REQUEST_CHECK_SETTING = 1001;
    private static int SPLASH_TIME_OUT=500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_location);



        //Animations
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.botton_animation);

        //hooks
        location_icon = findViewById(R.id.icon_location);
        enable_location = findViewById(R.id.btn_location);
        location_description = findViewById(R.id.location_description);

        location_icon.setAnimation(topAnim);
        location_description.setAnimation(bottomAnim);
        enable_location.setAnimation(bottomAnim);

        enable_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(4000);
                locationRequest.setFastestInterval(2000);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);

                Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                        .checkLocationSettings(builder.build());

                result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);
                            Toast.makeText(location_activity.this, "Location is On", Toast.LENGTH_SHORT).show();


                        } catch (ApiException e) {
                            switch (e.getStatusCode()){
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                        resolvableApiException.startResolutionForResult(location_activity.this,REQUEST_CHECK_SETTING);
                                    } catch (IntentSender.SendIntentException sendIntentException) {

                                    }
                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;

                            }
                        }
                    }
                });

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTING){

            switch (resultCode){
                case Activity.RESULT_OK:
                    Toast.makeText(location_activity.this, "Location is turned ON", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(location_activity.this,homeActivity.class);
                    startActivity(homeIntent);
                    finish();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(location_activity.this, "Location is Required to be Turned ON", Toast.LENGTH_SHORT).show();

            }
        }
    }
}