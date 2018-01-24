package com.ioter.clothesstrore.wdiget;

import android.os.Bundle;

/**
 * 
 *         按队列方式执行线程中和主线程中的操作
 */
public abstract class EasyQueueThread extends EasyBaseQueueThread
{
    /**
     * 在线程中操作
     * 
     * @param priority 信息的优先级
     * @param arg1 信息附带的整型数据
     * @param arg2 信息附带的整型数据
     * @param obj 信息附带的Object数据
     * @param bundle 信息附带的Bundle数据
     * @return （doInUI）的传参数据
     */
    protected abstract Object doInWork(int priority, int arg1, int arg2, Object obj, Bundle bundle);

    /**
     * 在主线程中操作
     * 
     * @param obj （doInWork）返回的结果
     */
    protected abstract void doInUI(Object obj);
    
    /**
     * 销毁，退出页面时必须执行
     */
    public void destroy()
    {
        super.destroy();
    }

    /**
     * 设置在线程操作（doInWork）后，延迟更新主线程操作（doInUI）的时间间隔
     * 
     * @param delayUITime 延迟的时间间隔，以毫秒为单位
     */
    public void setUIDelayTime(long delayUIMillis)
    {
        super.setUIDelayTime(delayUIMillis);
    }

    /**
     * 发送信息到线程中操作（doInWork）
     * 
     * @param priority 信息的优先级，优先按数值从大到小处理，不能等于-1
     */
    public void sendWorkMsg(int priority)
    {
        super.sendWorkMsg(priority);
    }

    /**
     * 发送信息到线程中操作（doInWork）
     * 
     * @param priority 信息的优先级，优先按数值从大到小处理，不能等于-1
     * @param arg1 信息附带的整型数据
     * @param arg2 信息附带的整型数据
     * @param obj 信息附带的Object数据
     * @param bundle 信息附带的Bundle数据
     */
    public void sendWorkMsg(int priority, int arg1, int arg2, Object obj, Bundle bundle)
    {
        super.sendWorkMsg(priority, arg1, arg2, obj, bundle);
    }

    /**
     * 延迟发送信息到线程中操作（doInWork）
     * 
     * @param priority 信息的优先级，优先按数值从大到小处理，不能等于-1
     * @param arg1 信息附带的整型数据
     * @param arg2 信息附带的整型数据
     * @param obj 信息附带的Object数据
     * @param bundle 信息附带的Bundle数据
     * @param delayMillis 延迟的时间间隔，以毫秒为单位
     */
    public void sendWorkMsgDelayed(int priority, int arg1, int arg2, Object obj, Bundle bundle, long delayMillis)
    {
        super.sendWorkMsgDelayed(priority, arg1, arg2, obj, bundle, delayMillis);
    }

    /**
     * 最先发送信息到线程中操作（doInWork）
     * 
     * @param priority 信息的优先级，优先按数值从大到小处理，不能等于-1
     * @param arg1 信息附带的整型数据
     * @param arg2 信息附带的整型数据
     * @param obj 信息附带的Object数据
     * @param bundle 信息附带的Bundle数据
     */
    public void sendWorkMsgAtFrontOfQueue(int priority, int arg1, int arg2, Object obj, Bundle bundle)
    {
        super.sendWorkMsgAtFrontOfQueue(priority, arg1, arg2, obj, bundle);
    }
    
    /**
     * 移除对应priority的发送信息的任务
     * 
     * @param priority 信息的优先级，优先按数值从大到小处理，不能等于-1
     */
    public void remove(int priority)
    {
        super.remove(priority);
    }
}