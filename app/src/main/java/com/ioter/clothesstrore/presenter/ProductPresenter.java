package com.ioter.clothesstrore.presenter;

import com.ioter.clothesstrore.been.requestBeen.ClothBean;
import com.ioter.clothesstrore.common.rx.RxHttpReponseCompat;
import com.ioter.clothesstrore.common.rx.subscriber.ProgressSubcriber;
import com.ioter.clothesstrore.data.ProductInfoModel;
import com.ioter.clothesstrore.presenter.contract.ProductInfoContract;

import javax.inject.Inject;

public class ProductPresenter extends BasePresenter<ProductInfoModel, ProductInfoContract.ProductInfoView>
{
    @Inject
    public ProductPresenter(ProductInfoModel model, ProductInfoContract.ProductInfoView view)
    {
        super(model, view);

    }


    public void requestDatas(final String epc)
    {
        mModel.getProductByEpc(epc).compose(RxHttpReponseCompat.<ClothBean>compatResult())
                .subscribe(new ProgressSubcriber<ClothBean>(mContext, mView)
                {
                    @Override
                    public void onNext(ClothBean clothBean)
                    {
                        mView.showResult(epc, clothBean);
                    }
                });
    }

    public void requestDatas(final String epc, final IEpcData iEpcData)
    {
        mModel.getProductByEpc(epc).compose(RxHttpReponseCompat.<ClothBean>compatResult())
                .subscribe(new ProgressSubcriber<ClothBean>(mContext, mView)
                {
                    @Override
                    public void onNext(ClothBean clothBean)
                    {
                        //mView.showResult(epc, clothBean);
                        iEpcData.showResult(epc, clothBean);
                    }
                });
    }

    public interface IEpcData
    {
        void showResult(String epc, ClothBean clothBean);
    }
}
