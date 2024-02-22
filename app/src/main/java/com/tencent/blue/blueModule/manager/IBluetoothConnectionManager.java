package com.tencent.blue.blueModule.manager;

public interface IBluetoothConnectionManager {
    void waitToConnect();
    void disconnect();
    boolean isConnected();
    void sendData(byte[] data);


}