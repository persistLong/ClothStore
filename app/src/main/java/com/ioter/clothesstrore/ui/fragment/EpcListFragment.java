package com.ioter.clothesstrore.ui.fragment;

import android.graphics.Color;
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
import com.ioter.clothesstrore.presenter.contract.ProductInfoContract;
import com.ioter.clothesstrore.ui.adapter.EpcAdapter;
import com.ioter.clothesstrore.ui.adapter.ItemLayout;

import java.util.ArrayList;

import butterknife.BindView;

public class EpcListFragment extends BaseFragment implements ProductInfoContract.ProductInfoView
{

    @BindView(R.id.common_lv)
    ListView mEpcList;
    private MyAapter mAdapter;
    @BindView(R.id.head_tv)
    TextView mHeadTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.activity_epc_list;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {

    }

    @Override
    public void init(View view)
    {
        mHeadTv.setText("相同款式");
        mEpcList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                EpcClothBean data = mAdapter.getItem(position);
                if (mAdapter != null)
                {
                    mAdapter.setSelectPosition(position);
                }
            }
        });
        if (mAdapter == null)
        {
            mAdapter = new MyAapter(LayoutInflater.from(this.getActivity()));
            mEpcList.setAdapter(mAdapter);
        }
/*        ArrayList<String> list = new ArrayList<>();
        list.add("45414153304C30435730");
        list.add("454343534F4D4F43574F");
        list.add("454343534F4D4F434344");
        list.add("454343534F4D4F434B51");

        list.add("454444534F534F434842");
        list.add("454444534F4C4F434A48");
        list.add("45414153584C30434830");
        list.add("45414153584C30435A30");
        list.add("45414153584C30435030");
        setEpcList(list);*/

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

    public void setEpc(String epc)
    {
        if (mAdapter == null)
        {
            mAdapter = new MyAapter(LayoutInflater.from(this.getActivity()));
            mEpcList.setAdapter(mAdapter);
        }
        EpcClothBean data = mAdapter.getData(epc);//原本的数据就不去更新
        if (data == null)
        {
            data = new EpcClothBean();
            data.setEpc(epc);
        }
        mAdapter.updateData(data);
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

        private int defaultSelection = 0;

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
            TextView nameTv, styleTv, priceTv, sizeTv, colorTv;
            ImageView imgIv;
            View mainView;

            @Override
            public View initView(LayoutInflater inflater, ViewGroup viewGroup)
            {
                mainView = inflater.inflate(R.layout.listitem_epc, viewGroup, false);
                nameTv = (TextView) mainView.findViewById(R.id.epc_name);
                styleTv = (TextView) mainView.findViewById(R.id.epc_style);
                priceTv = (TextView) mainView.findViewById(R.id.epc_price);
                sizeTv = (TextView) mainView.findViewById(R.id.epc_size);
                colorTv = (TextView) mainView.findViewById(R.id.epc_color);
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
                    nameTv.setText(cloth.getName());
                    styleTv.setText("款式风格 " + cloth.getStyleNo());
                    priceTv.setText("价格 " + cloth.getPrice());
                    sizeTv.setText("尺寸 " + cloth.getSize());
                    colorTv.setText("颜色 " + cloth.getColor());

                    if (cloth.getImgFullUrl() != null && cloth.getImgFullUrl().length() > 0)
                    {
                        ImageLoader.load(item.getCloth().getImgFullUrl(), imgIv);
                    }
                }
                if (position == defaultSelection)
                {// 选中时设置单纯颜色
                    mainView.setBackgroundColor(Color.parseColor("#00ff00"));
                } else
                {// 未选中时设置selector
                    mainView.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }

            @Override
            public void onClick(View v, EpcClothBean data)
            {

            }
        }

        /**
         * @param position 设置高亮状态的item
         */
        public void setSelectPosition(int position)
        {
            if (!(position < 0 || position > mAdapter.getCount()))
            {
                defaultSelection = position;
                notifyDataSetChanged();
            }
        }
    }

}
