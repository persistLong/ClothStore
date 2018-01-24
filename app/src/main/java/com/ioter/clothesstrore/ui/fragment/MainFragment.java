package com.ioter.clothesstrore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.wdiget.BannerLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/20.
 */

public class MainFragment extends BaseFragment
{
    @BindView(R.id.banner)
    BannerLayout mBannerLayout;

    private int mDevice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.fragment_main;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init(View view)
    {
        initBanner();
    }

    private void initBanner()
    {
        List<String> views = new ArrayList<String>();
        String bannerUrl1 = "http://www.kaltendin.com.cn/upload/201712/19/201712191607116562.jpg";
        String bannerUrl2 = "http://www.kaltendin.com.cn/upload/201712/19/201712191607513750.jpg";
        String bannerUrl3 = "http://www.kaltendin.com.cn/upload/201709/25/201709251825592656.jpg";
        views.add(bannerUrl1);
        views.add(bannerUrl2);
        views.add(bannerUrl3);
        mBannerLayout.setViewUrls(views);

       mBannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {

            }
        });
    }


}
