package com.tencent.blue.manager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.util.Log;

public class BluetoothHidMouse {
    private boolean mLeftClick;
    private boolean mRightClick;

    private final BluetoothHidDevice myHidDevice;

    private final BluetoothDevice remoteComputer;

    public boolean canFire = true;

    public  BluetoothHidMouse(BluetoothHidDevice service, BluetoothDevice hostDevice) {
        this.myHidDevice = service;
        this.remoteComputer = hostDevice;
    }
    String TAG = "BluetoothHidMouse";

    public void sendLeftClick(boolean click) {
        if (myHidDevice == null) {
            Log.e(TAG, "sendMouse failed, hid device is null!");
            return;
        }
        if (remoteComputer == null) {
            Log.e(TAG, "sendMouse failed, hid device is not connected!");
            return;
        }

        // 按下左键
        mLeftClick = click;
        sendMouse((byte) 0x00, (byte) 0x00);

        // 确保短暂的延迟以模拟点击效果
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 释放左键
        mLeftClick = false;
        sendMouse((byte) 0x00, (byte) 0x00);
    }

    public void sendRightClick(boolean click) {
        if (myHidDevice == null) {
            Log.e(TAG, "sendMouse failed, hid device is null!");
            return;
        }
        if (remoteComputer == null) {
            Log.e(TAG, "sendMouse failed, hid device is not connected!");
            return;
        }

        // 按下右键
        mRightClick = click;
        sendMouse((byte) 0x00, (byte) 0x00);

        // 确保短暂的延迟以模拟点击效果
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 释放右键
        mRightClick = false;
        sendMouse((byte) 0x00, (byte) 0x00);
    }

    @SuppressLint("MissingPermission")
    public void sendMouse(byte dx, byte dy) {
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
        if (mLeftClick) {
            bytes[0] |= 0b1;
        } else {
            bytes[0] &= 0b11111110;
        }

        if (mRightClick) {
            bytes[0] |= 0b10;
        } else {
            bytes[0] &= 0b11111101;
        }
        //使用二进制的方式把bytes[0]打印出来
        String binaryString = String.format("%8s", Integer.toBinaryString(bytes[0] & 0xFF)).replace(' ', '0');
        bytes[1] = dx;
        bytes[2] = dy;
        Log.d(TAG, "sendMouse dx:" + dx + ",dy：" + dy +"mLeftClick:" + mLeftClick + ",mRightClick:" + mRightClick);
        Log.d(TAG, "bytes[0] binary: " + binaryString);
        mLeftClick = false;
        mRightClick = false;
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
