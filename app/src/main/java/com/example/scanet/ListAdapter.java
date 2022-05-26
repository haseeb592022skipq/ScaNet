package com.example.scanet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<DeviceList> {

    public ListAdapter(Context context,ArrayList<DeviceList> deviceArrayList){
        super(context,R.layout.list_item,deviceArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        DeviceList devicelist = getItem(position);

        if (convertView==null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        TextView ip = convertView.findViewById(R.id.ip);
        TextView hostname = convertView.findViewById(R.id.hostname);
        TextView mac = convertView.findViewById(R.id.mac);
        TextView vendor = convertView.findViewById(R.id.vendor);

        ip.setText(devicelist.ip);
        mac.setText(devicelist.mac);
        vendor.setText(devicelist.vendor);
        hostname.setText(devicelist.hostname);

        return convertView;
    }
}
