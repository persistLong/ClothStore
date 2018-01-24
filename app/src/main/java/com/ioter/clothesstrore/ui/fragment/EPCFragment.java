package com.ioter.clothesstrore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.been.requestBeen.ClothBean;
import com.ioter.clothesstrore.been.requestBeen.EpcClothBean;
import com.ioter.clothesstrore.common.imageloader.ImageLoader;
import com.ioter.clothesstrore.common.util.ACache;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.di.component.DaggerProductComponent;
import com.ioter.clothesstrore.di.module.ProductModule;
import com.ioter.clothesstrore.presenter.ProductPresenter;
import com.ioter.clothesstrore.presenter.contract.ProductInfoContract;
import com.ioter.clothesstrore.ui.adapter.EpcAdapter;
import com.ioter.clothesstrore.ui.adapter.ItemLayout;

import java.util.ArrayList;

import butterknife.BindView;

public class EPCFragment extends BaseFragment<ProductPresenter> implements ProductInfoContract.ProductInfoView
{

    @BindView(R.id.common_lv)
    ListView mEpcList;
    private MyAapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_product_detail;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
        DaggerProductComponent.builder().appComponent(appComponent).productModule(new ProductModule(this)).build().inject(this);
    }

    public interface IEpcClick
    {
        void write(String oldEpc, String newEpc);
    }

    public void setIEpcClick(IEpcClick iEpcClick)
    {
        this.iEpcClick = iEpcClick;
    }

    private IEpcClick iEpcClick;

    @Override
    public void init(View view)
    {
        mEpcList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                EpcClothBean data = mAdapter.getItem(position);
                if (iEpcClick != null)
                {
                    iEpcClick.write(data.getEpc(), "11111111112222222222333" + position);
                }
            }
        });
    }

    public ProductPresenter getPresenter()
    {
        return mPresenter;
    }


    public void setEpcList(ArrayList<String> epcList)
    {
        if (mAdapter == null)
        {
            mAdapter = new MyAapter(LayoutInflater.from(this.getActivity()));
            mEpcList.setAdapter(mAdapter);
        }
        ArrayList<EpcClothBean> list = new ArrayList<>();

        for (String item : epcList)
        {
            EpcClothBean data = mAdapter.getData(item);//原本的数据就不去更新
            if (data == null)
            {
                data = new EpcClothBean();
                data.setEpc(item);
            }
            list.add(data);
        }
        mAdapter.reFresh(list);
    }

    public void clearData()
    {
        if (mAdapter != null)
        {
            mAdapter.clearData();
        }
    }

    @Override
    public void showResult(String epc, ClothBean indexBean)
    {
        EpcClothBean data = mAdapter.getData(epc);
        if (data != null)
        {
            data.setCloth(indexBean);
            mAdapter.notifyDataSetChanged();
        }

    }

    private class MyAapter extends EpcAdapter<EpcClothBean>
    {

        public MyAapter(LayoutInflater layoutInflater)
        {
            super(layoutInflater);
        }

        @Override
        public void sortDataList(ArrayList<EpcClothBean> dataList)
        {

        }


        @Override
        public ItemLayout<EpcClothBean> getItemLayout(EpcClothBean data)
        {
            return new ViewHolder();
        }

        private class ViewHolder extends ItemLayout<EpcClothBean>
        {
            TextView nameTv, styleTv, priceTv;
            ImageView imgIv;

            @Override
            public View initView(LayoutInflater inflater, ViewGroup viewGroup)
            {
                View mainView = inflater.inflate(R.layout.listitem_product, viewGroup, false);
                nameTv = (TextView) mainView.findViewById(R.id.epc_name);
                styleTv = (TextView) mainView.findViewById(R.id.epc_style);
                priceTv = (TextView) mainView.findViewById(R.id.epc_price);
                imgIv = (ImageView) mainView.findViewById(R.id.epc_iv);
                return mainView;
            }

            @Override
            public void setDefaultView(int position, EpcClothBean data)
            {

            }

            @Override
            public void setView(int position, EpcClothBean data)
            {
                EpcClothBean item = (EpcClothBean) getItem(position);
                ClothBean cloth = item.getCloth();
                String result = ACache.get(AppApplication.getApplication()).getAsString(item.getEpc());
                if (result != null)
                {
                    item.setCloth(AppApplication.getGson().fromJson(result, ClothBean.class));
                }
                if (cloth != null)
                {
                    nameTv.setText(item.getEpc());
                    styleTv.setText(cloth.getStyleNo());
                    priceTv.setText("￥" + cloth.getPrice());
                    if (cloth.getImgUrl() != null && cloth.getImgUrl().length() > 0)
                    {
                        ImageLoader.load(item.getCloth().getImgUrl(), imgIv);
                    }
                }
            }

            @Override
            public void onClick(View v, EpcClothBean data)
            {
            }
        }
    }

}
