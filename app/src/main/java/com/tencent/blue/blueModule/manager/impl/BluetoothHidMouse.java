package com.tencent.blue.blueModule.manager.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.util.Log;

public class BluetoothHidMouse {
    private boolean mLeftClick;
    private boolean mRightClick;

    private final BluetoothHidDevice service;

    private final BluetoothDevice mHostDevice;

    public  BluetoothHidMouse(BluetoothHidDevice service, BluetoothDevice hostDevice) {
        this.service = service;
        this.mHostDevice = hostDevice;
    }

    public void sendLeftClick(boolean click) {
        mLeftClick = click;
        senMouse((byte) 0x00, (byte) 0x00);
    }
    private static final String TAG = "Connect Manager:";

    public void sendRightClick(boolean click) {
        mRightClick = click;
        senMouse((byte) 0x00, (byte) 0x00);
    }

    @SuppressLint("MissingPermission")
    public void senMouse(byte dx, byte dy) {
        if (service == null) {
            Log.e(TAG, "senMouse failed,  hid device is null!");
            return;
        }
        if (mHostDevice == null) {
            Log.e(TAG, "senMouse failed,  hid device is not connected!");
            return;
        }

        byte[] bytes = new byte[5];
        //bytes[0]字节：bit0: 1表示左键按下 0表示左键抬起 | bit1: 1表示右键按下 0表示右键抬起 | bit2: 1表示中键按下 | bit7～3：补充的常数，无意义，这里为0即可
        bytes[0] = (byte) (bytes[0] | (mLeftClick ? 1 : 0));
        bytes[0] = (byte) (bytes[0] | (mRightClick ? 1 : 0) << 1);
        bytes[1] = dx;
        bytes[2] = dy;
        Log.d(TAG, "senMouse   Left:" + mLeftClick+ ",Right:" + mRightClick );
        service.sendReport(mHostDevice, 4, bytes);
    }

    @SuppressLint("MissingPermission")
    public void sendWheel(byte hWheel, byte vWheel) {
        if (service == null) {
            Log.e(TAG, "sendWheel failed,  hid device is null!");
            return;
        }
        if (mHostDevice == null) {
            Log.e(TAG, "sendWheel failed,  hid device is not connected!");
            return;
        }

        byte[] bytes = new byte[5];
        bytes[3] = vWheel; //垂直滚轮
        bytes[4] = hWheel; //水平滚轮
        Log.d(TAG, "sendWheel vWheel:" + vWheel + ",hWheel：" + hWheel);
        service.sendReport(mHostDevice, 4, bytes);
    }

}
