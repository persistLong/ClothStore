package com.ioter.clothesstrore.wdiget.coverflow;

//import com.nineoldandroids.animation.Animator;
//import com.nineoldandroids.animation.ValueAnimator;
//import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
//import com.nineoldandroids.view.ViewHelper;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.ioter.clothesstrore.R;

import java.util.ArrayList;

public class CoverFlowView extends RelativeLayout {
    private AdapterDataSetObserver mDataSetObserver;

    public enum CoverFlowGravity {
        TOP, BOTTOM, CENTER_VERTICAL
    }

    public enum CoverFlowLayoutMode {
        MATCH_PARENT, WRAP_CONTENT
    }

    protected CoverFlowGravity mGravity;

    protected CoverFlowLayoutMode mLayoutMode;

    private Scroller mScroller;
    /**
     * To store reflections need to remove
     */
    private ArrayList<View> removeViewArray;

    private SparseArray<View> showViewArray;

    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;

    private int mWidth; // 控件的宽度

    private float reflectHeightFraction = 0;
    private int reflectGap = 0;

    private int mChildHeight; // child的高度
    private int mChildTranslateY;
//	private int mReflectionTranslateY;

    private int mVisibleChildCount; // 一屏显示的图片数量

    // the visible views left and right 左右两边显示的个数
    protected int VISIBLE_VIEWS = 1;

    private ACoverFlowAdapter mAdapter;

    private float mOffset;
//    private int mLastOffset;


    // 基础alphaֵ
    private int ALPHA_DATUM;
    private int STANDARD_ALPHA;
    // 基础缩放值
    private float CARD_SCALE;

    private static float MOVE_POS_MULTIPLE = 3.0f;
    private static final int TOUCH_MINIMUM_MOVE = 5;
    private static final float MOVE_SPEED_MULTIPLE = 1;
    private static final float MAX_SPEED = 6.0f;
    private static final float FRICTION = 10.0f;

    private VelocityTracker mVelocity;

    public CoverFlowView(Context context) {
        super(context);
        init();
    }

    public CoverFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
        init();
    }

    public CoverFlowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
        init();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoverFlowView);

        int totalVisibleChildren = a.getInt(
                R.styleable.CoverFlowView_visibleViews, 3);
        if (totalVisibleChildren % 2 == 0) { // 一屏幕必须是奇数显示
            throw new IllegalArgumentException(
                    "visible views must be an odd number");
        }
        if(totalVisibleChildren < 3) {
            throw new IllegalArgumentException(
                    "visible views must be a number greater than 3");
        }

        VISIBLE_VIEWS = totalVisibleChildren >> 1; // 计算出左右两两边的显示个数
        mVisibleChildCount = totalVisibleChildren;

