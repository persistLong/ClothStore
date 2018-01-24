package com.ioter.clothesstrore.wdiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.ioter.clothesstrore.R;

@SuppressLint("AppCompatCustomView")
public class FixHeiPagingViewPager extends ViewPager
{
    private double wh_ratio = 0.0;

    public FixHeiPagingViewPager(Context context)
    {
        super(context);
        wh_ratio = 2.0;
    }

    public FixHeiPagingViewPager(Context context, double mWh_ratio)
    {
        super(context);

        this.wh_ratio = mWh_ratio;
    }

    public FixHeiPagingViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FixHeiViewPager);
        wh_ratio = typedArray.getFloat(R.styleable.FixHeiViewPager_vpratio, (float) 1.0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                - getPaddingRight();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width / wh_ratio),
                MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
