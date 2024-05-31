//package com.tencent.blue.blueTooth;
//
//import android.content.Context;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewConfiguration;
//
//
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//public class CustomMotionListener implements View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
//
//    private final GestureDetector mGestureDetector;
//    private BluetoothHidManager mBluetoothHidManager;
//    private int mPointCount;
//
//    private long mDoubleFingerTime;
//
//    private final ScheduledExecutorService mExecutorService;
//
//    private float mPreX;
//    private float mPreY;
//    private boolean mLongPress;
//
//    public CustomMotionListener(Context context, BluetoothHidManager bluetoothHidManager) {
//        mBluetoothHidManager = bluetoothHidManager;
//        mGestureDetector = new GestureDetector(context, this);
//        mGestureDetector.setOnDoubleTapListener(this);
//        mExecutorService = new ScheduledThreadPoolExecutor(1,
//                new BasicThreadFactory.Builder().namingPattern("mouse-schedule-pool-%d").daemon(true).build());
//    }
//
//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public boolean onDoubleTap(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public boolean onDoubleTapEvent(MotionEvent e) {
//        //左键单指双击（选中文本的效果）
//        if (e.getAction() == MotionEvent.ACTION_DOWN) {
//            mBluetoothHidManager.sendLeftClick(true);
//        } else if (e.getAction() == MotionEvent.ACTION_UP) {
//            mBluetoothHidManager.sendLeftClick(false);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onDown(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        //左键单击
//        mBluetoothHidManager.sendLeftClick(true);
//        mBluetoothHidManager.sendLeftClick(false);
//        return true;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        //双指滚动，x为水平滚动，y为垂直滚动，消抖处理
//        if (mPointCount == 2) {
//            if (Math.abs(distanceX) > Math.abs(distanceY))  {
//                distanceX = distanceX > 0 ? 1 : distanceX < 0 ? -1 : 0;
//                distanceY = 0;
//            } else {
//                distanceY = distanceY > 0 ? -1 : distanceY < 0 ? 1 : 0;
//                distanceX = 0;
//            }
//
//            mBluetoothHidManager.sendWheel((byte) (distanceX), (byte) (distanceY));
//        }
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//        //单键长按效果
//        mBluetoothHidManager.sendLeftClick(true);
//        mLongPress = true;
//    }
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        return false;
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//        if (mGestureDetector.onTouchEvent(event)) {
//            return true;
//        }
//        mPointCount = event.getPointerCount();
//
//        switch (event.getActionMasked()) {
//            case MotionEvent.ACTION_POINTER_DOWN:
//                //双指单击代表右键记录时间
//                if (event.getPointerCount() == 2) {
//                    mDoubleFingerTime = System.currentTimeMillis();
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                //单指代表移动鼠标
//                if (event.getPointerCount() == 1) {
//                    float dx = x - mPreX;
//                    if (dx > 127) dx = 127;
//                    if (dx < -128) dx = -128;
//
//                    float dy = y - mPreY;
//                    if (dy > 127) dy = 127;
//                    if (dy < -128) dy = -128;
//
//                    mBluetoothHidManager.senMouse((byte) dx, (byte) dy);
//                } else {
//                    mBluetoothHidManager.senMouse((byte) 0, (byte) 0);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                if (mLongPress) {
//                    mBluetoothHidManager.sendLeftClick(false);
//                    mLongPress = false;
//                }
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                //双指按下代表右键
//                if (event.getPointerCount() == 2 && System.currentTimeMillis() - mDoubleFingerTime < ViewConfiguration.getDoubleTapTimeout()) {
//                    mBluetoothHidManager.sendRightClick(true);
//                    //延时释放避免无效
//                    mExecutorService.scheduleWithFixedDelay(new Runnable() {
//                        @Override
//                        public void run() {
//                            mBluetoothHidManager.sendRightClick(false);
//                        }
//                    }, 0, 50, TimeUnit.MILLISECONDS);                }
//                break;
//            default:
//                break;
//        }
//        mPreX = x;
//        mPreY = y;
//        return true;
//    }
//}
//
