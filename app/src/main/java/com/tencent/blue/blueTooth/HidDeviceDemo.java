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
                Log.d(TAG, "有设备连接");
                if (i == BluetoothProfile.HID_DEVICE) {
                    if (!(bluetoothProfile instanceof BluetoothHidDevice)) {
                        Log.e(TAG, "不是HID设备");
                        return;
                    }
                    //发现设备了
                    mHidDevice = (BluetoothHidDevice) bluetoothProfile;
                    registerBluetoothHid();
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

                Log.d(TAG, "连接状态发送变化:" + (pluggedDevice != null ? pluggedDevice.getName() : "null") + " registered:" + registered);

                if (registered) {
                    //表示注册成功
                    List<BluetoothDevice> matchingDevices = mHidDevice.getDevicesMatchingConnectionStates(new int[]{BluetoothProfile.STATE_CONNECTED});

                    Log.d(TAG, "已经匹配上的设备 : " + matchingDevices + "  " + mHidDevice.getConnectionState(pluggedDevice));
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

    public void Send(byte[] data) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        mHidDevice.sendReport(mHostDevice, 8, data);

    }

    public void close() {
        if (mHidDevice != null) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
              mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            mHidDevice.unregisterApp();
        }
    }

    private static final int KEY_PACKET_MODIFIER_KEY_INDEX = 0;

    public static final int MODIFIER_KEY_SHIFT = 2;

    private static final int KEY_PACKET_KEY_INDEX = 2;

    private static final byte[] EMPTY_REPORT = new byte[8];

    public void test(){
        if (mHidDevice != null) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            Log.d(TAG, "test: 发送f");
            mHidDevice.sendReport(mHostDevice, 8, new byte[]{0, 0, 4, 0, 0, 0, 0, 0});
//            mHidDevice.sendReport(mHostDevice, 8, new byte[]{0, 0, 0, 0, 0, 0, 0, 0});

        }
    }


    public static byte modifier(final String aChar) {
        switch (aChar) {
            case "A":
            case "B":
            case "C":
            case "D":
            case "E":
            case "F":
            case "G":
            case "H":
            case "I":
            case "J":
            case "K":
            case "L":
            case "M":
            case "N":
            case "O":
            case "P":
            case "Q":
            case "R":
            case "S":
            case "T":
            case "U":
            case "V":
            case "W":
            case "X":
            case "Y":
            case "Z":
            case "!":
            case "@":
            case "#":
            case "$":
            case "%":
            case "^":
            case "&":
            case "*":
            case "(":
            case ")":
            case "_":
            case "+":
            case "{":
            case "}":
            case "|":
            case ":":
            case "\"":
            case "~":
            case "<":
            case ">":
            case "?":
                return MODIFIER_KEY_SHIFT;
            default:
                return 0;
        }
    }

    /**
     * Key code for US Keyboard
     *
     * @param aChar String contains one character
     * @return keyCode
     */
    public static byte keyCode(final String aChar) {
        switch (aChar) {
            case "A":
            case "a":
                return 0x04;
            case "B":
            case "b":
                return 0x05;
            case "C":
            case "c":
                return 0x06;
            case "D":
            case "d":
                return 0x07;
            case "E":
            case "e":
                return 0x08;
            case "F":
            case "f":
                return 0x09;
            case "G":
            case "g":
                return 0x0a;
            case "H":
            case "h":
                return 0x0b;
            case "I":
            case "i":
                return 0x0c;
            case "J":
            case "j":
                return 0x0d;
            case "K":
            case "k":
                return 0x0e;
            case "L":
            case "l":
                return 0x0f;
            case "M":
            case "m":
                return 0x10;
            case "N":
            case "n":
                return 0x11;
            case "O":
            case "o":
                return 0x12;
            case "P":
            case "p":
                return 0x13;
            case "Q":
            case "q":
                return 0x14;
            case "R":
            case "r":
                return 0x15;
            case "S":
            case "s":
                return 0x16;
            case "T":
            case "t":
                return 0x17;
            case "U":
            case "u":
                return 0x18;
            case "V":
            case "v":
                return 0x19;
            case "W":
            case "w":
                return 0x1a;
            case "X":
            case "x":
                return 0x1b;
            case "Y":
            case "y":
                return 0x1c;
            case "Z":
            case "z":
                return 0x1d;
            case "!":
            case "1":
                return 0x1e;
            case "@":
            case "2":
                return 0x1f;
            case "#":
            case "3":
                return 0x20;
            case "$":
            case "4":
                return 0x21;
            case "%":
            case "5":
                return 0x22;
            case "^":
            case "6":
                return 0x23;
            case "&":
            case "7":
                return 0x24;
            case "*":
            case "8":
                return 0x25;
            case "(":
            case "9":
                return 0x26;
            case ")":
            case "0":
                return 0x27;
            case "\n": // LF
                return 0x28;
            case "\b": // BS
                return 0x2a;
            case "\t": // TAB
                return 0x2b;
            case " ":
                return 0x2c;
            case "_":
            case "-":
                return 0x2d;
            case "+":
            case "=":
                return 0x2e;
            case "{":
            case "[":
                return 0x2f;
            case "}":
            case "]":
                return 0x30;
            case "|":
            case "\\":
                return 0x31;
            case ":":
            case ";":
                return 0x33;
            case "\"":
            case "'":
                return 0x34;
            case "~":
            case "`":
                return 0x35;
            case "<":
            case ",":
                return 0x36;
            case ">":
            case ".":
                return 0x37;
            case "?":
            case "/":
                return 0x38;
            default:
                return 0;
        }
    }
    public void sendKeyUp() {
        Send(EMPTY_REPORT);
    }

    public void sendKeys(final String text) {
        String lastKey = null;
        for (int i = 0; i < text.length(); i++) {
            final String key = text.substring(i, i + 1);
            final byte[] report = new byte[8];
            report[KEY_PACKET_MODIFIER_KEY_INDEX] = modifier(key);
            report[KEY_PACKET_KEY_INDEX] = keyCode(key);

            if (key.equals(lastKey)) {
                sendKeyUp();
            }
            Send(report);
            lastKey = key;
        }
        sendKeyUp();
    }

}