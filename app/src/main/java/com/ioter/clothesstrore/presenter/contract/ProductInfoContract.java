package com.ioter.clothesstrore.presenter.contract;


import com.ioter.clothesstrore.been.requestBeen.ClothBean;
import com.ioter.clothesstrore.ui.BaseView;

public interface ProductInfoContract
{

    interface ProductInfoView extends BaseView
    {
        void showResult(String epc,ClothBean indexBean);
    }


}
