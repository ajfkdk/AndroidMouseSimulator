package com.tencent.blue;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.tencent.blue.manager.BluetoothConnectionManager;
import com.tencent.blue.manager.BluetoothHidMouse;

public class MainActivity extends AppCompatActivity {
    // 创建蓝牙连接管理器
    BluetoothConnectionManager connectionManager;

    // 创建键盘模拟器，注入连接管理器
    BluetoothHidMouse mouse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化蓝牙连接管理器
        connectionManager = new BluetoothConnectionManager(this);



        // 连接设备
        connectionManager.waitToConnect();

//        -- 连接区域 --

        Button sendSingle = findViewById(R.id.sendSingle);
        sendSingle.setOnClickListener(v -> {
            mouse = new BluetoothHidMouse(connectionManager.getService(), connectionManager.getHostDevice());
            // 如果设备已连接，发送按键
            if (connectionManager.isConnected()) {
                //x左滑20
                mouse.senMouse((byte) 0x14, (byte) 0x00);

            }else{
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }

        });

        Button sendCombination = findViewById(R.id.sendCombination);
        sendCombination.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
                mouse.sendLeftClick(true);
            }else{
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });

        Button sendText = findViewById(R.id.sendString);
        sendText.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
//                y下滑2，循环30次
                for (int i = 0; i < 50; i++) {
                    mouse.senMouse((byte) 0x00, (byte) 0x02);
                }
//                mouse.senMouse((byte) 0x00, (byte) 0x14);
            }else{
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });


        Button openSettingsButton = findViewById(R.id.openSettings);
        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "断开连接", Toast.LENGTH_SHORT).show();
        connectionManager.disconnect();

    }


}