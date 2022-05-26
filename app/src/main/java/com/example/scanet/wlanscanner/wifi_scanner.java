package com.example.scanet.wlanscanner;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanet.R;
import com.example.scanet.wlanscanner.dialogs.DialogAbout;
import com.example.scanet.wlanscanner.dialogs.DialogFilter;
import com.example.scanet.wlanscanner.dialogs.DialogPermissions;
import com.example.scanet.wlanscanner.dialogs.DialogSettings;
import com.example.scanet.wlanscanner.events.EventManager;
import com.example.scanet.wlanscanner.events.Events;
import com.example.scanet.wlanscanner.events.Events.EventID;
import com.example.scanet.wlanscanner.events.IEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings("ALL")
public class  wifi_scanner extends AppCompatActivity implements IEventListener {

    public final static boolean SHOWROOM_MODE_ENABLED	= false;

    public final static int MAX_SCAN_RESULT_AGE			= 35; // (in seconds)

    public final static int FRAGMENT_ID_WLANLIST		= 0;
    public final static int FRAGMENT_ID_DIAGRAM_24GHZ	= 1;
    public final static int FRAGMENT_ID_DIAGRAM_5GHZ	= 2;
    public final static int FRAGMENT_ID_DIAGRAM_6GHZ	= 3;

    private final static String[] permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    private ActionBar.Tab tab1, tab2, tab3, tab4;
    private Fragment fragmentWLANList;
    private Fragment fragmentDiagram24GHz;
    private Fragment fragmentDiagram5GHz;
    private Fragment fragmentDiagram6GHz;
    private int currentFragmentID;

    private MenuItem buttonToggleScan;
    private ImageView ivPauseButton;
    private Animation animPauseButton;

    private ImageView ivRefreshIndicator;
    private Animation animRefreshIndicator;

    private MenuItem buttonFilter;

    private WifiManager wm;

    private ArrayList<ScanResult> scanResultListOrig;
    private ArrayList<ScanResult> scanResultListFiltered;

    private BroadcastReceiver brScanResults;

    private boolean wlanEnabledByApp;

    private boolean scanEnabled;
    private boolean scanTimerIsRunning		= false;
    private long lastScanResultReceivedTime = 0;

    private SharedPreferences sharedPrefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventManager.sharedInstance().addListener(this, EventID.USER_QUIT);
        EventManager.sharedInstance().addListener(this, EventID.FILTER_CHANGED);

