package com.lanhu.explosion.serial;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;


public class SerialPort extends Port {

    private static final String TAG = "SerialPort";

    private FileInputStream mInput;
    private FileOutputStream mOutput;
    private String mPath;
    private int mBaudrate;
    private int mDataBit;
    private int mParity;
    private int mStopBit;
    private int mFlowCtrl;
    private boolean isOpen = false;

    static {
        System.loadLibrary("serial_port");
    }

    /**
     * @param path        串口路径
     * @param baudrate    波特率
     * @param data        数据位
     * @param parity      奇偶校验位
     * @param stopBits    停止位
     * @param flowControl 流控
     */
    public SerialPort(String path, int baudrate, int data, int parity, int stopBits, int flowControl) {
        mPath = path;
        mBaudrate = baudrate;
        mDataBit = data;
        mParity = parity;
        mStopBit = stopBits;
        mFlowCtrl = flowControl;
    }

    @Override
    public boolean open() {
        if(isOpen){
            Log.e(TAG, "is opened!!!");
            return true;
        }
        try {
            File file = new File(mPath);
            mInput = new FileInputStream(file);
            mOutput = new FileOutputStream(file);

            FileDescriptor fileDescriptor = mInput.getFD();
            Field field = fileDescriptor.getClass().getDeclaredField("descriptor");
            field.setAccessible(true);
            int fd = field.getInt(fileDescriptor);
            SerialNative.setSerial(fd, mBaudrate, mDataBit, mParity, mStopBit, mFlowCtrl);
            isOpen = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    @Override
    public boolean send(byte[] body){
        if(!isOpen){
            return false;
        }
        try {
            mOutput.write(body);
            mOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean sendAutoEnter(byte[] body){
        if(!isOpen){
            return false;
        }
        try {
            mOutput.write(body);
            mOutput.write('\n');
            mOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public byte[] receiver()  throws RuntimeException{
        try {
            byte[] buffer = new byte[4096];
            int len = mInput.read(buffer);
            if (len == 0) {
                return null;
            }
            byte[] data = Arrays.copyOf(buffer, len);
            return data;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean close() {
        try {
            if (mInput != null) {
                mInput.close();
                mOutput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
