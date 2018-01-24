package com.ioter.clothesstrore.wdiget;

import android.support.v4.app.Fragment;

public interface IViewpagerFragmentCallback
{
    /**
     * @param pos 当前正在创建第几个fragment
     * @return	创建完的fragment
     */
    Fragment createFragment(int pos);
}