//        reflectHeightFraction = a.getFraction(
//                R.styleable.CoverFlowView_reflectionHeight, 100, 0, 0.0f);
//
//        if (reflectHeightFraction > 100) {
//            reflectHeightFraction = 100;
//        }
//        reflectHeightFraction /= 100;
//        reflectGap = a.getDimensionPixelSize(
//                R.styleable.CoverFlowView_reflectionGap, 0);

        mGravity = CoverFlowGravity.values()[a.getInt(
                R.styleable.CoverFlowView_coverflowGravity,
                CoverFlowGravity.CENTER_VERTICAL.ordinal())];

        mLayoutMode = CoverFlowLayoutMode.values()[a.getInt(
                R.styleable.CoverFlowView_coverflowLayoutMode,
                CoverFlowLayoutMode.WRAP_CONTENT.ordinal())];
        ALPHA_DATUM = a.getInt(R.styleable.CoverFlowView_coverflowAlpha, 76);
        CARD_SCALE = a.getFloat(R.styleable.CoverFlowView_coverflowScale, 0.25f);

        a.recycle();
    }

    private void init() {
        setWillNotDraw(false);
        setClickable(true);

        if (mScroller == null) {
            mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
        }
        if (showViewArray == null) {
            showViewArray = new SparseArray<View>();
        }
        if (removeViewArray == null) {
            removeViewArray = new ArrayList<View>();
        }

        // 计算透明度
        STANDARD_ALPHA = (255 - ALPHA_DATUM) / VISIBLE_VIEWS;

        if (mGravity == null) {
            mGravity = CoverFlowGravity.CENTER_VERTICAL;
        }
        if (mLayoutMode == null) {
            mLayoutMode = CoverFlowLayoutMode.WRAP_CONTENT;
        }

        initChildren(VISIBLE_VIEWS);
    }

    /**
     * 初始化添加所有的children。
     * @param midIndex 指定最中间的view的index。
     */
    private void initChildren(int midIndex) {
        removeAllViews();

        showViewArray.clear();
        removeViewArray.clear();

        mChildHeight = 0;
        mOffset = midIndex - VISIBLE_VIEWS;
//        mLastOffset = -1;

        isFirstin = true;
        lastMid = 0;
        lastViewOnTopIndex = -1;

        for (int i = 0, j = (midIndex - VISIBLE_VIEWS); i < mVisibleChildCount && mAdapter != null && i < mAdapter.getCount(); ++i, ++j) {
            View convertView = null;
            if (removeViewArray.size() > 0) {
                convertView = removeViewArray.remove(0);
            }
            int count = mAdapter.getCount();
            int index = j < 0 ? count + j : (j >= count ? j - count : j);
            View view = mAdapter.getView(index, convertView, this);
            showViewArray.put(index, view);

            //按Z轴顺序添加view，保持层叠效果
            if (i < VISIBLE_VIEWS) {
                addView(view);
            } else {
                addView(view, VISIBLE_VIEWS);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d("CoverFlowView", "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mAdapter == null || showViewArray.size() <= 0) {
            return;
        }

        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 控件高度
        int avaiblableHeight = heightSize - paddingTop - paddingBottom;

        int maxChildTotalHeight = 0;
        for (int i = 0; i < getChildCount() && i < mVisibleChildCount && i < showViewArray.size(); ++i) {

//			View view = showViewArray.get(i+firstIndex);
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);

//			final int childHeight = ScreenUtil.dp2px(getContext(), 110);
            final int childHeight = view.getMeasuredHeight();
            final int childTotalHeight = (int) (childHeight + childHeight
                    * reflectHeightFraction + reflectGap);

            // 孩子的最大高度
            maxChildTotalHeight = (maxChildTotalHeight < childTotalHeight) ? childTotalHeight
                    : maxChildTotalHeight;
        }

        // 如果控件模式为确切值 或者 最大是
        if (heightMode == MeasureSpec.EXACTLY
                || heightMode == MeasureSpec.AT_MOST) {
            // if height which parent provided is less than child need, scale
            // child height to parent provide
            // 如果控件高度小于孩子控件高度 则缩放孩子高度为控件高度
            if (avaiblableHeight < maxChildTotalHeight) {
                mChildHeight = avaiblableHeight;
            } else {
                // if larger than, depends on layout mode
                // if layout mode is match_parent, scale child height to parent
                // provide
                // 如果是填充父窗体模式 则将孩子的高度 设为控件高度
                if (mLayoutMode == CoverFlowLayoutMode.MATCH_PARENT) {
                    mChildHeight = avaiblableHeight;
                    // if layout mode is wrap_content, keep child's original
                    // height
                    // 如果是包裹内容 则将孩子的高度设为孩子允许的最大高度
                } else if (mLayoutMode == CoverFlowLayoutMode.WRAP_CONTENT) {
                    mChildHeight = maxChildTotalHeight;

                    // adjust parent's height
                    // 计算出控件的高度
                    if (heightMode == MeasureSpec.AT_MOST) {
                        heightSize = mChildHeight + paddingTop + paddingBottom;
                    }
                }
            }
        } else {
            // height mode is unspecified
            // 如果空间高度 没有明确定义
            // 如果孩子的模式为填充父窗体
            if (mLayoutMode == CoverFlowLayoutMode.MATCH_PARENT) {
                mChildHeight = avaiblableHeight;
                // 如果孩子的模式为包裹内容
            } else if (mLayoutMode == CoverFlowLayoutMode.WRAP_CONTENT) {
                mChildHeight = maxChildTotalHeight;

                // 计算出控件的高度
                // adjust parent's height
                heightSize = mChildHeight + paddingTop + paddingBottom;
            }
        }

        // Adjust movement in y-axis according to gravity
        // 计算出孩子的原点 Y坐标
        if (mGravity == CoverFlowGravity.CENTER_VERTICAL) {// 竖直居中
            mChildTranslateY = (heightSize >> 1) - (mChildHeight >> 1);
        } else if (mGravity == CoverFlowGravity.TOP) {// 顶部对齐
            mChildTranslateY = paddingTop;
        } else if (mGravity == CoverFlowGravity.BOTTOM) {// 底部对齐
            mChildTranslateY = heightSize - paddingBottom - mChildHeight;
        }

//		mReflectionTranslateY = (int) (mChildTranslateY + mChildHeight - mChildHeight
//				* reflectHeightFraction);

        setMeasuredDimension(widthSize, heightSize);
        mWidth = widthSize;
    }

    boolean isFirstin = true; //第一次初始化该控件
    int lastMid = 0; //最近的中间view的offset值
    int lastViewOnTopIndex = -1; //最近处理过的viewOnTop监听的index

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.d("CoverFlowView", "onLayout");
        if (mAdapter == null || mAdapter.getCount() <= 0 || showViewArray.size() <= 0) {
            return;
        }

        final float offset = mOffset;
        int mid = (int) Math.floor(offset + 0.5);

        //右边孩子的数量
        int rightChild = (mVisibleChildCount % 2 == 0) ? (mVisibleChildCount >> 1) - 1
                : mVisibleChildCount >> 1;
        //左边孩子的数量
        int leftChild = mVisibleChildCount >> 1;

        if (!isFirstin) {
            if (lastMid + 1 == mid) { //右滑至item出现了
                int actuallyPositionStart = getActuallyPosition(lastMid - leftChild);
                View view = showViewArray.get(actuallyPositionStart);
                showViewArray.remove(actuallyPositionStart);
                removeViewArray.add(view);
                removeView(view);

                View convertView = null;
                if (removeViewArray.size() > 0) {
                    convertView = removeViewArray.remove(0);
                }
                int actuallyPositionEnd = getActuallyPosition(mid + rightChild);

                View viewItem = mAdapter.getView(actuallyPositionEnd, convertView, this);

                showViewArray.put(actuallyPositionEnd, viewItem);
                addView(viewItem);

                int actuallyPositionMid = getActuallyPosition(mid);
                View midView = showViewArray.get(actuallyPositionMid);
                midView.bringToFront();
            } else if (lastMid - 1 == mid) { //左滑至item出现了
                int actuallyPositionEnd = getActuallyPosition(lastMid + rightChild);
                View view = showViewArray.get(actuallyPositionEnd);
                showViewArray.remove(actuallyPositionEnd);
                removeViewArray.add(view);
                removeView(view);

                View convertView = null;
                if (removeViewArray.size() > 0) {
                    convertView = removeViewArray.remove(0);
                }
                int actuallyPositionstart = getActuallyPosition(mid - leftChild);

                View viewItem = mAdapter.getView(actuallyPositionstart, convertView, this);

                showViewArray.put(actuallyPositionstart, viewItem);
                addView(viewItem, 0);

                int actuallyPositionMid = getActuallyPosition(mid);
                View midView = showViewArray.get(actuallyPositionMid);
                midView.bringToFront();
            }
        } else { //第一次进入
            isFirstin = false;
        }

        lastMid = mid;

        int i;
        // draw the left children
        // 计算左边孩子的位置
        int startPos = mid - leftChild;
        for (i = startPos; i < mid; ++i) {
            layoutLeftChild(i, i - offset);
        }

        // 计算 右边 和 中间
        int endPos = mid + rightChild;
        for (i = endPos; i >= mid; i--) {
            layoutRightChild(i, i - offset);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d("CoverFlowView", "onDraw");
        final float offset = mOffset;

        if ((offset - (int) offset) == 0.0f) {
            int top = getActuallyPosition((int) offset);
            if(top != lastViewOnTopIndex) {
                lastViewOnTopIndex = top;
                if (mViewOnTopListener != null)
                    mViewOnTopListener.viewOnTop(top, getTopView());
            }
        }
        super.onDraw(canvas);
    }

    private View layoutLeftChild(int position, float offset) {
        //获取实际的position
        int actuallyPosition = getActuallyPosition(position);
        View child = showViewArray.get(actuallyPosition);
        if (child != null) {
            makeChildTransformer(child, actuallyPosition, offset);
        }
        return child;
    }

    private View layoutRightChild(int position, float offset) {
        //获取实际的position
        int actuallyPosition = getActuallyPosition(position);
        View child = showViewArray.get(actuallyPosition);
        if (child != null) {
            makeChildTransformer(child, actuallyPosition, offset);
        }

        return child;
    }

    /**
     * <ul>
     * <li>对bitmap进行伪3d变换</li>
     * </ul>
     */
    private void makeChildTransformer(View child, int position, float offset) {
//    	child.layout(0, 0, ScreenUtil.dp2px(getContext(), 200),ScreenUtil.dp2px(getContext(), 110));
        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());

        float scale;
        scale = 1 - Math.abs(offset) * CARD_SCALE;


        // 延x轴移动的距离应该根据center图片决定
        float translateX;

        final int originalChildHeight = (int) (mChildHeight - mChildHeight
                * reflectHeightFraction - reflectGap);

//        final int childTotalHeight = (int) (child.getHeight()
//                + child.getHeight() * reflectHeightFraction + reflectGap);

        final float originalChildHeightScale = (float) originalChildHeight
                / child.getHeight();

        final float childHeightScale = originalChildHeightScale * scale;

        final int childWidth = (int) (child.getWidth() * childHeightScale);

        final int centerChildWidth = (int) (child.getWidth() * originalChildHeightScale);

//        final int centerChildWidth = (int) (child.getWidth() * childHeightScale);

        int leftSpace = ((mWidth >> 1) - paddingLeft) - (centerChildWidth >> 1);
        int rightSpace = (((mWidth >> 1) - paddingRight) - (centerChildWidth >> 1));

        //计算出水平方向的x坐标
        if (offset <= 0)
            translateX = ((float) leftSpace / VISIBLE_VIEWS)
                    * (VISIBLE_VIEWS + offset) + paddingLeft;
        else
            translateX = mWidth - ((float) rightSpace / VISIBLE_VIEWS)
                    * (VISIBLE_VIEWS - offset) - childWidth
                    - paddingRight;

        //根据offset 算出透明度
        float alpha = 254 - Math.abs(offset) * STANDARD_ALPHA;
        ViewHelper.setAlpha(child, 0);
//        child.setAlpha(0);
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 254) {
            alpha = 254;
        }
        ViewHelper.setAlpha(child, alpha / 254.0f);
