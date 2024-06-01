package com.tencent.blue.viewclass;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.blue.R;
import com.tencent.blue.manager.NewBlueConnectManager;
import com.tencent.blue.storage.HostDevice;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {
    private List<HostDevice> devices;

    private static final String TAG = "DeviceListAdapter";

    NewBlueConnectManager blueConnectManager;

    public DeviceListAdapter(List<HostDevice> devices, NewBlueConnectManager connectionManager) {
        this.devices = devices;
        this.blueConnectManager = connectionManager;
    }

    public void connectToDevice(int position) {
        HostDevice device = devices.get(position);
        Log.d(TAG, "connectToDevice address: " + device.getAddress());
        Log.d(TAG, "connectToDevice name: " + device.getName());
        blueConnectManager.activeConnect(device.getName());
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceLastConnected;
        DeviceListAdapter adapter;

        public DeviceViewHolder(@NonNull View itemView, DeviceListAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);
            deviceLastConnected = itemView.findViewById(R.id.device_last_connected);

            // Set click listener on the item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.d(TAG, "Item clicked at position: " + position);
                adapter.connectToDevice(position);
            }
        }
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);
        return new DeviceViewHolder(view, this);
    }
    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        HostDevice device = devices.get(position);
        holder.deviceName.setText("Name: " + device.getName());
        holder.deviceAddress.setText("Address: " + device.getAddress());
        holder.deviceLastConnected.setText("Last Connected: " + device.getLastConnected());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void updateDevices(List<HostDevice> newDevices) {
        devices.clear();
        devices.addAll(newDevices);
        notifyDataSetChanged();
    }


}