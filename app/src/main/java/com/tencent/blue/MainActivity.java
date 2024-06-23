package com.tencent.blue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.blue.manager.NewBlueConnectManager;
import com.tencent.blue.storage.DeviceStorage;
import com.tencent.blue.storage.HostDevice;
import com.tencent.blue.viewclass.DeviceListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // 创建蓝牙连接管理器
    NewBlueConnectManager connectionManager;

    private UdpServer udpServer;

    // 设备列表适配器
    DeviceListAdapter deviceListAdapter;

    TextView forceValueTextView;

    // 添加用于显示视频流的 ImageView
    private int forceValue = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 初始化蓝牙连接管理器
        connectionManager = new NewBlueConnectManager(this, new DeviceStorage(this));
        // 连接设备
        connectionManager.init();


        // 初始化 ImageView
        // 初始化RecyclerView和适配器

        // 启动UDP服务器
        // 启动UDP服务器
        udpServer = new UdpServer();
        udpServer.start(connectionManager);


        // 按钮事件处理
        setupButtonListeners();

    }

    private void setupButtonListeners() {

        Button slipLeft = findViewById(R.id.slipLeft);
        slipLeft.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
                connectionManager.mouse.move((byte) 100, (byte) 0);
            } else {
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });

        Button simpleChick = findViewById(R.id.simpleChick);
        simpleChick.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
                connectionManager.mouse.move((byte) 0, (byte) 100);

            } else {
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });

        Button firecontrollerSmall = findViewById(R.id.fireControllersmall);
        firecontrollerSmall.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
                connectionManager.mouse.sendLeftClick(true);
            } else {
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });

        TextView slipDown = findViewById(R.id.fireController);
        slipDown.setOnClickListener(v -> {
            if (!connectionManager.isConnected()) {

                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
                return;
            }
            connectionManager.mouse.canFire = !connectionManager.mouse.canFire;
            if (connectionManager.mouse.canFire) {
                slipDown.setText("开火");
            } else {
                slipDown.setText("停火");
            }
        });

        Button showPaired = findViewById(R.id.showPaired);
        showPaired.setOnClickListener(v -> {
           connectionManager.showPaired();
        });

        Button activeBlue = findViewById(R.id.activeBlue);
        activeBlue.setOnClickListener(v -> {
            connectionManager.defaultConnect();
        });

        Button passitiveScan = findViewById(R.id.passitiveScan);
        passitiveScan.setOnClickListener(v -> {
            connectionManager.passiveScan();
        });

        //压枪力度的控制
        forceValueTextView = findViewById(R.id.force_value);
        Button decreaseForceButton = findViewById(R.id.decrease_force);
        Button increaseForceButton = findViewById(R.id.increase_force);

        decreaseForceButton.setOnClickListener(v -> {
            if (forceValue > 0) {
                forceValue--;
                updateForceValue();
            }
        });

        increaseForceButton.setOnClickListener(v -> {
            if (forceValue < 10) { // assuming 10 is the max value
                forceValue++;
                updateForceValue();
            }
        });
    }

    private void updateForceValue() {
        if (forceValue>127) {
            forceValue = 127;
            Toast.makeText(this, "力度已达最大值", Toast.LENGTH_SHORT).show();
        } else if (forceValue < -127) {
            forceValue = -127;
            Toast.makeText(this, "力度已达最小值", Toast.LENGTH_SHORT).show();
        }
        udpServer.setFocreValue(forceValue);
        forceValueTextView.setText(String.valueOf(forceValue));

        Log.d("MainActivity", "Force value: " + forceValue);
    }

    private void updateDeviceList() {
        String src = PreferenceManager.getDefaultSharedPreferences(this).getString(DeviceStorage.DEVICES_KEY, "[]");
        List<HostDevice> devices = new Gson().fromJson(src, new TypeToken<ArrayList<HostDevice>>() {
        }.getType());
        deviceListAdapter.updateDevices(devices);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "断开连接", Toast.LENGTH_SHORT).show();
        udpServer.stop(); // 停止UDP服务器
    }

    public void updateBluetoothStatus() {
        runOnUiThread(this::updateDeviceList);
    }
}