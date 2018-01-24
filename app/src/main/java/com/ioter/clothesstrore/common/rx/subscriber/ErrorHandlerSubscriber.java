package com.ioter.clothesstrore.common.rx.subscriber;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ioter.clothesstrore.common.exception.BaseException;
import com.ioter.clothesstrore.common.rx.RxErrorHandler;
import com.ioter.clothesstrore.ui.activity.MainActivity;


public abstract  class ErrorHandlerSubscriber<T> extends DefualtSubscriber<T> {


    protected RxErrorHandler mErrorHandler = null;

    protected Context mContext;

    public ErrorHandlerSubscriber(Context context){

        this.mContext = context;


        mErrorHandler = new RxErrorHandler(mContext);

    }


    @Override
    public void onError(Throwable e) {

        BaseException baseException =  mErrorHandler.handleError(e);

        if(baseException==null){
            e.printStackTrace();
            Log.d("ErrorHandlerSubscriber",e.getMessage());
        }
        else {

            mErrorHandler.showErrorMessage(baseException);
            if(baseException.getCode() == BaseException.ERROR_TOKEN){
                toLogin();
            }

        }

    }

    private void toLogin() {

        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }


}
