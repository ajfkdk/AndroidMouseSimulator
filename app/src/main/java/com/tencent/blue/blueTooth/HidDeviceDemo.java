package com.tencent.blue.blueTooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.tencent.blue.MainActivity;

import java.util.List;
import java.util.concurrent.Executors;

public class HidDeviceDemo {
    private static final String TAG = "HidDeviceDemo";
    private BluetoothHidDevice mHidDevice;
    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private Activity mActivity;

    private final BluetoothHidDeviceAppSdpSettings sdpSettings = new BluetoothHidDeviceAppSdpSettings(
            HidConfig.NAME, HidConfig.DESCRIPTION, HidConfig.PROVIDER,
            BluetoothHidDevice.SUBCLASS1_COMBO, HidConfig.KEYBOARD_COMBO);

    private BluetoothDevice mHostDevice;

    public void init(BluetoothAdapter adapter, Activity activity) {
        mActivity = activity;
        mBtAdapter = adapter;


        mBtAdapter.getProfileProxy(mActivity, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                Log.d(TAG, "onServiceConnected:" + i);
                if (i == BluetoothProfile.HID_DEVICE) {
                    if (!(bluetoothProfile instanceof BluetoothHidDevice)) {
                        Log.e(TAG, "Proxy received but it's not BluetoothHidDevice");
                        return;
                    }
                    mHidDevice = (BluetoothHidDevice) bluetoothProfile;
                    registerBluetoothHid();
                    //启动设备发现
                    if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, 1);
                    }
                    mActivity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 1);
                }

            }

            @Override
            public void onServiceDisconnected(int i) {
                Log.d(TAG, "onServiceDisconnected:" + i);

            }
        }, BluetoothProfile.HID_DEVICE);

    }

    private void registerBluetoothHid() {

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        mHidDevice.registerApp(sdpSettings, null, null, Executors.newCachedThreadPool(), new BluetoothHidDevice.Callback() {
            @Override
            public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                }
                Log.d(TAG, "onAppStatusChanged:" + (pluggedDevice != null ? pluggedDevice.getName() : "null") + " registered:" + registered);
                if (registered) {

                    List<BluetoothDevice> matchingDevices = mHidDevice.getDevicesMatchingConnectionStates(new int[]{BluetoothProfile.STATE_CONNECTED});

                    Log.d(TAG, "paired devices: " + matchingDevices + "  " + mHidDevice.getConnectionState(pluggedDevice));
                    if (pluggedDevice != null && mHidDevice.getConnectionState(pluggedDevice) != BluetoothProfile.STATE_CONNECTED) {
                        boolean result = mHidDevice.connect(pluggedDevice);
                        Log.d(TAG, "hidDevice connect:" + result);
                    } else if (matchingDevices != null && matchingDevices.size() > 0) {
                        //TODO 选择设备进行连接
                    } else {
                        //TODO 蓝牙HID注册成功，但未进行配对。
                    }
                }
            }

            @Override
            public void onConnectionStateChanged(BluetoothDevice device, int state) {
                Log.d(TAG, "onConnectionStateChanged:" + device + "  state:" + state);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    mHostDevice = device;
                }
                if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    mHostDevice = null;
                } else if (state == BluetoothProfile.STATE_CONNECTING) {
                    //TODO 正在连接
                    Log.d(TAG, "onConnectionStateChanged: 正在连接");
                }
            }
        });

    }

    public void SendKey() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        mHidDevice.sendReport(mHostDevice, 8, new byte[]{0, 0, 4, 0, 0, 0, 0, 0});

    }
}