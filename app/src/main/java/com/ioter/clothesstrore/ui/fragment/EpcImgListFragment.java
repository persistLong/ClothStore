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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.common.imageloader.ImageLoader;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.wdiget.FixHeiImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class EpcImgListFragment extends BaseFragment
{
    @BindView(R.id.ic_common_lv)
    ListView mLv;
    @BindView(R.id.iv_arrow_up)
    ImageView mUpIv;
    @BindView(R.id.iv_arrow_down)
    ImageView mDownIv;

    private EpcImgListAdapter listAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public interface IEpcClick
    {
        void show(int position);
    }

    public void setIEpcClick(IEpcClick iEpcClick)
    {
        this.iEpcClick = iEpcClick;
    }

    private IEpcClick iEpcClick;

    @Override
    public int setLayout()
    {
        return R.layout.fragment_epc_img;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init(View view)
    {
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (listAdapter != null)
                {
                    listAdapter.setSelectPosition(position);
                }
                if (iEpcClick != null)
                {
                    iEpcClick.show(position);
                }
            }
        });
        listAdapter = new EpcImgListAdapter(mActivity);
        mLv.setAdapter(listAdapter);

    }

    public void show(String imgUrl)
    {
        ArrayList<String> views = new ArrayList<String>();
        views.add(imgUrl);
        if (listAdapter != null)
        {
            listAdapter.update(views);
        }
    }


    private int getHeight()
    {
        if (listAdapter == null && listAdapter.getCount() > 0)
        {
            return 0;
        }
        View v = listAdapter.getView(0, null, mLv);//最近添加的item
        v.measure(0, 0);
        return v.getMeasuredHeight();
    }

    private int item_Height;

    @OnClick({R.id.iv_arrow_down, R.id.iv_arrow_up})
    public void click(View view)
    {
        if (item_Height == 0)
        {
            item_Height = getHeight();
        }
        if (view.getId() == R.id.iv_arrow_up)
        {
            mLv.smoothScrollBy(item_Height, 10);
        } else if (view.getId() == R.id.iv_arrow_down)
        {
            mLv.smoothScrollBy(-item_Height, 10);
        }
    }


    private class EpcImgListAdapter extends BaseAdapter
    {

        private Context mContext;
        private ArrayList<String> mDataList;
        private int defaultSelection = 0;

        public EpcImgListAdapter(Context context)
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_epc_img, parent, false);
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
                holder.img_lly.setBackgroundResource(R.drawable.bg_border);
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
            if (!(position < 0 || position > listAdapter.getCount()))
            {
                defaultSelection = position;
                notifyDataSetChanged();
            }
        }

    }


}
