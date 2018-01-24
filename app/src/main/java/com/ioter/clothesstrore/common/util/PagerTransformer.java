package com.ioter.clothesstrore.common.util;

import android.support.v4.view.ViewPager;
import android.view.View;

/**整个Transformer对viewpager的页面进行了缩放和Y轴旋转
 * 代码非常简短，简单的介绍下，可以看到postion主要分为
 * [-Infinity,-1)
 * (1,+Infinity]
 * [-1,1]
 * 这三个区间，对于前两个，拿我们的页面上目前显示的3个Page来说，前两个分别对应左右两个露出一点的Page，那么对于alpha值，只需要设置为最小值即可。
 * 对于[-1,1]，这个就需要详细分析了，我们这里拿：第一页->第二页这个过程来说，主要看position的变化
 * 第1页->第2页
 * 页1的postion变化为：从0到-1
 * 页2的postion变化为：从1到0
 * 第一页到第二页，实际上就是左滑，第一页到左边，第二页成为currentItem到达中间，那么对应alpha的变化应该是：
 * 页1到左边，对应alpha应该是：1到minAlpha
 * 页2到中间，成为currentItem，对应alpha应该是：minAlpha到1
 * Created by yqr on 2016/11/9 0009.
 */

public class PagerTransformer implements ViewPager.PageTransformer {

    public static final float minScale = 0.9f;
    private static final float DEFAULT_MAX_ROTATE = 15f;
    private float mMaxRotate = DEFAULT_MAX_ROTATE;
    public static final float DEFAULT_CENTER = 0.4f;

    private static final float DEFAULT_MIN_ALPHA = 0.5f;
    private float mMinAlpha = DEFAULT_MIN_ALPHA;
    /**
     * 每个状态的view该显示多大是根据position的值来设置的。
     * 本例中值关心3张图片的大小，就只有3个状态。position小于-1 ， -1到1 大于1
     * 等于-1 就是屏幕上左边的图片 ， 等于1 是屏幕右边的图 等于0是中间的图。
     * @param page
     * @param position -- 第一个view的position初始为0 ，向左滑第一个view的position就慢慢减少成为负数。
     *                 第二个view的position初始为1，向左滑慢慢变成0.变成0时就是这个view在最中间的时候。
     *                 整个过程，每个view的position都是在变的、
     */
    @Override
    public void transformPage(View page, float position) {

        page.setPivotY(page.getHeight()/2);//旋转轴

        if (position < -1) {
            page.setAlpha(mMinAlpha);
            page.setScaleY(minScale);
            page.setScaleX(minScale);
            page.setRotationY(-1 * mMaxRotate);
            page.setPivotX(page.getWidth());

        } else if (position <= 1) { // [-1,1]
            page.setRotationY(position * mMaxRotate);
            if (position < 0) //[0，-1]
            {
                float factor = minScale + (1 - minScale) * (1 + position);
                page.setScaleY(factor);
                page.setScaleX(factor);


//                page.setPivotX(page.getWidth() * (DEFAULT_CENTER + DEFAULT_CENTER * (-position)));
                page.setPivotX(page.getWidth());

                float factor2 = mMinAlpha + (1 - mMinAlpha) * (1 + position);
                page.setAlpha(factor);

            } else//[1，0]
            {
                float factor = minScale + (1 - minScale) * (1 - position);
                page.setScaleY(factor);
                page.setScaleX(factor);

//                page.setPivotX(page.getWidth() * DEFAULT_CENTER * (1 - position));
                page.setPivotX(0);

                float factor2 = mMinAlpha + (1 - mMinAlpha) * (1 - position);
                page.setAlpha(factor2);
            }
        } else { // (1,+Infinity]
            page.setScaleY(minScale);
            page.setScaleX(minScale);


            page.setRotationY(1 * mMaxRotate);
            page.setPivotX(0);

            page.setAlpha(mMinAlpha);
        }
    }
}