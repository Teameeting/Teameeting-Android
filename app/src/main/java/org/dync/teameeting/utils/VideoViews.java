//package org.dync.teameeting.utils;
//
//import android.app.Activity;
//import android.opengl.GLSurfaceView;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import org.anyrtc.util.AppRTCUtils;
//import org.dync.teameeting.R;
//import org.dync.teameeting.TeamMeetingApp;
//import org.webrtc.RendererCommon;
//import org.webrtc.VideoRenderer;
//import org.webrtc.VideoRendererGui;
//import org.webrtc.VideoTrack;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
///**
// * Created by Eric on 2016/1/5.
// */
//public class VideoViews implements View.OnTouchListener{
//    private static final boolean mDebug = TeamMeetingApp.mIsDebug;
//    private static final String TAG = "VideoViews";
//    private  int SUB_X= 5;
//    private  int SUB_Y = 65;
//    private  static int SUB_WIDTH = 18;
//    private  static int SUB_HEIGHT = 16;
//
//    //private VideoViewEvent mEvents;
//    private GLSurfaceView mVideoView;
//    public VideoView mLocalRender;
//    private HashMap<String, VideoView> mRemoteRenders;
//    private static RelativeLayout mParentLayout;
//    private static ImageView mVoiceClose;
//    private static ImageView mVideoClose;
//    private static Activity mContent;
//    private static int    mScreenWidth  ;
//    private static int    mScreenHeight ;
//
//    public interface VideoViewEvent {
//        void OnScreenSwitch(String strBeforeFullScrnId, String strNowFullScrnId);
//    }
//
//    protected static class VideoView {
//        public String strPeerId;
//        public int index;
//        public int x;
//        public int y;
//        public int w;
//        public int h;
//        public VideoTrack mVideoTrack = null;
//        public VideoRenderer mRenderer = null;
//        public VideoRenderer.Callbacks mCallback = null;
//
//        private boolean  mVoiceShowFalg = false;
//        private boolean  mVideoShowFalg = false;
//        private ImageView mVoiceView ;
//        private ImageView mVideoView ;
//        private int width = mScreenWidth*SUB_WIDTH/(100*3);
//        private int height = mScreenHeight*SUB_HEIGHT/(100*3);
//
//        public VideoView(String strPeerId, int index, int x, int y, int w, int h) {
//            this.strPeerId = strPeerId;
//            this.index = index;
//            this.x = x;
//            this.y = y;
//            this.w = w;
//            this.h = h;
//
//        }
//        public Boolean Fullscreen() {
//            return w == 100 || h == 100;
//        }
//        public Boolean Hited(int px, int py) {
//            if(!Fullscreen()) {
//                int left = x * mScreenWidth / 100;
//                int top = y * mScreenHeight / 100;
//                int right = (x + w) * mScreenWidth / 100;
//                int bottom = (y + h) * mScreenHeight / 100;
//                if(mDebug)
//                Log.e(TAG, "Hited: "+" left "+left+" top "+top+" right "+right +" bottom "+bottom+" px "+px+" py "+py);
//                if((px >= left && px <= right) && (py >= top && py<= bottom)) {
//                    return true;
//                }
//            }
//            return false;
//        }
//        private void updateRender(VideoRenderer.Callbacks callbacks) {
//            AppRTCUtils.assertIsTrue(mVideoTrack != null);
//            mVideoTrack.removeRenderer(mRenderer);
//            mCallback = callbacks;
//            mRenderer = new VideoRenderer(mCallback);
//            mVideoTrack.addRenderer(mRenderer);
//        }
//
//        private void showVoiceView(){
//            mVoiceView = new ImageView(mContent);
//            mVoiceView.setImageResource(R.drawable.small_mic_muted);
//            mVoiceView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            ViewGroup.LayoutParams layoutParamsVoice = new RelativeLayout.LayoutParams(width,height);
//            mParentLayout.addView(mVoiceView,1,layoutParamsVoice);
//            mVoiceView.setVisibility(View.GONE);
//
//            mVideoView = new ImageView(mContent);
//            mVideoView.setImageResource(R.drawable.video_close_small);
//            mVideoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            ViewGroup.LayoutParams layoutParamsVideo = new RelativeLayout.LayoutParams(width,height);
//            mParentLayout.addView(mVideoView,1,layoutParamsVideo);
//            mVideoView.setVisibility(View.GONE);
//
//        }
//
//        private void updateView(){
//            if(mVoiceShowFalg) {
//                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mVoiceView.getLayoutParams();
//                if (Fullscreen()) {
//                    marginLayoutParams.leftMargin = mScreenWidth/2 - width;
//                    marginLayoutParams.topMargin = mScreenHeight/2 - height;
//                    // mVoiceView.setVisibility(View.GONE);
//                    mVoiceClose.setVisibility(View.VISIBLE);
//                    mVoiceView.setVisibility(View.GONE);
//                } else {
//                    marginLayoutParams.leftMargin = mScreenWidth * (x + SUB_WIDTH) / 100 - width;
//                    marginLayoutParams.topMargin = mScreenHeight * y / 100;
//                    //  mVoiceClose.setVisibility(View.GONE);
//                    mVoiceView.setVisibility(View.VISIBLE);
//                    if(mDebug){
//                        Log.e(TAG, "updateView: "+" marginLayoutParams.leftMargin "+marginLayoutParams.leftMargin+" marginLayoutParams.topMargin "+marginLayoutParams.topMargin );
//                    }
//                }
//
//                mVoiceView.setLayoutParams(marginLayoutParams);
//            }else{
//                if (Fullscreen()) {
//                    mVoiceClose.setVisibility(View.GONE);
//                }else{
//                    mVoiceView.setVisibility(View.GONE);
//                }
//
//            }
//
//
//            if(mVideoShowFalg){
//
//                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mVideoView.getLayoutParams();
//                if (Fullscreen()) {
//                    marginLayoutParams.leftMargin = mScreenWidth - width;
//                    marginLayoutParams.topMargin = 0;
//                    // mVoiceView.setVisibility(View.GONE);
//                    mVideoClose.setVisibility(View.VISIBLE);
//                    mVideoView.setVisibility(View.GONE);
//                } else {
//                    marginLayoutParams.leftMargin = mScreenWidth * (x + SUB_WIDTH/2) / 100 - width/2;
//                    marginLayoutParams.topMargin = mScreenHeight * (y +SUB_HEIGHT/2)/ 100-height/2;
//                    //  mVoiceClose.setVisibility(View.GONE);
//                    mVideoView.setVisibility(View.VISIBLE);
//                    if(mDebug){
//                        Log.e(TAG, "updateView: "+" marginLayoutParams.leftMargin "+marginLayoutParams.leftMargin+" marginLayoutParams.topMargin "+marginLayoutParams.topMargin );
//                    }
//                }
//
//                mVideoView.setLayoutParams(marginLayoutParams);
//            }else{
//                if (Fullscreen()) {
//                    mVideoClose.setVisibility(View.GONE);
//                }else{
//                    mVideoView.setVisibility(View.GONE);
//                }
//            }
//        }
//    }
//
//
//
///*    public VideoViews(VideoViewEvent evnets, GLSurfaceView videoView) {
//        mEvents = evnets;
//        mVideoView = videoView;
//        mVideoView.setOnTouchListener(this);
//        mRemoteRenders = new HashMap<>();
//        mLocalRender = new VideoView("localRender", 0, 0, 0, 100, 100);
//    }*/
//
//    public VideoViews(GLSurfaceView videoView, RelativeLayout parentLayout ,ImageView voiceClose,ImageView videoClose, Activity content ) {
//        //mEvents = evnets;
//        mContent = content;
//        mParentLayout = parentLayout;
//        mVoiceClose = voiceClose;
//        mVideoClose = videoClose;
//        mVideoView = videoView;
//        mScreenWidth =ScreenUtils.getScreenWidth(mContent);
//        mScreenHeight =ScreenUtils.getScreenHeight(mContent)-ScreenUtils.getStatusHeight(mContent);
//        mVideoView.setOnTouchListener(this);
//        mRemoteRenders = new HashMap<>();
//        mLocalRender = new VideoView("localRender", 0, 0, 0, 100, 100);
//
//    }
//
//    public void OpenLocalRender(VideoTrack track) {
//        mLocalRender.mVideoTrack = track;
//        if(mLocalRender.mRenderer == null) {
//            mLocalRender.mCallback = VideoRendererGui.create(mLocalRender.x, mLocalRender.y, mLocalRender.w, mLocalRender.h,
//                    RendererCommon.ScalingType.SCALE_ASPECT_FILL, false);
//            mLocalRender.mRenderer = new VideoRenderer(mLocalRender.mCallback);
//        }
//        mLocalRender.mVideoTrack.addRenderer(mLocalRender.mRenderer);
//        mLocalRender.showVoiceView();
//
//    }
//
//    public VideoTrack LocalVideoTrack() {
//        return mLocalRender.mVideoTrack;
//    }
//
//    public void CloseLocalRender() {
//        if(mLocalRender.mVideoTrack != null) {
//            VideoRendererGui.remove(mLocalRender.mCallback);
//            mLocalRender.mCallback = null;
//            mLocalRender.mRenderer = null;
//            mLocalRender.mVideoTrack = null;
//            mParentLayout.removeView(mLocalRender.mVoiceView);
//            mParentLayout.removeView(mLocalRender.mVideoView);
//        }
//    }
//
//    public void OpenRemoteRender(String peerId, VideoTrack track) {
//
//        if(mRemoteRenders.size() == 0){
//            SUB_WIDTH = 24;
//            SUB_HEIGHT = 18;
//        }else {
//            SUB_WIDTH = 18;
//            SUB_HEIGHT = 16;
//        }
//
//        VideoView remoteRender = mRemoteRenders.get(peerId);
//        if(remoteRender == null) {
//            int size = mRemoteRenders.size() + 1;
//            remoteRender = new VideoView(peerId, size, ( 100 - size* (SUB_WIDTH + SUB_X)), SUB_Y, SUB_WIDTH, SUB_HEIGHT);
//            remoteRender.mCallback = VideoRendererGui.create(remoteRender.x, remoteRender.y, remoteRender.w, remoteRender.h,
//                    RendererCommon.ScalingType.SCALE_ASPECT_FILL, false);
//            remoteRender.mRenderer = new VideoRenderer(remoteRender.mCallback);
//            remoteRender.mVideoTrack = track;
//            remoteRender.mVideoTrack.addRenderer(remoteRender.mRenderer);
//            mRemoteRenders.put(peerId, remoteRender);
//            remoteRender.showVoiceView();
//            updateVideoView();
//            if(mRemoteRenders.size() == 1) {
//                SwitchViewToFullscreen(remoteRender, mLocalRender);
//            }
//
//        }
//
//
//    }
//
//    public void RemoveRemoteRender(String peerId) {
//        VideoView remoteRender = mRemoteRenders.get(peerId);
//        if(remoteRender != null) {
//            if(remoteRender.Fullscreen()) {
//                SwitchIndex1ToFullscreen(remoteRender);
//            }
//            if(remoteRender.index < mRemoteRenders.size()) {
//                BubbleSortSubView(remoteRender);
//            }
//            if(remoteRender.mVideoTrack != null) {
//                VideoRendererGui.remove(remoteRender.mCallback);
//                remoteRender.mCallback = null;
//                remoteRender.mRenderer = null;
//                remoteRender.mVideoTrack = null;
//                mParentLayout.removeView(remoteRender.mVoiceView);
//                mParentLayout.removeView(remoteRender.mVideoView);
//            }
//            mRemoteRenders.remove(peerId);
//            updateVideoView();
//        }
//    }
//
//    private void SwitchIndex1ToFullscreen(VideoView fullscrnView) {
//        AppRTCUtils.assertIsTrue(fullscrnView != null);
//        VideoView view1 = null;
//        if(mLocalRender.index == 1) {
//            view1 = mLocalRender;
//        } else {
//            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry<String, VideoView> entry = iter.next();
//                VideoView render = entry.getValue();
//                if(render.index == 1)
//                {
//                    view1 = render;
//                    break;
//                }
//            }
//        }
//        SwitchViewPosition(view1, fullscrnView);
//        //mEvents.OnScreenSwitch(fullscrnView.strPeerId, view1.strPeerId);
//    }
//
//    private VideoView GetFullScreen() {
//        if(mLocalRender.Fullscreen())
//            return mLocalRender;
//        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry<String, VideoView> entry = iter.next();
//            String peerId = entry.getKey();
//            VideoView render = entry.getValue();
//            if(render.Fullscreen())
//                return render;
//        }
//        return null;
//    }
//
//    private void SwitchViewPosition(VideoView view1, VideoView view2) {
//        AppRTCUtils.assertIsTrue(view1 != null && view2 != null);
//        int index, x, y, w, h;
//        index = view1.index;
//        x = view1.x;
//        y = view1.y;
//        w = view1.w;
//        h = view1.h;
//        view1.index = view2.index;
//        view1.x = view2.x;
//        view1.y = view2.y;
//        view1.w = view2.w;
//        view1.h = view2.h;
//
//        view2.index = index;
//        view2.x = x;
//        view2.y = y;
//        view2.w = w;
//        view2.h = h;
//
//        VideoRenderer.Callbacks callback1 = view1.mCallback;
//        VideoRenderer.Callbacks callback2 = view2.mCallback;
//        view1.updateRender(callback2);
//        view2.mCallback = callback1;
//        view1.updateView();
//        view2.updateView();
//    }
//
//    private void SwitchViewToFullscreen(VideoView view1, VideoView fullscrnView) {
//        AppRTCUtils.assertIsTrue(view1 != null && fullscrnView != null);
//        VideoView v1 = view1;
//        VideoView v2 = fullscrnView;
//        int index, x, y, w, h;
//        index = view1.index;
//        x = view1.x;
//        y = view1.y;
//        w = view1.w;
//        h = view1.h;
//        view1.index = fullscrnView.index;
//        view1.x = fullscrnView.x;
//        view1.y = fullscrnView.y;
//        view1.w = fullscrnView.w;
//        view1.h = fullscrnView.h;
//
//        fullscrnView.index = index;
//        fullscrnView.x = x;
//        fullscrnView.y = y;
//        fullscrnView.w = w;
//        fullscrnView.h = h;
//
//        VideoRenderer.Callbacks callback1 = view1.mCallback;
//        VideoRenderer.Callbacks callback2 = fullscrnView.mCallback;
//        view1.updateRender(callback2);
//        fullscrnView.updateRender(callback1);
//        //mEvents.OnScreenSwitch(fullscrnView.strPeerId, view1.strPeerId);
//
//
//        v2.updateView();
//        v1.updateView();
//
//    }
//
//    public void BubbleSortSubView(VideoView view) {
//        if(view.index + 1 == mLocalRender.index) {
//            SwitchViewPosition(mLocalRender, view);
//        } else {
//            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry<String, VideoView> entry = iter.next();
//                VideoView render = entry.getValue();
//                if(view.index + 1 == render.index) {
//                    SwitchViewPosition(render, view);
//                    break;
//                }
//            }
//        }
//        if(view.index < mRemoteRenders.size()) {
//            BubbleSortSubView(view);
//        }
//    }
//
//    /**
//     * Landscape vertical screen  change
//     */
//    public void onScreenChanged(){
//        if(mDebug)
//            Log.e(TAG, "onScreenChanged: " +mRemoteRenders.size() );
//        mScreenWidth =ScreenUtils.getScreenWidth(mContent);
//        mScreenHeight =ScreenUtils.getScreenHeight(mContent)-ScreenUtils.getStatusHeight(mContent);
//
//        if(mScreenHeight>mScreenWidth){
//
//            SUB_Y = 65;
//
//            if(mRemoteRenders.size() == 1){
//                SUB_WIDTH = 24;
//                SUB_HEIGHT = 18;
//            }else {
//
//                SUB_WIDTH = 18;
//                SUB_HEIGHT = 16;
//            }
//
//
//
//        }else{
//
//            SUB_Y = 45;
//            SUB_WIDTH = 18;
//            SUB_HEIGHT = 25;
//
//        }
//
//
//        updateVideoView();
//    }
//
//    /**
//     * updateMediaImage
//     *
//     * @param publishId
//     * @param flag
//     */
//
//    public void updateRemoteVoiceImage(String publishId,boolean flag){
//
//        if(mRemoteRenders.containsKey(publishId)) {
//            VideoView videoView = mRemoteRenders.get(publishId);
//            videoView.mVoiceShowFalg = !flag;
//            videoView.updateView();
//        }
//
//    }
//
//    /**
//     *updateLocalMediaImage
//     * @param flag
//     */
//
//    public void updateLocalVoiceImage(boolean flag){
//        mLocalRender.mVoiceShowFalg = !flag;
//        mLocalRender.updateView();
//    }
//
//
//    /**
//     * updateRemoteVideoImage
//     * @param publishId
//     * @param flag
//     */
//    public void updateRemoteVideoImage(String publishId,boolean flag){
//
//        if(mRemoteRenders.containsKey(publishId)) {
//            VideoView videoView = mRemoteRenders.get(publishId);
//            videoView.mVideoShowFalg = !flag;
//            videoView.updateView();
//        }
//
//    }
//
//    /**
//     *updateLocalVideoImage
//     * @param flag
//     */
//
//    public void updateLocalVideoImage(boolean flag){
//        mLocalRender.mVideoShowFalg = !flag;
//        mLocalRender.updateView();
//    }
//
//    /**
//     * updateVideoView
//     */
//    private void updateVideoView(){
//
//        int startPosition = (100-SUB_WIDTH*mRemoteRenders.size())/2;
//        int remotePosition ;
//        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry<String, VideoView> entry = iter.next();
//
//            VideoView render = entry.getValue();
//            if(render.Fullscreen()){
//                render = mLocalRender;
//            }
//
//            render.y = SUB_Y;
//
//            remotePosition = startPosition+(render.index-1)*SUB_WIDTH;
//            render.x = remotePosition;
//            VideoRendererGui.update(render.mCallback,remotePosition,SUB_Y,SUB_WIDTH,SUB_HEIGHT,
//                    RendererCommon.ScalingType.SCALE_ASPECT_FILL,false);
//            render.updateView();
//            if(mDebug)
//                Log.e(TAG, "updateVideoView: remotePosition "+remotePosition );
//        }
//
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if(event.getAction() == MotionEvent.ACTION_DOWN) {
//            int startX = (int) event.getX();
//            int startY = (int) event.getY();
//            if(mLocalRender.Hited(startX, startY)) {
//                return true;
//            } else {
//                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry<String, VideoView> entry = iter.next();
//                    String peerId = entry.getKey();
//                    VideoView render = entry.getValue();
//                    if(render.Hited(startX, startY)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        else if (event.getAction() == MotionEvent.ACTION_UP) {
//            int startX = (int) event.getX();
//            int startY = (int) event.getY();
//            if(mLocalRender.Hited(startX, startY)) {
//
//                SwitchViewToFullscreen(mLocalRender, GetFullScreen());
//
//                return true;
//            } else {
//                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry<String, VideoView> entry = iter.next();
//                    String peerId = entry.getKey();
//                    VideoView render = entry.getValue();
//                    if(render.Hited(startX, startY)) {
//                        SwitchViewToFullscreen(render, GetFullScreen());
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//}
