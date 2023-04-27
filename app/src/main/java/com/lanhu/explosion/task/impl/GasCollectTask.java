package com.lanhu.explosion.task.impl;

import android.util.Log;
import android.widget.Toast;

import com.lanhu.explosion.R;
import com.lanhu.explosion.bean.GasItem;
import com.lanhu.explosion.bean.GasStandardItem;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.serial.SerialPort;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.DataUtils;

import java.util.ArrayList;

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

    private ArrayList<GasItem> mList = new ArrayList<>();

    private int process = 0;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        process = 0;
        mList.clear();
        mList.add(new GasItem(GasItem.TYPE_CO, GasStandardItem.sMap.get(GasItem.TYPE_CO)));
        mList.add(new GasItem(GasItem.TYPE_H2S, GasStandardItem.sMap.get(GasItem.TYPE_H2S)));
        mList.add(new GasItem(GasItem.TYPE_O2, GasStandardItem.sMap.get(GasItem.TYPE_O2)));
        mList.add(new GasItem(GasItem.TYPE_CH4, GasStandardItem.sMap.get(GasItem.TYPE_CH4)));
    }

    @Override
    protected void onPostExecute(Object result) {
        boolean suc = (boolean)result;

        MToast.makeText(suc ? R.string.toast_collect_finish : R.string.toast_collect_err, Toast.LENGTH_LONG).show();
    }

    public ArrayList<GasItem> getGasList() {
        return mList;
    }

    @Override
    protected Object doInBackground(Object... objs) {
        try {
            // todo pump machine
            while (process < 90) {
                process += 1;
                publishProgress(process);
                Thread.sleep(1000);
            }

            byte[] buffer = requestDeviceData();
            if (buffer != null) {
                int[] values = parseData(buffer);
//                int[] values = new int[]{25, 11, 208, 6};
                if (values != null) {
                    long time = System.currentTimeMillis();
                    for (int i = 0; i < values.length; i++) {
                        mList.get(i).setValue(values[i]);
                        mList.get(i).setTime(time);
                    }
                    GasItem.mList.clear();
                    GasItem.mList.addAll(mList);
                    DBManager.getInstance().insertGas(mList);

                    process = 100;
                    publishProgress(process);
                    return true;
                    // todo upload
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        process = 100;
        return false;
    }

    public int getProcess() {
        return process;
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
                    }
                }
            };
            receiver.start();
            port.send(modeAsk);
            Thread.sleep(1000);
            port.send(test);
            Thread.sleep(2000);
            port.close();
            receiver.join();

            return buffer;

        } catch (Exception e) {

        }
        return null;
    }

    private int[] parseData(byte[] buffer) {
        int index = 0;
        while (index < buffer.length - 10) {
            if (buffer[index] == (byte) 0xff && buffer[index + 1] == (byte) 0x86) {
                int[] values = new int[4];
                values[0] = DataUtils.byteArrayToShort(buffer, index + 2);
                values[1] = DataUtils.byteArrayToShort(buffer, index + 4);
                values[2] = DataUtils.byteArrayToShort(buffer, index + 6);
                values[3] = DataUtils.byteArrayToShort(buffer, index + 8);
                return values;
            }
            index++;
        }
        Log.e("GasCollectTask", "parseData null");
        return null;
    }

}
