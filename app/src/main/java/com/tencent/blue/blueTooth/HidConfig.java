package com.tencent.blue.blueTooth;

public class HidConfig {

    public final static String NAME = "Evin Keyboard";

    public final static String DESCRIPTION = "Evin for you";

    public final static String PROVIDER = "Evin";

    public static final byte[] KEYBOARD_COMBO =
            {
                    (byte) 0x05, (byte) 0x01, //USAGE_PAGE (Generic Desktop)
                    (byte) 0x09, (byte) 0x06, //USAGE (Keyboard)
                    (byte) 0xA1, (byte) 0x01, //COLLECTION (Application)
                    (byte) 0x85, (byte) 0x08, //REPORT_ID (8)
                    (byte) 0x05, (byte) 0x07, //USAGE_PAGE (Keyboard)
                    (byte) 0x19, (byte) 0xE0, //USAGE_MINIMUM (Keyboard LeftControl)
                    (byte) 0x29, (byte) 0xE7, //USAGE_MAXIMUM (Keyboard Right GUI)
                    (byte) 0x15, (byte) 0x00, //LOGICAL_MINIMUM (0)
                    (byte) 0x25, (byte) 0x01, //LOGICAL_MAXIMUM (1)
                    //第一个字节
                    (byte) 0x75, (byte) 0x01, //REPORT_SIZE (1)
                    (byte) 0x95, (byte) 0x08, //REPORT_COUNT (8)
                    (byte) 0x81, (byte) 0x02, //INPUT (Data,Var,Abs)
                    //第二个字节
                    (byte) 0x95, (byte) 0x01, //REPORT_COUNT (1)
                    (byte) 0x75, (byte) 0x08, //REPORT_SIZE (8)
                    (byte) 0x81, (byte) 0x03, //INPUT (Cnst,Var,Abs)
                    //后六个字节
                    (byte) 0x95, (byte) 0x06, //REPORT_COUNT (6)
                    (byte) 0x75, (byte) 0x08, //REPORT_SIZE (8)
                    (byte) 0x15, (byte) 0x00, //LOGICAL_MINIMUM (0)
                    (byte) 0x25, (byte) 0x65, //LOGICAL_MAXIMUM (101)
                    (byte) 0x05, (byte) 0x07, //USAGE_PAGE (Keyboard)
                    (byte) 0x19, (byte) 0x00, //USAGE_MINIMUM (Reserved (no event indicated))
                    (byte) 0x29, (byte) 0x65, //USAGE_MAXIMUM (Keyboard Application)
                    (byte) 0x81, (byte) 0x00, //INPUT (Data,Ary,Abs)
                    (byte) 0xC0  //END_COLLECTION
            };
}
