package com.ioter.clothesstrore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.presenter.BasePresenter;
import com.ioter.clothesstrore.ui.BaseActivity;
import com.ioter.clothesstrore.ui.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView
{

    private Unbinder mUnbinder;

    private AppApplication mApplication;

    private View mRootView;

    public BaseActivity mActivity;


    @Inject
    public T mPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        mRootView = inflater.inflate(setLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        mActivity = (BaseActivity) getActivity();
        init(mRootView);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        this.mApplication = (AppApplication) getActivity().getApplication();
        setupAcitivtyComponent(mApplication.getAppComponent());

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mUnbinder != Unbinder.EMPTY)
        {
            mUnbinder.unbind();
        }
    }

    @Override
    public void showLoading()
    {
    }

    @Override
    public void showError(String msg)
    {
    }

    @Override
    public void dismissLoading()
    {
    }

    public abstract int setLayout();

    public abstract void setupAcitivtyComponent(AppComponent appComponent);


    public abstract void init(View view);


}