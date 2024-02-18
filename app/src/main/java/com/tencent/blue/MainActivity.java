package com.tencent.blue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tencent.blue.blueTooth.BluetoothService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new BluetoothService(this).setupBluetoothConnection();
    }
}