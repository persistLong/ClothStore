package com.ioter.clothesstrore.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.ioter.clothesstrore.R;
import com.ioter.clothesstrore.common.util.DebugUtil;
import com.ioter.clothesstrore.di.component.AppComponent;
import com.ioter.clothesstrore.video.SampleControlVideo;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.StandardVideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import butterknife.BindView;


public class PlayFragment extends BaseFragment implements StandardVideoAllCallBack
{

    @BindView(R.id.detail_player)
    SampleControlVideo detailPlayer;

    protected boolean isPlay;

    protected boolean isPause;

    protected OrientationUtils orientationUtils;

    public static PlayFragment newInstance(String playUrl)
    {
        PlayFragment playFragment = new PlayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("playUrl", playUrl);
        playFragment.setArguments(bundle);
        return playFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout()
    {
        return R.layout.fragment_play;
    }


    @Override
    public void setupAcitivtyComponent(AppComponent appComponent)
    {
    }

    @Override
    public void init(View view)
    {
        isPlay = false;
        isPause = false;
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            String playUrl = arguments.getString("playUrl");
            if (!TextUtils.isEmpty(playUrl))
            {
                detailPlayer.release();
                initVideo();
                getGSYVideoOptionBuilder(playUrl).setStandardVideoAllCallBack(this).build(detailPlayer);

/*                detailPlayer.startPlayLogic();
                //先播放，才旋转
                //直接横屏
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(mActivity, true, true);*/
            }
        }
    }


    public void play()
    {
        if (detailPlayer == null)
        {
            return;
        }
        detailPlayer.getStartButton().performClick();
    }

    private GSYVideoOptionBuilder getGSYVideoOptionBuilder(String url)
    {
        //内置封面可参考SampleCoverVideo
        ImageView imageView = new ImageView(mActivity);
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

    /**
     * 选择普通模式
     */
    public void initVideo()
    {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(mActivity, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        if (detailPlayer.getFullscreenButton() != null)
        {
            detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //直接横屏
                    orientationUtils.resolveByClick();
                    //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                    detailPlayer.startWindowFullscreen(mActivity, true, true);
                }
            });
        }
    }

    private void loadCover(ImageView imageView, String url)
    {

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_map_2);
  /*      Bitmap videoThumbnail = Util.getVideoThumbnail(url);
        if (videoThumbnail != null)
        {
            imageView.setImageBitmap(videoThumbnail);
        } else
        {
            imageView.setImageResource(R.mipmap.ic_map_2);
        }*/

        //ImageLoader.load(url, imageView);

/*        Glide.with(AppApplication.getApplication())
                .load(Uri.fromFile(new File(url)))
                .into(imageView);*/

    }


/*    @Override
    public void onBackPressed()
    {
        if (orientationUtils != null)
        {
            orientationUtils.backToProtVideo();
        }
        if (StandardGSYVideoPlayer.backFromWindowFull(mActivity))
        {
            return;
        }
        super.onBackPressed();
    }*/


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause)
        {
            detailPlayer.onConfigurationChanged(mActivity, newConfig, orientationUtils);
        }
    }

    @Override
    public void onPrepared(String url, Object... objects)
    {
        if (orientationUtils == null)
        {
            throw new NullPointerException("initVideo() or initVideoBuilderMode() first");
        }
        //开始播放了才能旋转和全屏
        orientationUtils.setEnable(false);
        isPlay = true;
    }

    @Override
    public void onClickStartIcon(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickStartIcon");
    }

    @Override
    public void onClickStartError(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickStartError");
    }

    @Override
    public void onClickStop(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickStop");
    }

    @Override
    public void onClickStopFullscreen(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickStopFullscreen");
    }

    @Override
    public void onClickResume(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickResume");
    }

    @Override
    public void onClickResumeFullscreen(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickResumeFullscreen");
    }

    @Override
    public void onClickSeekbar(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickSeekbar");
    }

    @Override
    public void onClickSeekbarFullscreen(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickSeekbarFullscreen");
    }

    @Override
    public void onAutoComplete(String url, Object... objects)
    {
        StandardGSYVideoPlayer.backFromWindowFull(mActivity);
        DebugUtil.printe("video", "onAutoComplete");
    }

    @Override
    public void onEnterFullscreen(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickStartIcon");
    }

    @Override
    public void onQuitFullscreen(String url, Object... objects)
    {
        if (orientationUtils != null)
        {
            orientationUtils.backToProtVideo();
        }
        DebugUtil.printe("video", "onQuitFullscreen");
    }

    @Override
    public void onQuitSmallWidget(String url, Object... objects)
    {
        DebugUtil.printe("video", "onQuitSmallWidget");
    }

    @Override
    public void onEnterSmallWidget(String url, Object... objects)
    {
        DebugUtil.printe("video", "onEnterSmallWidget");
    }

    @Override
    public void onTouchScreenSeekVolume(String url, Object... objects)
    {
        DebugUtil.printe("video", "onTouchScreenSeekVolume");
    }

    @Override
    public void onTouchScreenSeekPosition(String url, Object... objects)
    {
        DebugUtil.printe("video", "onTouchScreenSeekPosition");
    }

    @Override
    public void onTouchScreenSeekLight(String url, Object... objects)
    {
        DebugUtil.printe("video", "onTouchScreenSeekLight");
    }

    @Override
    public void onPlayError(String url, Object... objects)
    {
        DebugUtil.printe("video", "onPlayError");
    }

    @Override
    public void onClickStartThumb(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickStartThumb");
    }

    @Override
    public void onClickBlank(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickBlank");
    }

    @Override
    public void onClickBlankFullscreen(String url, Object... objects)
    {
        DebugUtil.printe("video", "onClickBlankFullscreen");
    }


    @Override
    public void onPause()
    {
        super.onPause();
        detailPlayer.getCurrentPlayer().onVideoPause();
        isPause = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        detailPlayer.getCurrentPlayer().onVideoResume();
        isPause = false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (detailPlayer != null)
        {
            detailPlayer.getCurrentPlayer().release();
        }
        if (orientationUtils != null)
        {
            orientationUtils.releaseListener();
        }
    }
}
