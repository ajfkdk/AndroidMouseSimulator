package com.tencent.blue.blueModule.Simulator.impl;

import com.tencent.blue.blueModule.Simulator.IKeyboardSimulator;
import com.tencent.blue.blueModule.manager.IBluetoothConnectionManager;

public class KeyboardSimulator implements IKeyboardSimulator {
    private IBluetoothConnectionManager connectionManager;

    public KeyboardSimulator(IBluetoothConnectionManager manager) {
        this.connectionManager = manager;
    }

    @Override
    public void pressKey(int keyCode) {

    }

    @Override
    public void releaseKey(int keyCode) {

    }

    @Override
    public void sendCombination(int[] keyCodes) {

    }

    @Override
    public void sendText(String text) {

    }

}