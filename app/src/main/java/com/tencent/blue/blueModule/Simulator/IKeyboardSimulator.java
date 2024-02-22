package com.tencent.blue.blueModule.Simulator;

/*
*   这个接口定义了模拟键盘操作的基本行为，如按下键、释放键、发送组合键等。
* */
public interface IKeyboardSimulator {
    void pressKey(int keyCode);
    void releaseKey();
    void sendCombination(int[] keyCodes);
    void sendText(String text);
}