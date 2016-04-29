package org.dync.teameeting.utils;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.anyrtc.common.AnyRTCViewEvents;
import org.anyrtc.util.AppRTCUtils;
import org.anyrtc.view.PercentFrameLayout;
import org.dync.teameeting.R;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Eric on 2016/3/4.
 */
public class AnyRTCViews implements View.OnTouchListener, AnyRTCViewEvents {
    private static final boolean mDebug = true;
    private static final String TAG = "AnyRTCViews";
    private static final int SUB_X = 2;
    private static int SUB_Y = 72;
    private static int SUB_WIDTH = 20;
    private static int SUB_HEIGHT = 18;
    private static int mScreenWidth;
    private static int mScreenHeight;
    private static ImageView mVoiceClose;
    private static ImageView mVideoClose;
    private static Context mContent;
    private HashMap<String, Boolean> mVoiceSetting = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> mVideoSetting = new HashMap<String, Boolean>();
    private VideoViewPeopleNumEvent mVideoViewPeopleNumEvent;

    public interface VideoViewEvent {
        void OnScreenSwitch(String strBeforeFullScrnId, String strNowFullScrnId);
    }

    public interface VideoViewPeopleNumEvent {
        void OnPeopleNumChange(int peopleNum);
    }

    /**
     * 人数改变回掉监听
     * @param videoViewPeopleNumEvent
     */
    public void setVideoViewPeopleNumEvent(VideoViewPeopleNumEvent videoViewPeopleNumEvent) {
        mVideoViewPeopleNumEvent = videoViewPeopleNumEvent;
    }

    /**
     * 内部类
     */
    protected class VideoView {
        public String strPeerId;
        public int index;  //当前像的编号
        public int x;
        public int y;
        public int w;
        public int h;
        public PercentFrameLayout mLayout = null;
        public SurfaceViewRenderer mView = null; // 显示像的控件
        public VideoTrack mVideoTrack = null;
        public VideoRenderer mRenderer = null;
        private boolean mVoiceShowFalg = false;
        private boolean mVideoShowFalg = false;
        private ImageView mVoiceImageView;
        private ImageView mVideoImageView;
        private int width = mScreenWidth * SUB_WIDTH / (100 * 3);
        private int height = mScreenHeight * SUB_HEIGHT / (100 * 3);

        public VideoView(String strPeerId, Context ctx, int index, int x, int y, int w, int h) {
            this.strPeerId = strPeerId;
            this.index = index;
            this.x = x;
            this.y = y;
            this.w = w; //长
            this.h = h; //宽

            mLayout = new PercentFrameLayout(ctx);
            mLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }

        public Boolean Fullscreen() {
            return w == 100 || h == 100;
        }

        public Boolean Hited(int px, int py) {
            if (!Fullscreen()) {
                int left = x * mScreenWidth / 100;
                int top = y * mScreenHeight / 100;
                int right = (x + w) * mScreenWidth / 100;
                int bottom = (y + h) * mScreenHeight / 100;
                if (mDebug)
                    Log.e(TAG, "Hited: " + "px " + px + " py " + py + " left " + left + " top " + top + " right " + right + " bottom " + bottom);
                if ((px >= left && px <= right) && (py >= top && py <= bottom)) {
                    return true;
                }
            }
            return false;
        }

        private void updateVideoLayoutView(PercentFrameLayout layout, SurfaceViewRenderer view) {
            mLayout = layout;
            mView = view;
            if (mVideoTrack != null) {
                mVideoTrack.removeRenderer(mRenderer);
                mRenderer = new VideoRenderer(mView);
                mVideoTrack.addRenderer(mRenderer);
            }
            mView.requestLayout();
        }


