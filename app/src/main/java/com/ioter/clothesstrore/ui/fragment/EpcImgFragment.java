package com.ioter.clothesstrore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.di.component.AppComponent;

import butterknife.BindView;
import butterknife.OnClick;

public class EpcImgFragment extends BaseFragment
{
    @BindView(R.id.epc_iv)
    ImageView mImg;
    @BindView(R.id.tid_name_tv)
    TextView mName;
    @BindView(R.id.tid_pirce_tv)
    TextView mPrice;

    private int mPosition;

    public interface PageOnItemClick
    {
        public void itemClick(int position);
    }

    public EpcImgFragment setItemClick(PageOnItemClick mItemClick)
    {
        this.mItemClick = mItemClick;
        return this;
    }

    private PageOnItemClick mItemClick;


    public static EpcImgFragment newInstance(String imgUrl, int position)
    {
        EpcImgFragment epcImgFragment = new EpcImgFragment();
        Bundle bundle = new Bundle();
        bundle.putString("imgUrl", imgUrl);
        bundle.putInt("position", position);
        epcImgFragment.setArguments(bundle);
        return epcImgFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.listitem_epc_img_detail;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init(View view)
    {
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            String imgUrl = arguments.getString("imgUrl");
            if (!TextUtils.isEmpty(imgUrl))
            {
                //ImageLoader.load(imgUrl, mImg);
                int num = (int) (Math.random() * 4);
                switch (num)
                {
                    case 0:
                        mImg.setImageResource(R.mipmap.ic_map_1);
                        break;
                    case 1:
                        mImg.setImageResource(R.mipmap.ic_map_2);
                        break;
                    case 2:
                        mImg.setImageResource(R.mipmap.ic_map_3);
                        break;
                    case 3:
                        mImg.setImageResource(R.mipmap.ic_map_4);
                        break;
                }
            }
            mPosition = arguments.getInt("position");
        }
    }


    @OnClick(R.id.epc_iv)
    public void onclickImg()
    {
        if (mItemClick != null)
        {
            mItemClick.itemClick(mPosition);
        }
    }
}
