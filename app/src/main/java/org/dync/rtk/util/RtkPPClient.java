package org.dync.rtk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

import org.dync.rtk.util.RtkPeerConnection.PeerConnectionParameters;
import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.IceCandidate;
import org.webrtc.MediaCodecVideoEncoder;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoCapturerAndroid.CameraErrorHandler;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.MediaConstraints.KeyValuePair;
import android.content.Context;
import android.opengl.EGLContext;
import android.util.Log;

public abstract class RtkPPClient implements RtkPeerConnection.PeerConnectionEvents {
	private final static String TAG = "RtkPPClient";
	public static final String VIDEO_TRACK_ID = "ARDAMSv0";
	public static final String AUDIO_TRACK_ID = "ARDAMSa0";
	public static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
	public static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
	public static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
	public static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
	public static final String MAX_VIDEO_RATIO_CONSTRAINT = "maxAspectRatio";
	public static final String MIN_VIDEO_RATIO_CONSTRAINT = "minAspectRatio";
	public static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
	public static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
	public static final int HD_VIDEO_WIDTH = 1280;
	public static final int HD_VIDEO_HEIGHT = 720;
	public static final int MIN_VIDEO_WIDTH = 352;
	public static final int MIN_VIDEO_HEIGHT = 288;
	public static final int MIN_VIDEO_FPS = 15;

	protected final LooperExecutor mExecutor;
	protected PeerConnectionFactory.Options mOptions = null;
	protected PeerConnectionFactory mFactory;
	protected MediaStream mLocalMediaStream;
	protected VideoSource mVideoSource;
	protected VideoCapturerAndroid mVideoCapturer;

	protected MediaConstraints mVideoConstraints;
	protected MediaConstraints mAudioConstraints;
	/* 本地图像显示 */
	protected VideoTrack mLocalVideoTrack;
	protected AudioTrack mLocalAudioTrack;

	private RtkPPClientEvents mEvents;
	protected PeerConnectionParameters mPeerConnectionParameters;

	private int mNumberOfCameras;
	private boolean bVideoCallEnabled;
	// enableVideo is set to true if video should be rendered and sent.
	private boolean bRenderVideo;
	// renderAudio is set to true default.
	private boolean bRenderAudio;
	// for application background.
	private boolean bVideoSourceStopped;
	/**
	 * SingleCall is set to false default.
	 */
	private boolean bSingleCall;

	protected List<PeerConnection.IceServer> mIceServers;
	protected List<PeerConnection.IceServer> mIceServersNull;

	/**
	 * Data channels events
	 */
	public static interface RtkPPClientEvents {
		public void onOutgoingMessage(String peerId, String message);

		public VideoRenderer onCreatPeerRender(String peerId);

		public void onCreateVideoTrack(String peerId, VideoTrack videoTrack);

		public void onUpdatePeerRender(String peerId);

		public void onRemovePeerRender(String peerId);

		public void onPeerDataChannelOpened(String peerId);

		public void onPeerDataChannelRecvMessage(String peerId, String message);

		public void onPeerDataChannelClosed(String peerId);

		public void onPeerEncoderStatistics(StatsReport[] reports);

	}

	public RtkPPClient(RtkPPClientEvents events) {
		RtkUtils.assertIsTrue(null != events);
		{// * Init all data.
			mEvents = events;
			mFactory = null;
			mLocalMediaStream = null;
			mVideoSource = null;
			mVideoCapturer = null;

			mNumberOfCameras = 0;
			bVideoCallEnabled = true;
			bRenderVideo = true;
			bRenderAudio = true;
		}

		{// * New all value.
			mIceServers = new ArrayList<PeerConnection.IceServer>();
			mIceServers.add(new PeerConnection.IceServer("stun:123.59.68.21"));
			mIceServers.add(new PeerConnection.IceServer("turn:123.59.68.21", "rtk007", "007pass"));
			mIceServersNull = new ArrayList<PeerConnection.IceServer>();
		}

		{// * Start looper thread.
			// Looper thread is started once in private ctor and is used for all
			// peer connection API calls to ensure new peer connection factory
			// is created on the same thread as previously destroyed factory.
			mExecutor = new LooperExecutor();
			mExecutor.requestStart();
		}

		PeerConnectionFactory.initializeFieldTrials("");
	}

