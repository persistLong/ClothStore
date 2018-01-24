package com.ioter.clothesstrore.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ioter.clothesstrore.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.Timer;
import java.util.TimerTask;

import static com.shuyu.gsyvideoplayer.utils.CommonUtil.hideNavKey;

public class FloatingControlVideo extends StandardGSYVideoPlayer
{

    protected DismissControlViewTimerTask mDismissControlViewTimerTask;

    //记住切换数据源类型
    private int mType = 1;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public FloatingControlVideo(Context context, Boolean fullFlag)
    {
        super(context, fullFlag);
    }

    public FloatingControlVideo(Context context)
    {
        super(context);
    }

    public FloatingControlVideo(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void init(Context context)
    {
        if (getActivityContext() != null)
        {
            this.mContext = getActivityContext();
        } else
        {
            this.mContext = context;
        }

        initInflate(mContext);

        mTextureViewContainer = (ViewGroup) findViewById(R.id.surface_container);
        mStartButton = findViewById(R.id.start);

        if (isInEditMode())
            return;
        mScreenWidth = getActivityContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getActivityContext().getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) getActivityContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mStartButton = findViewById(com.shuyu.gsyvideoplayer.R.id.start);
        mStartButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickStartIcon();
            }
        });
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.layout_floating_video;
    }


    @Override
    protected void startPrepare()
    {
        if (GSYVideoManager.instance().listener() != null)
        {
            GSYVideoManager.instance().listener().onCompletion();
        }
        GSYVideoManager.instance().setListener(this);
        GSYVideoManager.instance().setPlayTag(mPlayTag);
        GSYVideoManager.instance().setPlayPosition(mPlayPosition);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        //((Activity) getActivityContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBackUpPlayingBufferState = -1;
        GSYVideoManager.instance().prepare(mUrl, mMapHeadData, mLooping, mSpeed);
        setStateAndUi(CURRENT_STATE_PREPAREING);
    }

    @Override
    public void onAutoCompletion()
    {
        setStateAndUi(CURRENT_STATE_AUTO_COMPLETE);

        mSaveChangeViewTIme = 0;

        if (mTextureViewContainer.getChildCount() > 0)
        {
            mTextureViewContainer.removeAllViews();
        }

        if (!mIfCurrentIsFullscreen)
            GSYVideoManager.instance().setLastListener(null);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        //((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        releaseNetWorkState();

        if (mVideoAllCallBack != null && isCurrentMediaListener())
        {
            Debuger.printfLog("onAutoComplete");
            mVideoAllCallBack.onAutoComplete(mOriginUrl, mTitle, this);
        }
    }

    @Override
    public void onCompletion()
    {
        //make me normal first
        setStateAndUi(CURRENT_STATE_NORMAL);

        mSaveChangeViewTIme = 0;

        if (mTextureViewContainer.getChildCount() > 0)
        {
            mTextureViewContainer.removeAllViews();
        }

        if (!mIfCurrentIsFullscreen)
        {
            GSYVideoManager.instance().setListener(null);
            GSYVideoManager.instance().setLastListener(null);
        }
        GSYVideoManager.instance().setCurrentVideoHeight(0);
        GSYVideoManager.instance().setCurrentVideoWidth(0);

        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        //((Activity) getActivityContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        releaseNetWorkState();

    }


    @Override
    protected Context getActivityContext()
    {
        return getContext();
    }

    @Override
    protected void startDismissControlViewTimer()
    {
        cancelDismissControlViewTimer();
        mDismissControlViewTimer = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        mDismissControlViewTimer.schedule(mDismissControlViewTimerTask, mDismissControlTime);
    }

    @Override
    protected void cancelDismissControlViewTimer()
    {
        if (mDismissControlViewTimer != null)
        {
            mDismissControlViewTimer.cancel();
            mDismissControlViewTimer = null;
        }
        if (mDismissControlViewTimerTask != null)
        {
            mDismissControlViewTimerTask.cancel();
            mDismissControlViewTimerTask = null;
        }

    }

    private class DismissControlViewTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            if (mCurrentState != CURRENT_STATE_NORMAL
                    && mCurrentState != CURRENT_STATE_ERROR
                    && mCurrentState != CURRENT_STATE_AUTO_COMPLETE)
            {
                if (getActivityContext() != null)
                {
                    FloatingControlVideo.this.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            hideAllWidget();
                            setViewShowState(mLockScreen, GONE);
                            if (mHideKey && mIfCurrentIsFullscreen && mShowVKey)
                            {
                                hideNavKey(mContext);
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar)
    {
        FloatingControlVideo sampleVideo = (FloatingControlVideo) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mType = mType = 3;
        //sampleVideo.resolveTransform();
        sampleVideo.resolveTypeUI();
        //sampleVideo.resolveRotateUI();
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        return sampleVideo;
    }

    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer)
    {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null)
        {
            FloatingControlVideo sampleVideo = (FloatingControlVideo) gsyVideoPlayer;
            sampleVideo.mType = mType = 1;
            resolveTypeUI();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        super.onSurfaceTextureAvailable(surface, width, height);
        resolveRotateUI();
    }


    private void resolveRotateUI()
    {
        if (!mHadPlay)
        {
            return;
        }
        mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();
    }

    private void resolveTypeUI()
    {
        if (!mHadPlay)
        {
            return;
        }
        if (mType == 1)
        {
            //mMoreScale.setText("16:9");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        } else if (mType == 2)
        {
            //mMoreScale.setText("4:3");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_4_3);
        } else if (mType == 3)
        {
            //mMoreScale.setText("全屏");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        } else if (mType == 4)
        {
            //mMoreScale.setText("拉伸全屏");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        } else if (mType == 0)
        {
            //mMoreScale.setText("默认比例");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        }
        changeTextureViewShowType();
        if (mTextureView != null)
            mTextureView.requestLayout();
    }
}