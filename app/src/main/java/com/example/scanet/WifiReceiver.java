package com.example.scanet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> deviceList = new ArrayList<>();
            for (ScanResult scanResult : wifiList) {
                System.out.println("-----------------"+scanResult);
                sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.BSSID);
                deviceList.add(scanResult.SSID + "\n" + scanResult.BSSID + "\n"+scanResult.level+" dBm  |  "+scanResult.frequency+" MHz"+"\n"+scanResult.capabilities+"\n");

            }
            Toast.makeText(context, sb, Toast.LENGTH_SHORT).show();
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray()){@Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }};
            wifiDeviceList.setAdapter(arrayAdapter);

        }
    }
}
