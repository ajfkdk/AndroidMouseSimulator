package com.tencent.blue.storage;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
/**
 * 这个类用于存储已知的蓝牙设备。
 */
public class DeviceStorage {
    // 定义一个常量字符串作为设备的键
    public static final String DEVICES_KEY = "devices";

    private Context context;  // 用于访问应用程序的上下文
    private List<HostDevice> devices;  // 存储蓝牙设备的列表

    /**
     * 创建并加载设备存储。
     *
     * @param context 用于访问偏好设置的上下文
     */
    public DeviceStorage(Context context) {
        this.context = context;  // 初始化上下文
        load();  // 加载设备信息
    }

    /**
     * 从偏好设置中加载设备列表。
     */
    public void load() {
        // 从共享偏好设置中获取存储的设备列表字符串，默认为"[]"
        String src = PreferenceManager.getDefaultSharedPreferences(context).getString(DEVICES_KEY, "[]");
        // 使用Gson库将字符串转换为设备列表对象
        devices = new Gson().fromJson(src, new TypeToken<ArrayList<HostDevice>>() { }.getType());
    }

    /**
     * 将设备列表保存到偏好设置中。
     */
    public void save() {
        // 按照设备最后连接时间逆序排序
        devices.sort((o1, o2) -> -Long.compare(o1.getLastConnected(), o2.getLastConnected()));
        //把Address相同的设备去重
        for (int i = 0; i < devices.size(); i++) {
            for (int j = i + 1; j < devices.size(); j++) {
                if (devices.get(i).getAddress().equals(devices.get(j).getAddress())) {
                    devices.remove(j);
                    j--;
                }
            }
        }
        // 使用Gson库将设备列表转换为字符串
        String src = new Gson().toJson(devices);
        // 将设备列表字符串存储到共享偏好设置中
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(DEVICES_KEY, src).apply();
    }

    /**
     * 返回所有已知的设备。
     *
     * @return 已知主机设备的列表
     */
    public List<HostDevice> getDevices() {
        return devices;  // 返回设备列表
    }

    /**
     * 返回指定索引处的主机设备。
     *
     * @param i 设备的索引
     * @return 已知设备
     */
    public HostDevice getDevice(int i) {
        return devices.get(i);  // 返回指定索引的设备
    }

    /**
     * 返回具有特定蓝牙MAC地址的主机设备。
     *
     * @param address 蓝牙MAC地址
     * @return 已知设备
     */
    public HostDevice getDevice(String address) {
        // 遍历设备列表，查找匹配的MAC地址
        for (HostDevice device : devices) {
            if (device.getAddress().equals(address)) return device;  // 找到匹配设备则返回
        }
        return null;  // 如果没有匹配设备则返回null
    }
    /**
     * 添加一个设备到存储中。
     *
     * @param device 要添加的已知设备
     */
    public void addDevice(HostDevice device) {
        devices.add(device);  // 将设备添加到设备列表中
        save();  // 保存设备列表到偏好设置中
    }

    /**
     * 从已知设备中移除位于指定索引的设备。
     *
     * @param i 设备的索引
     */
    public void removeDevice(int i) {
        HostDevice device = getDevice(i);  // 获取指定索引的设备

        if (device != null) {
            removeDevice(device);  // 如果设备存在，则移除它
        }
    }

    /**
     * 移除具有特定蓝牙MAC地址的设备。
     *
     * @param address 该设备的蓝牙MAC地址
     */
    public void removeDevice(String address) {
        HostDevice device = getDevice(address);  // 获取具有指定MAC地址的设备

        if (device != null) {
            removeDevice(device);  // 如果设备存在，则移除它
        }
    }

    /**
     * 移除一个已知设备。
     *
     * @param device 要移除的设备
     */
    public void removeDevice(HostDevice device) {
        devices.remove(device);  // 从设备列表中移除设备
        save();  // 保存设备列表到偏好设置中
    }

    /**
     * 返回某个设备是否存在于已知设备列表中。
     *
     * @param address 该设备的蓝牙MAC地址
     * @return 设备是否存在
     */
    public boolean hasDevice(String address) {
        return getDevice(address) != null;  // 如果能通过MAC地址找到设备，则返回true，否则返回false
    }

    public BluetoothDevice getStoredHostDevice() {
        return null;
    }
}
