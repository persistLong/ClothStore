package com.ioter.clothesstrore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.been.requestBeen.Attachment;
import com.ioter.clothesstrore.been.requestBeen.ClothBean;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.di.component.DaggerProductComponent;
import com.ioter.clothesstrore.di.module.ProductModule;
import com.ioter.clothesstrore.presenter.ProductPresenter;
import com.ioter.clothesstrore.presenter.contract.ProductInfoContract;
import com.ioter.clothesstrore.wdiget.APagingViewPager;
import com.ioter.clothesstrore.wdiget.IViewpagerFragmentCallback;
import com.ioter.clothesstrore.wdiget.ViewpagerFragmentAdapter;

import java.util.ArrayList;

import butterknife.BindView;

public class EpcDetailFragment extends BaseFragment<ProductPresenter> implements ProductInfoContract.ProductInfoView, EpcImgListFragment.IEpcClick
{
    private EpcImgListFragment epcImgListFragment;// 图片滑动列表
    private EpcContentFragment epcContentFragment;

    @BindView(R.id.img_play_viewpager)
    APagingViewPager epcImgPlayViewpager;
    private ViewpagerFragmentAdapter mFragmentAdapter;
    private ArrayList<Fragment> epcImgBigFragments = new ArrayList<Fragment>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_epc_detail;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerProductComponent.builder().appComponent(appComponent).productModule(new ProductModule(this)).build().inject(this);
    }

    @Override
    public void init(View view)
    {
        epcImgListFragment = new EpcImgListFragment();
        epcImgListFragment.setIEpcClick(this);
        epcImgBigFragments = new ArrayList<>();
        epcContentFragment = new EpcContentFragment();
        epcImgPlayViewpager.setPagingEnable(true);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.list_frame, epcImgListFragment).add(R.id.together_frame, epcContentFragment);
        transaction.commitAllowingStateLoss();

/*        final String imgUrl = "http://192.168.31.67:8083/images/CC01.jpg";
        String playUrl = "http://192.168.31.67:8083/Images/2017/12/1d8d4271-21ba-4991-b692-2a0582dd1c16.mp4";


        if (mFragmentAdapter == null)
        {
            mFragmentAdapter = new ViewpagerFragmentAdapter(getChildFragmentManager());
            epcImgPlayViewpager.setAdapter(mFragmentAdapter);
        }
        final String finalPlayUrl = playUrl;
        mFragmentAdapter.setNewCount(2, new IViewpagerFragmentCallback()
        {
            @Override
            public Fragment createFragment(int pos)
            {
                Fragment epcFragment = null;
                Bundle bundle = new Bundle();
                switch (pos)
                {
                    case 0:
                        epcFragment = new PlayFragment();
                        bundle.putString("playUrl", finalPlayUrl);
                        break;
                    case 1:
                        epcFragment = new EpcImgFragment();
                        bundle.putString("imgUrl", imgUrl);
                        break;
                }
                epcFragment.setArguments(bundle);
                return epcFragment;
            }
        });*/

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
                //图片列表
                if (epcImgListFragment != null)
                {
                    epcImgListFragment.show(imgUrl);
                }

                //大图
                if (epcImgBigFragments.size() > 0)
                {
                    epcImgBigFragments.clear();
                }

                final ArrayList<String> pathUrl = new ArrayList<>();
                pathUrl.add(imgUrl);
                Attachment attachment = data.getAttachment();
                String playUrl = null;
                if (attachment != null)
                {
                    //playUrl = "http://192.168.31.67:8083/Images/2017/12/1d8d4271-21ba-4991-b692-2a0582dd1c16.mp4";
                    playUrl = attachment.getFileFullPath();
                    if (playUrl != null && playUrl.length() > 0)
                    {
                        pathUrl.add(0, playUrl);
                    }
                }


                if (mFragmentAdapter == null)
                {
                    mFragmentAdapter = new ViewpagerFragmentAdapter(getChildFragmentManager());
                    epcImgPlayViewpager.setAdapter(mFragmentAdapter);
                }
                mFragmentAdapter.setNewCount(pathUrl.size(), new IViewpagerFragmentCallback()
                {
                    @Override
                    public Fragment createFragment(int pos)
                    {
                        Fragment epcFragment = null;
                        Bundle bundle = new Bundle();
                        String url = pathUrl.get(pos);
                        if (url != null && url.length() > 0)
                        {
                            if (url.endsWith(".mp4"))
                            {
                                epcFragment = new PlayFragment();
                                bundle.putString("playUrl", url);
                            } else
                            {
                                epcFragment = new EpcImgFragment();
                                bundle.putString("imgUrl", imgUrl);
                            }
                            epcFragment.setArguments(bundle);
                        }
                        return epcFragment;
                    }
                });

                //详细内容信息
                if (epcContentFragment != null)
                {
                    epcContentFragment.show(data);
                }

            }
        }
    }

    //从读写器读到的epc值
    public void showEpcDetail(String epc)
    {
        mPresenter.requestDatas(epc);
    }

    @Override
    public void show(int position)
    {
        epcImgPlayViewpager.setCurrentItem(position);
    }

    //清空视频数据
    public void clearData()
    {
        if (mFragmentAdapter != null)
        {
            mFragmentAdapter.clear();
        }
    }

    @Override
    public void onDestroy()
    {
        if (mFragmentAdapter != null)
        {
            mFragmentAdapter.destory();
        }
        super.onDestroy();
    }
}
