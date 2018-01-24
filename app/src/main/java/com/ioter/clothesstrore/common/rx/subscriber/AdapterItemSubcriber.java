package com.ioter.clothesstrore.common.rx.subscriber;

import android.content.Context;

import com.ioter.clothesstrore.common.exception.BaseException;


public abstract class AdapterItemSubcriber<T> extends ErrorHandlerSubscriber<T>
{

    public AdapterItemSubcriber(Context context)
    {
        super(context);
    }
    @Override
    public void onStart()
    {
    }

    @Override
    public void onCompleted()
    {

    }

    @Override
    public void onError(Throwable e)
    {
        e.printStackTrace();
        BaseException baseException = mErrorHandler.handleError(e);
    }

}
