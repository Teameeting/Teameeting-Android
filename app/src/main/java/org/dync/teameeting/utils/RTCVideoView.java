package org.dync.teameeting.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.anyrtc.meet_kit.RTCViewHelper;
import org.dync.teameeting.R;
import org.webrtc.EglBase;
import org.webrtc.PercentFrameLayout;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Eric on 2016/7/26.
 */
public class RTCVideoView implements RTCViewHelper {
    private static final String TAG = "RTCVideoView";
    private static Context mContent;
    private static int SUB_X = 72;
    private static int SUB_Y = 2;
    private static int SUB_WIDTH = 20;
    private static int SUB_HEIGHT = 18;

    private static int mScreenWidth;
    private static int mScreenHeight;
    private static ImageView mVoiceClose;
    private static ImageView mVideoClose;
    private HashMap<String, Boolean> mVoiceSetting = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> mVideoSetting = new HashMap<String, Boolean>();
    private VideoViewPeopleEvent mVideoViewPeopleEvent;

    public interface VideoViewPeopleEvent {
        void OnPeopleNumChange(int peopleNum);
    }

    /**
     * 人数改变回掉监听
     *
     * @param videoViewPeopleEvent
     */
    public void setVideoViewPeopleNumEvent(VideoViewPeopleEvent videoViewPeopleEvent) {
        mVideoViewPeopleEvent = videoViewPeopleEvent;
    }

    protected static class VideoView {
        public String strPeerId;
        public int index;
        public int x;
        public int y;
        public int w;
        public int h;
        public PercentFrameLayout mLayout = null;
        public SurfaceViewRenderer mView = null;
        public VideoRenderer mRenderer = null;
        private RelativeLayout layoutCamera = null;

        private boolean mVoiceShowFalg = false;
        private boolean mVideoShowFalg = false;
        private ImageView mVoiceImageView;
        private ImageView mVideoImageView;
        private int width = mScreenWidth * SUB_WIDTH / (100 * 3);
        private int height = mScreenHeight * SUB_HEIGHT / (100 * 3);


        public VideoView(String strPeerId, Context ctx, EglBase eglBase, int index, int x, int y, int w, int h) {
            this.strPeerId = strPeerId;
            this.index = index;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

            mLayout = new PercentFrameLayout(ctx);
            mLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            View view = View.inflate(ctx, R.layout.layout_top_right, null);

            mView = (SurfaceViewRenderer) view.findViewById(R.id.suface_view);
            layoutCamera = (RelativeLayout) view.findViewById(R.id.layout_camera);
            mView.init(eglBase.getEglBaseContext(), null);
            mView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mLayout.addView(view);
        }

        public Boolean Fullscreen() {
            return w == 100 || h == 100;
        }

