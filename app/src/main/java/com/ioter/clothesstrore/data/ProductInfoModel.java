package com.ioter.clothesstrore.data;


import com.ioter.clothesstrore.been.requestBeen.BaseBean;
import com.ioter.clothesstrore.been.requestBeen.ClothBean;
import com.ioter.clothesstrore.data.http.ApiService;

import rx.Observable;


public class ProductInfoModel
{

    private ApiService mApiService;

    public ProductInfoModel(ApiService apiService){

        this.mApiService  =apiService;
    }


    public Observable<BaseBean<ClothBean>> getProductByEpc(String epc){

        return  mApiService.getProductByEpc(epc);
    }


}
