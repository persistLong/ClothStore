package com.ioter.clothesstrore.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.been.requestBeen.ClothBean;
import com.ioter.clothesstrore.common.imageloader.ImageLoader;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.wdiget.FixHeiImageView;
import com.ioter.clothesstrore.wdiget.FixHeiTextView;

import java.util.ArrayList;

import butterknife.BindView;

public class EpcContentFragment extends BaseFragment
{

    @BindView(R.id.epc_name)
    TextView name_tv;
    @BindView(R.id.price_tv)
    TextView price_tv;
    @BindView(R.id.size_gridView)
    GridView sizeGridView;
    @BindView(R.id.color_gridView)
    GridView colorGridView;

    private SizeAdapter sizeAdapter;
    private ColorAdapter colorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.fragment_epc_content;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {

    }

    @Override
    public void init(View view)
    {
        sizeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

            }
        });
        sizeAdapter = new SizeAdapter(mActivity);
        sizeGridView.setAdapter(sizeAdapter);

        colorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

            }
        });
        colorAdapter = new ColorAdapter(mActivity);
        colorGridView.setAdapter(colorAdapter);

    }


    public void show(ClothBean clothBean)
    {
        price_tv.setText("¥"+clothBean.getPrice());//按住Alt键不松，连续在小键盘上输入0165四个数字松开Alt

        ArrayList<String> sizes = new ArrayList<>();
        sizes.add(clothBean.getSize());
        sizeAdapter.update(sizes);

        ArrayList<String> colors = new ArrayList<>();
        colors.add(clothBean.getImgFullUrl());
        colors.add(clothBean.getImgFullUrl());
        colors.add(clothBean.getImgFullUrl());
        colors.add(clothBean.getImgFullUrl());
        colors.add(clothBean.getImgFullUrl());
        colors.add(clothBean.getImgFullUrl());

        colorAdapter.update(colors);
    }

    private class SizeAdapter extends BaseAdapter
    {

        private Context mContext;
        private ArrayList<String> mDataList;

        public SizeAdapter(Context context)
        {
            mContext = context;
            mDataList = new ArrayList<String>();
        }

        public void update(ArrayList<String> dataList)
        {
            if (dataList.size() == 0)
            {
                return;
            }
            mDataList.clear();
            mDataList.addAll(dataList);
            dataList.clear();
            notifyDataSetChanged();
        }

        public void clear()
        {
            mDataList.clear();
            notifyDataSetChanged();
        }

        public ArrayList<String> getDataList()
        {
            return mDataList;
        }

        @Override
        public int getCount()
        {
            return mDataList.size();
        }

        @Override
        public String getItem(int position)
        {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_epc_size, parent, false);
                holder = new ViewHolder();
                holder.sizeTv = (FixHeiTextView) convertView.findViewById(R.id.size_tv);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            String item = getItem(position);
            if (!TextUtils.isEmpty(item))
            {
                holder.sizeTv.setText(item);
            }

            return convertView;
        }

        private class ViewHolder
        {
            FixHeiTextView sizeTv;
        }

    }

    private class ColorAdapter extends BaseAdapter
    {

        private Context mContext;
        private ArrayList<String> mDataList;
        private int defaultSelection = 0;

        public ColorAdapter(Context context)
        {
            mContext = context;
            mDataList = new ArrayList<String>();
        }

        public void update(ArrayList<String> dataList)
        {
            if (dataList.size() == 0)
            {
                return;
            }
            mDataList.clear();
            mDataList.addAll(dataList);
            dataList.clear();
            notifyDataSetChanged();
        }

        public void clear()
        {
            mDataList.clear();
            notifyDataSetChanged();
        }

        public ArrayList<String> getDataList()
        {
            return mDataList;
        }

        @Override
        public int getCount()
        {
            return mDataList.size();
        }

        @Override
        public String getItem(int position)
        {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_epc_color, parent, false);
                holder = new ViewHolder();
                holder.epcIv = (FixHeiImageView) convertView.findViewById(R.id.epc_iv);
                holder.img_lly = (LinearLayout) convertView.findViewById(R.id.epc_lly);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            String item = getItem(position);

            if (!TextUtils.isEmpty(item))
            {
                ImageLoader.load(item, holder.epcIv);
            }
            if (position == defaultSelection)
            {// 选中时设置单纯颜色
                holder.img_lly.setBackgroundResource(R.drawable.bg_border_red);
            } else
            {// 未选中时设置selector
                holder.img_lly.setBackground(null);
            }

            return convertView;
        }

        private class ViewHolder
        {
            FixHeiImageView epcIv;
            LinearLayout img_lly;
        }

        /**
         * @param position 设置高亮状态的item
         */
        public void setSelectPosition(int position)
        {
            if (!(position < 0 || position > colorAdapter.getCount()))
            {
                defaultSelection = position;
                notifyDataSetChanged();
            }
        }

    }


}
