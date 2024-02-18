package com.tencent.blue.blueTooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BluetoothService {
    private static final int REQUEST_ENABLE_BT = 1;
    private final Context mContext;
    private final BluetoothAdapter mBluetoothAdapter;

    public BluetoothService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setupBluetoothConnection() {
        // 检查设备是否支持蓝牙
        if (mBluetoothAdapter == null) {
            // 设备不支持蓝牙
            Log.e("BluetoothService", "Device doesn't support Bluetooth");
            return;
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 请求 BLUETOOTH_CONNECT 权限
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        } else {
            enableBluetooth();
        }
    }

    public void enableBluetooth() {
        // 如果蓝牙未打开，则请求用户开启
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText(mContext, "蓝牙开启！", Toast.LENGTH_SHORT).show();
            // startDiscovery();
        }
    }

    // 在您的 Activity 中处理权限请求的结果
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被授予，启用蓝牙
                    enableBluetooth();
                } else {
                    // 权限被拒绝，向用户解释为什么需要这个权限
                    Toast.makeText(mContext, "需要蓝牙权限来进行连接", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}