	public void createPeerConnectionFactory(final Context context, final EGLContext renderEGLContext,
			final PeerConnectionParameters peerConnectionParameters, final VideoRenderer localRender) {
		this.mPeerConnectionParameters = peerConnectionParameters;

		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				createPeerConnectionFactoryInternal(context, renderEGLContext, localRender);
			}
		});
	}

	public boolean isbSingleCall() {
		return bSingleCall;
	}

	public void setbSingleCall(boolean bSingleCall) {
		this.bSingleCall = bSingleCall;
	}

	/*mediaMode: 0 sendOnly, 1 recveOnly, 2 sendRecv*/
	public void createPeerConnection(final String peerId, final int mediaMode) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				RtkUtils.assertIsTrue(null != mFactory);
				createPeerConnectionInternal(peerId, mediaMode);
			}
		});
	}

	public void destroyPeerConnection(final String peerId) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				RtkUtils.assertIsTrue(null != mFactory);
				destroyPeerConnectionInternal(peerId);
				mEvents.onRemovePeerRender(peerId);
			}
		});
	}
	
	public void sendOffer(final String peerId) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				RtkUtils.assertIsTrue(null != mFactory);
				sendOfferInternal(peerId);
			}
		});
	}

	public void sendOffers() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				RtkUtils.assertIsTrue(null != mFactory);
				sendOffersInternal();
			}
		});
	}

	public void recvOffer(final String peerId, final SessionDescription sdp) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				RtkUtils.assertIsTrue(null != mFactory);
				recvOfferInternal(peerId, sdp);
			}
		});
	}

	public void recvAnswer(final String peerId, final SessionDescription sdp) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				RtkUtils.assertIsTrue(null != mFactory);
				recvAnswerInternal(peerId, sdp);
			}
		});
	}

	public void recvCandidate(final String peerId, final IceCandidate candidate) {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				RtkUtils.assertIsTrue(null != mFactory);
				recvCandidateInternal(peerId, candidate);
			}
		});
	}

	/**
	 * 切换摄像头
	 */
	public void switchCamera() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				switchCameraInternal();
			}
		});
	}

	/**
	 * 本地图像是否开启
	 * 
	 * @return true/false
	 */
	public boolean localVideoEnabled() {
		return bRenderVideo;
	}

	/**
	 * 开启本地的图像
	 */
	public void setLocalVideoEnabled() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (!bRenderVideo)
					setLocalVideoEnabledInternal(true);
			}
		});
	}

	/**
	 * 关闭本地的图像
	 */
	public void setLocalVideoDisabled() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (bRenderVideo)
					setLocalVideoEnabledInternal(false);
			}
		});
	}

	/**
	 * 本地声音是否开启
	 * 
	 * @return true/false
	 */
	public boolean localAudioEnabled() {
		return bRenderAudio;
	}

	/**
	 * 开启本地声音
	 */
	public void setLocalVoiceEnabled() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (!bRenderAudio)
					setLocalVoiceEnabledInternal(true);
			}
		});
	}

	/**
	 * 关闭本地声音
	 */
	public void setLocalVoiceDisabled() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (bRenderAudio)
					setLocalVoiceEnabledInternal(false);
			}
		});
	}

	/**
	 * 关闭所有, 执行后此实例将不可使用
	 */
	public void close() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				closeInternal();
				{
					Log.d(TAG, "Closing LocalMediaStream.");
					if (mLocalMediaStream != null) {
						mLocalMediaStream.dispose();
						mLocalMediaStream = null;
					}

					Log.d(TAG, "Closing video source.");
					if (mVideoSource != null) {
						mVideoSource.dispose();
						mVideoSource = null;
					}

					Log.d(TAG, "Closing peer connection factory.");
					if (mFactory != null) {
						mFactory.dispose();
						mFactory = null;
					}
					mOptions = null;
					Log.d(TAG, "Closing peer connection done.");
				}
			}
		});

		mExecutor.requestStop();
		mPeerConnectionParameters = null;
	}

	/**
	 * 获取本地图像
	 * 
	 * @return
	 */
	public VideoTrack getLocalVideoTrack() {
		final Exchanger<VideoTrack> result = new Exchanger<VideoTrack>();
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (!bSingleCall) {
					RtkUtils.assertIsTrue(mLocalVideoTrack != null);
				}				
				LooperExecutor.exchange(result, mLocalVideoTrack);
			}
		});

		return LooperExecutor.exchange(result, null); // |null| is a dummy value
														// here.
	};

	/**
	 * 停止发送本地视频
	 */
	public void stopVideoSource() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (mVideoSource != null && !bVideoSourceStopped) {
					Log.d(TAG, "Stop video source.");
					mVideoSource.stop();
					bVideoSourceStopped = true;
				}
			}
		});
	}

	/**
	 * 开启发送本地视频
	 */
	public void startVideoSource() {
		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				if (mVideoSource != null && bVideoSourceStopped) {
					Log.d(TAG, "Restart video source.");
					mVideoSource.restart();
					bVideoSourceStopped = false;
				}
			}
		});
	}

	/**
	 * Private function.
	 */
	private void createPeerConnectionFactoryInternal(Context context, EGLContext renderEGLContext,
			VideoRenderer localRender) {
		Log.d(TAG, "Create peer connection factory with EGLContext " + renderEGLContext + ". Use video: "
				+ mPeerConnectionParameters.videoCallEnabled);
		if (mFactory != null)
			return;
		bVideoCallEnabled = mPeerConnectionParameters.videoCallEnabled;
		if (!PeerConnectionFactory.initializeAndroidGlobals(context, true, true,
				mPeerConnectionParameters.videoCodecHwAcceleration)) {
			Log.e(TAG, "Failed to initializeAndroidGlobals");
		}

		mFactory = new PeerConnectionFactory();
		{
			mLocalMediaStream = mFactory.createLocalMediaStream("ARDAMS");
			createMediaConstraintsInternal();
			if (bVideoCallEnabled) {
				String cameraDeviceName = CameraEnumerationAndroid.getDeviceName(0);
				String frontCameraDeviceName = CameraEnumerationAndroid.getNameOfFrontFacingDevice();
				if (mNumberOfCameras > 1 && frontCameraDeviceName != null) {
					cameraDeviceName = frontCameraDeviceName;
				}
				Log.d(TAG, "Opening camera: " + cameraDeviceName);

				if (null == mVideoCapturer) {
					mVideoCapturer = VideoCapturerAndroid.create(cameraDeviceName, new CameraErrorHandler() {

						@Override
						public void onCameraError(String arg0) {
							// TODO Auto-generated method stub

						}
					});
				}
				if (mVideoCapturer == null) {
					Log.e(TAG, "Failed to open camera");
					return;
				}
//				if (!bSingleCall) {
//					mLocalMediaStream.addTrack(createVideoTrack(mVideoCapturer, localRender));
//				}
				
				mLocalMediaStream.addTrack(createVideoTrack(mVideoCapturer, localRender));
				if (bSingleCall) {
//					setLocalVideoDisabled();
//					mLocalMediaStream.removeTrack(mLocalVideoTrack);
				}
				
				mFactory.setVideoHwAccelerationOptions(renderEGLContext);
			}

			mLocalAudioTrack = mFactory.createAudioTrack(AUDIO_TRACK_ID, mFactory.createAudioSource(mAudioConstraints));
//			if (!bSingleCall) {
//				mLocalMediaStream.addTrack(mLocalAudioTrack);
//			}

			mLocalMediaStream.addTrack(mLocalAudioTrack);
			if (bSingleCall) {
//				setLocalVoiceDisabled();
//				mLocalMediaStream.removeTrack(mLocalAudioTrack);
			}
		}
		if (mOptions != null) {
			Log.d(TAG, "Factory networkIgnoreMask option: " + mOptions.networkIgnoreMask);
			mFactory.setOptions(mOptions);
		}
		Log.d(TAG, "Peer connection factory created.");
	}

	protected abstract void createPeerConnectionInternal(String peerId, int mediaMode);

	protected abstract void destroyPeerConnectionInternal(String peerId);

	protected abstract void sendOfferInternal(String peerId);
	
	protected abstract void sendOffersInternal();

	protected abstract void recvOfferInternal(String peerId, SessionDescription sdp);

	protected abstract void recvAnswerInternal(String peerId, SessionDescription sdp);

	protected abstract void recvCandidateInternal(String peerId, IceCandidate candidate);

	protected abstract void closeInternal();
	
	public abstract void sendDataCmd(String cmd);

	private void createMediaConstraintsInternal() {
		// Check if there is a camera on device and disable video call if not.
		mNumberOfCameras = CameraEnumerationAndroid.getDeviceCount();
		if (mNumberOfCameras == 0) {
			Log.w(TAG, "No camera on device. Switch to audio only call.");
			bVideoCallEnabled = false;
		}
		// Create video constraints if video call is enabled.
		if (bVideoCallEnabled) {
			mVideoConstraints = new MediaConstraints();
			int videoWidth = mPeerConnectionParameters.videoWidth;
			int videoHeight = mPeerConnectionParameters.videoHeight;

			// If VP8 HW video encoder is supported and video resolution is not
			// specified force it to HD.
			if ((videoWidth == 0 || videoHeight == 0) && mPeerConnectionParameters.videoCodecHwAcceleration
					&& (MediaCodecVideoEncoder.isVp8HwSupported() || MediaCodecVideoEncoder.isH264HwSupported())) {
				videoWidth = HD_VIDEO_WIDTH;
				videoHeight = HD_VIDEO_HEIGHT;
			}

			// Add video resolution constraints.
			if (videoWidth > 0 && videoHeight > 0) {
				int minVideoWidth = Math.min(videoWidth, MIN_VIDEO_WIDTH);
				int minVideoHeight = Math.min(videoHeight, MIN_VIDEO_HEIGHT);
				int maxVideoWidth = Math.max(videoWidth, MIN_VIDEO_WIDTH);
				int maxVideoHeight = Math.max(videoHeight, MIN_VIDEO_HEIGHT);
				mVideoConstraints.mandatory
						.add(new KeyValuePair(MIN_VIDEO_WIDTH_CONSTRAINT, Integer.toString(minVideoWidth)));
				mVideoConstraints.mandatory
						.add(new KeyValuePair(MAX_VIDEO_WIDTH_CONSTRAINT, Integer.toString(maxVideoWidth)));
				mVideoConstraints.mandatory
						.add(new KeyValuePair(MIN_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(minVideoHeight)));
				mVideoConstraints.mandatory
						.add(new KeyValuePair(MAX_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(maxVideoHeight)));
			}

			// Add fps constraints.
			int videoFps = mPeerConnectionParameters.videoFps;
			if (videoFps > 0) {
				int minVideoFps = Math.min(videoFps, MIN_VIDEO_FPS);
				int maxVideoFps = Math.max(videoFps, MIN_VIDEO_FPS);
				mVideoConstraints.mandatory
						.add(new KeyValuePair(MIN_VIDEO_FPS_CONSTRAINT, Integer.toString(minVideoFps)));
				mVideoConstraints.mandatory
						.add(new KeyValuePair(MAX_VIDEO_FPS_CONSTRAINT, Integer.toString(maxVideoFps)));
			}
		}

		// Create audio constraints.
		mAudioConstraints = new MediaConstraints();
	}

	private VideoTrack createVideoTrack(VideoCapturerAndroid capturer, VideoRenderer localRender) {
		mVideoSource = mFactory.createVideoSource(capturer, mVideoConstraints);

		mLocalVideoTrack = mFactory.createVideoTrack(VIDEO_TRACK_ID, mVideoSource);
		mLocalVideoTrack.setEnabled(true);
		mLocalVideoTrack.addRenderer(localRender);

		return mLocalVideoTrack;
	}

	private void switchCameraInternal() {
		if (!bVideoCallEnabled || mNumberOfCameras < 2 || mVideoCapturer == null) {
			Log.e(TAG, "Failed to switch camera. Video: " + bVideoCallEnabled + ". Number of cameras: "
					+ mNumberOfCameras);
			return; // No video is sent or only one camera is available or error
					// happened.
		}
		Log.d(TAG, "Switch camera");
		mVideoCapturer.switchCamera(null);
	}

	private void setLocalVideoEnabledInternal(boolean enable) {
		bRenderVideo = enable;
		if (mLocalVideoTrack != null) {
			mLocalVideoTrack.setEnabled(bRenderVideo);
		}
	}

	private void setLocalVoiceEnabledInternal(boolean enable) {
		bRenderAudio = enable;
		if (mLocalAudioTrack != null) {
			mLocalAudioTrack.setEnabled(bRenderAudio);
		}
	}

	// Return the active connection stats, or null if active connection is not
	// found.
	private String getActiveConnectionStats(StatsReport report) {
		StringBuilder activeConnectionbuilder = new StringBuilder();
		// googCandidatePair to show information about the active
		// connection.
		for (StatsReport.Value value : report.values) {
			if (value.name.equals("googActiveConnection") && value.value.equals("false")) {
				return null;
			}
			String name = value.name.replace("goog", "");
			activeConnectionbuilder.append(name).append("=").append(value.value).append("\n");
		}
		return activeConnectionbuilder.toString();
	}

	// Update the heads-up display with information from |reports|.
	private void updateHUD(StatsReport[] reports) {
		StringBuilder builder = new StringBuilder();
		for (StatsReport report : reports) {
			Log.d(TAG, "Stats: " + report.toString());
			// bweforvideo to show statistics for video Bandwidth Estimation,
			// which is global per-session.
			if (report.id.equals("bweforvideo")) {
				for (StatsReport.Value value : report.values) {
					String name = value.name.replace("goog", "").replace("Available", "").replace("Bandwidth", "")
							.replace("Bitrate", "").replace("Enc", "");
					builder.append(name).append("=").append(value.value).append(" ");
				}
				builder.append("\n");
			} else if (report.type.equals("googCandidatePair")) {
				String activeConnectionStats = getActiveConnectionStats(report);
				if (activeConnectionStats == null) {
					continue;
				}
				builder.append(activeConnectionStats);
			} else {
				continue;
			}
			builder.append("\n");
		}
	}

	private Map<String, String> getReportMap(StatsReport report) {
		Map<String, String> reportMap = new HashMap<String, String>();
		for (StatsReport.Value value : report.values) {
			reportMap.put(value.name, value.value);
		}
		return reportMap;
	}

	/**
	 * get the Frame Rate, Actual Bitrate and Remote Candidate Type from report
	 * 
	 * @param reports
	 *            the video report
	 */
	private void updateEncoderStatistics(StatsReport[] reports) {
		String fps = null;
		String targetBitrate = null;
		String actualBitrate = null;
		String recvByte = null;
		String sendByte = null;
		for (StatsReport report : reports) {
			if (report.type.equals("ssrc") && report.id.contains("ssrc") && report.id.contains("send")) {
				Map<String, String> reportMap = getReportMap(report);
				String trackId = reportMap.get("googTrackId");
				if (trackId != null && trackId.contains(VIDEO_TRACK_ID)) {
					fps = reportMap.get("googFrameRateSent");
				}
			} else if (report.id.equals("bweforvideo")) {
				Map<String, String> reportMap = getReportMap(report);
				targetBitrate = reportMap.get("googTargetEncBitrate");
				actualBitrate = reportMap.get("googActualEncBitrate");
			} else if (report.type.equals("googCandidatePair")) {
				Map<String, String> reportMap = getReportMap(report);
				if (reportMap.get("googActiveConnection").equals("true")
						&& reportMap.get("googRemoteCandidateType").equals("relay")) {
					recvByte = reportMap.get("bytesReceived");
					sendByte = reportMap.get("bytesSent");
//					System.out.println("\n \n \n actualBitrate----------------->" + actualBitrate);
//					System.out.println("\n \n \n recvByte----------------->" + recvByte);
//					System.out.println("\n \n \n sendByte----------------->" + sendByte);
				}
			}
		}
		String stat = "";
		if (fps != null) {
			stat += "Fps:  " + fps;
		}
		if (targetBitrate != null) {
			stat += "    Target BR: " + targetBitrate;
		}
		if (actualBitrate != null) {
			stat += "    Actual BR: " + actualBitrate;
		}

	}

	/**
	 * For PeerConnectionEvents.
	 */
	@Override
	public void onLocalDescription(String peerId, SessionDescription sdp) {
		// TODO Auto-generated method stub
		// Log.e(TAG, "onLocalDescription: " + sdp.description + " peerId " +
		// peerId);
		String fmt = "{\"type\":\"%s\",\"sdp\":\"%s\"}";
		String strMsg = String.format(fmt, sdp.type.canonicalForm(), sdp.description);

		mEvents.onOutgoingMessage(peerId, strMsg);
	}

	@Override
	public void onIceCandidate(String peerId, IceCandidate candidate) {
		// TODO Auto-generated method stub
		String fmt = "{\"sdpMid\":\"%s\",\"sdpMLineIndex\":%d,\"candidate\":\"%s\"}";
		String strMsg = String.format(fmt, candidate.sdpMid, candidate.sdpMLineIndex, candidate.sdp);
		mEvents.onOutgoingMessage(peerId, strMsg);
	}

	@Override
	public void onIceConnected(String peerId) {
		// TODO Auto-generated method stub
		mEvents.onUpdatePeerRender(peerId);
	}

	@Override
	public void onIceDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddStream(String peerId, MediaStream stream) {
		// TODO Auto-generated method stub

		VideoRenderer remoteRender = mEvents.onCreatPeerRender(peerId);
		if (stream.videoTracks.size() >= 1) {
			VideoTrack remoteVideoTrack = stream.videoTracks.get(0);
			remoteVideoTrack.setEnabled(bRenderVideo);
			remoteVideoTrack.addRenderer(remoteRender);

			mEvents.onCreateVideoTrack(peerId, remoteVideoTrack);
		}
	}

	@Override
	public void onRemoveStream(String peerId, MediaStream stream) {
		// TODO Auto-generated method stub
		mEvents.onRemovePeerRender(peerId);
	}

	@Override
	public void onPeerConnectionClosed(String peerId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerConnectionStatsReady(StatsReport[] reports) {
		// TODO Auto-generated method stub
		// updateHUD(reports);
		updateEncoderStatistics(reports);
		mEvents.onPeerEncoderStatistics(reports);
	}

	@Override
	public void onPeerConnectionError(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPeerDataChannelOpened(String peerId) {
		// TODO Auto-generated method stub
		mEvents.onPeerDataChannelOpened(peerId);
	}

	@Override
	public void onPeerDataChannelRecvMessage(String peerId, String message) {
		// TODO Auto-generated method stub
		mEvents.onPeerDataChannelRecvMessage(peerId, message);
	}

	@Override
	public void onPeerDataChannelClosed(String peerId) {
		// TODO Auto-generated method stub
		mEvents.onPeerDataChannelClosed(peerId);
	}
}
