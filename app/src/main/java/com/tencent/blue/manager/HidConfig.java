package com.tencent.blue.manager;
public class HidConfig {
    public final static String MOUSE_NAME = "VV Mouse";

    public final static String DESCRIPTION = "VV for you";

    public final static String PROVIDER = "VV";

    public static final byte[] MOUSE_COMBO = {
            (byte) 0x05, (byte) 0x01,              // USAGE_PAGE (Generic Desktop)
            (byte) 0x09, (byte) 0x02,              // USAGE (Mouse)
            (byte) 0xa1, (byte) 0x01,              // COLLECTION (Application)
            (byte) 0x85, (byte) 0x04,              // REPORT_ID (4)
            (byte) 0x09, (byte) 0x01,              //  USAGE (Pointer)
            (byte) 0xa1, (byte) 0x00,              //  COLLECTION (Physical)
            (byte) 0x05, (byte) 0x09,              //   USAGE_PAGE (Button)
            (byte) 0x19, (byte) 0x01,              //   USAGE_MINIMUM (Button 1)
            (byte) 0x29, (byte) 0x02,              //   USAGE_MAXIMUM (Button 2)
            (byte) 0x15, (byte) 0x00,              //   LOGICAL_MINIMUM (0)
            (byte) 0x25, (byte) 0x01,              //   LOGICAL_MAXIMUM (1)
            (byte) 0x95, (byte) 0x03,              //   REPORT_COUNT (3)
            (byte) 0x75, (byte) 0x01,              //   REPORT_SIZE (1)
            (byte) 0x81, (byte) 0x02,              //   INPUT (Data,Var,Abs)
            (byte) 0x95, (byte) 0x01,              //   REPORT_COUNT (1)
            (byte) 0x75, (byte) 0x05,              //   REPORT_SIZE (5)
            (byte) 0x81, (byte) 0x03,              //   INPUT (Cnst,Var,Abs)
            (byte) 0x05, (byte) 0x01,              //   USAGE_PAGE (Generic Desktop)
            (byte) 0x09, (byte) 0x30,              //   USAGE (X)
            (byte) 0x09, (byte) 0x31,              //   USAGE (Y)
            (byte) 0x09, (byte) 0x38,              //   USAGE (Wheel)
            (byte) 0x15, (byte) 0x81,              //   LOGICAL_MINIMUM (-127)
            (byte) 0x25, (byte) 0x7F,              //   LOGICAL_MAXIMUM (127)
            (byte) 0x75, (byte) 0x08,              //   REPORT_SIZE (8)
            (byte) 0x95, (byte) 0x03,              //   REPORT_COUNT (3)
            (byte) 0x81, (byte) 0x06,              //   INPUT (Data,Var,Rel)
            //水平滚轮
            (byte) 0x05, (byte) 0x0c,              //   USAGE_PAGE (Consumer Devices)
            (byte) 0x0a, (byte) 0x38, (byte) 0x02, //   USAGE (AC Pan)
            (byte) 0x15, (byte) 0x81,              //   LOGICAL_MINIMUM (-127)
            (byte) 0x25, (byte) 0x7f,              //   LOGICAL_MAXIMUM (127)
            (byte) 0x75, (byte) 0x08,              //   REPORT_SIZE (8)
            (byte) 0x95, (byte) 0x01,              //   REPORT_COUNT (1)
            (byte) 0x81, (byte) 0x06,              //   INPUT (Data,Var,Rel)

            (byte) 0xc0,                           //  END_COLLECTION
            (byte) 0xc0,                           // END_COLLECTION
    };
}