        sharedPrefs         = getPreferences(Context.MODE_PRIVATE);
        scanEnabled         = sharedPrefs.getBoolean(Util.PREF_SCAN_ENABLED, true);
        wlanEnabledByApp    = sharedPrefs.getBoolean(Util.PREF_WLAN_ENABLED_BY_APP, false);
        currentFragmentID   = sharedPrefs.getInt(Util.PREF_SELECTED_TAB, FRAGMENT_ID_WLANLIST);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_wifiscanner);

        FrameLayout rootLayout = (FrameLayout) findViewById(android.R.id.content);
        View.inflate(this, R.layout.refresh_indicator, rootLayout);
        ivRefreshIndicator = (ImageView) findViewById(R.id.refresh_indicator);
        ivRefreshIndicator.setVisibility(View.INVISIBLE);

        animRefreshIndicator = AnimationUtils.loadAnimation(this, R.anim.anim_refresh_indicator);

        //ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        fragmentWLANList 		= new FragmentWLANList();
        fragmentDiagram24GHz 	= new FragmentDiagram24GHz();
        fragmentDiagram5GHz 	= new FragmentDiagram5GHz();
        fragmentDiagram6GHz 	= new FragmentDiagram6GHz();


        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("List").setTabListener(new MyTabListener(this, fragmentWLANList)), 0, currentFragmentID == FRAGMENT_ID_WLANLIST);


        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("2.4 GHz").setTabListener(new MyTabListener(this, fragmentDiagram24GHz)), 1, currentFragmentID == FRAGMENT_ID_DIAGRAM_24GHZ);


        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("5 GHz").setTabListener(new MyTabListener(this, fragmentDiagram5GHz)), 2, currentFragmentID == FRAGMENT_ID_DIAGRAM_5GHZ);

        getSupportActionBar().addTab(     getSupportActionBar().newTab().setText("6 GHz").setTabListener(new MyTabListener(this, fragmentDiagram6GHz))
                , 3, currentFragmentID == FRAGMENT_ID_DIAGRAM_6GHZ);

        ivPauseButton = new ImageView(wifi_scanner.this);
        ivPauseButton.setImageResource(R.drawable.ic_pause);
        ivPauseButton.setClickable(true);
        ivPauseButton.setFocusable(true);
        ivPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScanEnabled(false);
                invalidateOptionsMenu();
            }
        });

        animPauseButton = AnimationUtils.loadAnimation(this, R.anim.anim_pause_button);

        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        scanResultListOrig 		= new ArrayList<>();
        scanResultListFiltered 	= new ArrayList<>();

        brScanResults = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                onReceivedScanResults();
            }
        };

        registerReceiver(brScanResults, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        requestScan();

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(Util.PREF_SCAN_ENABLED, scanEnabled);
        editor.putBoolean(Util.PREF_WLAN_ENABLED_BY_APP, wlanEnabledByApp);
        editor.putInt(Util.PREF_SELECTED_TAB, currentFragmentID);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(brScanResults);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_buttons, menu);

        buttonToggleScan 	= menu.findItem(R.id.actionbutton_toggle_scan);
        buttonFilter 		= menu.findItem(R.id.actionbutton_filter);
        updateFilterButton();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (scanEnabled) {
            if (ivPauseButton.getAnimation() == null) {
                ivPauseButton.startAnimation(animPauseButton);
            }
            buttonToggleScan.setActionView(ivPauseButton);
        }
        else {
            ivPauseButton.clearAnimation();
            buttonToggleScan.setActionView(null);
            buttonToggleScan.setIcon(R.drawable.ic_play);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbutton_toggle_scan:
                setScanEnabled(! scanEnabled);
                invalidateOptionsMenu();
                return true;
            case R.id.actionbutton_filter:
                new DialogFilter(this).show();
                return true;
            case R.id.actionbutton_settings:
                new DialogSettings(this).show();
                return true;
            case R.id.actionbutton_about:
                new DialogAbout(this).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onReceivedScanResults() {
        if (!scanEnabled) {
            return;
        }

        List<ScanResult> scanResults = null;
        if (SHOWROOM_MODE_ENABLED) {
            scanResults = createShowRoomScanResults();
        }
        else {
            scanResults = wm.getScanResults();
        }

        scanResultListOrig.clear();
        scanResultListFiltered.clear();

        for (ScanResult sr : scanResults) {

            if (android.os.Build.VERSION.SDK_INT >= 17) {
                long age = ((SystemClock.elapsedRealtime() * 1000) - sr.timestamp) / 1000000;
                // if the wlan was last seen more than MAX_SCAN_RESULT_AGE seconds ago, do not add it to the list
                if (age > MAX_SCAN_RESULT_AGE) {
                    continue;
                }
            }

            scanResultListOrig.add(sr);

            if (checkFilter(sr)) {
                scanResultListFiltered.add(sr);
            }
        }

        Animation anim = ivRefreshIndicator.getAnimation();
        if (anim == null || (anim != null && anim.hasEnded())) {
            ivRefreshIndicator.setVisibility(View.VISIBLE);
            ivRefreshIndicator.startAnimation(animRefreshIndicator);
            ivRefreshIndicator.setVisibility(View.GONE);
        }

        EventManager.sharedInstance().sendEvent(Events.EventID.SCAN_RESULT_CHANGED);
        invalidateOptionsMenu();

        lastScanResultReceivedTime = System.currentTimeMillis();
        requestScan();
    }

    private boolean checkFilter(ScanResult sr) {
        SharedPreferences sharedPrefs 	= getPreferences(Context.MODE_PRIVATE);

        boolean filterSSIDEnabled 		= sharedPrefs.getBoolean(Util.PREF_FILTER_SSID_ENABLED, false);
        String filterSSID 				= sharedPrefs.getString(Util.PREF_FILTER_SSID, "");

        boolean filterChannelEnabled 	= sharedPrefs.getBoolean(Util.PREF_FILTER_CHANNEL_ENABLED, false);
        String filterChannel			= sharedPrefs.getString(Util.PREF_FILTER_CHANNEL, "");

        boolean filterCapabiliEnabled 	= sharedPrefs.getBoolean(Util.PREF_FILTER_CAPABILI_ENABLED, false);
        String filterCapabili			= sharedPrefs.getString(Util.PREF_FILTER_CAPABILI, "");

        if (filterSSIDEnabled && ! sr.SSID.toLowerCase().contains(filterSSID.toLowerCase())) {
            return false;
        }

        if (filterChannelEnabled) {
            int fChannel = Integer.parseInt(filterChannel);
            int[] frequencies = Util.getFrequencies(sr);

            boolean channelFound = false;
            for (int f : frequencies) {
                if (Util.getChannel(f) == fChannel) {
                    channelFound = true;
                }
            }

            if (! channelFound) {
                return false;
            }
        }

        if (filterCapabiliEnabled && ! sr.capabilities.toLowerCase().contains(filterCapabili.toLowerCase())) {
            return false;
        }

        return true;
    }

    private void onFilterChanged() {
        ArrayList<ScanResult> mList = new ArrayList<>();

        for (ScanResult sr : scanResultListOrig) {
            if (checkFilter(sr)) {
                mList.add(sr);
            }
        }

        scanResultListFiltered = mList;
        updateFilterButton();
        EventManager.sharedInstance().sendEvent(Events.EventID.SCAN_RESULT_CHANGED);
    }

    private void updateFilterButton() {
        SharedPreferences sharedPrefs 	= getPreferences(Context.MODE_PRIVATE);
        boolean filterSSIDEnabled 		= sharedPrefs.getBoolean(Util.PREF_FILTER_SSID_ENABLED, false);
        boolean filterChannelEnabled 	= sharedPrefs.getBoolean(Util.PREF_FILTER_CHANNEL_ENABLED, false);
        boolean filterCapabiliEnabled 	= sharedPrefs.getBoolean(Util.PREF_FILTER_CAPABILI_ENABLED, false);

        if (filterSSIDEnabled || filterChannelEnabled || filterCapabiliEnabled) {
            buttonFilter.setIcon(R.drawable.ic_filter_active);
        }
        else {
            buttonFilter.setIcon(R.drawable.ic_filter);
        }
    }

    private void setWLANEnabled(boolean enable) {
        if (enable && wm.isWifiEnabled() == false) {
            showToast("Enabling WLAN...");
            wm.setWifiEnabled(true);
            wlanEnabledByApp = true;
        }
        else if (!enable && wm.isWifiEnabled() && wlanEnabledByApp) {
            showToast("Disabling WLAN...");
            wm.setWifiEnabled(false);
            wlanEnabledByApp = false;
        }
    }

    public ArrayList<ScanResult> getScanResults() {
        return scanResultListFiltered;
    }

    public void showToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 180);
        toast.show();
    }

    private void setScanEnabled(boolean enable) {
        scanEnabled = enable;
        if (enable) {
            requestScan();
        }
    }

    private void requestScan() {
        if (! scanEnabled || scanTimerIsRunning) {
            return;
        }

        setWLANEnabled(true);

        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        float scanDelay = sharedPrefs.getFloat(Util.PREF_SETTING_SCAN_DELAY, Util.getDefaultScanDelay());

        long delay = (long) Math.max(0, scanDelay - (System.currentTimeMillis() - lastScanResultReceivedTime));

        scanTimerIsRunning = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                scanTimerIsRunning = false;
                if (scanEnabled) {
                    wm.startScan();
                }
            }
        }, delay);
    }

    public void setCurrentFragmentID(int fragmentID) {
        currentFragmentID = fragmentID;
    }

    public int getCurrentFragmentID() {
        return currentFragmentID;
    }

    @Override
    public void handleEvent(EventID eventID) {
        switch (eventID) {
            case USER_QUIT:
                setScanEnabled(false);
                setWLANEnabled(false);

                currentFragmentID 	= FRAGMENT_ID_WLANLIST;
                scanEnabled 		= true;

                finish();
                break;

            case FILTER_CHANGED:
                onFilterChanged();
                break;

            default:
                break;
        }
    }

    @TargetApi(30)
    private List<ScanResult> createShowRoomScanResults() {
        List<ScanResult> scanResults = new ArrayList<>();

        ScanResult sr = new ScanResult();
        sr.SSID			= "SSID-1";
        sr.BSSID		= "73:44:f3:93:4a:71";
        sr.level 		= -38;
        sr.centerFreq0	= Util.getFrequency(Util.FrequencyBand.TWO_FOUR_GHZ, 6);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_40MHZ;
        sr.capabilities	= "WPA2-PSK-CCMP ESS WPS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        sr = new ScanResult();
        sr.SSID			= "SSID-2";
        sr.BSSID		= "43:81:f3:2a:19:e8";
        sr.level 		= -45;
        sr.centerFreq0	= Util.getFrequency(Util.FrequencyBand.TWO_FOUR_GHZ, 6);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_40MHZ;
        sr.capabilities	= "WPA2-PSK-CCMP ESS WPS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        sr = new ScanResult();
        sr.SSID			= "SSID-3";
        sr.BSSID		= "cc:2a:65:21:b9:1a";
        sr.level 		= -51;
        sr.frequency	= Util.getFrequency(Util.FrequencyBand.TWO_FOUR_GHZ, 1);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_20MHZ;
        sr.capabilities	= "WPA2-PSK-CCMP ESS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        sr = new ScanResult();
        sr.SSID			= "SSID-4";
        sr.BSSID		= "5a:2b:83:2e:40:f7";
        sr.level		= -74;
        sr.frequency	= Util.getFrequency(Util.FrequencyBand.TWO_FOUR_GHZ, 13);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_20MHZ;
        sr.capabilities	= "WPA2-PSK-CCMP ESS WPS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        sr = new ScanResult();
        sr.SSID			= "SSID-5";
        sr.BSSID		= "3c:8e:41:49:b9:22";
        sr.level 		= -81;
        sr.centerFreq0	= Util.getFrequency(Util.FrequencyBand.TWO_FOUR_GHZ, 8);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_40MHZ;
        sr.capabilities	= "WPA2-PSK-CCMP ESS WPS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        sr = new ScanResult();
        sr.SSID			= "SSID-6";
        sr.BSSID		= "84:1c:7e:31:7a:82";
        sr.level 		= -86;
        sr.frequency	= Util.getFrequency(Util.FrequencyBand.TWO_FOUR_GHZ, 13);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_20MHZ;
        sr.capabilities	= "ESS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        sr = new ScanResult();
        sr.SSID			= "SSID-7";
        sr.BSSID		= "62:28:bc:c3:8a:91";
        sr.level 		= -92;
        sr.centerFreq0	= Util.getFrequency(Util.FrequencyBand.TWO_FOUR_GHZ, 8);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_40MHZ;
        sr.capabilities	= "WPA2-PSK-CCMP ESS WPS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        sr = new ScanResult();
        sr.SSID			= "SSID-8";
        sr.BSSID		= "34:98:af:33:9f:44";
        sr.level 		= -93;
        sr.centerFreq0	= Util.getFrequency(Util.FrequencyBand.SIX_GHZ, 39);
        sr.centerFreq1	= Util.getFrequency(Util.FrequencyBand.SIX_GHZ, 87);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ;
        sr.capabilities	= "ESS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        sr = new ScanResult();
        sr.SSID			= "SSID-9";
        sr.BSSID		= "e4:e6:66:b3:78:1a";
        sr.level		= -96;
        sr.centerFreq0	= Util.getFrequency(Util.FrequencyBand.SIX_GHZ, 17);
        sr.channelWidth	= ScanResult.CHANNEL_WIDTH_160MHZ;
        sr.capabilities	= "ESS";
        sr.timestamp	= System.currentTimeMillis();
        scanResults.add(sr);

        return scanResults;
    }
}

