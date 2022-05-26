package com.example.scanet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanet.databinding.ActivityDeviceListBinding;
import com.stealthcopter.networktools.IPTools;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DeviceListActivity extends AppCompatActivity {
    ActivityDeviceListBinding binding;
    lookup obj = new lookup();
    /*private TextView resultText;
    private ScrollView scrollView;*/
    List<Device> list = new ArrayList<Device>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //app full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_device_list);
        binding = ActivityDeviceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*resultText = findViewById(R.id.resultext);
        scrollView = findViewById(R.id.scrollView1);*/

        InetAddress ipAddress = IPTools.getLocalIPv4Address();
        if (ipAddress != null){
            String ip_address = ipAddress.getHostAddress();
        }


        findSubnetDevices();

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

    private void findSubnetDevices() {


        final long startTimeMillis = System.currentTimeMillis();
        /*appendResultsText("Scanning for devices...\n\n");*/

        SubnetDevices subnetDevices = SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
            @Override
            public void onDeviceFound(Device device) {
                //appendResultsText("Device : " + device.ip+"Hostname : "+ device.hostname+"Mac : "+device.mac);

                //device.vendor = obj.lookupVendor(device.mac);
                list.add(device);
                //appendResultsText(device.toString());
            }

            @Override
            public void onFinished(ArrayList<Device> devicesFound) {
                List<String> ip_list = new ArrayList<String>();
                List<String> mac_list = new ArrayList<String>();
                List<String> hostname_list = new ArrayList<String>();
                List<String> vendor_list = new ArrayList<String>();
                ArrayList<DeviceList> devicelistArrayList = new ArrayList<>();
                float timeTaken =  (System.currentTimeMillis() - startTimeMillis)/1000.0f;

                System.out.println(list);
                list = obj.scan(list);
                System.out.println(list);
                for (int i = 0; i < list.size(); i++) {

                    ip_list.add(list.get(i).ip);
                    mac_list.add(list.get(i).mac);
                    hostname_list.add(list.get(i).hostname);
                    vendor_list.add(list.get(i).vendor);

                    //appendResultsText(list.get(i)+"");
                    /*appendResultsText("IP : "+list.get(i).ip+"      |      Hostname : "+list.get(i).hostname+"\nMAC : "+list.get(i).mac+"\nVendor : "+list.get(i).vendor+"\n\n");*/
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        System.out.println(ip_list);
                        int a = ip_list.size();
                        System.out.println(a);

                        for(int i=0;i<ip_list.size();i++){

                            DeviceList devicelist = new DeviceList(ip_list.get(i),hostname_list.get(i),mac_list.get(i),vendor_list.get(i));
                            devicelistArrayList.add(devicelist);
                        }
                        ListAdapter listAdapter = new ListAdapter(DeviceListActivity.this,devicelistArrayList);
                        binding.listview.setAdapter(listAdapter);
                        binding.listview.setClickable(true);
                        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                                Intent dd = new Intent(DeviceListActivity.this,DeviceDetails.class);
                                dd.putExtra("ip",ip_list.get(position));
                                dd.putExtra("hostname",hostname_list.get(position));
                                dd.putExtra("mac",mac_list.get(position));
                                dd.putExtra("vendor",vendor_list.get(position));
                                startActivity(dd);

                            }
                        });

                    }
                });

                /*appendResultsText("\n\nDevices Found: " + devicesFound.size());
                appendResultsText("Finished "+timeTaken+" s\n\n");*/
            }
        });

    }

}
