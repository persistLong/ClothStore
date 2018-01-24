package com.ioter.clothesstrore.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.common.util.DebugUtil;
import com.ioter.clothesstrore.data.TagRecord;
import com.ioter.clothesstrore.ui.SeriesBaseActivity;
import com.ioter.clothesstrore.video.floatUtil.EpcDetailActivity;

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

public class SocketService extends Service
{
    public static final String TAG = "SocketService";
    public static final String HOST = "192.168.31.67";
    public static final String EPC_DATAS = "epc_datas";
    public static final String MSG_DATAS = "msg_datas";
    public static final int PORT = 6000;
    private IoSession session = null;
    private IoConnector connector = null;


    private HashMap<String, TagRecord> epcToReadDataMap = new HashMap<String, TagRecord>();
    private List<String> epcToReadLastList = new ArrayList<>();

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        initMina();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }


    private void initMina()
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
        final Intent intent = new Intent();
        intent.setAction(EpcDetailActivity.ACTION_UPDATE_MSG_UI);
        intent.putExtra(MSG_DATAS, toast);
        sendBroadcast(intent);
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
                String epcString = (String) message;
                DebugUtil.printe("server", "messageReceived: " + epcString);
                if (TextUtils.isEmpty(epcString))
                {
                    return;
                }
                if (!epcToReadDataMap.keySet().contains(epcString) && !epcString.endsWith("hide"))
                {
                    TagRecord tagRecord = new TagRecord();
                    tagRecord.setEpcString(epcString);
                    epcToReadDataMap.put(epcString, tagRecord);
                }
                final ArrayList<String> epcToReadRecentList = new ArrayList<>();
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
                    Intent intent = new Intent();
                    intent.setAction(EpcDetailActivity.ACTION_UPDATE_DATA_UI);
                    intent.putExtra(EPC_DATAS, epcToReadRecentList);
                    sendBroadcast(intent);
                    epcToReadLastList.clear();
                    epcToReadLastList.addAll(epcToReadRecentList);
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
            if (session != null)
            {
                session.close(true);
            }
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
        if (session != null)
        {
            session.closeNow();
        }
        if (connector != null)
        {
            connector.dispose();
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
