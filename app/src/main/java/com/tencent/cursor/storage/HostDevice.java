package com.tencent.cursor.storage;

/**
 * 这个类表示一个已知的主机设备。
 */
public class HostDevice {

    // 设备的蓝牙MAC地址
    private String address;

    // 设备名称
    private String name;

    // 上次连接的时间戳（Unix时间戳）
    private long lastConnected;

    /**
     * 构造方法，创建一个主机设备实例。
     *
     * @param address 设备的蓝牙MAC地址
     * @param name    设备名称
     */
    public HostDevice(String address, String name) {
        this.address = address;  // 初始化设备地址
        this.name = name;        // 初始化设备名称
        this.lastConnected = -1; // 将上次连接时间初始化为-1，表示从未连接过
    }

    public HostDevice(String address, String name, long lastConnected) {
        this.address = address;  // 初始化设备地址
        this.name = name;        // 初始化设备名称
        this.lastConnected = lastConnected; // 将上次连接时间初始化为-1，表示从未连接过
    }

    /**
     * 设置设备的上次连接时间。
     *
     * @param lastConnected 上次连接的Unix时间戳
     */
    public void setLastConnected(long lastConnected) {
        this.lastConnected = lastConnected; // 更新上次连接时间
    }

    /**
     * 获取设备的蓝牙MAC地址。
     *
     * @return 蓝牙MAC地址
     */
    public String getAddress() {
        return address; // 返回设备地址
    }

    /**
     * 获取设备名称。
     *
     * @return 设备名称
     */
    public String getName() {
        return name; // 返回设备名称
    }

    /**
     * 获取设备的上次连接时间。
     *
     * @return 上次连接的Unix时间戳
     */
    public long getLastConnected() {
        return lastConnected; // 返回上次连接时间
    }
}