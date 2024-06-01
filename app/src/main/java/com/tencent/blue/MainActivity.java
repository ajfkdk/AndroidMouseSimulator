package com.tencent.blue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.blue.manager.BluetoothConnectionManager;
import com.tencent.blue.manager.BluetoothHidMouse;
import com.tencent.blue.storage.DeviceStorage;
import com.tencent.blue.storage.HostDevice;
import com.tencent.blue.viewclass.DeviceListAdapter;
import com.tencent.blue.viewclass.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // 创建蓝牙连接管理器
    BluetoothConnectionManager connectionManager;

    // 创建键盘模拟器，注入连接管理器
    BluetoothHidMouse mouse;

    // 设备列表适配器
    DeviceListAdapter deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化蓝牙连接管理器
        connectionManager = new BluetoothConnectionManager(this);

        // 连接设备
        connectionManager.waitToConnect();

        // 初始化RecyclerView和适配器
        RecyclerView deviceListRecyclerView = findViewById(R.id.device_list_recycler_view);
        deviceListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceListAdapter = new DeviceListAdapter(new ArrayList<>());
        deviceListRecyclerView.setAdapter(deviceListAdapter);

        // 读取存储的设备数据并更新RecyclerView
        updateDeviceList();

        // 按钮事件处理
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        Button sendSingle = findViewById(R.id.sendSingle);
        sendSingle.setOnClickListener(v -> {
            mouse = new BluetoothHidMouse(connectionManager.getService(), connectionManager.getHostDevice());
            if (connectionManager.isConnected()) {
                mouse.senMouse((byte) 0x14, (byte) 0x00);
            } else {
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });

        Button sendCombination = findViewById(R.id.sendCombination);
        sendCombination.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
                mouse.sendLeftClick(true);
            } else {
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });

        Button sendText = findViewById(R.id.sendString);
        sendText.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
                for (int i = 0; i < 50; i++) {
                    mouse.senMouse((byte) 0x00, (byte) 0x02);
                }
            } else {
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });

        Button openSettingsButton = findViewById(R.id.openSettings);
        openSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void updateDeviceList() {
        String src = PreferenceManager.getDefaultSharedPreferences(this).getString(DeviceStorage.DEVICES_KEY, "[]");
        List<HostDevice> devices = new Gson().fromJson(src, new TypeToken<ArrayList<HostDevice>>() {}.getType());
        deviceListAdapter.updateDevices(devices);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "断开连接", Toast.LENGTH_SHORT).show();
        connectionManager.disconnect();
    }

    public void updateBluetoothStatus() {
        runOnUiThread(this::updateDeviceList);
    }
}