        /**
         * 打开或者关闭视频
         */
        private void createImageView() {
            mVoiceImageView = new ImageView(mContent);
            mVoiceImageView.setImageResource(R.drawable.small_mic_muted);
            mVoiceImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ViewGroup.LayoutParams layoutParamsVoice = new RelativeLayout.LayoutParams(width, height);
            mVideoView.addView(mVoiceImageView, layoutParamsVoice);
            mVoiceImageView.setVisibility(View.GONE);

            mVideoImageView = new ImageView(mContent);
            mVideoImageView.setImageResource(R.drawable.video_close_small);
            mVideoImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ViewGroup.LayoutParams layoutParamsVideo = new RelativeLayout.LayoutParams(width, height);
            mVideoView.addView(mVideoImageView, layoutParamsVideo);
            mVideoImageView.setVisibility(View.GONE);
        }

        private void updateView() {
            if (mVoiceShowFalg) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mVoiceImageView.getLayoutParams();
                if (Fullscreen()) {
                    marginLayoutParams.leftMargin = mScreenWidth / 2 - width;
                    marginLayoutParams.topMargin = mScreenHeight / 2 - height;
                    mVoiceClose.setVisibility(View.VISIBLE);
                    mVoiceImageView.setVisibility(View.GONE);
                    if (mDebug) {
                        Log.e(TAG, "updateView: Fullscreen ");
                    }
                } else {
                    marginLayoutParams.leftMargin = mScreenWidth * (x + SUB_WIDTH) / 100 - width;
                    marginLayoutParams.topMargin = mScreenHeight * y / 100;
                    mVoiceImageView.setVisibility(View.VISIBLE);
                    if (mDebug) {
                        Log.e(TAG, "updateView: " + " marginLayoutParams.leftMargin " + marginLayoutParams.leftMargin + " marginLayoutParams.topMargin " + marginLayoutParams.topMargin);
                    }
                }

                mVoiceImageView.setLayoutParams(marginLayoutParams);
            } else {
                if (Fullscreen()) {
                    mVoiceClose.setVisibility(View.GONE);
                } else {
                    mVoiceImageView.setVisibility(View.GONE);
                }
            }


