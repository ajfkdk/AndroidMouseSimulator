package com.tencent.blue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.blue.storage.HostDevice;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {
    private List<HostDevice> devices;

    public DeviceListAdapter(List<HostDevice> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);
        return new DeviceViewHolder(view);
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

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceLastConnected;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);
            deviceLastConnected = itemView.findViewById(R.id.device_last_connected);
        }
    }
}