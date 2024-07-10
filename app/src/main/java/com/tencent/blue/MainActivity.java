package com.tencent.blue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.blue.manager.NewBlueConnectManager;
import com.tencent.blue.storage.DeviceStorage;


public class MainActivity extends AppCompatActivity {
    // 创建蓝牙连接管理器
    NewBlueConnectManager connectionManager;

    private UdpServer udpServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 初始化蓝牙连接管理器
        connectionManager = new NewBlueConnectManager(this, new DeviceStorage(this));
        // 连接设备
        connectionManager.init();

        // 启动UDP服务器
        udpServer = new UdpServer();
        udpServer.start(connectionManager);


        displayIpAddress();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "断开连接", Toast.LENGTH_SHORT).show();
        udpServer.stop(); // 停止UDP服务器
    }

    private void displayIpAddress() {
        TextView ipDisplay = findViewById(R.id.ipDisplay);
        TextView portDisplay = findViewById(R.id.portDisplay);
        // 获得局域网IP地址
        WifiManager wifiManager = (WifiManager) this.getSystemService(this.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = Formatter.formatIpAddress(ipAddress);
            ipDisplay.setText(ip);
            portDisplay.setText(String.valueOf(UdpServer.SERVER_PORT));
        }
    }


}