        public void close() {
            mLayout.removeView(mView);
            mView.release();
            mView = null;
            mRenderer = null;
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
                    Log.i(TAG, "updateView: Fullscreen ");
                } else {
                    marginLayoutParams.leftMargin = mScreenWidth * (x + SUB_WIDTH) / 100 - width;
                    marginLayoutParams.topMargin = mScreenHeight * y / 100;
                    mVoiceImageView.setVisibility(View.VISIBLE);
                    Log.i(TAG, "updateView: " + " marginLayoutParams.leftMargin " + marginLayoutParams.leftMargin + " marginLayoutParams.topMargin " + marginLayoutParams.topMargin);
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
                    Log.i(TAG, "updateView: " + " marginLayoutParams.leftMargin " + marginLayoutParams.leftMargin + " marginLayoutParams.topMargin " + marginLayoutParams.topMargin);
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
    private boolean mAutoLayout;
    private EglBase mRootEglBase;
    private static RelativeLayout mVideoView;
    private VideoView mLocalRender;

    private HashMap<String, VideoView> mRemoteRenders;

    public RTCVideoView(RelativeLayout videoView, Context ctx, EglBase eglBase, ImageView closeVoice, ImageView closeVideo) {
        mAutoLayout = false;
        mVideoView = videoView;
        mContent = ctx;
        mRootEglBase = eglBase;
        mVoiceClose = closeVoice;//话筒控制
        mVideoClose = closeVideo;//视屏前后控制
        mLocalRender = null;
        mRemoteRenders = new HashMap<>();
    }

    private int GetVideoRenderSize() {
        int size = mRemoteRenders.size();
        if (mLocalRender != null) {
            size += 1;
        }
        return size;
    }

    private void SwitchViewPosition(VideoView view1, VideoView view2) {
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

        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);
        view2.mLayout.setPosition(view2.x, view2.y, view2.w, view2.h);
        mVideoView.updateViewLayout(view1.mLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mVideoView.updateViewLayout(view2.mLayout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    public void BubbleSortSubView(VideoView view) {
        if (mLocalRender != null && view.index + 1 == mLocalRender.index) {
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

    public VideoRenderer getLocalVideoRenderer(){
        return mLocalRender.mRenderer;
    }

    public void OnRTCAVStatus(String peerId, boolean audioEnable, boolean videoEnable){
        updateRemoteVoiceImage(peerId, audioEnable);
        updateRemoteVideoImage(peerId, videoEnable);
        mVoiceSetting.put(peerId, audioEnable);
        mVideoSetting.put(peerId, videoEnable);
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
        Log.i(TAG, "onScreenChanged: " + mRemoteRenders.size());
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
    public VideoRenderer OnRtcOpenLocalRender() {
        int size = GetVideoRenderSize();
        if (size == 0) {
            mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100);
        } else {
            mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, size, SUB_X, (100 - size * (SUB_HEIGHT + SUB_Y)), SUB_WIDTH, SUB_HEIGHT);
            mLocalRender.mView.setZOrderMediaOverlay(true);
        }
        mVideoView.addView(mLocalRender.mLayout);

        mLocalRender.mLayout.setPosition(
                mLocalRender.x, mLocalRender.y, mLocalRender.w, mLocalRender.h);
        mLocalRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        mLocalRender.mRenderer = new VideoRenderer(mLocalRender.mView);
        mLocalRender.createImageView();
        return mLocalRender.mRenderer;
    }

    @Override
    public void OnRtcRemoveLocalRender() {
        if (mLocalRender != null) {
            mLocalRender.close();
            mLocalRender.mRenderer = null;

            mVideoView.removeView(mLocalRender.mLayout);
            mVideoView.removeView(mLocalRender.mVideoImageView);
            mVideoView.removeView(mLocalRender.mVoiceImageView);
            mLocalRender.mVideoImageView = null;
            mLocalRender.mVoiceImageView = null;
            mLocalRender.mLayout = null;
            mLocalRender = null;
            mVideoView = null;
        }
    }

    @Override
    public VideoRenderer OnRtcOpenRemoteRender(final String strRtcPeerId) {
        VideoView remoteRender = mRemoteRenders.get(strRtcPeerId);
        if (remoteRender == null) {
            int size = GetVideoRenderSize();
            if (size == 0) {
                remoteRender = new VideoView(strRtcPeerId, mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100);
            } else {
                remoteRender = new VideoView(strRtcPeerId, mVideoView.getContext(), mRootEglBase, size, SUB_X, (100 - size * (SUB_HEIGHT + SUB_Y)), SUB_WIDTH, SUB_HEIGHT);
                remoteRender.mView.setZOrderMediaOverlay(true);
            }

            mVideoView.addView(remoteRender.mLayout);

            remoteRender.mLayout.setPosition(
                    remoteRender.x, remoteRender.y, remoteRender.w, remoteRender.h);
            remoteRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            remoteRender.mRenderer = new VideoRenderer(remoteRender.mView);

            mRemoteRenders.put(strRtcPeerId, remoteRender);
            remoteRender.createImageView();
            updateVideoView();
            updateImageFlag();
            if (mVideoViewPeopleEvent != null) {
                mVideoViewPeopleEvent.OnPeopleNumChange(mRemoteRenders.size());
            }
        }
        return remoteRender.mRenderer;
    }

    @Override
    public void OnRtcRemoveRemoteRender(String peerId) {
        VideoView remoteRender = mRemoteRenders.get(peerId);
        if (remoteRender != null) {
            if (mRemoteRenders.size() > 1 && remoteRender.index <= mRemoteRenders.size()) {
                BubbleSortSubView(remoteRender);
            }
            remoteRender.close();
            mVideoView.removeView(remoteRender.mLayout);
            mVideoView.removeView(remoteRender.mVideoImageView);
            mVideoView.removeView(remoteRender.mVoiceImageView);

            remoteRender.mVideoImageView = null;
            remoteRender.mVoiceImageView = null;
            remoteRender.mLayout = null;

            mRemoteRenders.remove(peerId);
            updateVideoView();

            if (mVideoViewPeopleEvent != null) {
                mVideoViewPeopleEvent.OnPeopleNumChange(mRemoteRenders.size());
            }
        }
    }

}
