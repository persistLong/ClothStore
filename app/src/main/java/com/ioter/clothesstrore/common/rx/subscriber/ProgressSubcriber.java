package com.ioter.clothesstrore.common.rx.subscriber;

import android.content.Context;

import com.ioter.clothesstrore.common.exception.BaseException;
import com.ioter.clothesstrore.ui.BaseView;


public abstract class ProgressSubcriber<T> extends ErrorHandlerSubscriber<T>
{

    private BaseView mView;


    public ProgressSubcriber(Context context, BaseView view)
    {
        super(context);
        this.mView = view;

    }


    public boolean isShowProgress()
    {
        return true;
    }


    @Override
    public void onStart()
    {
        if (isShowProgress())
        {
            mView.showLoading();
        }
    }

    @Override
    public void onCompleted()
    {

        mView.dismissLoading();
    }

    @Override
    public void onError(Throwable e)
    {

        e.printStackTrace();

        BaseException baseException = mErrorHandler.handleError(e);
        mView.showError(baseException.getDisplayMessage());

    }

}
