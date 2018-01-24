package com.ioter.clothesstrore.video.floatUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.ui.SeriesBaseActivity;
import com.ioter.clothesstrore.ui.fragment.MainFragment;
import com.ioter.clothesstrore.ui.fragment.TidDetailFragment;
import com.ioter.clothesstrore.ui.fragment.TidDetailFragment3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class EpcDetailActivity extends SeriesBaseActivity
{
    public static final String MAIN = "main";
    public static final String EPCS = "epcs";

    public static final String ACTION_UPDATE_DATA_UI = "action.update_data_UI";
    public static final String ACTION_UPDATE_MSG_UI = "action.update_msg_UI";
    //private UpdateUIBroadcastReceiver broadcastReceiver;

/*    private class UpdateUIBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent == null || !SeriesBaseActivity.isForeground(EpcDetailActivity.this, EpcDetailActivity.this.getComponentName().getClassName()))
            {
                return;
            }

            if (ACTION_UPDATE_MSG_UI.equals(intent.getAction()))
            {
                String msgData = intent.getStringExtra(SocketService.MSG_DATAS);
                if (!TextUtils.isEmpty(msgData))
                {
                    ToastUtil.toast(msgData);
                }
            } else if (ACTION_UPDATE_DATA_UI.equals(intent.getAction()))
            {
                ArrayList<String> epc_list = (ArrayList<String>) intent.getSerializableExtra(SocketService.EPC_DATAS);
                OutPutEpcS(epc_list);
            }
        }

    }*/


    @Override
    public int setLayout()
    {
        return R.layout.activity_epc_main;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init()
    {
        // 动态注册广播
 /*       IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_DATA_UI);
        filter.addAction(ACTION_UPDATE_MSG_UI);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);*/

    /*    Intent startIntent = new Intent(this, SocketService.class);
        startService(startIntent);*/

        MainFragment fragment1 = new MainFragment();
        //EpcDetailFragment fragment2 = new EpcDetailFragment();
        //TidDetailFragment fragment2 = new TidDetailFragment();
        //TidDetailFragment2 fragment2 = new TidDetailFragment2();
        TidDetailFragment3 fragment2 = new TidDetailFragment3();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment1, MAIN).add(R.id.content_frame, fragment2, EPCS).hide(fragment2);
        transaction.commitAllowingStateLoss();



        //showFragment(MAIN);
    }

    @Override
    protected void OutPutEpcS(ArrayList<String> diffTags)
    {
        if (diffTags.size() > 0)
        {
/*            EpcDetailFragment epcDetailFragment = (EpcDetailFragment) getSupportFragmentManager().findFragmentByTag(EPCS);
            if (epcDetailFragment != null)
            {
                epcDetailFragment.showEpcDetail(diffTags.get(0));
                if (!epcDetailFragment.isVisible())
                {
                    showFragment(EPCS);
                }
            }*/
            TidDetailFragment tidDetailFragment = (TidDetailFragment) getSupportFragmentManager().findFragmentByTag(EPCS);
            if (tidDetailFragment != null)
            {
                tidDetailFragment.showEpcDetail(diffTags.get(0));
                if (!tidDetailFragment.isVisible())
                {
                    showFragment(EPCS);
                }
            }
        } else
        {
            MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN);
            if (!mainFragment.isVisible())
            {
                showFragment(MAIN);
               /* TidDetailFragment tidDetailFragment = (TidDetailFragment) getSupportFragmentManager().findFragmentByTag(EPCS);
                tidDetailFragment.clearData();*/
            }
        }
    }

    @Override
    protected void showEpc(String epc)
    {
        TidDetailFragment3 tidDetailFragment = (TidDetailFragment3) getSupportFragmentManager().findFragmentByTag(EPCS);
        if (tidDetailFragment != null)
        {
            if (!tidDetailFragment.isVisible())
            {
                showFragment(EPCS);
            }
            tidDetailFragment.showEpcDetail(epc);
        }
    }

    @Override
    protected void hieEpc()
    {
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN);
        if (!mainFragment.isVisible())
        {
            showFragment(MAIN);
        }
    }


    private String currentEpc;

    public void showFragment(String key)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        List<Fragment> fragmentList = fragmentManager.getFragments();
        if (fragmentList != null)
        {
            for (Fragment fragment : fragmentList)
            {
                if (fragment != null)
                {
                    String tag = fragment.getTag();
                    if (key.equals(tag))
                    {
                        transaction.show(fragment);
                    } else
                    {
                        transaction.hide(fragment);
                    }
                }
            }
        }
        if (transaction != null)
        {
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
/*        Intent stopIntent = new Intent(this, SocketService.class);
        stopService(stopIntent);*/
    }

    @Override
    protected void onDestroy()
    {
/*        if (broadcastReceiver != null)
        {
            // 注销广播
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }*/
        ExecutorService mThreadPool = AppApplication.getThreadPool();
        if (mThreadPool != null)// 取消线程池的所有任务
        {
            mThreadPool.shutdownNow();
            mThreadPool = null;
        }
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    public void onClick(View v)
    {

    }

    private static List<PermissionListener> mPermissionListenerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23){
            requestAlertWindowPermission();
        }
    }

    @RequiresApi(api = 23)
    private void requestAlertWindowPermission() {
        Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }


    @RequiresApi(api = 23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= 23){
            //todo 用23以上编译即可出现canDrawOverlays
            if (Util.hasPermission(this)) {
                mPermissionListener.onSuccess();
            } else {
                mPermissionListener.onFail();
            }
        }
        finish();
    }

    static synchronized void request(Context context, PermissionListener permissionListener) {
        if (mPermissionListenerList == null) {
            mPermissionListenerList = new ArrayList<>();
            mPermissionListener = new PermissionListener() {
                @Override
                public void onSuccess() {
                    for (PermissionListener listener : mPermissionListenerList) {
                        listener.onSuccess();
                    }
                }

                @Override
                public void onFail() {
                    for (PermissionListener listener : mPermissionListenerList) {
                        listener.onFail();
                    }
                }
            };
            Intent intent = new Intent(context, EpcDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        mPermissionListenerList.add(permissionListener);
    }


    private static PermissionListener mPermissionListener;
}
