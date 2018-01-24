package com.ioter.clothesstrore.wdiget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 重写了viewpager，主要增加了viewpager是否可以左右滑动的开关
 *
 *
 */
public class APagingViewPager extends ViewPager
{
	 private boolean enabled;

	public APagingViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.enabled = false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0)
	{
		if(enabled){
			return super.onInterceptTouchEvent(arg0);
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0)
	{
		if(enabled){
			return super.onTouchEvent(arg0);
		}
		return false;
	}

	public void setPagingEnable(boolean enable)
	{
		this.enabled = enable;
	}

}
