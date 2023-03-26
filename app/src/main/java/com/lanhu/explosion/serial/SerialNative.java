package com.lanhu.explosion.serial;

public class SerialNative {

    static {
        System.loadLibrary("serial_port");
    }

    /**
     * 设置串口参数
     *
     * @param fd        文件描述符
     * @param baudrate    波特率
     * @param data        数据位
     * @param parity      奇偶校验位
     * @param stopBits    停止位
     * @param flowControl 流控
     * @return fd
     */
    public static native void setSerial(int fd, int baudrate, int data, int parity, int stopBits,
                                            int flowControl);
}