//        child.setAlpha(alpha/254.0f);

        float adjustedChildTranslateY = 0;

//        //兼容api 10
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//            android.animation.ObjectAnimator anim1 = android.animation.ObjectAnimator.ofFloat(child, "scaleX",
//                    1.0f, childHeightScale);
//            android.animation.ObjectAnimator anim2 = android.animation.ObjectAnimator.ofFloat(child, "scaleY",
//                    1.0f, childHeightScale);
//            android.animation.AnimatorSet animSet = new android.animation.AnimatorSet();
//            animSet.setDuration(0);
//            //两个动画同时执行
//            animSet.playTogether(anim1, anim2);
//
//            //显示的调用invalidate
////            child.invalidate();
//            animSet.setTarget(child);
//            animSet.start();
//        } else {
//            ObjectAnimator anim1 = ObjectAnimator.ofFloat(child, "scaleX",
//                    1.0f, childHeightScale);
//            ObjectAnimator anim2 = ObjectAnimator.ofFloat(child, "scaleY",
//                    1.0f, childHeightScale);
//            AnimatorSet animSet = new AnimatorSet();
//            animSet.setDuration(0);
//            //两个动画同时执行
//            animSet.playTogether(anim1, anim2);
//
//            //显示的调用invalidate
////            child.invalidate();
//            animSet.setTarget(child);
//            animSet.start();
//        }
        float sc = childHeightScale;
        ViewHelper.setScaleX(child, sc);
        ViewHelper.setScaleY(child, sc);

        ViewHelper.setPivotX(child, 0);
        ViewHelper.setPivotY(child, child.getHeight() / 2);