            if (mVideoShowFalg) {

                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mVideoImageView.getLayoutParams();
                if (Fullscreen()) {
                    marginLayoutParams.leftMargin = mScreenWidth - width;
                    marginLayoutParams.topMargin = 0;
                    // mVoiceView.setVisibility(View.GONE);
                    mVideoClose.setVisibility(View.VISIBLE);
                    mVideoImageView.setVisibility(View.GONE);
                } else {
                    marginLayoutParams.leftMargin = mScreenWidth * (x + SUB_WIDTH / 2) / 100 - width / 2;
                    marginLayoutParams.topMargin = mScreenHeight * (y + SUB_HEIGHT / 2) / 100 - height / 2;
                    //  mVoiceClose.setVisibility(View.GONE);
                    mVideoImageView.setVisibility(View.VISIBLE);
                    if (mDebug) {
                        Log.e(TAG, "updateView: " + " marginLayoutParams.leftMargin " + marginLayoutParams.leftMargin + " marginLayoutParams.topMargin " + marginLayoutParams.topMargin);
                    }
                }

                mVideoImageView.setLayoutParams(marginLayoutParams);
            } else {
                if (Fullscreen()) {
                    mVideoClose.setVisibility(View.GONE);
                } else {
                    mVideoImageView.setVisibility(View.GONE);
                }
            }
        }
    }

    private EglBase mRootEglBase;
    private static RelativeLayout mVideoView; //显示根布局
    private VideoView mLocalRender;
    private HashMap<String, VideoView> mRemoteRenders;//远程像的一个集合

    public AnyRTCViews(RelativeLayout videoView, Context content, ImageView closeVoice, ImageView closeVideo) {
        AppRTCUtils.assertIsTrue(videoView != null);
        mVideoView = videoView;
        mContent = content;
        mVoiceClose = closeVoice;//话筒控制
        mVideoClose = closeVideo;//视屏前后控制
        mScreenWidth = ScreenUtils.getScreenWidth(mContent);
        mScreenHeight = ScreenUtils.getScreenHeight(mContent) - ScreenUtils.getStatusHeight(mContent);
        mVideoView.setOnTouchListener(this);
        mRootEglBase = EglBase.create();
        mRemoteRenders = new HashMap<>();
        mLocalRender = new VideoView("localRender", mVideoView.getContext(), 0, 0, 0, 100, 100);
    }

    public void destoryAnyRTCViews() {
        Log.e(TAG, "destoryAnyRTCViews: ");

        // VideoView remoteRender = mRemoteRenders.get(peerId);
        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, VideoView> entry = iter.next();
            VideoView remoteRender = entry.getValue();

            if (remoteRender.mView != null) {
                remoteRender.mView.release();
                remoteRender.mView = null;
                remoteRender.mRenderer = null;
            }
            mVideoView.removeView(remoteRender.mLayout);
            mVideoView.removeView(remoteRender.mVideoImageView);
            mVideoView.removeView(remoteRender.mVoiceImageView);


            remoteRender.mVideoImageView = null;
            remoteRender.mVoiceImageView = null;
            remoteRender.mLayout = null;
        }


        if (mVideoViewPeopleNumEvent != null) {
            mVideoViewPeopleNumEvent = null;
        }
        mVoiceClose = null;
        mVideoClose = null;
        //mVideoView = null;
    }

    public VideoTrack LocalVideoTrack() {
        return mLocalRender.mVideoTrack;
    }

    private void SwitchIndex1ToFullscreen(VideoView fullscrnView) {
        AppRTCUtils.assertIsTrue(fullscrnView != null);
        VideoView view1 = null;
        if (mLocalRender.index == 1) {
            view1 = mLocalRender;
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (render.index == 1) {
                    view1 = render;
                    break;
                }
            }
        }
        SwitchViewPosition(view1, fullscrnView);
    }

    private VideoView GetFullScreen() {
        if (mLocalRender.Fullscreen())
            return mLocalRender;
        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, VideoView> entry = iter.next();
            //* String peerId = entry.getKey();
            VideoView render = entry.getValue();
            if (render.Fullscreen())
                return render;
        }
        return null;
    }

    private void SwitchViewPosition(VideoView view1, VideoView view2) {
        AppRTCUtils.assertIsTrue(view1 != null && view2 != null);
        int index, x, y, w, h;
        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;
        view1.index = view2.index;
        view1.x = view2.x;
        view1.y = view2.y;
        view1.w = view2.w;
        view1.h = view2.h;

        view2.index = index;
        view2.x = x;
        view2.y = y;
        view2.w = w;
        view2.h = h;

        PercentFrameLayout layout_a = view1.mLayout;
        SurfaceViewRenderer view_a = view1.mView;
        PercentFrameLayout layout_b = view2.mLayout;
        SurfaceViewRenderer view_b = view2.mView;

        view1.updateVideoLayoutView(layout_b, view_b);
        view2.updateVideoLayoutView(layout_a, view_a);

        view1.updateView();
        view2.updateView();
    }

    private void SwitchViewToFullscreen(VideoView view1, VideoView fullscrnView) {
        AppRTCUtils.assertIsTrue(view1 != null && fullscrnView != null);
        int index, x, y, w, h;
        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = fullscrnView.index;
        view1.x = fullscrnView.x;
        view1.y = fullscrnView.y;
        view1.w = fullscrnView.w;
        view1.h = fullscrnView.h;

        fullscrnView.index = index;
        fullscrnView.x = x;
        fullscrnView.y = y;
        fullscrnView.w = w;
        fullscrnView.h = h;

        PercentFrameLayout layout_a = view1.mLayout;
        SurfaceViewRenderer view_a = view1.mView;
        PercentFrameLayout layout_b = fullscrnView.mLayout;
        SurfaceViewRenderer view_b = fullscrnView.mView;

        view1.updateVideoLayoutView(layout_b, view_b);
        fullscrnView.updateVideoLayoutView(layout_a, view_a);

        view1.updateView();
        fullscrnView.updateView();
    }

    public void BubbleSortSubView(VideoView view) {
        if (view.index + 1 == mLocalRender.index) {
            SwitchViewPosition(mLocalRender, view);
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (view.index + 1 == render.index) {
                    SwitchViewPosition(render, view);
                    break;
                }
            }
        }
        if (view.index < mRemoteRenders.size()) {
            BubbleSortSubView(view);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int startX = (int) event.getX();
            int startY = (int) event.getY();
            if (mLocalRender.Hited(startX, startY)) {
                return true;
            } else {
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();
                    String peerId = entry.getKey();
                    VideoView render = entry.getValue();
                    if (render.Hited(startX, startY)) {
                        return true;
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            int startX = (int) event.getX();
            int startY = (int) event.getY();
            if (mLocalRender.Hited(startX, startY)) {
                SwitchViewToFullscreen(mLocalRender, GetFullScreen());
                return true;
            } else {
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();
                    String peerId = entry.getKey();
                    VideoView render = entry.getValue();
                    if (render.Hited(startX, startY)) {
                        SwitchViewToFullscreen(render, GetFullScreen());
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * updateVideoView
     */
    private void updateVideoView() {

        int startPosition = (100 - SUB_WIDTH * mRemoteRenders.size()) / 2;
        int remotePosition;
        int index;
        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, VideoView> entry = iter.next();

            VideoView render = entry.getValue();
            if (render.Fullscreen()) {
                render = mLocalRender;
                index = mLocalRender.index;
            } else {
                index = render.index;
            }

            render.y = SUB_Y;

            remotePosition = startPosition + (index - 1) * SUB_WIDTH;
            //render.x = remotePosition;
            Log.e(TAG, "updateVideoView: remotePosition " + remotePosition + " startPosition " + startPosition +
                    " index " + index);

            if (!render.Fullscreen()) {
                render.x = remotePosition;
            } else {
                mLocalRender.x = remotePosition;
            }
            render.updateView();

            render.mLayout.setPosition(remotePosition, render.y, SUB_WIDTH, SUB_HEIGHT);
            render.mView.requestLayout();

        }

    }


    /**
     * MoveVideoView
     *
     * @param flag true   move
     *             <p>
     *             false   initial position
     */

    public void MoveVideoView(boolean flag) {

        if (flag) {
            if (mScreenHeight > mScreenWidth) {
                SUB_Y = 80;
                SUB_WIDTH = 20;
                SUB_HEIGHT = 18;

            } else {
                SUB_Y = 65;
                SUB_WIDTH = 20;
                SUB_HEIGHT = 25;
            }

        } else {
            if (mScreenHeight > mScreenWidth) {
                SUB_Y = 65;
                SUB_WIDTH = 20;
                SUB_HEIGHT = 18;

            } else {
                SUB_Y = 45;
                SUB_WIDTH = 20;
                SUB_HEIGHT = 25;
            }
        }

        int startPosition = (100 - SUB_WIDTH * mRemoteRenders.size()) / 2;
        int remotePosition;
        int index;
        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, VideoView> entry = iter.next();

            VideoView render = entry.getValue();
            if (render.Fullscreen()) {
                render = mLocalRender;
                index = mLocalRender.index;
            } else {
                index = render.index;
            }

            render.y = SUB_Y;

            remotePosition = startPosition + (index - 1) * SUB_WIDTH;
            //render.x = remotePosition;
            Log.e(TAG, "updateVideoView: remotePosition " + remotePosition + " startPosition " + startPosition +
                    " index " + index);

            render.updateView();

            render.mLayout.setPosition(remotePosition, render.y, SUB_WIDTH, SUB_HEIGHT);
            render.mView.requestLayout();

        }

    }


    /**
     * updateMediaImage
     *
     * @param publishId
     * @param flag
     */

    public void updateRemoteVoiceImage(String publishId, boolean flag) {

        if (mRemoteRenders.containsKey(publishId)) {
            VideoView videoView = mRemoteRenders.get(publishId);
            videoView.mVoiceShowFalg = !flag;
            videoView.updateView();
        }

    }

    /**
     * updateLocalMediaImage
     *
     * @param flag
     */

    public void updateLocalVoiceImage(boolean flag) {
        mLocalRender.mVoiceShowFalg = !flag;
        mLocalRender.updateView();
    }


    /**
     * updateRemoteVideoImage
     *
     * @param publishId
     * @param flag
     */
    public void updateRemoteVideoImage(String publishId, boolean flag) {

        if (mRemoteRenders.containsKey(publishId)) {
            VideoView videoView = mRemoteRenders.get(publishId);
            videoView.mVideoShowFalg = !flag;
            videoView.updateView();
        }

    }

    /**
     * updateLocalVideoImage
     *
     * @param flag
     */

    public void updateLocalVideoImage(boolean flag) {
        mLocalRender.mVideoShowFalg = !flag;
        mLocalRender.updateView();
    }


    /**
     * updateImageFlag
     */
    private void updateImageFlag() {
        Log.e(TAG, "updateImageFlag: ");
        Iterator<Map.Entry<String, Boolean>> iterator = mVideoSetting.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Boolean> entry = iterator.next();
            String publishId = entry.getKey();
            Boolean videoFlag = entry.getValue();
            updateRemoteVideoImage(publishId, videoFlag);
        }


        iterator = mVoiceSetting.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Boolean> entry = iterator.next();
            String publishId = entry.getKey();
            Boolean voiceFlag = entry.getValue();
            updateRemoteVoiceImage(publishId, voiceFlag);
        }
    }


    /**
     * Landscape vertical screen  change
     */
    public void onScreenChanged() {
        if (mDebug)
            Log.e(TAG, "onScreenChanged: " + mRemoteRenders.size());
        mScreenWidth = ScreenUtils.getScreenWidth(mContent);
        mScreenHeight = ScreenUtils.getScreenHeight(mContent) - ScreenUtils.getStatusHeight(mContent);

        if (mScreenHeight > mScreenWidth) {

            SUB_Y = 65;
            SUB_WIDTH = 20;
            SUB_HEIGHT = 18;

/*            if(mRemoteRenders.size() == 1){
                SUB_WIDTH = 24;
                SUB_HEIGHT = 18;
            }else {

                SUB_WIDTH = 20;
                SUB_HEIGHT = 18;
            }*/

        } else {

            SUB_Y = 45;
            SUB_WIDTH = 20;
            SUB_HEIGHT = 25;

        }


        updateVideoView();
    }

    /**
     * Implements for AnyRTCViewEvents.
     */
    @Override
    public EglBase GetEglBase() {
        return mRootEglBase;
    }

    @Override
    public void OnRtcOpenRemoteRender(String peerId, VideoTrack remoteTrack) {
        VideoView remoteRender = mRemoteRenders.get(peerId);
        if (remoteRender == null) {
           /* int size = mRemoteRenders.size() + 1;
            remoteRender = new VideoView(peerId,  mVideoView.getContext(), size, ( 100 - size* (SUB_WIDTH + SUB_X)), SUB_Y, SUB_WIDTH, SUB_HEIGHT);*/
            int size = mRemoteRenders.size() + 1;
            int startPosition = (100 - SUB_WIDTH * size) / 2;
            int remotePosition = startPosition + (size - 1) * SUB_WIDTH;
            remoteRender = new VideoView(peerId, mVideoView.getContext(), size, remotePosition, SUB_Y, SUB_WIDTH, SUB_HEIGHT);

            remoteRender.mView = new SurfaceViewRenderer(mVideoView.getContext());
            remoteRender.mView.init(mRootEglBase.getEglBaseContext(), null);
            remoteRender.mView.setZOrderMediaOverlay(true);
            remoteRender.mView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            remoteRender.mLayout.addView(remoteRender.mView);
            mVideoView.addView(remoteRender.mLayout, 0);

            remoteRender.mLayout.setPosition(remoteRender.x, remoteRender.y, remoteRender.w, remoteRender.h);

            remoteRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            remoteRender.mRenderer = new VideoRenderer(remoteRender.mView);

            remoteRender.mVideoTrack = remoteTrack;
            remoteRender.mVideoTrack.addRenderer(remoteRender.mRenderer);
            mRemoteRenders.put(peerId, remoteRender);
            remoteRender.createImageView();
            updateVideoView();
            updateImageFlag();

            if (mRemoteRenders.size() == 1) {
                SwitchViewToFullscreen(remoteRender, mLocalRender);
            }

            mVideoViewPeopleNumEvent.OnPeopleNumChange(mRemoteRenders.size());
        }
    }

    @Override
    public void OnRtcRemoveRemoteRender(String peerId) {
        VideoView remoteRender = mRemoteRenders.get(peerId);
        if (remoteRender != null) {
            remoteRender.mVideoTrack = null;
            if (remoteRender.Fullscreen()) {
                SwitchIndex1ToFullscreen(remoteRender);
            }
            if (remoteRender.index < mRemoteRenders.size()) {
                BubbleSortSubView(remoteRender);
            }

            if (remoteRender.mView != null) {
                remoteRender.mView.release();
                remoteRender.mView = null;
                remoteRender.mRenderer = null;
            }
            mVideoView.removeView(remoteRender.mLayout);
            mVideoView.removeView(remoteRender.mVideoImageView);
            mVideoView.removeView(remoteRender.mVoiceImageView);


            remoteRender.mVideoImageView = null;
            remoteRender.mVoiceImageView = null;
            remoteRender.mLayout = null;

            mRemoteRenders.remove(peerId);
            updateVideoView();

            mVideoViewPeopleNumEvent.OnPeopleNumChange(mRemoteRenders.size());
            Log.e(TAG, "OnRtcRemoveRemoteRender: ");

        }
    }

    @Override
    public void OnRtcOpenLocalRender(VideoTrack localTrack) {
        mLocalRender.mVideoTrack = localTrack;
        if (mLocalRender.mView == null) {
            mLocalRender.mView = new SurfaceViewRenderer(mVideoView.getContext());
            mLocalRender.mView.init(mRootEglBase.getEglBaseContext(), null);
            mLocalRender.mView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mLocalRender.mLayout.addView(mLocalRender.mView);
            mVideoView.addView(mLocalRender.mLayout);

            mLocalRender.mLayout.setPosition(mLocalRender.x, mLocalRender.y, mLocalRender.w, mLocalRender.h);
            mLocalRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            mLocalRender.mRenderer = new VideoRenderer(mLocalRender.mView);
        }
        mLocalRender.mVideoTrack.addRenderer(mLocalRender.mRenderer);
        mLocalRender.createImageView();

    }

    @Override
    public void OnRtcRemoveLocalRender() {
        if (mLocalRender.mVideoTrack != null) {
            mLocalRender.mVideoTrack = null;
            mLocalRender.mView.release();
            mLocalRender.mView = null;
            mLocalRender.mRenderer = null;

        }
        mVideoView.removeView(mLocalRender.mLayout);
        mVideoView.removeView(mLocalRender.mVideoImageView);
        mVideoView.removeView(mLocalRender.mVoiceImageView);
        mLocalRender.mVideoImageView = null;
        mLocalRender.mVoiceImageView = null;
        mLocalRender.mLayout = null;
        mVideoView = null;
        Log.e(TAG, "OnRtcRemoveLocalRender: ");
    }

    @Override
    public void OnRtcRemoteAVStatus(String peerId, boolean audioEnable, boolean videoEnable) {
        if (mDebug)
            Log.e(TAG, "onRtcRemoteAVStatus: publishId " + peerId + " audioEnable " + audioEnable + " videoEnable " + videoEnable);
        updateRemoteVoiceImage(peerId, audioEnable);
        updateRemoteVideoImage(peerId, videoEnable);
        mVoiceSetting.put(peerId, audioEnable);
        mVideoSetting.put(peerId, videoEnable);
    }
}
