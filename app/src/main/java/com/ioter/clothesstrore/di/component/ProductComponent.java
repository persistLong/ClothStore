package com.ioter.clothesstrore.di.component;


import com.ioter.clothesstrore.di.ActivityScope;
import com.ioter.clothesstrore.di.module.ProductModule;
import com.ioter.clothesstrore.ui.fragment.EPCFragment;
import com.ioter.clothesstrore.ui.fragment.EpcDetailFragment;
import com.ioter.clothesstrore.ui.fragment.TidDetailFragment;
import com.ioter.clothesstrore.ui.fragment.TidDetailFragment2;
import com.ioter.clothesstrore.ui.fragment.TidDetailFragment3;

import dagger.Component;


@ActivityScope
@Component(modules = ProductModule.class ,dependencies = AppComponent.class)
public interface ProductComponent
{
    void inject(EPCFragment fragment);
    void inject(EpcDetailFragment fragment);
    void inject(TidDetailFragment fragment);
    void inject(TidDetailFragment2 fragment);
    void inject(TidDetailFragment3 fragment);
}
