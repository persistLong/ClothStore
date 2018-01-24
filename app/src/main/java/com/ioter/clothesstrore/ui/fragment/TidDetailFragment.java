package com.ioter.clothesstrore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

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

public class TidDetailFragment extends BaseFragment<ProductPresenter> implements ProductInfoContract.ProductInfoView, EpcImgListFragment.IEpcClick
{

    @BindView(R.id.tid_pirce_tv)
    TextView price_tv;
    @BindView(R.id.tid_name_tv)
    TextView name_tv;
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
        return R.layout.activity_tid_detail;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerProductComponent.builder().appComponent(appComponent).productModule(new ProductModule(this)).build().inject(this);
    }

    @Override
    public void init(View view)
    {
        epcImgPlayViewpager.setPagingEnable(true);
    }


    //后台指令回调
    @Override
    public void showResult(String epc, ClothBean data)
    {
        if (data != null)
        {
            name_tv.setText(data.getName());
            price_tv.setText("¥ " + data.getPrice());

            final String imgUrl = data.getImgFullUrl();
            if (imgUrl != null && imgUrl.length() > 0)
            {
                //大图
                if (epcImgBigFragments.size() > 0)
                {
                    epcImgBigFragments.clear();
                }

                final ArrayList<String> pathUrl = new ArrayList<>();
                pathUrl.add(imgUrl);
                pathUrl.add(imgUrl);
                Attachment attachment = data.getAttachment();
                String playUrl = null;
                if (attachment != null)
                {
                    //playUrl = "http://192.168.31.67:8083/Images/2017/12/1d8d4271-21ba-4991-b692-2a0582dd1c16.mp4";
                    playUrl = attachment.getFileFullPath();
                    if (playUrl != null && playUrl.length() > 0)
                    {
                        pathUrl.add(playUrl);
                        Fragment epcFragment = new PlayFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("playUrl", playUrl);
                        epcFragment.setArguments(bundle);
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        transaction.add(R.id.play_frame, epcFragment);
                        transaction.commitAllowingStateLoss();
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
