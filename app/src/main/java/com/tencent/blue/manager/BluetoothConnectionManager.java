package com.tencent.blue.manager;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tencent.blue.blueTooth.HidConfig;

public class BluetoothConnectionManager {
    private Activity mActivity;

    private static final int PERMISSION_CODE = 100;

    private BluetoothHidDevice service;
    private BluetoothDevice mHostDevice;

    private static final String TAG = "Connect Manager:";

    private BluetoothAdapter mBluetoothAdapter;

    private final BluetoothHidDeviceAppSdpSettings sdpSettings = new BluetoothHidDeviceAppSdpSettings(
            HidConfig.MOUSE_NAME,
            HidConfig.DESCRIPTION,
            HidConfig.PROVIDER,
            BluetoothHidDevice.SUBCLASS1_MOUSE,
            HidConfig.MOUSE_COMBO);


    public BluetoothHidDevice getService() {
        return service;
    }

    public BluetoothDevice getHostDevice() {
        return mHostDevice;
    }

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
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); // 可被发现的时间（秒）
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            mActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
            return;
        }
        mActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
    }

    public void init() {

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
                }
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // 发现设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // 处理设备 (例如：保存到列表，显示给用户)
                    Log.d(TAG, "onReceive: " + device.getName() + " : " + device.getAddress());
                    if (device.getName() != null ) {
                        mHostDevice = device;
                        Log.d(TAG, "onReceive: " + device.getName() + " : " + device.getAddress());
                        mBluetoothAdapter.cancelDiscovery();
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        service.connect(mHostDevice);
                    }
                }
            }
        };

// 注册接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mActivity, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);

// 开始扫描

        mBluetoothAdapter.startDiscovery();

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
                    } else {
                        Log.d(TAG, "onServiceConnected: 是HID设备");
                    }
                    // 发现HID设备
                    service = (BluetoothHidDevice) bluetoothProfile;
                    registerBluetoothHid(); // 注册HID设备
                } else {
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
        boolean result = service.registerApp(sdpSettings, null, null, mActivity.getMainExecutor(), new BluetoothHidDevice.Callback() {
            @Override
            public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                }
                Log.d(TAG, "应用状态变化: " + (registered ? "已注册" : "未注册") +
                        ", 插入设备: " + (pluggedDevice != null ? pluggedDevice.getName() : "无"));
            }

            @Override
            public void onConnectionStateChanged(BluetoothDevice device, int state) {
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                }
                Log.d(TAG, "连接状态变化: " + device.getName() + ", 状态: " + state);
                switch (state) {
                    case BluetoothProfile.STATE_DISCONNECTED:
                        Log.d(TAG, "设备已断开连接: " + device.getName());
                        break;
                    case BluetoothProfile.STATE_CONNECTING:
                        Log.d(TAG, "设备正在连接: " + device.getName());
                        break;
                    case BluetoothProfile.STATE_CONNECTED:
                        Log.d(TAG, "设备已连接: " + device.getName());
                        mHostDevice = device;
                        service.connect(device);
                        break;
                    case BluetoothProfile.STATE_DISCONNECTING:
                        Log.d(TAG, "设备正在断开连接: " + device.getName());
                        break;
                }
            }


        });

        if (result) {
            Log.d(TAG, "registerBluetoothHid: 注册成功");
        } else {
            Log.d(TAG, "registerBluetoothHid: 注册失败");
        }
    }


    public void waitToConnect() {
        init();
    }

    public void disconnect() {
        if (mHostDevice != null) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            service.disconnect(mHostDevice);
            Toast.makeText(mActivity, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isConnected() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        if (mHostDevice != null) {
            return service.getConnectionState(mHostDevice) == BluetoothHidDevice.STATE_CONNECTED;
        } else {
            return false;
        }
    }

    public void sendData(byte[] data) {
        if (mHostDevice != null) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            service.sendReport(mHostDevice, 4, data);
            Log.d(TAG, "sendData: ");

        } else {
            Toast.makeText(mActivity, "设备未连接", Toast.LENGTH_SHORT).show();
        }
    }
}