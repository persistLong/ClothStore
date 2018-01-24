package com.ioter.clothesstrore.video;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.common.util.Util;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

public class FloatPlayerView extends FrameLayout
{

    FloatingControlVideo videoPlayer;

    public FloatPlayerView(Context context)
    {
        super(context);
        init();
    }

    public FloatPlayerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FloatPlayerView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {

        videoPlayer = new FloatingControlVideo(getContext());

        initVideo();

        videoPlayer.release();

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.LEFT;

        addView(videoPlayer, layoutParams);

        String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";


        videoPlayer.setUp(source1, true, "");

        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);
 /*       Bitmap videoThumbnail = Util.getVideoThumbnail(source1);
        if (videoThumbnail != null)
        {
            imageView.setImageBitmap(videoThumbnail);
        } else
        {
            imageView.setImageResource(R.mipmap.xxx1);
        }*/
        videoPlayer.setThumbImageView(imageView);

        //增加封面
        /*ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.xxx1);
        videoPlayer.setThumbImageView(imageView);*/

        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(false);

        videoPlayer.getStartButton().performClick();

    }


    protected OrientationUtils orientationUtils;

    /**
     * 选择普通模式
     */
    public void initVideo()
    {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils((Activity) getContext(), videoPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        if (videoPlayer.getFullscreenButton() != null)
        {
            videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //直接横屏
                    orientationUtils.resolveByClick();
                    //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                    videoPlayer.startWindowFullscreen(getContext(), true, true);
                }
            });
        }
    }

    private GSYVideoOptionBuilder getGSYVideoOptionBuilder(String url)
    {
        //内置封面可参考SampleCoverVideo
        ImageView imageView = new ImageView(getContext());
        loadCover(imageView, url);
        return new GSYVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle("")
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(true)//打开动画
                //.setNeedLockFull(true)
                .setNeedLockFull(false)
                .setSeekRatio(1);
    }

    private void loadCover(ImageView imageView, String url)
    {

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap videoThumbnail = Util.getVideoThumbnail(url);
        if (videoThumbnail != null)
        {
            imageView.setImageBitmap(videoThumbnail);
        } else
        {
            imageView.setImageResource(R.mipmap.xxx1);
        }

        //ImageLoader.load(url, imageView);

/*        Glide.with(AppApplication.getApplication())
                .load(Uri.fromFile(new File(url)))
                .into(imageView);*/

    }


    public void onPause()
    {
        videoPlayer.onVideoPause();
    }

    public void onResume()
    {
        videoPlayer.onVideoResume();
    }

    public void onBackPressed()
    {
        //释放所有
        videoPlayer.setStandardVideoAllCallBack(null);
        GSYVideoPlayer.releaseAllVideos();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        videoPlayer.onConfigurationChanged((Activity) getContext(), newConfig, orientationUtils);
    }



}