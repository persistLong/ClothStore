package com.ioter.clothesstrore.di.component;

import android.app.Application;

import com.google.gson.Gson;
import com.ioter.clothesstrore.data.http.ApiService;
import com.ioter.clothesstrore.di.module.AppModule;
import com.ioter.clothesstrore.di.module.HttpModule;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent
{

    public Application getApplication();

    public Gson getGson();

    public ApiService getApiService();

    public ExecutorService getExecutorService();

}
