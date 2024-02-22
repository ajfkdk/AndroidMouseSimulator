package com.tencent.blue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.blue.blueModule.Simulator.IKeyboardSimulator;
import com.tencent.blue.blueModule.Simulator.impl.KeyboardSimulator;
import com.tencent.blue.blueModule.manager.IBluetoothConnectionManager;
import com.tencent.blue.blueModule.manager.impl.BluetoothConnectionManager;
import com.tencent.blue.blueModule.utils.KeyCode;

public class MainActivity extends AppCompatActivity {
    // 创建蓝牙连接管理器
    IBluetoothConnectionManager connectionManager;

    // 创建键盘模拟器，注入连接管理器
    IKeyboardSimulator keyboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化蓝牙连接管理器
        connectionManager = new BluetoothConnectionManager(this);
        // 初始化键盘模拟器
        keyboard = new KeyboardSimulator(connectionManager);

        // 连接设备
        connectionManager.waitToConnect();
        Button sendSingle = findViewById(R.id.sendSingle);
        sendSingle.setOnClickListener(v -> {

            // 如果设备已连接，发送按键
            if (connectionManager.isConnected()) {
                keyboard.pressKey(KeyCode.KEY_A); // 按下A键
                keyboard.releaseKey(); // 释放
            }else{
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }

        });

        Button sendCombination = findViewById(R.id.sendCombination);
        sendCombination.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
                int[] keyCodes = new int[]{KeyCode.MODIFIER_LEFT_CTRL, KeyCode.KEY_A};
                keyboard.sendCombination(keyCodes);
                keyboard.releaseKey();
            }else{
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
            }
        });

        Button sendText = findViewById(R.id.sendString);
        sendText.setOnClickListener(v -> {
            if (connectionManager.isConnected()) {
                keyboard.sendText("with his song!");
            }else{
                Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
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