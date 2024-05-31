//package com.tencent.blue.blueTooth;
//
//import android.util.Log;
//
//public class BluetoothHidManager {
//    private boolean mLeftClick;
//    private boolean mRightClick;
//
//    private
//
//    public void sendLeftClick(boolean click) {
//        mLeftClick = click;
//        senMouse((byte) 0x00, (byte) 0x00);
//    }
//
//    public void sendRightClick(boolean click) {
//        mRightClick = click;
//        senMouse((byte) 0x00, (byte) 0x00);
//    }
//
//    public void senMouse(byte dx, byte dy) {
//        if (mHidDevice == null) {
//            Log.e(TAG, "senMouse failed,  hid device is null!");
//            return;
//        }
//        if (mHostDevice == null) {
//            Log.e(TAG, "senMouse failed,  hid device is not connected!");
//            return;
//        }
//
//        byte[] bytes = new byte[5];
//        //bytes[0]字节：bit0: 1表示左键按下 0表示左键抬起 | bit1: 1表示右键按下 0表示右键抬起 | bit2: 1表示中键按下 | bit7～3：补充的常数，无意义，这里为0即可
//        bytes[0] = (byte) (bytes[0] | (mLeftClick ? 1 : 0));
//        bytes[0] = (byte) (bytes[0] | (mRightClick ? 1 : 0) << 1);
//        bytes[1] = dx;
//        bytes[2] = dy;
//        Log.d(TAG, "senMouse   Left:" + mLeftClick+ ",Right:" + mRightClick + ",bytes: " + BluetoothUtils.bytesToHexString(bytes));
//        mHidDevice.sendReport(mHostDevice, 4, bytes);
//    }
//
//    public void sendWheel(byte hWheel, byte vWheel) {
//        if (mHidDevice == null) {
//            Log.e(TAG, "sendWheel failed,  hid device is null!");
//            return;
//        }
//        if (mHostDevice == null) {
//            Log.e(TAG, "sendWheel failed,  hid device is not connected!");
//            return;
//        }
//
//        byte[] bytes = new byte[5];
//        bytes[3] = vWheel; //垂直滚轮
//        bytes[4] = hWheel; //水平滚轮
//        Log.d(TAG, "sendWheel vWheel:" + vWheel + ",hWheel：" + hWheel);
//        mHidDevice.sendReport(mHostDevice, 4, bytes);
//    }
//
//}
