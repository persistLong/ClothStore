package com.ioter.clothesstrore.wdiget;


import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class ViewpagerFragmentAdapter extends FragmentStatePagerAdapter
{
    /**
     * 存储临时的fragment
     */
    private SparseArray<Fragment> mSpFragments;

    public SparseArray<Fragment> getSpFragments()
    {
        return mSpFragments;
    }

    /**
     * fragment管理者
     */
    private FragmentManager mFragmentManager;
    /**
     * 需要使用到activity里方法的回调
     */
    private IViewpagerFragmentCallback mVpCallback;
    /**
     * 总项数
     */
    private int mItemCount = 0;

    public ViewpagerFragmentAdapter(FragmentManager fm)
    {
        super(fm);
        this.mSpFragments = new SparseArray<Fragment>();
        this.mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int arg0)
    {
        Fragment mFragment = null;
        if (mVpCallback != null)
        {
            mFragment = mVpCallback.createFragment(arg0);
            mSpFragments.put(arg0, mFragment);
        }
        return mFragment;
    }

    /**
     * 设置getitem的回调
     *
     * @param callback
     */
    public void setGitemCallBack(IViewpagerFragmentCallback callback)
    {
        this.mVpCallback = callback;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void destroyItem(ViewGroup viewgroup, int i, Object obj)
    {
        if (obj == null || !mFragmentManager.getFragments().contains((Fragment) obj))
        {
            return;
        }
        if (mSpFragments != null && mSpFragments.size() > 0 && mSpFragments.get(i) != null)
        {
            mSpFragments.remove(i);

        }
        super.destroyItem(viewgroup, i, obj);
    }

    @Override
    public int getItemPosition(Object arg0)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        return mItemCount;
    }

    /**
     * 设置数量
     *
     * @param itemCount
     */
    public void setNewCount(int itemCount, IViewpagerFragmentCallback callback)
    {
        this.mSpFragments.clear();
        this.mVpCallback = callback;
        this.mItemCount = itemCount;
        notifyDataSetChanged();
    }

    /**
     * 销毁fragment
     */
    public void destory()
    {
        if (mSpFragments != null && mSpFragments.size() > 0)
        {
            mSpFragments.clear();
        }
        if (mVpCallback != null)
        {
            mVpCallback = null;
        }

    }

    public void clear()
    {
        if (mSpFragments != null && mSpFragments.size() > 0)
        {
            mSpFragments.clear();
        }
        if (mVpCallback != null)
        {
            mVpCallback = null;
        }
        notifyDataSetChanged();
    }
}
