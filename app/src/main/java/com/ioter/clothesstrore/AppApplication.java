package com.ioter.clothesstrore;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.di.component.DaggerAppComponent;
import com.ioter.clothesstrore.di.module.AppModule;
import com.ioter.clothesstrore.ui.BaseActivity;
import com.ioter.clothesstrore.wdiget.cache.ClothsStroreCaughtException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


public class AppApplication extends Application
{

    private AppComponent mAppComponent;

    public static ExecutorService getThreadPool()
    {
        return mThreadPool;
    }

    private static ExecutorService mThreadPool;

    private static AppApplication mApplication;

    public static AppApplication getApplication()
    {
        return mApplication;
    }

    public AppComponent getAppComponent()
    {
        return mAppComponent;
    }

    private static List<BaseActivity> mActivityCache;

    public static Gson mGson;

    public static Gson getGson()
    {
        return mGson;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .build();
        mApplication = (AppApplication) mAppComponent.getApplication();
        mThreadPool = mAppComponent.getExecutorService();
        mGson = mAppComponent.getGson();
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        mActivityCache = new ArrayList<BaseActivity>();
        Thread.setDefaultUncaughtExceptionHandler(new ClothsStroreCaughtException());// 注册全局异常捕获
    }

    public void addActivity(BaseActivity activity)
    {
        mActivityCache.add(activity);
    }

    public static BaseActivity getCurrentActivity()
    {
        if (mActivityCache.size() > 0)
        {
            for (int i = mActivityCache.size() - 1; i > -1; i--)
            {
                BaseActivity activity = mActivityCache.get(i);
                if (activity != null)
                {
                    return activity;
                }
            }
        }
        return null;
    }

    public static boolean removeActivity(BaseActivity activity)
    {
        return mActivityCache.remove(activity);
    }

}
