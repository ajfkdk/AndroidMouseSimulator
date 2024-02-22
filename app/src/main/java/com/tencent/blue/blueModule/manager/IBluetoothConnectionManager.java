package com.tencent.blue.blueModule.manager;

public interface IBluetoothConnectionManager {
    void connect();
    void disconnect();
    boolean isConnected();
    void sendData(byte[] data);
}