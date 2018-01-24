package com.ioter.clothesstrore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.been.requestBeen.ClothBean;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.di.component.DaggerProductComponent;
import com.ioter.clothesstrore.di.module.ProductModule;
import com.ioter.clothesstrore.presenter.ProductPresenter;
import com.ioter.clothesstrore.presenter.contract.ProductInfoContract;
import com.ioter.clothesstrore.video.FloatPlayerView;
import com.ioter.clothesstrore.video.floatUtil.FloatWindow;
import com.ioter.clothesstrore.video.floatUtil.MoveType;
import com.ioter.clothesstrore.video.floatUtil.Screen;
import com.ioter.clothesstrore.wdiget.IViewpagerFragmentCallback;
import com.ioter.clothesstrore.wdiget.ViewpagerFragmentAdapter;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import butterknife.BindView;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;

public class TidDetailFragment2 extends BaseFragment<ProductPresenter> implements ProductInfoContract.ProductInfoView, EpcImgListFragment.IEpcClick
{

    @BindView(R.id.pager_container)
    PagerContainer pagerContainer;
    @BindView(R.id.loading_IndicatiorView)
    AVLoadingIndicatorView indicatorView;
    private ViewPager viewPager;
    private ViewpagerFragmentAdapter mFragmentAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_tid_detail2;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerProductComponent.builder().appComponent(appComponent).productModule(new ProductModule(this)).build().inject(this);
    }

    @Override
    public void init(View view)
    {
        pagerContainer.setOverlapEnabled(true);
        viewPager = pagerContainer.getViewPager();



//大图
        final ArrayList<String> pathUrl = new ArrayList<>();
        pathUrl.add("http://172.16.33.84/Files/2018/1/2c4f83e3-8a7c-42f1-a3c1-6416bcfe8f43.jpg");
        pathUrl.add("http://172.16.33.84/Files/2018/1/2c4f83e3-8a7c-42f1-a3c1-6416bcfe8f43.jpg");
        pathUrl.add("http://172.16.33.84/Files/2018/1/2c4f83e3-8a7c-42f1-a3c1-6416bcfe8f43.jpg");
        pathUrl.add("http://172.16.33.84/Images/2017/12/1d8d4271-21ba-4991-b692-2a0582dd1c16.mp4");
        if (mFragmentAdapter == null)
        {
            mFragmentAdapter = new ViewpagerFragmentAdapter(getChildFragmentManager());
            viewPager.setAdapter(mFragmentAdapter);
        }
        mFragmentAdapter.setNewCount(pathUrl.size(), new IViewpagerFragmentCallback()
        {
            @Override
            public Fragment createFragment(int pos)
            {
                String path = pathUrl.get(pos);
                if (path.endsWith(".mp4"))
                {
                    return PlayFragment.newInstance(path);
                }
                return EpcImgFragment.newInstance(path,pos);
            }
        });
        viewPager.setOffscreenPageLimit(pathUrl.size());
        new CoverFlow.Builder()
                .with(viewPager)
                .scale(0.3f)
                .pagerMargin(0f)
                .spaceSize(0f)
                .rotationY(25f)
                .build();

/*
        if (FloatWindow.get() != null)
        {
            return;
        }
        FloatPlayerView floatPlayerView = new FloatPlayerView(mActivity);
        FloatWindow
                .with(AppApplication.getApplication())
                .setView(floatPlayerView)
                .setWidth(Screen.width, 0.3f)
                .setHeight(Screen.width, (float) ((0.3 * 0.5625) * 1f))
                .setX(Screen.width, 0.0f)
                .setY(Screen.height, 1.0f)
                .setMoveType(MoveType.slide)
                .setFilter(false)
                .setMoveStyle(500, new BounceInterpolator())
                .build();
        FloatWindow.get().show();*/


        indicatorView.hide();
        indicatorView.setVisibility(View.GONE);
        pagerContainer.setVisibility(View.VISIBLE);
    }

    //后台指令回调
    @Override
    public void showResult(String epc, ClothBean data)
    {
        if (data != null)
        {
            final String imgUrl = data.getImgFullUrl();
            if (imgUrl != null && imgUrl.length() > 0)
            {
                //大图
                final ArrayList<String> pathUrl = new ArrayList<>();
                pathUrl.add("http://172.16.33.84/Files/2018/1/2c4f83e3-8a7c-42f1-a3c1-6416bcfe8f43.jpg");
                pathUrl.add("http://172.16.33.84/Files/2018/1/2c4f83e3-8a7c-42f1-a3c1-6416bcfe8f43.jpg");
                pathUrl.add("http://172.16.33.84/Files/2018/1/2c4f83e3-8a7c-42f1-a3c1-6416bcfe8f43.jpg");
                //pathUrl.add("http://172.16.33.84/Images/2017/12/1d8d4271-21ba-4991-b692-2a0582dd1c16.mp4");
                if (mFragmentAdapter == null)
                {
                    mFragmentAdapter = new ViewpagerFragmentAdapter(getChildFragmentManager());
                    viewPager.setAdapter(mFragmentAdapter);
                }
                mFragmentAdapter.setNewCount(pathUrl.size(), new IViewpagerFragmentCallback()
                {
                    @Override
                    public Fragment createFragment(int pos)
                    {
                        String path = pathUrl.get(pos);
                        if (path.endsWith(".mp4"))
                        {
                            return PlayFragment.newInstance(path);
                        }
                        return EpcImgFragment.newInstance(path,pos);
                    }
                });
                viewPager.setOffscreenPageLimit(pathUrl.size());
                new CoverFlow.Builder()
                        .with(viewPager)
                        .scale(0.3f)
                        .pagerMargin(0f)
                        .spaceSize(0f)
                        .rotationY(25f)
                        .build();


                if (FloatWindow.get() != null)
                {
                    return;
                }
                FloatPlayerView floatPlayerView = new FloatPlayerView(AppApplication.getApplication());
                FloatWindow
                        .with(AppApplication.getApplication())
                        .setView(floatPlayerView)
                        .setWidth(Screen.width, 0.4f)
                        .setHeight(Screen.width, 0.4f)
                        .setX(Screen.width, 0.8f)
                        .setY(Screen.height, 0.3f)
                        .setMoveType(MoveType.slide)
                        .setFilter(false)
                        .setMoveStyle(500, new BounceInterpolator())
                        .build();
                FloatWindow.get().show();

                //Manually setting the first View to be elevated
  /*              viewPager.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Fragment fragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                        ViewCompat.setElevation(fragment.getView(), 8.0f);
                    }
                });*/
                indicatorView.hide();
                indicatorView.setVisibility(View.GONE);
                pagerContainer.setVisibility(View.VISIBLE);

            }
        }
    }

    //从读写器读到的epc值
    public void showEpcDetail(String epc)
    {
        indicatorView.setVisibility(View.VISIBLE);
        indicatorView.show();
        pagerContainer.setVisibility(View.GONE);
        mPresenter.requestDatas(epc);
    }

    @Override
    public void show(int position)
    {

    }

    @Override
    public void onDestroy()
    {
        GSYVideoManager.instance().releaseMediaPlayer();
        FloatWindow.destroy();
        super.onDestroy();
    }
}
