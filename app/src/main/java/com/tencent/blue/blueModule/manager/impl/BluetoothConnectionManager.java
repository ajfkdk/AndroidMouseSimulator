package com.tencent.blue.blueModule.manager.impl;

import static android.app.Activity.RESULT_OK;

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
            HidConfig.MOUSE_NAME,
            HidConfig.DESCRIPTION,
            HidConfig.PROVIDER,
            BluetoothHidDevice.SUBCLASS1_COMBO,
            HidConfig.MOUSE_COMBO);

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

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE_BT = 2;

    private void makeDeviceDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); // 可被发现的时间（秒）
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
    }

    public void init() {


        if (!mBluetoothAdapter.isEnabled()) {
            // 请求用户启用蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // 使设备可被发现
            makeDeviceDiscoverable();
        }
        // 获取蓝牙HID设备的代理
        mBluetoothAdapter.getProfileProxy(mActivity, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                // 当有设备连接时调用

                // 检查设备类型是否为HID设备
                if (i == BluetoothProfile.HID_DEVICE) {
                    Log.d(TAG, "onServiceConnected: HID设备已连接");
                    // 检查bluetoothProfile是否为BluetoothHidDevice的实例
                    if (!(bluetoothProfile instanceof BluetoothHidDevice)) {
                        Log.e(TAG, "不是HID设备");
                        return;
                    }else{
                        Log.d(TAG, "onServiceConnected: 是HID设备");
                    }
                    // 发现HID设备
                    mHidDevice = (BluetoothHidDevice) bluetoothProfile;
                    registerBluetoothHid(); // 注册HID设备
                }else{
                    Log.d(TAG, "onServiceConnected: HID设备未连接");
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
        // 检查是否具有蓝牙连接权限
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，申请蓝牙连接权限
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        Log.d(TAG, "registerBluetoothHid: 进来了");

        // 注册HID设备
        boolean result=mHidDevice.registerApp(sdpSettings, null, null, Executors.newCachedThreadPool(), new BluetoothHidDevice.Callback() {
            @Override
            public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
                Log.d(TAG, "onAppStatusChanged: 注册状态发生变化");
                // 检查是否具有蓝牙连接权限
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有权限，申请蓝牙连接权限
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                }

                // 打印连接状态变化的日志
                Log.d(TAG, "连接状态发送变化:" + (pluggedDevice != null ? pluggedDevice.getName() : "null") + " registered:" + registered);

                if (registered) {
                    // 注册成功
                    List<BluetoothDevice> matchingDevices = mHidDevice.getDevicesMatchingConnectionStates(new int[]{BluetoothProfile.STATE_CONNECTED});

                    // 打印已匹配上的设备信息
                    Log.d(TAG, "已经匹配上的设备 : " + matchingDevices + "  " + mHidDevice.getConnectionState(pluggedDevice));
                    Log.d(TAG, "onAppStatusChanged: mHidDevice.getConnectionState(pluggedDevice)：" + mHidDevice.getConnectionState(pluggedDevice));

                    // 如果有已插入的设备且设备未连接，则尝试连接
                    if (pluggedDevice != null && mHidDevice.getConnectionState(pluggedDevice) != BluetoothProfile.STATE_CONNECTED) {
                        boolean result = mHidDevice.connect(pluggedDevice);
                        Log.d(TAG, "hidDevice 成功连接！！:" + result);
                    }
                }
                if (!registered) {
                    // 注册失败
                    Log.d(TAG, "注册失败");
                }
            }

            @Override
            public void onConnectionStateChanged(BluetoothDevice device, int state) {
                // 打印连接状态变化的日志
                Log.d(TAG, "连接状态发生变化:" + device + "  state:" + state);

                // 根据状态更新主机设备信息
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    mHostDevice = device;
                }
                if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    mHostDevice = null;
                } else if (state == BluetoothProfile.STATE_CONNECTING) {
                    // 正在连接
                    Log.d(TAG, "onConnectionStateChanged: 正在连接");
                }
            }


        });

        if (result) {
            Log.d(TAG, "registerBluetoothHid: 注册成功");
        } else {
            Log.d(TAG, "registerBluetoothHid: 注册失败");
        }
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