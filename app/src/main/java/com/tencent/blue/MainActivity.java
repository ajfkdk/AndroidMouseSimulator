package com.tencent.blue;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.blue.blueTooth.AcceptThread;
import com.tencent.blue.blueTooth.BluetoothService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BluetoothService bs = new BluetoothService(this);
        //获取textview
        TextView info = findViewById(R.id.info);

        //获取debug按钮
        Button debug = findViewById(R.id.debug);
        //设置按钮点击事件
        debug.setOnClickListener(v -> {
            BluetoothAdapter adapter = bs.getAdapter();
            bs.enableDiscoverable(adapter);
        });

        Button Server = findViewById(R.id.server);
        AcceptThread acceptThread = new AcceptThread(bs.getAdapter(), this);
        Server.setOnClickListener(v -> {
            acceptThread.start();

        });



    }


}