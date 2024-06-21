package com.tencent.blue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.tencent.blue.manager.NewBlueConnectManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UdpServer {

    private static final String TAG = "UdpServer";
    private static final int SERVER_PORT = 12345; // Ensure this matches the Python client port
    private final ExecutorService executorService;
    private Future<?> udpServerTask;
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 1080;

    private static final int maxMoveStep = 127;

    // 当前鼠标位置
    private int currentX = 960;
    private int currentY = 540;

    // 屏幕中心位置
    private static final int SCREEN_CENTER_X = SCREEN_WIDTH / 2;
    private static final int SCREEN_CENTER_Y = SCREEN_HEIGHT / 2;

    private NewBlueConnectManager connectionManager;


    private static final int BUFFER_SIZE = 1400; // 每个数据块的大小

    private static final int HEADER_SIZE = 5; // 消息头大小（1字节的消息类型 + 2字节的块索引 + 2字节的总块数）

    private byte[][] imageChunks;
    private int totalChunks;
    private int receivedChunks;

    BitmapFactory.Options bitFactoryoptions;

    // 压枪力度
    private int forceValue = 3;

    public UdpServer() {
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void start(NewBlueConnectManager connectionManager) {
        udpServerTask = executorService.submit(this::runServer);
        Log.d(TAG, "start: UDP server started");
        this.connectionManager = connectionManager;
        bitFactoryoptions = new BitmapFactory.Options();//用于设置图片解码的参数
        bitFactoryoptions.inPreferredConfig = Bitmap.Config.RGB_565; // 使用RGB_565配置作为备用

    }

    public void stop() {
        if (udpServerTask != null) {
            udpServerTask.cancel(true);
        }
        executorService.shutdown();
    }

    private void runServer() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(SERVER_PORT);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                onMessageReceived(buffer);
            }
        } catch (Exception e) {
            if (!Thread.currentThread().isInterrupted()) {
                Log.e(TAG, "Error in UDP server", e);
            }
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            Log.d(TAG, "UDP server stopped");
        }
    }

    private void onMessageReceived(byte[] message) {
        try {
              ByteBuffer byteBuffer = ByteBuffer.wrap(message).order(ByteOrder.BIG_ENDIAN);
            byte messageType = byteBuffer.get(); // 读取消息头
            switch (messageType) {
                case 0x01: // 鼠标移动
                    int x = byteBuffer.getInt();
                    int y = byteBuffer.getInt();
                    Log.d(TAG, "onMessageReceived: Move Mouse x=" + x + ", y=" + y);
                    moveTo(x, y);
                    break;
                case 0x02: // 图像数据
                    int chunkIndex = byteBuffer.getShort(); // 当前数据块的索引
                    int totalChunks = byteBuffer.getShort(); // 总数据块数量
                    byte[] imageData = new byte[message.length - HEADER_SIZE];
                    byteBuffer.get(imageData); // 读取图像数据

                    // 初始化数据缓冲区
                    if (imageChunks == null || this.totalChunks != totalChunks) {
                        this.totalChunks = totalChunks;
                        this.receivedChunks = 0;
                        this.imageChunks = new byte[totalChunks][];
                    }

                    // 存储当前数据块
                    if (imageChunks[chunkIndex] == null) {
                        imageChunks[chunkIndex] = imageData;
                        receivedChunks++;
                    }

                    // 检查是否接收完所有数据块
                    if (receivedChunks == totalChunks) {
                        byte[] completeImageData = new byte[totalChunks * (BUFFER_SIZE - HEADER_SIZE)];
                        int offset = 0;
                        for (byte[] chunk : imageChunks) {
                            if (chunk != null) {
                                System.arraycopy(chunk, 0, completeImageData, offset, chunk.length);
                                offset += chunk.length;
                            }
                        }
                        receivedChunks = 0; // 重置计数器
                        imageChunks = null; // 清空数据缓冲区
                    }
                    break;
                case 0x03: // 鼠标左键点击
                    Log.d(TAG, "onMessageReceived: 鼠标左键点击");
//                    把forceValue作为下压力度，转为byte类型然后发送
                    if (forceValue>127) {
                        forceValue = 127;
                    }
                    connectionManager.mouse.sendMouse((byte) 0x00, (byte) forceValue);
                case 0x04: // 新鼠标压枪
                    int px = byteBuffer.getInt();
                    int py = byteBuffer.getInt();
                    moveTo(px, py);
                    Log.d(TAG, "onMessageReceived: 新鼠标压枪");
                default:
                    Log.e(TAG, "Unknown message type: " + messageType);

            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing binary message", e);
        }
    }

    private void moveTo(int targetX, int targetY) {
        if (targetX < -maxMoveStep || targetX > maxMoveStep || targetY < -maxMoveStep || targetY > maxMoveStep) {
            Log.e(TAG, "Target position out of bounds");
            return;
        }

        if (!connectionManager.isConnected()) {
            Log.d(TAG, "moveTo: 设备未连接");
            return;
        }
        connectionManager.mouse.sendMouse((byte) targetX, (byte) targetY);
//        初始化鼠标位置
    }

    public void setFocreValue(int forceValue) {
        if (forceValue < 0 || forceValue > 10) {
            Log.e(TAG, "Invalid force value: " + forceValue);
            return;
        }
        this.forceValue = forceValue;
    }

}