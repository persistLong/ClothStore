package com.ioter.clothesstrore.wdiget.coverflow;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimatorProxy extends Animation
{


    public static AnimatorProxy wrap(View view)
    {
        AnimatorProxy proxy = (AnimatorProxy)PROXIES.get(view);
        if(proxy == null || proxy != view.getAnimation())
        {
            proxy = new AnimatorProxy(view);
            PROXIES.put(view, proxy);
        }
        return proxy;
    }

    private AnimatorProxy(View view)
    {
        mAlpha = 1.0F;
        mScaleX = 1.0F;
        mScaleY = 1.0F;
        setDuration(0L);
        setFillAfter(true);
        view.setAnimation(this);
        mView = new WeakReference(view);
    }

    public float getAlpha()
    {
        return mAlpha;
    }

    public void setAlpha(float alpha)
    {
        if(mAlpha != alpha)
        {
            mAlpha = alpha;
            View view = (View)mView.get();
            if(view != null)
                view.invalidate();
        }
    }

    public float getPivotX()
    {
        return mPivotX;
    }

    public void setPivotX(float pivotX)
    {
        if(!mHasPivot || mPivotX != pivotX)
        {
            prepareForUpdate();
            mHasPivot = true;
            mPivotX = pivotX;
            invalidateAfterUpdate();
        }
    }

    public float getPivotY()
    {
        return mPivotY;
    }

    public void setPivotY(float pivotY)
    {
        if(!mHasPivot || mPivotY != pivotY)
        {
            prepareForUpdate();
            mHasPivot = true;
            mPivotY = pivotY;
            invalidateAfterUpdate();
        }
    }

    public float getRotation()
    {
        return mRotationZ;
    }

    public void setRotation(float rotation)
    {
        if(mRotationZ != rotation)
        {
            prepareForUpdate();
            mRotationZ = rotation;
            invalidateAfterUpdate();
        }
    }

    public float getRotationX()
    {
        return mRotationX;
    }

    public void setRotationX(float rotationX)
    {
        if(mRotationX != rotationX)
        {
            prepareForUpdate();
            mRotationX = rotationX;
            invalidateAfterUpdate();
        }
    }

    public float getRotationY()
    {
        return mRotationY;
    }

    public void setRotationY(float rotationY)
    {
        if(mRotationY != rotationY)
        {
            prepareForUpdate();
            mRotationY = rotationY;
            invalidateAfterUpdate();
        }
    }

    public float getScaleX()
    {
        return mScaleX;
    }

    public void setScaleX(float scaleX)
    {
        if(mScaleX != scaleX)
        {
            prepareForUpdate();
            mScaleX = scaleX;
            invalidateAfterUpdate();
        }
    }

    public float getScaleY()
    {
        return mScaleY;
    }

    public void setScaleY(float scaleY)
    {
        if(mScaleY != scaleY)
        {
            prepareForUpdate();
            mScaleY = scaleY;
            invalidateAfterUpdate();
        }
    }

    public int getScrollX()
    {
        View view = (View)mView.get();
        if(view == null)
            return 0;
        else
            return view.getScrollX();
    }

    public void setScrollX(int value)
    {
        View view = (View)mView.get();
        if(view != null)
            view.scrollTo(value, view.getScrollY());
    }

    public int getScrollY()
    {
        View view = (View)mView.get();
        if(view == null)
            return 0;
        else
            return view.getScrollY();
    }

    public void setScrollY(int value)
    {
        View view = (View)mView.get();
        if(view != null)
            view.scrollTo(view.getScrollX(), value);
    }

    public float getTranslationX()
    {
        return mTranslationX;
    }

    public void setTranslationX(float translationX)
    {
        if(mTranslationX != translationX)
        {
            prepareForUpdate();
            mTranslationX = translationX;
            invalidateAfterUpdate();
        }
    }

    public float getTranslationY()
    {
        return mTranslationY;
    }

    public void setTranslationY(float translationY)
    {
        if(mTranslationY != translationY)
        {
            prepareForUpdate();
            mTranslationY = translationY;
            invalidateAfterUpdate();
        }
    }

    public float getX()
    {
        View view = (View)mView.get();
        if(view == null)
            return 0.0F;
        else
            return (float)view.getLeft() + mTranslationX;
    }

    public void setX(float x)
    {
        View view = (View)mView.get();
        if(view != null)
            setTranslationX(x - (float)view.getLeft());
    }

    public float getY()
    {
        View view = (View)mView.get();
        if(view == null)
            return 0.0F;
        else
            return (float)view.getTop() + mTranslationY;
    }

    public void setY(float y)
    {
        View view = (View)mView.get();
        if(view != null)
            setTranslationY(y - (float)view.getTop());
    }

    private void prepareForUpdate()
    {
        View view = (View)mView.get();
        if(view != null)
            computeRect(mBefore, view);
    }

    private void invalidateAfterUpdate()
    {
        View view = (View)mView.get();
        if(view == null || view.getParent() == null)
        {
            return;
        } else
        {
            RectF after = mAfter;
            computeRect(after, view);
            after.union(mBefore);
            ((View)view.getParent()).invalidate((int)Math.floor(after.left), (int)Math.floor(after.top), (int)Math.ceil(after.right), (int)Math.ceil(after.bottom));
            return;
        }
    }

    private void computeRect(RectF r, View view)
    {
        float w = view.getWidth();
        float h = view.getHeight();
        r.set(0.0F, 0.0F, w, h);
        Matrix m = mTempMatrix;
        m.reset();
        transformMatrix(m, view);
        mTempMatrix.mapRect(r);
        r.offset(view.getLeft(), view.getTop());
        if(r.right < r.left)
        {
            float f = r.right;
            r.right = r.left;
            r.left = f;
        }
        if(r.bottom < r.top)
        {
            float f = r.top;
            r.top = r.bottom;
            r.bottom = f;
        }
    }

    private void transformMatrix(Matrix m, View view)
    {
        float w = view.getWidth();
        float h = view.getHeight();
        boolean hasPivot = mHasPivot;
        float pX = hasPivot ? mPivotX : w / 2.0F;
        float pY = hasPivot ? mPivotY : h / 2.0F;
        float rX = mRotationX;
        float rY = mRotationY;
        float rZ = mRotationZ;
        if(rX != 0.0F || rY != 0.0F || rZ != 0.0F)
        {
            Camera camera = mCamera;
            camera.save();
            camera.rotateX(rX);
            camera.rotateY(rY);
            camera.rotateZ(-rZ);
            camera.getMatrix(m);
            camera.restore();
            m.preTranslate(-pX, -pY);
            m.postTranslate(pX, pY);
        }
        float sX = mScaleX;
        float sY = mScaleY;
        if(sX != 1.0F || sY != 1.0F)
        {
            m.postScale(sX, sY);
            float sPX = -(pX / w) * (sX * w - w);
            float sPY = -(pY / h) * (sY * h - h);
            m.postTranslate(sPX, sPY);
        }
        m.postTranslate(mTranslationX, mTranslationY);
    }

    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        View view = (View)mView.get();
        if(view != null)
        {
            t.setAlpha(mAlpha);
            transformMatrix(t.getMatrix(), view);
        }
    }

    public static final boolean NEEDS_PROXY;
    private static final WeakHashMap PROXIES = new WeakHashMap();
    private final WeakReference mView;
    private final Camera mCamera = new Camera();
    private boolean mHasPivot;
    private float mAlpha;
    private float mPivotX;
    private float mPivotY;
    private float mRotationX;
    private float mRotationY;
    private float mRotationZ;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;
    private final RectF mBefore = new RectF();
    private final RectF mAfter = new RectF();
    private final Matrix mTempMatrix = new Matrix();

    static 
    {
        NEEDS_PROXY = Integer.valueOf(android.os.Build.VERSION.SDK).intValue() < 11;
    }

}
