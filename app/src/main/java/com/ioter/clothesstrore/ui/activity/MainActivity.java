package com.ioter.clothesstrore.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ioter.clothesstrore.AppApplication;
import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.common.util.ACache;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.ui.BaseActivity;
import com.ioter.clothesstrore.ui.fragment.EPCFragment;
import com.ioter.clothesstrore.ui.fragment.MainFragment;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity
{
    public static final String MAIN = "main";
    public static final String EPCS = "epcs";
    public static final String PLAY = "play";
    private String mCurrentKey;

    @BindView(R.id.power_et)
    EditText power_et;
    @BindView(R.id.connect_bt)
    Button connect_bt;
    @BindView(R.id.connect_lly)
    LinearLayout connect_lly;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public int setLayout()
    {
        return R.layout.activity_main;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init()
    {
        connect_bt.setOnClickListener(this);
        MainFragment fragment1 = new MainFragment();
        EPCFragment fragment2 = new EPCFragment();
        //fragment2.setIEpcClick(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment1, MAIN).add(R.id.content_frame, fragment2, EPCS).hide(fragment2);
        transaction.commitAllowingStateLoss();
        showFragment(MAIN);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    public void showFragment(String key)
    {
        mCurrentKey = key;
        FragmentTransaction transaction = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();
        if (fragmentList != null)
        {
            for (Fragment fragment : fragmentList)
            {
                if (fragment != null)
                {
                    String tag = fragment.getTag();
                    if (key.equals(tag))
                    {
                        if (fragment.isHidden())
                        {
                            if (transaction == null)
                            {
                                transaction = fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            }
                            transaction.show(fragment);
                        }
                    } else
                    {
                        if (!fragment.isHidden())
                        {
                            if (transaction == null)
                            {
                                transaction = fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            }
                            transaction.hide(fragment);
                        }
                    }
                }
            }
        }
        if (transaction != null)
        {
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(mCurrentKey);
            if (fragment instanceof EPCFragment)
            {
                showFragment(MAIN);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy()
    {
        ACache.get(AppApplication.getApplication()).clear();
        super.onDestroy();
    }



    @Override
    public void onClick(View v)
    {

    }
}
