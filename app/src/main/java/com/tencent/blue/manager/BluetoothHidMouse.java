package com.tencent.blue.manager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.util.Log;

public class BluetoothHidMouse {

    private final BluetoothHidDevice myHidDevice;

    private final BluetoothDevice remoteComputer;

    public boolean canFire = true;

    public  BluetoothHidMouse(BluetoothHidDevice service, BluetoothDevice hostDevice) {
        this.myHidDevice = service;
        this.remoteComputer = hostDevice;
    }
    String TAG = "BluetoothHidMouse";

    @SuppressLint("MissingPermission")
    public void sendLeftClick(boolean click) {
        if (myHidDevice == null) {
            Log.e(TAG, "sendMouse failed, hid device is null!");
            return;
        }
        if (remoteComputer == null) {
            Log.e(TAG, "sendMouse failed, hid device is not connected!");
            return;
        }
        if (!canFire){
            return;
        }
        // 按下左键
        byte[] bytes = new byte[5];
        bytes[0] |= 0b1;//按下左键
        myHidDevice.sendReport(remoteComputer, 4, bytes);

        // 确保短暂的延迟以模拟点击效果
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 释放左键
        bytes[0] &= 0b11111110;
        myHidDevice.sendReport(remoteComputer, 4, bytes);
    }
    @SuppressLint("MissingPermission")
    public void sendRightClick(boolean click) {
        if (myHidDevice == null) {
            Log.e(TAG, "sendMouse failed, hid device is null!");
            return;
        }
        if (remoteComputer == null) {
            Log.e(TAG, "sendMouse failed, hid device is not connected!");
            return;
        }
        if (!canFire){
            return;
        }
        // 按下右键
        byte[] bytes = new byte[5];
        bytes[0] |= 0b10;//按下右键
        myHidDevice.sendReport(remoteComputer, 4, bytes);
        // 确保短暂的延迟以模拟点击效果
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 释放右键
        bytes[0] &= 0b11111101;
        myHidDevice.sendReport(remoteComputer, 4, bytes);
    }

    @SuppressLint("MissingPermission")
    public void move(byte dx, byte dy) {
        if (myHidDevice == null) {
            Log.e(TAG, "sendMouse failed,  hid device is null!");
            return;
        }
        if (remoteComputer == null) {
            Log.e(TAG, "sendMouse failed,  hid device is not connected!");
            return;
        }

        if (!canFire){
            return;
        }


        byte[] bytes = new byte[5];
        //bytes[0]字节：bit0: 1表示左键按下 0表示左键抬起 | bit1: 1表示右键按下 0表示右键抬起 | bit2: 1表示中键按下 | bit7～3：补充的常数，无意义，这里为0即可
        bytes[0] = 0;

        //使用二进制的方式把bytes[0]打印出来
        String binaryString = String.format("%8s", Integer.toBinaryString(bytes[0] & 0xFF)).replace(' ', '0');
        bytes[1] = dx;
        bytes[2] = dy;
        myHidDevice.sendReport(remoteComputer, 4, bytes);
    }

    @SuppressLint("MissingPermission")
    public void sendWheel(byte hWheel, byte vWheel) {
        if (myHidDevice == null) {
            Log.e(TAG, "sendWheel failed,  hid device is null!");
            return;
        }
        if (remoteComputer == null) {
            Log.e(TAG, "sendWheel failed,  hid device is not connected!");
            return;
        }

        byte[] bytes = new byte[5];
        bytes[3] = vWheel; //垂直滚轮
        bytes[4] = hWheel; //水平滚轮
        Log.d(TAG, "sendWheel vWheel:" + vWheel + ",hWheel：" + hWheel);
        myHidDevice.sendReport(remoteComputer, 4, bytes);
    }

}
