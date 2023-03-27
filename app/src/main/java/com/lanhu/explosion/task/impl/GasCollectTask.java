package com.lanhu.explosion.task.impl;

import android.util.Log;

import com.lanhu.explosion.bean.GasInfo;
import com.lanhu.explosion.bean.GasStandardInfo;
import com.lanhu.explosion.serial.SerialPort;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.store.SharedPrefManager;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.DataUtils;

/**
 * 0        1        2        3        4        5        6        7        8        9        10
 * 起始位   命令      CO高位   CO低位   H2S高位   H2S低位   O2高位    O2低位   CH4高位   CH4低位    校验值
 * <p>
 * 0xFF 0x86 0x00 0x00 0x00 0x00 0x00 0xD1 0x00
 */
public class GasCollectTask extends ATask<Integer> {

    private static final String PATH = "/dev/ttyS3";
    private static final int BAUD_RATE = 9600;
    private static final int DATA = 8;
    private static final int PARITY = 0;
    private static final int STOP_BITS = 1;
    private static final int FLOW_CONTROL = 0;

    @Override
    protected Object doInBackground(Object... objs) {
        try {
            publishProgress(5);
            Thread.sleep(1000);
            publishProgress(50);

            byte[] buffer = requestDeviceData();
            if (buffer != null) {
                GasInfo info = parseData(buffer);
                if (info != null) {
                    GasStandardInfo.checkGas(GasStandardInfo.sInfo, info);
                    DBManager.getInstance().insertGas(info);
                    GasInfo.sInfo = info;
                }
                publishProgress(100);
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        publishProgress(100);
        return null;
    }


    private byte[] requestDeviceData() {
        try {
            byte[] buffer = new byte[1024];
            byte[] modeAsk = {(byte) 0xff, (byte) 0x01, (byte) 0x78, (byte) 0x41, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x46, (byte) 0x0a};
            byte[] test = {(byte) 0xff, (byte) 0x01, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x79, (byte) 0x0a};
            SerialPort port = new SerialPort(PATH, BAUD_RATE, DATA, PARITY, STOP_BITS, FLOW_CONTROL);
            port.open();

            Thread receiver = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        int pos = 0;
                        while (true) {
                            byte[] data = port.receiver();
                            if (data != null) {
                                System.arraycopy(data, 0, buffer, pos, data.length);
                                pos += data.length;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            receiver.start();
            port.send(modeAsk);
            Thread.sleep(1000);
            port.send(test);
            Thread.sleep(100000);
            port.close();
            receiver.join();

            return buffer;

        } catch (Exception e) {

        }
        return null;
    }

    private GasInfo parseData(byte[] buffer) {
        int index = 0;
        Log.e("GasCollectTask", "parseData" + DataUtils.bytesToString(buffer));
        while (index < buffer.length - 10) {
            if (buffer[index] == (byte) 0xff && buffer[index + 1] == (byte) 0x86) {
                GasInfo info = new GasInfo();
                info.CO = DataUtils.byteArrayToShort(buffer, index + 2);
                info.H2S = DataUtils.byteArrayToShort(buffer, index + 4);
                info.O2 = DataUtils.byteArrayToShort(buffer, index + 6);
                info.CH4 = DataUtils.byteArrayToShort(buffer, index + 8);
                info.time = System.currentTimeMillis();
                return info;
            }
            index++;
        }
        Log.e("GasCollectTask", "parseData null");
        return null;
    }

}
