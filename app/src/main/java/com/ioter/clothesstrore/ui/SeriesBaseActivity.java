package com.ioter.clothesstrore.ui;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.common.util.DebugUtil;
import com.ioter.clothesstrore.common.util.ToastUtil;
import com.ioter.clothesstrore.data.TagRecord;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class SeriesBaseActivity extends BaseActivity
{
    public static final String HOST = "172.16.33.84";
    public static final int PORT = 6000;
    public static final String DEFAULT_HIDE_EPC = "default_hide_epc";
    private String currentEpc = DEFAULT_HIDE_EPC;//当前显示的衣服
    public static final int EPC_SHOW = 1;
    public static final int EPC_HIED = 2;


    private HashMap<String, TagRecord> epcToReadDataMap = new HashMap<String, TagRecord>();
    private List<String> epcToReadLastList = new ArrayList<>();
    private Handler mUIHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg == null)
            {
                return;
            }
            if (msg.what == EPC_SHOW)
            {
                showEpc((String) msg.obj);
            } else if (msg.what == EPC_HIED)
            {
                hieEpc();
            }
        }
    };

    protected abstract void OutPutEpcS(ArrayList<String> diffTags);

    protected abstract void showEpc(String epc);

    protected abstract void hieEpc();

    @Override
    protected void onResume()
    {
        initMina();
        initMsgThread();
        super.onResume();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        Dispose();
    }

    private void Dispose()
    {
        epcToReadDataMap.clear();
        epcToReadLastList.clear();
        if (session != null)
        {
            session.closeNow();
        }
        if (connector != null)
        {
            connector.dispose();
        }
    }


    private IoSession session = null;
    private IoConnector connector = null;

    public void initMina()
    {
        AppApplication.getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    connector = new NioSocketConnector();
                    connector.setHandler(new MyClientHandler());
                    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
                    connector.setConnectTimeoutMillis(6000);
                    //connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 6000);
                    connector.setDefaultRemoteAddress(new InetSocketAddress(HOST, PORT));

                    // 监听客户端是否断线
                    connector.addListener(new IoServiceListener()
                    {
                        @Override
                        public void sessionDestroyed(IoSession arg0) throws Exception
                        {
                            int failCount = 0;
                            while (true)
                            {
                                try
                                {
                                    Thread.sleep(3000);
                                    ConnectFuture future = connector.connect();
                                    future.awaitUninterruptibly();// 等待连接创建完成
                                    session = future.getSession();// 获得session
                                    if (session != null && session.isConnected())
                                    {
                                        epcToReadDataMap.clear();//重置数据
                                        session.getConfig().setReaderIdleTime(600);
                                        toastInUi("Socket断线重连成功");
                                        DebugUtil.printe("server", "断线重连成功");
                                        break;
                                    } else
                                    {
                                        DebugUtil.printe("server", "断线重连失败---->" + failCount + "次");
                                    }
                                } catch (Exception e)
                                {
                                    toastInUi("Socket断线重连失败");
                                    DebugUtil.printe("server", "断线重连失败");
                                }
                            }
                        }

                        @Override
                        public void sessionCreated(IoSession arg0) throws Exception
                        {

                        }

                        @Override
                        public void sessionClosed(IoSession arg0) throws Exception
                        {
                        }

                        @Override
                        public void serviceIdle(IoService arg0, IdleStatus arg1) throws Exception
                        {

                        }

                        @Override
                        public void serviceDeactivated(IoService arg0) throws Exception
                        {

                        }

                        @Override
                        public void serviceActivated(IoService arg0) throws Exception
                        {

                        }
                    });
                    ConnectFuture future = connector.connect();
                    future.awaitUninterruptibly();
                    session = future.getSession();
                    if (session != null && session.isConnected())
                    {
                        session.getConfig().setReaderIdleTime(600);
                        toastInUi("Socket连接成功");
                    } else
                    {
                        toastInUi("Socket连接失败，session is null");
                        DebugUtil.printe("server", "客户端连接异常:" + "session is null");
                    }
                } catch (Exception e)
                {
                    toastInUi("Socket连接失败" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }


    private void toastInUi(final String toast)
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtil.toast(toast);
            }
        });
    }

    public static boolean isForeground(Context context, String className)
    {
        if (context == null || TextUtils.isEmpty(className))
        {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0)
        {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
            {
                return true;
            }
        }

        return false;

    }

    public class MyClientHandler extends IoHandlerAdapter
    {
        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception
        {
            DebugUtil.printe("server", "exceptionCaught");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception
        {
            synchronized (SeriesBaseActivity.class)
            {
                if (message == null)
                {
                    return;
                }
                if (message instanceof String)
                {
                    String epcString = (String) message;
                    DebugUtil.printe("server", "messageReceived: " + epcString);
                    long currentTime = System.currentTimeMillis();
                    if (epcString == null || epcString.trim().length() == 0)
                    {
                        return;
                    }
                    if (!epcToReadDataMap.keySet().contains(epcString) && !epcString.endsWith("hide"))
                    {
                        TagRecord tagRecord = new TagRecord();
                        tagRecord.setEpcString(epcString);
                        tagRecord.setLastTime(currentTime);
                        epcToReadDataMap.put(epcString, tagRecord);
                    }


                    Iterator it = epcToReadDataMap.keySet().iterator();
                    while (it.hasNext())
                    {
                        String key = (String) it.next();
                        if (epcString.endsWith("hide") && epcString.split("\\|")[0].equals(key))
                        {
                            epcToReadDataMap.remove(key);
                            currentEpc = DEFAULT_HIDE_EPC;
                            mUIHandler.sendEmptyMessage(EPC_HIED);
                        }
                    }



         /*           final ArrayList<String> epcToReadRecentList = new ArrayList<>();
                    Iterator it = epcToReadDataMap.keySet().iterator();
                    while (it.hasNext())
                    {
                        String key = (String) it.next();
                        if (epcString.endsWith("hide") && epcString.split("\\|")[0].equals(key))
                        {
                            epcToReadDataMap.remove(key);
                        } else
                        {
                            epcToReadRecentList.add(key);
                        }
                    }
                    if (isdifferentList(epcToReadRecentList, epcToReadLastList))
                    {
                        mUIHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                OutPutEpcS(epcToReadRecentList);
                            }
                        });
                        epcToReadLastList.clear();
                        epcToReadLastList.addAll(epcToReadRecentList);
                    }*/
                }
            }
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception
        {
            DebugUtil.printe("server", "messageSent");
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception
        {
            DebugUtil.printe("server", "sessionClosed");
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception
        {
            DebugUtil.printe("server", "sessionCreated");
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception
        {
            DebugUtil.printe("server", "sessionIdle");
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception
        {
            DebugUtil.printe("server", "sessionOpened");
        }

    }


    private void initMsgThread()
    {
        AppApplication.getApplication().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    if (!epcToReadDataMap.isEmpty())
                    {
                        String showEpc = DEFAULT_HIDE_EPC;//该显示的衣服
                        Iterator it = epcToReadDataMap.keySet().iterator();
                        long currentTime = System.currentTimeMillis();
                        while (it.hasNext())
                        {
                            String key = (String) it.next();
                            TagRecord tagRecord = epcToReadDataMap.get(key);
                            long tempTime = currentTime - tagRecord.getLastTime();
                            DebugUtil.printe("server-time", "tempTime == " + tempTime);
                            if (tempTime > 100000)//超时移除
                            {
                                epcToReadDataMap.remove(key);
                                mUIHandler.sendEmptyMessage(EPC_HIED);
                            } else
                            {
                                showEpc = key;
                                break;
                            }
                        }
                        if (showEpc.length() > 0)//重置拿起的衣服的时间
                        {
                            Iterator it2 = epcToReadDataMap.keySet().iterator();
                            while (it2.hasNext())
                            {
                                String key = (String) it2.next();
                                if (!key.equals(showEpc))
                                {
                                    epcToReadDataMap.get(key).setLastTime(currentTime);
                                }
                            }
                            if (!showEpc.equals(currentEpc))
                            {
                                Message msg = Message.obtain();
                                msg.what = EPC_SHOW;
                                msg.obj = showEpc;
                                mUIHandler.sendMessage(msg);
                            }
                        }
                        currentEpc = showEpc;
                        DebugUtil.printe("server-epc", "epc == " + currentEpc);
                    }
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 判断两个List的不同
     *
     * @param list1
     * @param list2
     * @return
     */

    private boolean isdifferentList(List<String> list1, List<String> list2)
    {
        if (list1.size() != list2.size())
        {
            return true;
        }
        boolean isChange = false;
        for (String string : list1)
        {
            DebugUtil.printe("list1", string);
        }
        for (String string : list2)
        {
            DebugUtil.printe("list2", string);
        }
        for (String string : list1)
        {
            if (!list2.contains(string))
            {
                isChange = true;
                break;
            }
        }
        return isChange;
    }

    /* private WeakReference<Socket> mWeakSocket;

     private void initSocket()
     {//初始化Socket
         new Thread(new Runnable()
         {
             @Override
             public void run()
             {
                 try
                 {
                     Socket so = new Socket(HOST, PORT);
                     mWeakSocket = new WeakReference<Socket>(so);
                     Socket socket = mWeakSocket.get();
                     if (socket != null)
                     {
                         mUIHandler.post(new Runnable()
                         {
                             @Override
                             public void run()
                             {
                                 ToastUtil.toast("socket连接成功");
                             }
                         });
                         socket.setSoTimeout(550);
                         if (!socket.isClosed() && !socket.isInputShutdown())
                         {
                             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                             startServerReplyListener(reader);
                         }
                     }
                 } catch (Exception e)
                 {
                     e.printStackTrace();
                 } finally
                 {
                     try
                     {
                         if (null != mWeakSocket)
                         {
                             Socket sk = mWeakSocket.get();
                             if (!sk.isClosed())
                             {
                                 sk.close();
                             }
                             sk = null;
                             mWeakSocket = null;
                         }
                     } catch (Exception e)
                     {
                         e.printStackTrace();
                     }

                 }
             }
         }).start();
     }

     public void startServerReplyListener(final BufferedReader reader)
     {
         AppApplication.getThreadPool().submit(new Runnable()
         {
             @Override
             public void run()
             {
                 try
                 {
                     String epcString;
                     while ((epcString = reader.readLine()) != null)
                     {
                         DebugUtil.printe("Server", epcString);
                         if (!TextUtils.isEmpty(epcString))
                         {
                             long currentTime = System.currentTimeMillis();
                             TagRecord tagRecord;
                             if (epcToReadDataMap.keySet().contains(epcString))
                             {
                                 tagRecord = (TagRecord) epcToReadDataMap.get(epcString);
                                 if (currentTime - tagRecord.getLastTime() > 1000)
                                 {
                                     tagRecord.setPutBack(true);
                                 }
                                 tagRecord.setLastTime(currentTime);
                             } else
                             {
                                 tagRecord = new TagRecord();
                                 tagRecord.setEpcString(epcString);
                                 tagRecord.setLastTime(currentTime);
                                 epcToReadDataMap.put(epcString, tagRecord);
                             }
                             if (!epcToReadDataMap.isEmpty())
                             {
                                 ArrayList<TagRecord> list = new ArrayList<>();
                                 for (String key : epcToReadDataMap.keySet())
                                 {
                                     TagRecord record = epcToReadDataMap.get(key);
                                     if (record.isPutBack())
                                     {
                                         record.setPutBack(false);
                                         continue;
                                     }
                                     if (currentTime - record.getLastTime() > 1000)
                                     {
                                         list.add(record);
                                     }
                                 }
                                 if (list.size() > 0)
                                 {
                                     if (list.size() > 1)
                                     {
                                         Collections.sort(list, new Comparator<TagRecord>()
                                         {
                                             @Override
                                             public int compare(TagRecord o1, TagRecord o2)
                                             {
                                                 return (int) (o1.getLastTime() - o2.getLastTime());
                                             }
                                         });
                                     }
                              *//*       Message msg = Message.obtain();
                                    msg.what = MSG_READ_EPC_DATA;
                                    msg.obj = list.get(0).getEpcString();
                                    mUIHandler.sendMessage(msg);*//*
                                }
                            }
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
*/
    @Override
    protected void onDestroy()
    {
        if (session != null)
        {
            session.closeNow();
        }
        if (connector != null)
        {
            connector.dispose();
        }
        if (mUIHandler != null)
        {
            mUIHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
