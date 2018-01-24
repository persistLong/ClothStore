package com.ioter.clothesstrore.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.been.requestBeen.ClothBean;
import com.ioter.clothesstrore.common.util.PagerTransformer;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.di.component.DaggerProductComponent;
import com.ioter.clothesstrore.di.module.ProductModule;
import com.ioter.clothesstrore.presenter.ProductPresenter;
import com.ioter.clothesstrore.presenter.contract.ProductInfoContract;
import com.ioter.clothesstrore.wdiget.IViewpagerFragmentCallback;
import com.ioter.clothesstrore.wdiget.ViewpagerFragmentAdapter;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import butterknife.BindView;

public class TidDetailFragment3 extends BaseFragment<ProductPresenter> implements ProductInfoContract.ProductInfoView, EpcImgFragment.PageOnItemClick
{

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.container_view_pager)
    View containerPager;
    @BindView(R.id.loading_IndicatiorView)
    AVLoadingIndicatorView indicatorView;
    private ViewpagerFragmentAdapter mFragmentAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_tid_detail3;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerProductComponent.builder().appComponent(appComponent).productModule(new ProductModule(this)).build().inject(this);
    }

    @Override
    public void init(View view)
    {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
        layoutParams.leftMargin = (int) (((WindowManager) AppApplication.getApplication().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth() / 4);
        layoutParams.rightMargin = layoutParams.leftMargin;
        viewPager.setLayoutParams(layoutParams);
        //设置Page间间距
        viewPager.setPageMargin(60);
        //设置缓存的页面数量
        viewPager.setOffscreenPageLimit(6);
        viewPager.setPageTransformer(true, new PagerTransformer());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (mFragmentAdapter != null)
                {
                    SparseArray<Fragment> spFragments = mFragmentAdapter.getSpFragments();
                    if (spFragments != null && spFragments.size() > 0)
                    {
                        PlayFragment playFragment = null;
                        for (int i = 0; i < spFragments.size(); i++)
                        {
                            Fragment fragment = spFragments.get(i);
                            if (fragment instanceof PlayFragment)
                            {
                                playFragment = (PlayFragment) fragment;
                                break;
                            }
                        }
                        if (playFragment == null)
                        {
                            return;
                        }

                        Fragment fragment = spFragments.get(position);
                        if (fragment instanceof PlayFragment && fragment == playFragment)
                        {
                            playFragment.detailPlayer.getCurrentPlayer().startPlayLogic();
                        } else
                        {
                            playFragment.detailPlayer.getCurrentPlayer().onVideoPause();
                            playFragment.detailPlayer.getThumbImageViewLayout().setVisibility(View.VISIBLE);
                            playFragment.detailPlayer.findViewById(R.id.layout_bottom).setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });


    }

    //后台指令回调
    @Override
    public void showResult(String epc, ClothBean data)
    {
        indicatorView.hide();
        indicatorView.setVisibility(View.GONE);
        containerPager.setVisibility(View.VISIBLE);
        if (data != null)
        {
            final String imgUrl = data.getImgFullUrl();
            if (imgUrl != null && imgUrl.length() > 0)
            {
//大图
                final ArrayList<String> pathUrl = new ArrayList<>();
                pathUrl.add("http://www.kaltendin.com.cn/upload/201712/19/201712191607116562.jpg");
                pathUrl.add("http://www.kaltendin.com.cn/upload/201712/19/201712191607116562.jpg");
                pathUrl.add("http://www.kaltendin.com.cn/upload/201712/19/201712191607116562.jpg");
                pathUrl.add("http://www.kaltendin.com.cn/upload/201712/19/201712191607116562.jpg");
                pathUrl.add("http://www.kaltendin.com.cn/upload/201712/19/201712191607116562.jpg");
                pathUrl.add("http://www.kaltendin.com.cn/upload/201712/19/201712191607116562.jpg");
                pathUrl.add("http://172.16.33.84/Images/2017/12/1d8d4271-21ba-4991-b692-2a0582dd1c16.mp4");
                pathUrl.add("http://a.rovinj.cn/4c06028d4e444f5987d1e07b96049133/6841d567a549497eabd8a03947bda781-5287d2089db37e62345123a1be272f8b.mp4?auth_key=1516766813-232957898-0-96709fa3aa9bf84e9b06bee18d3c8eb3");
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
                        if (path.endsWith(".mp4") || pos == 7)
                        {
                            return PlayFragment.newInstance(path);
                        }
                        return EpcImgFragment.newInstance(path, pos).setItemClick(TidDetailFragment3.this);
                    }
                });
                if (pathUrl.size() > 1)
                {
                    viewPager.setCurrentItem(1);
                }
            }
        }
    }

    //从读写器读到的epc值
    public void showEpcDetail(String epc)
    {
        indicatorView.setVisibility(View.VISIBLE);
        indicatorView.show();
        containerPager.setVisibility(View.GONE);
        mPresenter.requestDatas(epc);
    }


    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        if (hidden)
        {// 不在最前端界面显示
            if (mFragmentAdapter != null)
            {
                SparseArray<Fragment> spFragments = mFragmentAdapter.getSpFragments();
                if (spFragments != null && spFragments.size() > 0)
                {
                    PlayFragment playFragment = null;
                    for (int i = 0; i < spFragments.size(); i++)
                    {
                        Fragment fragment = spFragments.get(i);
                        if (fragment instanceof PlayFragment)
                        {
                            playFragment = (PlayFragment) fragment;
                            break;
                        }
                    }
                    if (playFragment == null)
                    {
                        return;
                    }
                    playFragment.detailPlayer.getCurrentPlayer().onVideoPause();
                }
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void itemClick(int position)
    {
        if (viewPager != null && viewPager.getCurrentItem() != position)
        {
            viewPager.setCurrentItem(position);
        }
    }
}
