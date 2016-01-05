package org.dync.rtk.util;

import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

/**
 * 每一路视频显示封装类
 * 
 * By Dync.inc - 2015/7/20
 * 
 * @author Maozongwu
 *
 */
public class RtkVideoRender {
	private int mScrnIndex;
	private String mPeerId;
	private VideoRenderer mVideoRenderer;
	private VideoRenderer.Callbacks mCallbacks;
	private VideoTrack mVideoTrack;

	public RtkVideoRender(String peerId, VideoRenderer.Callbacks callbacks, VideoRenderer renderer) {
		// TODO Auto-generated constructor stub
		setScrnIndex(0);
		this.mPeerId = peerId;
		this.mCallbacks = callbacks;
		if (null == renderer) {
			mVideoRenderer = new VideoRenderer(mCallbacks);
		} else {
			mVideoRenderer = renderer;
		}
	}

	public int getScrnIndex() {
		return mScrnIndex;
	}

	public void setScrnIndex(int mScrnIndex) {
		this.mScrnIndex = mScrnIndex;
	}

	public void setVideoTrack(VideoTrack videoTrack) {
		mVideoTrack = videoTrack;
	}

	public void close() {
		if (mVideoRenderer != null) {
			mVideoRenderer.dispose();
			mVideoRenderer = null;
		}
		mVideoTrack = null;
		mCallbacks = null;
		mPeerId = null;
	}

	public String getPeerId() {
		return mPeerId;
	}

	public VideoRenderer getVideoRenderer() {
		return mVideoRenderer;
	}

	public VideoRenderer.Callbacks getCallbacks() {
		return mCallbacks;
	}

	public void setCallbacks(VideoRenderer.Callbacks callbacks) {
		updateRender(callbacks);
	}

	private void updateRender(VideoRenderer.Callbacks callbacks) {
//		RtkUtils.assertIsTrue(mVideoTrack != null);
		if (null != mVideoTrack) {
			mVideoTrack.removeRenderer(mVideoRenderer);
			mCallbacks = callbacks;
			mVideoRenderer = new VideoRenderer(mCallbacks);
			mVideoTrack.addRenderer(mVideoRenderer);
		}	
	}

	public static void switchCallbacks(RtkVideoRender render1, RtkVideoRender render2) {
		RtkUtils.assertIsTrue(render1 != null && render2 != null);

		VideoRenderer.Callbacks callback1 = render1.getCallbacks();
		VideoRenderer.Callbacks callback2 = render2.getCallbacks();
		render1.updateRender(callback2);
		render2.updateRender(callback1);
	}
}
