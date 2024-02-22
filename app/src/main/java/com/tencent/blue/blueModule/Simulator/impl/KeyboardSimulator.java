package com.tencent.blue.blueModule.Simulator.impl;

import com.tencent.blue.blueModule.Simulator.IKeyboardSimulator;
import com.tencent.blue.blueModule.manager.IBluetoothConnectionManager;
import com.tencent.blue.blueModule.utils.KeyCode;

public class KeyboardSimulator implements IKeyboardSimulator {
    private IBluetoothConnectionManager connectionManager;

    public KeyboardSimulator(IBluetoothConnectionManager manager) {
        this.connectionManager = manager;
    }

    @Override
    public void pressKey(int keyCode) {
        //根据键盘的按键码，构建一个byte数组，然后调用连接管理器的sendData方法发送数据
        byte[] data = new byte[8];
        data[0] = 0b00000000;
        data[2] = (byte) keyCode;
        connectionManager.sendData(data);

    }

    @Override
    public void releaseKey() {
        byte[] data = new byte[8];
        connectionManager.sendData(data);
    }

    @Override
    public void sendCombination(int[] keyCodes) {
        byte[] data = new byte[8];

        // 初始化修饰键字节为0
        byte modifier = 0x00;
        // 按键从第三个字节开始，因此初始化为2
        int index = 2;

        for (int keyCode : keyCodes) {
            switch (keyCode) {
                // 如果是修饰键，根据修饰键的类型设置相应的位
                case KeyCode.MODIFIER_LEFT_CTRL:
                    modifier |= 0x01; // 00000001
                    break;
                case KeyCode.MODIFIER_LEFT_SHIFT:
                    modifier |= 0x02; // 00000010
                    break;
                case KeyCode.MODIFIER_LEFT_ALT:
                    modifier |= 0x04; // 00000100
                    break;
                case KeyCode.MODIFIER_LEFT_GUI:
                    modifier |= 0x08; // 00001000
                    break;
                case KeyCode.MODIFIER_RIGHT_CTRL:
                    modifier |= 0x10; // 00010000
                    break;
                case KeyCode.MODIFIER_RIGHT_SHIFT:
                    modifier |= 0x20; // 00100000
                    break;
                case KeyCode.MODIFIER_RIGHT_ALT:
                    modifier |= 0x40; // 01000000
                    break;
                case KeyCode.MODIFIER_RIGHT_GUI:
                    modifier |= 0x80; // 10000000
                    break;
                // 如果是普通按键，将其放置在data数组的下一个位置
                default:
                    if (index < data.length) {
                        data[index++] = (byte) keyCode;
                    }
                    break;
            }
        }

        // 设置修饰键
        data[0] = modifier;

        // 保留第二个字节为0，符合HID标准

        // 发送数据
        connectionManager.sendData(data);
    }

    @Override
    public void sendText(String text) {
        // 假设你有一个函数 getHidCode(char c) 来获取字符的HID键码
        // 和一个函数 isShiftRequired(char c) 来判断是否需要按下Shift键
        for (char c : text.toCharArray()) {
            byte[] data = new byte[8];
            byte modifier = 0x00;
            byte keycode = getHidCode(c);

            // 检查是否需要按下Shift键
            if (isShiftRequired(c)) {
                modifier |= KeyCode.KEY_LEFT_SHIFT; // 假设Shift键的修饰键码是0x02
            }

            // 设置修饰键
            data[0] = modifier;

            // 设置按键码
            data[2] = keycode;

            // 发送按下按键的数据
            connectionManager.sendData(data);

            // 发送释放所有按键的数据
            releaseKey();
        }
    }

    private byte getHidCode(char c) {
        switch (c) {
            case 'A':
            case 'a':
                return 0x04;
            case 'B':
            case 'b':
                return 0x05;
            case 'C':
            case 'c':
                return 0x06;
            case 'D':
            case 'd':
                return 0x07;
            case 'E':
            case 'e':
                return 0x08;
            case 'F':
            case 'f':
                return 0x09;
            case 'G':
            case 'g':
                return 0x0a;
            case 'H':
            case 'h':
                return 0x0b;
            case 'I':
            case 'i':
                return 0x0c;
            case 'J':
            case 'j':
                return 0x0d;
            case 'K':
            case 'k':
                return 0x0e;
            case 'L':
            case 'l':
                return 0x0f;
            case 'M':
            case 'm':
                return 0x10;
            case 'N':
            case 'n':
                return 0x11;
            case 'O':
            case 'o':
                return 0x12;
            case 'P':
            case 'p':
                return 0x13;
            case 'Q':
            case 'q':
                return 0x14;
            case 'R':
            case 'r':
                return 0x15;
            case 'S':
            case 's':
                return 0x16;
            case 'T':
            case 't':
                return 0x17;
            case 'U':
            case 'u':
                return 0x18;
            case 'V':
            case 'v':
                return 0x19;
            case 'W':
            case 'w':
                return 0x1a;
            case 'X':
            case 'x':
                return 0x1b;
            case 'Y':
            case 'y':
                return 0x1c;
            case 'Z':
            case 'z':
                return 0x1d;
            case '!':
            case '1':
                return 0x1e;
            case '@':
            case '2':
                return 0x1f;
            case '#':
            case '3':
                return 0x20;
            case '$':
            case '4':
                return 0x21;
            case '%':
            case '5':
                return 0x22;
            case '^':
            case '6':
                return 0x23;
            case '&':
            case '7':
                return 0x24;
            case '*':
            case '8':
                return 0x25;
            case '(':
            case '9':
                return 0x26;
            case ')':
            case '0':
                return 0x27;

        }
        return 0;
    }

    private boolean isShiftRequired(char c) {
        // 确定字符是否需要Shift键
        // 这通常适用于大写字母和某些符号
        return Character.isUpperCase(c) || c == '!' || c == '@' || c == '#' || c == '$' || c == '%' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')';
    }

}