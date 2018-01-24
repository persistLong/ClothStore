package com.ioter.clothesstrore.di.module;

import com.ioter.clothesstrore.data.ProductInfoModel;
import com.ioter.clothesstrore.data.http.ApiService;
import com.ioter.clothesstrore.presenter.contract.ProductInfoContract;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductModule
{
    private ProductInfoContract.ProductInfoView mView;


    public ProductModule(ProductInfoContract.ProductInfoView view){

        this.mView = view;
    }

    @Provides
    public ProductInfoContract.ProductInfoView provideView(){

        return  mView;
    }

    @Provides
    public ProductInfoModel privodeModel(ApiService apiService)
    {
        return new ProductInfoModel(apiService);
    }
}
