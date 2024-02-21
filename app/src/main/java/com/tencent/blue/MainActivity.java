package com.tencent.blue;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.blue.blueTooth.AcceptThread;
import com.tencent.blue.blueTooth.BluetoothService;
import com.tencent.blue.blueTooth.HidDeviceDemo;

public class MainActivity extends AppCompatActivity {
    HidDeviceDemo hid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BluetoothService bs = new BluetoothService(this);
        //获取textview
        TextView info = findViewById(R.id.info);

        //获取debug按钮
        Button debug = findViewById(R.id.debug);

        hid = new HidDeviceDemo();
        hid.init(bs.getAdapter(), this);
        //设置按钮点击事件
        debug.setOnClickListener(v -> {
           hid.SendKey();
        });

        Button Server = findViewById(R.id.server);
        AcceptThread acceptThread = new AcceptThread(bs.getAdapter(), this);
        Server.setOnClickListener(v -> {
            acceptThread.start();

        });



    }


}