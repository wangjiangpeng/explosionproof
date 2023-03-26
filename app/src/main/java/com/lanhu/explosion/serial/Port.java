package com.lanhu.explosion.serial;

/**
 * Created by 联想 on 2017/11/13.
 */

public abstract class Port {

    protected int inMax;
    protected int outMax;

    protected int mInTimeOut = 1000;
    protected int mOutTimeOut = 1000;

    /**
     * 打开设备
     *
     * @return
     */
    public abstract boolean open();

    /**
     * 关闭设备
     *
     * @return
     */
    public abstract boolean close();

    /**
     * 发送
     *
     * @param sendBytes 发送的数据
     */
    public abstract boolean send(byte[] sendBytes);

    /**
     * 接收
     *
     * @return
     */
    public abstract byte[] receiver();

    /**
     *
     * @return 是否打开设备
     */
    public abstract boolean isOpen();

    public void setOutMax(int max){
        outMax = max;
    }

    public int getOutMax(){
        return outMax;
    }

    public void setInMax(int max){
        inMax = max;
    }

    public int getInMax(){
        return inMax;
    }

    public void setInTimeout(int time) {
        mInTimeOut = time;
    }

    public void setOutTimeout(int time) {
        mOutTimeOut = time;
    }

}
