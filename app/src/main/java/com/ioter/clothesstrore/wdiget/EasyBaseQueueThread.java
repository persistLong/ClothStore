package com.ioter.clothesstrore.wdiget;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ioter.clothesstrore.common.util.DebugUtil;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 
 *         按队列方式执行线程中和主线程中的操作的逻辑类
 */
public abstract class EasyBaseQueueThread
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
    
    private Handler mUIHandler, mWorkHandler;
    private PriorityQueue<Message> mQueue;
    private boolean mIsDestroy;
    private long mDelayUIMillis;

    /**
     * @param operator 在队列中处理的操作
     */
    public EasyBaseQueueThread()
    {
        mUIHandler = new Handler(Looper.getMainLooper());
        HandlerThread thread = new HandlerThread("EasyQueueThread");
        thread.start();
        mWorkHandler = new Handler(thread.getLooper())
        {
            private boolean isWait;

            @Override
            public void handleMessage(Message msg)
            {
                if (mIsDestroy)
                {
                    return;
                }
                if (msg.what == -1)// UI更新完成
                {
                    if (mQueue == null)
                    {
                        isWait = false;
                        return;
                    }
                    msg = mQueue.poll();
                    if (msg == null)
                    {
                        isWait = false;
                        return;
                    }
                } else if (isWait)// UI更新中
                {
                    if (mQueue == null)
                    {
                        mQueue = new PriorityQueue<Message>(11, new Comparator<Message>()
                        {
                            @Override
                            public int compare(Message lhs, Message rhs)
                            {
                                return rhs.what - lhs.what;
                            }
                        });
                    }
                    Message message = Message.obtain();
                    message.copyFrom(msg);
                    mQueue.add(message);
                    return;
                } else
                {
                    isWait = true;
                }
                if (mIsDestroy)
                {
                    return;
                }
                try
                {
                    final Object obj = doInWork(msg.what, msg.arg1, msg.arg2, msg.obj, msg.peekData());
                    if (mIsDestroy)
                    {
                        return;
                    }
                    mUIHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (mIsDestroy)
                            {
                                return;
                            }
                            try
                            {
                                doInUI(obj);
                            }
                            catch (Exception e)
                            {
                                DebugUtil.printe("EasyBaseQueueThread", "doInUI:" + e);
                            }
                            if (mIsDestroy)
                            {
                                return;
                            }
                            Message msg = Message.obtain();
                            msg.what = -1;
                            mWorkHandler.sendMessageAtFrontOfQueue(msg);
                        }
                    }, mDelayUIMillis);
                }
                catch (Exception e)
                {
                    isWait = false;
                    DebugUtil.printe("EasyBaseQueueThread", "doInWork:" + e);
                }
            }
        };
    }

    /**
     * 销毁，退出页面时必须执行
     */
    public void destroy()
    {
        mIsDestroy = true;
        if (mUIHandler != null)
        {
            mUIHandler.removeCallbacksAndMessages(null);
        }
        if (mWorkHandler != null)
        {
            mWorkHandler.removeCallbacksAndMessages(null);
            ((HandlerThread) mWorkHandler.getLooper().getThread()).quit();
        }
        
        if (mQueue != null)
        {
            mQueue.clear();
        }
    }

    /**
     * 设置在线程操作（doInWork）后，延迟更新主线程操作（doInUI）的时间间隔
     * 
     * @param delayUITime 延迟的时间间隔，以毫秒为单位
     */
    public void setUIDelayTime(long delayUIMillis)
    {
        mDelayUIMillis = delayUIMillis;
    }

    /**
     * 发送信息到线程中操作（doInWork）
     * 
     * @param priority 信息的优先级，优先按数值从大到小处理，不能等于-1
     */
    public void sendWorkMsg(int priority)
    {
        if (mIsDestroy || priority == -1)
        {
            return;
        }
        Message msg = Message.obtain();
        msg.what = priority;
        mWorkHandler.sendMessage(msg);
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
        if (mIsDestroy || priority == -1)
        {
            return;
        }
        Message msg = Message.obtain();
        msg.what = priority;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        msg.setData(bundle);
        mWorkHandler.sendMessage(msg);
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
        if (mIsDestroy || priority == -1)
        {
            return;
        }
        Message msg = Message.obtain();
        msg.what = priority;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        msg.setData(bundle);
        mWorkHandler.sendMessageDelayed(msg, delayMillis);
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
        if (mIsDestroy || priority == -1)
        {
            return;
        }
        Message msg = Message.obtain();
        msg.what = priority;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = obj;
        msg.setData(bundle);
        mWorkHandler.sendMessageAtFrontOfQueue(msg);
    }
    
    /**
     * 移除对应priority的发送信息的任务
     * 
     * @param priority 信息的优先级，优先按数值从大到小处理，不能等于-1
     */
    public void remove(int priority)
    {
        if (mIsDestroy || priority == -1)
        {
            return;
        }
        mWorkHandler.removeMessages(priority);
    }
}