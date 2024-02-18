package com.tencent.blue.blueTooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// 创建一个BluetoothService类来管理蓝牙功能
public class BluetoothService {
    // 请求码，用于Intent回调
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_SCAN = 2; // 适用于蓝牙扫描的请求码

    // 上下文，通常是调用服务的Activity
    private final Context mContext;

    // 蓝牙适配器，用于管理蓝牙硬件
    private final BluetoothAdapter mBluetoothAdapter;

    // 广播接收者，用于监听找到新蓝牙设备的意图
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // 获取动作（行为）字符串
            String action = intent.getAction();
            Log.d("hello", "onReceive: " + action);
            // 如果发现了蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 从Intent获取蓝牙设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // 检查蓝牙连接权限
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有权限，则请求权限
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                }
                // 确保设备不为空
                assert device != null;
                // 记录设备的名称和地址到日志
                Log.d("hello", "onReceive: " + device.getName() + " " + device.getAddress());
            }
        }
    };

    // 构造函数，初始化上下文和蓝牙适配器
    public BluetoothService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // 设置蓝牙连接的方法
    public void setupBluetoothConnection() {
        // 如果设备不支持蓝牙，则记录错误并返回
        if (mBluetoothAdapter == null) {
            Log.e("hello", "Device doesn't support Bluetooth");
            return;
        }

        // 检查蓝牙连接权限
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，则请求权限
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        } else {
            // 如果有权限，则启用蓝牙
            enableBluetooth();
        }
    }

    // 启用蓝牙的方法
    public void enableBluetooth() {
        // 如果蓝牙未开启
        if (!mBluetoothAdapter.isEnabled()) {
            // 创建一个请求启用蓝牙的Intent
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 检查权限并请求启用蓝牙
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            // 如果蓝牙已经开启，则显示Toast消息
            Log.d("hello", "Bluetooth is already enabled");
        }
        startDiscovery();

    }


    // 开始蓝牙设备发现的方法
    public void startDiscovery() {
        // 检查蓝牙扫描权限
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，则请求权限
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN);
        } else {
            // 如果有权限，则开始蓝牙扫描并注册广播接收者以监听找到设备的广播
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            mContext.registerReceiver(mReceiver, filter);
        }
    }

    // 停止蓝牙设备发现的方法
    public void stopDiscovery() {
        // 检查蓝牙扫描权限
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // 如果有权限，则取消蓝牙设备发现并注销广播接收者
            mBluetoothAdapter.cancelDiscovery();
            mContext.unregisterReceiver(mReceiver);
        }
    }
}