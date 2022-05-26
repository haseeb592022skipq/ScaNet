package com.example.scanet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.speedchecker.android.sdk.SpeedcheckerSDK;
import com.speedchecker.android.sdk.Public.SpeedTestListener;
import com.speedchecker.android.sdk.Public.SpeedTestResult;

public class speedtestActivity extends AppCompatActivity implements SpeedTestListener {

    private TextView mTextViewStage;
    private TextView mTextViewResult;
    private Button mButtonSpeedTest;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_speedtest);

        mTextViewStage = findViewById(R.id.textView_stage);
        mTextViewResult = findViewById(R.id.textView_result);
        mButtonSpeedTest = findViewById(R.id.button_speedTest);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        //TODO: 3. Initialize SpeedcheckerSDK inside a main activity.
        SpeedcheckerSDK.init(this);

        //TODO: 4. Ask Location permissions.
        SpeedcheckerSDK.askPermissions(this);
        //OR request location permissions manually. We need:
        //ACCESS_COARSE_LOCATION
        //ACCESS_FINE_LOCATION
        //ACCESS_BACKGROUND_LOCATION

        //TODO: 6. Set implemented interface.
        SpeedcheckerSDK.SpeedTest.setOnSpeedTestListener(this);

        mButtonSpeedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 7. Start Speed test.
                SpeedcheckerSDK.SpeedTest.startTest(speedtestActivity.this);
            }
        });

        //===========================
        //=== Additional features ===
        //===========================
        //NOTE: some features could not be available in some SDK versions.
        // Please, contact us to get more information

        //# Interrupt speed test
        //SpeedcheckerSDK.SpeedTest.interruptTest();

        //# Register listener to get additional information (IP, ISP).
        // This callback will fire after internet speed test
        //SpeedcheckerSDK.SpeedTest.setAfterTestUserInfoListener(new SpeedTestListener.AfterTestUserInfo() {
        //    @Override
        //    public void getUserInfoCallback(String IP, String ISP) {
        //
        //    }
        //});
    }

    //TODO: 5. Implement the "SpeedTestListener" interface and override methods.
    @Override
    public void onTestStarted() {
        mTextViewStage.setText("Test Started");
        mTextViewResult.setText("-");

    }

    @Override
    public void onFetchServerFailed() {
        mTextViewStage.setText("Fetch Server Failed");
        mTextViewResult.setText("-");

    }

    @Override
    public void onFindingBestServerStarted() {
        mTextViewStage.setText("Finding best server");
        mTextViewResult.setText("...");

    }

    @Override
    public void onTestFinished(SpeedTestResult speedTestResult) {
        String finalStr =
                "Server: " + speedTestResult.getServer().Domain + "\n"
                        + "Ping: " + speedTestResult.getPing() + " ms" + "\n"
                        + "Download speed: " + speedTestResult.getDownloadSpeed() + " Mb/s" + "\n"
                        + "Upload speed: " + speedTestResult.getUploadSpeed() + " Mb/s" + "\n"
                        + "Connection type: " + speedTestResult.getConnectionTypeHuman() + "\n";
        mTextViewStage.setText("Test Finished");
        mTextViewResult.setText(finalStr);

    }

    @Override
    public void onPingStarted() {
        mTextViewStage.setText("Ping Started");
        mTextViewResult.setText("...");

    }

    @Override
    public void onPingFinished(int ping, int jitter) {
        mTextViewStage.setText("Ping Finished");
        mTextViewResult.setText(ping + " ms | jitter: " + jitter);
    }

    @Override
    public void onDownloadTestStarted() {
        mTextViewStage.setText("Download Test Started");
        mTextViewResult.setText("...");

    }

    @Override
    public void onDownloadTestProgress(int i, double speedMbs, double transferredMb) {
        mTextViewStage.setText("Download Test Progress");
        mTextViewResult.setText(i + "% -> " + speedMbs + " Mb/s\nTransferredMb: " + transferredMb);

    }

    @Override
    public void onDownloadTestFinished(double v) {
        mTextViewStage.setText("Download Test Finished");
        mTextViewResult.setText(v + " Mb/s");

    }

    @Override
    public void onUploadTestStarted() {
        mTextViewStage.setText("Upload Test Started");
        mTextViewResult.setText("...");

    }

    @Override
    public void onUploadTestProgress(int i, double speedMbs, double transferredMb) {
        mTextViewStage.setText("Upload Test Progress");
        mTextViewResult.setText(i + "% -> " + speedMbs + " Mb/s\nTransferredMb: " + transferredMb);

    }

    @Override
    public void onUploadTestFinished(double v) {
        mTextViewStage.setText("Upload Test Finished");
        mTextViewResult.setText(v + " Mb/s");

    }

    @Override
    public void onTestWarning(String s) {
        mTextViewStage.setText("Test Warning");
        mTextViewResult.setText(s);

    }

    @Override
    public void onTestFatalError(String s) {
        mTextViewStage.setText("Test Fatal Error");
        mTextViewResult.setText(s);
    }

    @Override
    public void onTestInterrupted(String s) {
        mTextViewStage.setText("Test Interrupted");
        mTextViewResult.setText(s);

    }

}
