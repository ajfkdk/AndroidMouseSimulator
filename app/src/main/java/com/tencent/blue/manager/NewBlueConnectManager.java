package com.tencent.blue.manager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tencent.blue.MainActivity;
import com.tencent.blue.storage.DeviceStorage;
import com.tencent.blue.storage.HostDevice;

import java.util.Set;

public class NewBlueConnectManager {
    private final Activity mActivity;

    private DeviceStorage devices;

    public BluetoothAdapter bluetoothAdapter;

    private BluetoothHidDevice myHidDevice;

    private BluetoothDevice remoteComputer;

    public BluetoothHidMouse mouse = null;

    private static final int PERMISSION_CODE = 100;
    private static final int PERMISSION_CODE_BACKGROUND_LOCATION = 1001;
    private static final int PERMISSION_CODE_BLUETOOTH_CONNECT = 1002;
    private static final int REQUEST_ENABLE_BT = 1003;
    private static final int REQUEST_DISCOVERABLE_BT = 1004;

    private final BluetoothHidDeviceAppSdpSettings sdpSettings = new BluetoothHidDeviceAppSdpSettings(
            HidConfig.MOUSE_NAME,
            HidConfig.DESCRIPTION,
            HidConfig.PROVIDER,
            BluetoothHidDevice.SUBCLASS1_MOUSE,
            HidConfig.MOUSE_COMBO);

    private BluetoothAdapter getAdapter() {
        // 检查并请求后台位置权限
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSION_CODE_BACKGROUND_LOCATION);
        }

        // 检查并请求蓝牙连接权限
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    PERMISSION_CODE_BLUETOOTH_CONNECT);
        }
        BluetoothAdapter bluetoothAdapter = null;
        //BluetoothAdapter bluetoothAdapter 获取默认的蓝牙适配器
        BluetoothManager bluetoothManage = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManage != null) {
            bluetoothAdapter = bluetoothManage.getAdapter();
        }
        // 检查设备是否支持蓝牙
        if (bluetoothAdapter == null) {
            Toast.makeText(mActivity, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return null; // 设备不支持蓝牙，返回 null
        }

        // 检查蓝牙是否已开启
        if (!bluetoothAdapter.isEnabled()) {
            // 蓝牙未开启，发起请求以启用蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Toast.makeText(mActivity, "蓝牙已开启", Toast.LENGTH_SHORT).show();
        }

        // 返回蓝牙适配器对象
        return bluetoothAdapter;
    }

    private static final String TAG = "new Connect Manager:";

    public NewBlueConnectManager(Activity activity, DeviceStorage deviceStorage) {
        mActivity = activity;
        devices = deviceStorage;
    }


    public void init() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_CODE);
        }
        bluetoothAdapter = getAdapter();
        if (bluetoothAdapter == null) {
            return;
        }

        // 获取蓝牙HID设备的代理
        boolean profileProxy = bluetoothAdapter.getProfileProxy(mActivity, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                //和蓝牙配置文件服务建立连接
                Log.d(TAG, "onServiceConnected: 和蓝牙配置文件服务建立连接");
                if (profile == BluetoothProfile.HID_DEVICE) {
                    // 得到了一个模拟出来的蓝牙HID设备
                    myHidDevice = (BluetoothHidDevice) proxy;
                    // 注册蓝牙HID设备
                    registerBluetoothHid();
                } else {
                    Log.d(TAG, "onServiceConnected: 未知的蓝牙配置文件, profile: " + profile);
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                Log.d(TAG, "onServiceDisconnected: 和蓝牙配置文件服务断开连接");
            }
        }, BluetoothProfile.HID_DEVICE);

        if (!profileProxy) {
            Log.d(TAG, "init: 蓝牙HID设备代理获取失败");
            return;
        }

    }

    public void passiveScan() {
        makeDeviceDiscoverable();
        registerBluetoothHid();
    }

    public void defaultConnect(){
        HostDevice device = devices.getDevice(0);
        if (device != null) {
            activeConnect(device.getAddress());
        }else{
            Toast.makeText(mActivity, "没有已配对设备", Toast.LENGTH_SHORT).show();
        }
    }

    public void activeConnect(String address) {
        remoteComputer = bluetoothAdapter.getRemoteDevice(address);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_CODE);
        }

        if (remoteComputer == null) {
            Log.d(TAG, "activeConnect: 设备未找到");
            return;
        } else {
            Log.d(TAG, "activeConnect: 设备已找到");
            Log.d(TAG, "activeConnect: 设备名称: " + remoteComputer.getName());
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        boolean isPaired = false;
        for (BluetoothDevice device : pairedDevices) {
            Log.d(TAG, "activeConnect----------------");
            Log.d(TAG, "activeConnect name: " + device.getName());
            Log.d(TAG, "activeConnect: address: " + device.getAddress());
            Log.d(TAG, "activeConnect: address: " + address);
            Log.d(TAG, "activeConnect----------------");

            if (device.getAddress().equals(address)) {
                isPaired = true;
                break;
            }
        }
        if (!isPaired) {
            Log.d(TAG, "activeConnect: 设备未配对");
            return;
        }

        Log.d(TAG, "activeConnect: bluetoothAdapter.isEnabled() " + bluetoothAdapter.isEnabled());
        boolean connect = myHidDevice.connect(remoteComputer);
        if (connect) {
            Log.d(TAG, "activeConnect: 连接成功");
        } else {
            Log.d(TAG, "activeConnect: 连接失败");
        }
    }


    private void makeDeviceDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); // 可被发现的时间（秒）
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            mActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
            return;
        }
        mActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
    }

    // 开启
    private void registerBluetoothHid() {
        // 检查是否具有蓝牙连接权限
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，申请蓝牙连接权限
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }

        // 注册HID设备
        myHidDevice.registerApp(sdpSettings, null, null, mActivity.getMainExecutor(), new BluetoothHidDevice.Callback() {
            @Override
            public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                }
                Log.d(TAG, "应用状态变化: " + (registered ? "已注册" : "未注册") + ", 插入设备: " + (pluggedDevice != null ? pluggedDevice.getName() : "无"));
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
                        HostDevice hostDevice = new HostDevice(device.getAddress(), device.getName(), System.currentTimeMillis());
                        devices.addDevice(hostDevice);
                        ((MainActivity) mActivity).updateBluetoothStatus();
                        remoteComputer = device;
                        mouse = new BluetoothHidMouse(myHidDevice, remoteComputer);
                        Toast.makeText(mActivity, "设备已连接", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTING:
                        Log.d(TAG, "设备正在断开连接: " + device.getName());
                        break;
                }
            }


        });

    }



    public boolean isConnected() {
        return mouse != null;
    }

    public void showPaired() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_CODE);
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // 检查是否有已配对设备
        if (!pairedDevices.isEmpty()) {
            // 遍历已配对设备集合
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceAddress = device.getAddress(); // MAC 地址

                // 显示设备信息
                Log.d("PairedDevice", "设备名称: " + deviceName + ", 设备地址: " + deviceAddress);
            }
        } else {
            Log.d(TAG, "showPaired: 没有已配对设备");
        }
    }


}