//        child.setPivotX(0);
//        child.setPivotY(child.getHeight()/2);

        ViewHelper.setTranslationX(child, translateX);
        ViewHelper.setTranslationY(child, mChildTranslateY + adjustedChildTranslateY);
//        child.setTranslationX(translateX);
//        child.setTranslationY(mChildTranslateY+ adjustedChildTranslateY);
    }

    /**
     * 获取顶部Item position
     *
     * @return int
     */
    public int getTopViewPosition() {
        return getActuallyPosition(lastMid);
    }

    /**
     * 获取顶部Item View
     *
     * @return top view
     */
    public View getTopView() {
        return showViewArray.get(getTopViewPosition());
    }

    /**
     * Convert draw-index to index in adapter
     *
     * @param position position to draw
     * @return actual position
     */
    private int getActuallyPosition(int position) {
        if(mAdapter == null)
            return position;
        int max = mAdapter.getCount();

        position += VISIBLE_VIEWS;
        while (position < 0 || position >= max) {
            if (position < 0) {
                position += max;
            } else if (position >= max) {
                position -= max;
            }
        }

        return position;
    }

    public void setAdapter(ACoverFlowAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        if(adapter.getCount() < mVisibleChildCount) {
            throw new IllegalArgumentException("adapter's count must be greater than visible views number");
        }
        mAdapter = adapter;
        initChildren(VISIBLE_VIEWS);

        if (mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
        }
        mAdapter.registerDataSetObserver(mDataSetObserver);

        requestLayout();
    }

    public ACoverFlowAdapter getAdapter() {
        return mAdapter;
    }

    private boolean onTouchMove = false; //是否正在执行触摸移动逻辑

    private View touchViewItem = null;
    private boolean isOnTopView = false;
    private float downY;
    private float downX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        if (isOnAnimator) {
            return false;
        }
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onTouchMove = true;
                if (mScroller.computeScrollOffset()) {
                    mScroller.abortAnimation();
                    invalidate();
                    requestLayout();
                }
                touchBegan(event);
                touchViewItem = getTopView();
                isOnTopView = inRangeOfView(touchViewItem, event);
                downX = event.getX();
                downY = event.getY();
                if (isOnTopView) {
                    sendLongClickAction();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                touchMoved(event);

                if (Math.abs(downX - event.getX()) >= 10
                        || Math.abs(downY - event.getY()) >= 10) { //10像素以外，不属于点击事件
                    removeLongClickAction();
                    touchViewItem = null;
                    isOnTopView = false;
                }

                return true;
            case MotionEvent.ACTION_UP:
                removeLongClickAction();
                if (isOnTopView && touchViewItem == getTopView()
                        && inRangeOfView(touchViewItem, event)) {
                    if (mTopViewClickLister != null) {
                        mTopViewClickLister.onClick(getTopViewPosition(), getTopView());
                    }
                }

                touchViewItem = null;
                isOnTopView = false;
                touchEnded(event);

                //不是点击top view。并且启用点击切换。并且点击了
                if (!isOnTopView && clickSwitchEnable
                        && Math.abs(downX - event.getX()) < 10
                        && Math.abs(downY - event.getY()) < 10
                        && event.getEventTime() - event.getDownTime() < 500) {
                    if (atLeftOfView(getTopView(), event)) {
                        gotoPrevious();
                    } else if (atRightOfView(getTopView(), event)) {
                        gotoForward();
                    }
                }
                return true;
        }

        return false;
    }

    private Runnable longClickRunnable = null;

    /**
     * 发送长点击事件Runnable
     */
    private void sendLongClickAction() {
        removeLongClickAction();
        longClickRunnable = new Runnable() {
            @Override
            public void run() {
                touchViewItem = null;
                isOnTopView = false;
                if (mTopViewLongClickLister != null) {
                    mTopViewLongClickLister.onLongClick(getTopViewPosition(), getTopView());
                }
            }
        };
        postDelayed(longClickRunnable, 600L);
    }

    /**
     * 移除长点击事件Runnable
     */
    private void removeLongClickAction() {
        if (longClickRunnable != null) {
            removeCallbacks(longClickRunnable);
            longClickRunnable = null;
        }
    }

    private boolean inRangeOfView(View view, MotionEvent ev) {
        Rect frame = new Rect();
        getViewRect(view, frame);
        return frame.contains((int) ev.getX(), (int) ev.getY());
    }

    private boolean atRightOfView(View view, MotionEvent ev) {
        Rect frame = new Rect();
        getViewRect(view, frame);
        return ev.getX() > frame.right;
    }

    private boolean atLeftOfView(View view, MotionEvent ev) {
        Rect frame = new Rect();
        getViewRect(view, frame);
        return ev.getX() < frame.left;
    }

    private static void getViewRect(View v, Rect rect) {
//        rect.left = (int) (v.getLeft() + v.getTranslationX());
//        rect.top = (int) (v.getTop() + v.getTranslationY());
//        rect.right = rect.left + v.getWidth();
//        rect.bottom = rect.top + v.getHeight();
        rect.left = (int) ViewHelper.getX(v);
        rect.top = (int) ViewHelper.getY(v);
        rect.right = rect.left + v.getWidth();
        rect.bottom = rect.top + v.getHeight();
    }

    private boolean mTouchMoved;
    private float mTouchStartPos;
    private float mTouchStartX;
    private float mTouchStartY;

    private long mStartTime;
    private float mStartOffset;

    private void touchBegan(MotionEvent event) {
        endAnimation();

        float x = event.getX();
        mTouchStartX = x;
        mTouchStartY = event.getY();
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        mStartOffset = mOffset;

        mTouchMoved = false;

        mTouchStartPos = (x / mWidth) * MOVE_POS_MULTIPLE - 5;
        mTouchStartPos /= 2;

        mVelocity = VelocityTracker.obtain();
        mVelocity.addMovement(event);
    }

    private Runnable mAnimationRunnable;

    private void endAnimation() {
        if (mAnimationRunnable != null) {
            mOffset = (float) Math.floor(mOffset + 0.5);

            invalidate();
            requestLayout();

            removeCallbacks(mAnimationRunnable);
            mAnimationRunnable = null;
        }
    }

    private void touchMoved(MotionEvent event) {
        float pos = (event.getX() / mWidth) * MOVE_POS_MULTIPLE - 5;
        pos /= 2;

        if (!mTouchMoved) {
            float dx = Math.abs(event.getX() - mTouchStartX);
            float dy = Math.abs(event.getY() - mTouchStartY);
            if (dx < TOUCH_MINIMUM_MOVE && dy < TOUCH_MINIMUM_MOVE)
                return;
            mTouchMoved = true;
        }

        mOffset = mStartOffset + mTouchStartPos - pos;

        invalidate();
        requestLayout();
        mVelocity.addMovement(event);
    }

    private void touchEnded(MotionEvent event) {
        float pos = (event.getX() / mWidth) * MOVE_POS_MULTIPLE - 5;
        pos /= 2;

        if (mTouchMoved || (mOffset - Math.floor(mOffset)) != 0) {
            mStartOffset += mTouchStartPos - pos;
            mOffset = mStartOffset;

            mVelocity.addMovement(event);

            mVelocity.computeCurrentVelocity(1000);
            float speed = mVelocity.getXVelocity();

            speed = (speed / mWidth) * MOVE_SPEED_MULTIPLE;
            if (speed > MAX_SPEED)
                speed = MAX_SPEED;
            else if (speed < -MAX_SPEED)
                speed = -MAX_SPEED;

            startAnimation(-speed);
        } else {
//            Log.e(VIEW_LOG_TAG,
//                    " touch ==>" + showViewArray.keyAt(showViewArray.indexOfValue(getTopView())) + "," + event.getX() + " , " + event.getY());
            onTouchMove = false;
        }

        mVelocity.clear();
        mVelocity.recycle();
    }

    private float mStartSpeed;
    private float mDuration;

    private void startAnimation(float speed) {
        if (mAnimationRunnable != null) {
            onTouchMove = false;
            return;
        }

        float delta = speed * speed / (FRICTION * 2);
        if (speed < 0)
            delta = -delta;

        float nearest = mStartOffset + delta;
        nearest = (float) Math.floor(nearest + 0.5f);

        mStartSpeed = (float) Math.sqrt(Math.abs(nearest - mStartOffset)
                * FRICTION * 2);
        if (nearest < mStartOffset)
            mStartSpeed = -mStartSpeed;

        mDuration = Math.abs(mStartSpeed / FRICTION);
        mStartTime = AnimationUtils.currentAnimationTimeMillis();

        mAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                driveAnimation();
            }
        };
        post(mAnimationRunnable);
    }

    private void driveAnimation() {
        float elapsed = (AnimationUtils.currentAnimationTimeMillis() - mStartTime) / 1000.0f;
        if (elapsed >= mDuration) {
            endAnimation();
            onTouchMove = false;
        } else {
            updateAnimationAtElapsed(elapsed);
            post(mAnimationRunnable);
        }
    }

    private void updateAnimationAtElapsed(float elapsed) {
        if (elapsed > mDuration)
            elapsed = mDuration;

        float delta = Math.abs(mStartSpeed) * elapsed - FRICTION * elapsed
                * elapsed / 2;
        if (mStartSpeed < 0)
            delta = -delta;

        mOffset = mStartOffset + delta;
        invalidate();
        requestLayout();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

//        算出移动到某一个虚拟点时  mOffset的值  然后invalidate 重绘
        if (mScroller.computeScrollOffset()) {
            final int currX = mScroller.getCurrX();

            mOffset = (float) currX / 100;

            invalidate();
        }
    }

    private boolean clickSwitchEnable = true;

    /**
     * 是否可以点击左右侧 来切换上下张
     */
    public void setClick2SwitchEnabled(boolean enabled) {
        clickSwitchEnable = enabled;
    }

    private boolean isOnAnimator = false; //是否正在进行点击切换动画

    /**
     * 翻到前页
     */
    public void gotoPrevious() {
        doAnimator(-1.0f);
    }

    /**
     * 前进到后一页
     */
    public void gotoForward() {
        doAnimator(1.0f);
    }

    private void doAnimator(float target) {
        if (isOnAnimator || onTouchMove) { //如果正在执行点击切换动画 或者 正在执行触摸移动
            return;
        }
        if (target == 0)
            return;

        isOnAnimator = true;
        ValueAnimator animator = ValueAnimator.ofFloat(mOffset, mOffset + target);
        animator.setDuration(300).start();
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (Float) animation.getAnimatedValue();

//				System.out.println("##################3  mOffset : " + mOffset);

                invalidate();
                requestLayout();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isOnAnimator = false;
            }
        });
    }

    /**
     * 选择某一个位置
     *
     * @param selection seleciton
     * @param smooth    是否平滑滑动过去，还是直接切换
     * @throws IndexOutOfBoundsException
     */
    public void setSelection(int selection, boolean smooth) {
        if (smooth) {
            int count = mAdapter.getCount();
            if (selection < 0 || selection >= count) {
                throw new IndexOutOfBoundsException("selection out of bound!");
            }

            if (isOnAnimator || onTouchMove) { //如果正在执行点击切换动画 或者 正在执行触摸移动
                return;
            }

            float offset = mOffset;
            int curPos = getActuallyPosition((int) offset);

            //求出当前位置到达selection位置的最短路径
            int distance1 = selection - curPos;
            int distance2 = selection + count - curPos;
            int distance3 = selection - count - curPos;

            int minDistance = Math.abs(distance1) < Math.abs(distance2) ? distance1 : distance2;
            minDistance = Math.abs(minDistance) < Math.abs(distance3) ? minDistance : distance3;

            doAnimator(minDistance);
        } else {
            initChildren(selection);
            requestLayout();
        }
    }

    private OnTopViewClickListener mTopViewClickLister;
    private OnViewOnTopListener mViewOnTopListener;
    private OnTopViewLongClickListener mTopViewLongClickLister;

    public OnViewOnTopListener getOnViewOnTopListener() {
        return mViewOnTopListener;
    }

    public void setOnViewOnTopListener(OnViewOnTopListener mViewOnTopListener) {
        this.mViewOnTopListener = mViewOnTopListener;
    }

    /**
     * 设置TopView的点击监听
     */
    public void setOnTopViewClickListener(OnTopViewClickListener topViewClickLister) {
        this.mTopViewClickLister = topViewClickLister;
    }

    public OnTopViewClickListener getOnTopViewClickListener() {
        return this.mTopViewClickLister;
    }

    /**
     * 设置TopView的长点击监听
     */
    public void setOnTopViewLongClickListener(OnTopViewLongClickListener topViewLongClickLister) {
        this.mTopViewLongClickLister = topViewLongClickLister;
    }

    public OnTopViewLongClickListener getOnTopViewLongClickListener() {
        return this.mTopViewLongClickLister;
    }


    public interface OnViewOnTopListener {

        void viewOnTop(int position, View itemView);

    }

    public interface OnTopViewClickListener {

        void onClick(int position, View itemView);

    }

    public interface OnTopViewLongClickListener {

        void onLongClick(int position, View itemView);

    }

    class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() { //数据改变了，更新child的数据
            int visibleCount = mVisibleChildCount;

            int mid = lastMid;
            //右边孩子的数量
            int rightChild = (visibleCount % 2 == 0) ? (visibleCount >> 1) - 1
                    : visibleCount >> 1;
            //左边孩子的数量
            int leftChild = visibleCount >> 1;

            int startPos = mid - leftChild;
            int endPos = mid + rightChild;

            for (int i = startPos; i <= endPos; i++) {
                int actuallyPosition = getActuallyPosition(i);
                View view = showViewArray.get(actuallyPosition);
                mAdapter.refreshView(view, actuallyPosition);
            }
        }
    }
}
