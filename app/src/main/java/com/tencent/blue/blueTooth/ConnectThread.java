package com.tencent.blue.blueTooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;

    private final String TAG = "ConnectThread";

    private static final java.util.UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private final BluetoothAdapter bluetoothAdapter;

    private final Activity mActivity;

    public ConnectThread(BluetoothDevice device, Activity activity, BluetoothAdapter adapter) {
        bluetoothAdapter = adapter;
        mActivity = activity;
        BluetoothSocket tmp = null;

        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
        //关闭蓝牙扫描，加速与蓝牙服务器的连接
        bluetoothAdapter.cancelDiscovery();

        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to waitToConnect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }


//        manageMyConnectedSocket(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
