package com.tencent.blue.blueModule.manager.impl;

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
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tencent.blue.blueModule.manager.IBluetoothConnectionManager;
import com.tencent.blue.blueTooth.HidConfig;

import java.util.List;
import java.util.concurrent.Executors;

public class BluetoothConnectionManager implements IBluetoothConnectionManager {
    private Activity mActivity;

    private static final int PERMISSION_CODE = 100;

    private BluetoothHidDevice mHidDevice;

    private static final String TAG = "Connect Manager:";

    private BluetoothAdapter mBluetoothAdapter;

    private final BluetoothHidDeviceAppSdpSettings sdpSettings = new BluetoothHidDeviceAppSdpSettings(
            HidConfig.NAME,
            HidConfig.DESCRIPTION,
            HidConfig.PROVIDER,
            BluetoothHidDevice.SUBCLASS1_COMBO,
            HidConfig.KEYBOARD_COMBO);

    private BluetoothDevice mHostDevice;

    private BluetoothAdapter getAdapter() {
        // 检查是否有后台位置权限
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，申请后台位置权限
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSION_CODE);
        }

        // 检查是否有蓝牙连接权限
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，申请蓝牙连接权限
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }

        // 获取默认的蓝牙适配器
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 检查设备是否支持蓝牙
        if (bluetoothAdapter == null) {
            // 设备不支持蓝牙，显示提示信息
            Toast.makeText(mActivity, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        } else {
            // 检查蓝牙是否已开启
            if (!bluetoothAdapter.isEnabled()) {
                // 蓝牙未开启，发起请求以启用蓝牙
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBtIntent, 1);
            } else {
                // 蓝牙已开启，显示提示信息
                Toast.makeText(mActivity, "蓝牙已开启", Toast.LENGTH_SHORT).show();
            }
        }

        // 返回蓝牙适配器对象
        return bluetoothAdapter;
    }

    public BluetoothConnectionManager(Activity activity) {
        this.mActivity = activity;
        mBluetoothAdapter = getAdapter();

    }


    public void init() {
        // 获取蓝牙HID设备的代理
        mBluetoothAdapter.getProfileProxy(mActivity, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                // 当有设备连接时调用
                Log.d(TAG, "有设备连接");

                // 检查设备类型是否为HID设备
                if (i == BluetoothProfile.HID_DEVICE) {
                    // 检查bluetoothProfile是否为BluetoothHidDevice的实例
                    if (!(bluetoothProfile instanceof BluetoothHidDevice)) {
                        Log.e(TAG, "不是HID设备");
                        return;
                    }
                    // 发现HID设备
                    mHidDevice = (BluetoothHidDevice) bluetoothProfile;
                    registerBluetoothHid(); // 注册HID设备
                }
            }

            @Override
            public void onServiceDisconnected(int i) {
                // 当服务断开连接时调用
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

                Log.d(TAG, "连接状态发送变化:" + (pluggedDevice != null ? pluggedDevice.getName() : "null") + " registered:" + registered);

                if (registered) {
                    //表示注册成功
                    List<BluetoothDevice> matchingDevices = mHidDevice.getDevicesMatchingConnectionStates(new int[]{BluetoothProfile.STATE_CONNECTED});

                    Log.d(TAG, "已经匹配上的设备 : " + matchingDevices + "  " + mHidDevice.getConnectionState(pluggedDevice));
                    if (pluggedDevice != null && mHidDevice.getConnectionState(pluggedDevice) != BluetoothProfile.STATE_CONNECTED) {
                        boolean result = mHidDevice.connect(pluggedDevice);
                        Log.d(TAG, "hidDevice connect:" + result);
                    } else if (matchingDevices != null && matchingDevices.size() > 0) {
//                        Toast.makeText(mActivity, "没有设备当前连接，但存在已配对的设备（matchingDevices列表不为空）", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onAppStatusChanged: 没有设备当前连接，但存在已配对的设备（matchingDevices列表不为空）");
                    } else {
//                        Toast.makeText(mActivity, "没有已配对或已连接的设备", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onAppStatusChanged: 没有已配对或已连接的设备");
                    }
                }
            }

            @Override
            public void onConnectionStateChanged(BluetoothDevice device, int state) {
                Log.d(TAG, "连接状态发生变化:" + device + "  state:" + state);
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


    @Override
    public void waitToConnect() {
        init();
    }

    @Override
    public void disconnect() {
        if (mHostDevice != null) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            mHidDevice.disconnect(mHostDevice);
            Toast.makeText(mActivity, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isConnected() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        List<BluetoothDevice> devicesMatchingConnectionStates = mHidDevice.getDevicesMatchingConnectionStates(new int[]{BluetoothProfile.STATE_CONNECTED});
        return devicesMatchingConnectionStates != null && devicesMatchingConnectionStates.size() > 0;
    }

    @Override
    public void sendData(byte[] data) {
        if (mHostDevice != null) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            mHidDevice.sendReport(mHostDevice, 4, data);
            Log.d(TAG, "sendData: ");

        }else{
            Toast.makeText(mActivity, "设备未连接", Toast.LENGTH_SHORT).show();
        }
    }
}