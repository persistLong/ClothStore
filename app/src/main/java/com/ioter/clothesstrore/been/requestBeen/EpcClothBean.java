package com.ioter.clothesstrore.been.requestBeen;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.common.rx.RxHttpReponseCompat;
import com.ioter.clothesstrore.common.rx.subscriber.AdapterItemSubcriber;
import com.ioter.clothesstrore.common.util.ACache;
import com.ioter.clothesstrore.ui.adapter.ItemData;

/**
 * Created by Administrator on 2017/12/11.
 */

public class EpcClothBean extends ItemData
{
    public String getEpc()
    {
        return epc;
    }

    public void setEpc(String epc)
    {
        this.epc = epc;
    }

    private String epc;

    public ClothBean getCloth()
    {
        return cloth;
    }

    public void setCloth(ClothBean cloth)
    {
        this.cloth = cloth;
    }

    private ClothBean cloth = new ClothBean();

    @Override
    public String getUniqueKey()
    {
        return epc;
    }

    private Boolean isTake = false;

    @Override
    public void takeData()
    {
        if (isTake != null && !isTake)
        {
            isTake = null;
            AppApplication.getApplication().getAppComponent().getApiService().getProductByEpc(epc).compose(RxHttpReponseCompat.<ClothBean>compatResult())
                    .subscribe(new AdapterItemSubcriber<ClothBean>(AppApplication.getApplication())
                    {
                        @Override
                        public void onNext(ClothBean clothBean)
                        {
                            if (clothBean != null)
                            {
                                setCloth(clothBean);
                                ACache.get(AppApplication.getApplication()).put(epc, AppApplication.getGson().toJson(clothBean));
                                notifyChanged();
                                isTake = true;
                            } else
                            {
                                isTake = false;
                            }
                        }
                    });
        }
    }

}
