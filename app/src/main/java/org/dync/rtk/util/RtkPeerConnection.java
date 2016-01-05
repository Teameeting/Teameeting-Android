package org.dync.rtk.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.webrtc.DataChannel;
import org.webrtc.DataChannel.Buffer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.MediaConstraints.KeyValuePair;
import org.webrtc.PeerConnection.IceConnectionState;

import android.util.Log;

/**
 * 点对点连接
 * 
 * By Dync.inc - 2015/7/20
 * 
 * @author Maozongwu
 *
 */
public class RtkPeerConnection {
	private static final String TAG = "RtkPeerConnection";
	private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
	private static final String VIDEO_CODEC_PARAM_MAX_BITRATE = "x-google-max-bitrate";
	private static final String VIDEO_CODEC_PARAM_MIN_BITRATE = "x-google-min-bitrate";
	private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
	private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
	private static final String FIELD_TRIAL_VP9 = "WebRTC-SupportVP9/Enabled/";
	private static final String VIDEO_CODEC_VP8 = "VP8";
	private static final String VIDEO_CODEC_H264 = "H264";
	private static final String AUDIO_CODEC_OPUS = "opus";
	private static final String AUDIO_CODEC_ISAC = "ISAC";

	/**
	 * Peer connection parameters.
	 */
	public static class PeerConnectionParameters {
		public final boolean videoCallEnabled;
		public final boolean dataChannelEnabled;
		public final boolean loopback;
		public final int videoWidth;
		public final int videoHeight;
		public final int videoFps;
		public final int videoStartBitrate;
		public final String videoCodec;
		public final boolean videoCodecHwAcceleration;
		public final int audioStartBitrate;
		public final String audioCodec;
		public final boolean cpuOveruseDetection;

		/**
		 * 构造一个PeerConnection的参数实例
		 * 
		 * @param videoCallEnabled 
		 * @param dataChannelEnabled 数据传输通道是否可用：true：打开；false：关闭；
		 * @param loopback 是否是开启本地环回：true：打开；false：关闭；
		 * @param videoWidth 视频的宽；
		 * @param videoHeight 视频的高；
		 * @param videoFps 视频的帧率；
		 * @param videoStartBitrate	视频的码流
		 * @param videoCodec 视频的编解码格式；
		 * @param videoCodecHwAcceleration 视频是否开启硬件编解码；
		 * @param audioStartBitrate 音频的码流；
		 * @param audioCodec 音频编码格式；
		 * @param cpuOveruseDetection 是否使用cpu监测
		 */
		public PeerConnectionParameters(boolean videoCallEnabled, boolean dataChannelEnabled, boolean loopback,
				int videoWidth, int videoHeight, int videoFps, int videoStartBitrate, String videoCodec,
				boolean videoCodecHwAcceleration, int audioStartBitrate, String audioCodec,
				boolean cpuOveruseDetection) {
			this.videoCallEnabled = videoCallEnabled;
			this.dataChannelEnabled = dataChannelEnabled;
			this.loopback = loopback;
			this.videoWidth = videoWidth;
			this.videoHeight = videoHeight;
			this.videoFps = videoFps;
			this.videoStartBitrate = videoStartBitrate;
			this.videoCodec = videoCodec;
			this.videoCodecHwAcceleration = videoCodecHwAcceleration;
			this.audioStartBitrate = audioStartBitrate;
			this.audioCodec = audioCodec;
			this.cpuOveruseDetection = cpuOveruseDetection;
		}
	}

	/**
	 * Peer connection mEvents.
	 */
	public static interface PeerConnectionEvents {
		/**
		 * Callback fired once local SDP is created and set.
		 */
		public void onLocalDescription(String peerId, final SessionDescription sdp);

		/**
		 * Callback fired once local Ice candidate is generated.
		 */
		public void onIceCandidate(String peerId, final IceCandidate candidate);

		/**
		 * Callback fired once connection is established (IceConnectionState is
		 * CONNECTED).
		 */
		public void onIceConnected(String peerId);

		/**
		 * Callback fired once connection is closed (IceConnectionState is
		 * DISCONNECTED).
		 */
		public void onIceDisconnected();

		/**
		 * Callback new stream.
		 */
		public void onAddStream(String peerId, MediaStream stream);

		/**
		 * Callback stream closed.
		 */
		public void onRemoveStream(String peerId, MediaStream stream);

		/**
		 * Callback fired once peer connection is closed.
		 */
		public void onPeerConnectionClosed(String peerId);

		/**
		 * Callback fired once peer connection statistics is ready.
		 */
		public void onPeerConnectionStatsReady(final StatsReport[] reports);

		/**
		 * Callback fired once peer connection error happened.
		 */
		public void onPeerConnectionError(final String description);

		/**
		 * Callback fired once data channel opened.
		 */
		public void onPeerDataChannelOpened(String peerId);

		/**
		 * Callback fired once data channel recv message.
		 */
		public void onPeerDataChannelRecvMessage(String peerId, String message);

		/**
		 * Callback fired once data channel closed.
		 */
		public void onPeerDataChannelClosed(String peerId);
	}

	private final PCObserver pcObserver = new PCObserver();
	private final SDPObserver sdpObserver = new SDPObserver();
	private final DCObserver dcObjserver = new DCObserver();
	private final LooperExecutor mExecutor;
	// Queued remote ICE candidates are consumed only after both local and
	// remote descriptions are set. Similarly local ICE candidates are sent to
	// remote peer after both local and remote description are set.
	private LinkedList<IceCandidate> queuedRemoteCandidates;

	private String mPeerId;
	private PeerConnectionEvents mEvents;

	private boolean videoCallEnabled;
	private boolean preferH264;
	private boolean mIsInitiator;
	private boolean mNeedDChannel;
	private int mMediaMode;	/*0 sendOnly, 1 recveOnly, 2 sendRecv*/
	private int mMaxAudBitrate;
	private int mMaxVidBitrate;
	private MediaStream mLocalMediaStream;
	private PeerConnection mPeerConnection;
	private DataChannel mDataChannel;
	private MediaConstraints mPcConstraints;
	private MediaConstraints mSdpMediaConstraints;
	private SessionDescription mLocalSdp; // either offer or answer SDP
	private Timer statsTimer;

	public RtkPeerConnection(LooperExecutor executor, String peerId, PeerConnectionEvents events) {
		mExecutor = executor;
		mPeerId = peerId;
		mEvents = events;

		mMediaMode = 2;
		mIsInitiator = false;
		mNeedDChannel = false;
		mMaxAudBitrate = 75;
		mMaxVidBitrate = 512;
		mPeerConnection = null;
		mDataChannel = null;
		mPcConstraints = null;
		mSdpMediaConstraints = null;
		mLocalSdp = null;
		statsTimer = new Timer();
	}

	public void CreateConnection(PeerConnectionParameters peerConnectionParameters, PeerConnectionFactory factory,
			MediaStream localMediaStream, List<PeerConnection.IceServer> iceServers, int mediaMode) {
		mLocalMediaStream = localMediaStream;
		mMediaMode = mediaMode;
		videoCallEnabled = peerConnectionParameters.videoCallEnabled;
		{// * createMediaConstraintsInternal
			// Create peer connection constraints.
			mPcConstraints = new MediaConstraints();
			// Enable DTLS for normal calls and disable for loopback calls.
			if (peerConnectionParameters.loopback) {
				mPcConstraints.optional.add(new KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "false"));
			} else {
				mPcConstraints.optional.add(new KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
			}

			mMaxVidBitrate = peerConnectionParameters.videoStartBitrate;
			mNeedDChannel = peerConnectionParameters.dataChannelEnabled;
			boolean videoCallEnabled = peerConnectionParameters.videoCallEnabled;
			// Create SDP constraints.
			mSdpMediaConstraints = new MediaConstraints();
			if(peerConnectionParameters.loopback) {
				mSdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveAudio", "true"));
				mSdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveVideo", "true"));
			} else {
				switch(mMediaMode) {
				case 0:	// sendOnly
				{
					mSdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveAudio", "false"));
					mSdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveVideo", "false"));
				}
				break;
				case 1:	// recvOnly
				case 2:	// sendRecv
				{
					mSdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveAudio", "true"));
					if (videoCallEnabled) {
						mSdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveVideo", "true"));
					} else {
						mSdpMediaConstraints.mandatory.add(new KeyValuePair("OfferToReceiveVideo", "false"));
					}
				}
					break;
				}
				
			}
		}
		
		 // Check if H.264 is used by default.
	    preferH264 = false;
	    if (videoCallEnabled && peerConnectionParameters.videoCodec != null
	        && peerConnectionParameters.videoCodec.equals(VIDEO_CODEC_H264)) {
	      preferH264 = true;
	    }

		{// * createPeerConnectionInternal
			if (factory == null) {
				Log.e(TAG, "Peerconnection factory is not created");
				return;
			}
			if (localMediaStream == null) {
				Log.e(TAG, "LocalMediaStream is not created");
				return;
			}
			Log.d(TAG, "Create peer connection");
			Log.d(TAG, "PCConstraints: " + mPcConstraints.toString());

			queuedRemoteCandidates = new LinkedList<IceCandidate>();
			PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
			// TCP candidates are only useful when connecting to a server that
			// supports
			// ICE-TCP.
			rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;

			mPeerConnection = factory.createPeerConnection(rtcConfig, mPcConstraints, pcObserver);

			// Set default WebRTC tracing and INFO libjingle logging.
			// NOTE: this _must_ happen while |factory| is alive!
			Logging.enableTracing("logcat:", EnumSet.of(Logging.TraceLevel.TRACE_DEFAULT), Logging.Severity.LS_INFO);
			if(mMediaMode != 1)
				mPeerConnection.addStream(localMediaStream);
		}
		Log.d(TAG, "Peer connection created.");
	}

	public void createOffer() {
		// Create offer. Offer SDP will be sent to answering client in
		// PeerConnectionEvents.onLocalDescription event.
		if (mPeerConnection != null) {
			Log.d(TAG, "PC Create OFFER");
			mIsInitiator = true;
			if (mNeedDChannel) {
				DataChannel.Init init = new DataChannel.Init();
				init.ordered = false;
				this.mDataChannel = mPeerConnection.createDataChannel("__CMD_CHANNEL", init);
				this.mDataChannel.registerObserver(dcObjserver);
			}
			mPeerConnection.createOffer(sdpObserver, mSdpMediaConstraints);
		}
	}

	public void createAnswer(SessionDescription sdpRemote) {
		if (mPeerConnection != null) {
			Log.d(TAG, "PC create ANSWER");
			mIsInitiator = false;
			System.out.println("ANSWER preferH264-------------------->" + preferH264);
			String sdpDescription = setBitrate(preferH264 ? VIDEO_CODEC_H264 : VIDEO_CODEC_VP8, true,
					sdpRemote.description, mMaxVidBitrate);
			SessionDescription newSdp = new SessionDescription(sdpRemote.type, sdpDescription);

			mPeerConnection.setRemoteDescription(sdpObserver, newSdp);

			mPeerConnection.createAnswer(sdpObserver, mSdpMediaConstraints);
		} else {
			Log.e(TAG, "PC not create ANSWER");
		}
	}

	public void recvAnswer(SessionDescription sdpRemote) {
		if (mPeerConnection != null) {
			Log.d(TAG, "PC set ANSWER");
			System.out.println("recv ANSWER preferH264-------------------->" + preferH264);
			String sdpDescription = setBitrate(preferH264 ? VIDEO_CODEC_H264 : VIDEO_CODEC_VP8, true, sdpRemote.description, mMaxVidBitrate);
			SessionDescription newSdp = new SessionDescription(sdpRemote.type, sdpDescription);
			mPeerConnection.setRemoteDescription(sdpObserver, newSdp);
		}
	}

	public void addRemoteIceCandidate(final IceCandidate candidate) {
		if (mPeerConnection != null) {
			if (queuedRemoteCandidates != null) {
				queuedRemoteCandidates.add(candidate);
			} else {
				mPeerConnection.addIceCandidate(candidate);
			}
		}
	}

	public void close() {
		if (mPeerConnection != null) {
			statsTimer.cancel();
			mPeerConnection.removeStream(mLocalMediaStream);
			mPeerConnection.dispose();
			mPeerConnection = null;
		}
	}

	/**
	 * 在P2P数据通道发送cmd命令
	 * 
	 * @param cmd
	 */
	public void SendDataCMD(String cmd) {
		ByteBuffer sendBuffer = null;
		try {
			sendBuffer = ByteBuffer.wrap(cmd.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != mDataChannel) {
			mDataChannel.send(new Buffer(sendBuffer, true));
		}
	}

	private void getStats() {
		if (mPeerConnection == null) {
			return;
		}
		boolean success = mPeerConnection.getStats(new StatsObserver() {
			@Override
			public void onComplete(final StatsReport[] reports) {
				mEvents.onPeerConnectionStatsReady(reports);
			}
		}, null);
		if (!success) {
			Log.e(TAG, "getStats() returns false!");
		}
	}

	public void enableStatsEvents(boolean enable, int periodMs) {
		if (enable) {
			try {
				statsTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						mExecutor.execute(new Runnable() {
							@Override
							public void run() {
								getStats();
							}
						});
					}
				}, 0, periodMs);
			} catch (Exception e) {
				Log.e(TAG, "Can not schedule statistics timer", e);
			}
		} else {
			statsTimer.cancel();
		}
	}

	/**
	 * Private function.
	 */
	private void drainCandidates() {
		if (queuedRemoteCandidates != null) {
			Log.d(TAG, "Add " + queuedRemoteCandidates.size() + " remote candidates");
			for (IceCandidate candidate : queuedRemoteCandidates) {
				mPeerConnection.addIceCandidate(candidate);
			}
			queuedRemoteCandidates = null;
		}
	}

	private SessionDescription modifySdpMaxBW(SessionDescription sdp) {
		StringBuffer desc = new StringBuffer();
		int audioLine = -1;
		int videoLine = -1;
		ArrayList<Integer> bLines = new ArrayList<Integer>();
		String[] lines = sdp.description.split("\r\n");
		for (int i = 0; i < lines.length; ++i) {
			if (lines[i].startsWith("m=audio")) {
				audioLine = i;
			} else if (lines[i].startsWith("m=video")) {
				videoLine = i;
			} else if (lines[i].startsWith("b=AS:")) {
				bLines.add(i);
			}
		}
		// TODO dk: this may want to check for existing B-Lines!
		boolean addVideoB = mMaxAudBitrate > 0;
		boolean addAudioB = mMaxVidBitrate > 0;
		for (int i = 0; i < lines.length; ++i) {
			desc.append(lines[i]);
			desc.append("\r\n");
			if (i == audioLine && addAudioB) {
				desc.append("b=AS:" + mMaxAudBitrate + "\r\n");
			} else if (i == videoLine && addVideoB) {
				desc.append("b=AS:" + mMaxVidBitrate + "\r\n");
			}
		}

		return new SessionDescription(sdp.type, desc.toString());
	}
	
	private static String setBitrate(String codec, boolean isVideoCodec, String sdpDescription, int bitrateKbps) {
		String[] lines = sdpDescription.split("\r\n");
		int rtpmapLineIndex = -1;
		boolean sdpFormatUpdated = false;
		String codecRtpMap = null;
		// Search for codec rtpmap in format
		// a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding
		// parameters>]
		String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
		Pattern codecPattern = Pattern.compile(regex);
		for (int i = 0; i < lines.length; i++) {
			Matcher codecMatcher = codecPattern.matcher(lines[i]);
			if (codecMatcher.matches()) {
				codecRtpMap = codecMatcher.group(1);
				rtpmapLineIndex = i;
				break;
			}
		}
		if (codecRtpMap == null) {
			Log.w(TAG, "No rtpmap for " + codec + " codec");
			return sdpDescription;
		}
		Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex]);

		// Check if a=fmtp string already exist in remote SDP for this codec and
		// update it with new bitrate parameter.
		regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
		codecPattern = Pattern.compile(regex);
		for (int i = 0; i < lines.length; i++) {
			Matcher codecMatcher = codecPattern.matcher(lines[i]);
			if (codecMatcher.matches()) {
				Log.d(TAG, "Found " + codec + " " + lines[i]);
				if (isVideoCodec) {
					lines[i] += "; " + VIDEO_CODEC_PARAM_MAX_BITRATE + "=" + bitrateKbps;
				} else {
					lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
				}
				Log.d(TAG, "Update remote SDP line: " + lines[i]);
				sdpFormatUpdated = true;
				break;
			}
		}

		StringBuilder newSdpDescription = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			newSdpDescription.append(lines[i]).append("\r\n");
			// Append new a=fmtp line if no such line exist for a codec.
			if (!sdpFormatUpdated && i == rtpmapLineIndex) {
				String bitrateSet;
				if (isVideoCodec) {
					bitrateSet = "a=fmtp:" + codecRtpMap + " " + VIDEO_CODEC_PARAM_MAX_BITRATE + "=" + bitrateKbps;
				} else {
					bitrateSet = "a=fmtp:" + codecRtpMap + " " + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
				}
				Log.d(TAG, "Add remote SDP line: " + bitrateSet);
				newSdpDescription.append(bitrateSet).append("\r\n");
			}

		}
		return newSdpDescription.toString();
	}
	
	private static String preferCodec(String sdpDescription, String codec, boolean isAudio) {
		String[] lines = sdpDescription.split("\r\n");
		int mLineIndex = -1;
		String codecRtpMap = null;
		// a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding
		// parameters>]
		String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
		Pattern codecPattern = Pattern.compile(regex);
		String mediaDescription = "m=video ";
		if (isAudio) {
			mediaDescription = "m=audio ";
		}
		for (int i = 0; (i < lines.length) && (mLineIndex == -1 || codecRtpMap == null); i++) {
			if (lines[i].startsWith(mediaDescription)) {
				mLineIndex = i;
				continue;
			}
			Matcher codecMatcher = codecPattern.matcher(lines[i]);
			if (codecMatcher.matches()) {
				codecRtpMap = codecMatcher.group(1);
				continue;
			}
		}
		if (mLineIndex == -1) {
			Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
			return sdpDescription;
		}
		if (codecRtpMap == null) {
			Log.w(TAG, "No rtpmap for " + codec);
			return sdpDescription;
		}
		Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at " + lines[mLineIndex]);
		String[] origMLineParts = lines[mLineIndex].split(" ");
		if (origMLineParts.length > 3) {
			StringBuilder newMLine = new StringBuilder();
			int origPartIndex = 0;
			// Format is: m=<media> <port> <proto> <fmt> ...
			newMLine.append(origMLineParts[origPartIndex++]).append(" ");
			newMLine.append(origMLineParts[origPartIndex++]).append(" ");
			newMLine.append(origMLineParts[origPartIndex++]).append(" ");
			newMLine.append(codecRtpMap);
			for (; origPartIndex < origMLineParts.length; origPartIndex++) {
				if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
					newMLine.append(" ").append(origMLineParts[origPartIndex]);
				}
			}
			lines[mLineIndex] = newMLine.toString();
			Log.d(TAG, "Change media description: " + lines[mLineIndex]);
		} else {
			Log.e(TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
		}
		StringBuilder newSdpDescription = new StringBuilder();
		for (String line : lines) {
			newSdpDescription.append(line).append("\r\n");
		}
		return newSdpDescription.toString();
	}

	// Implementation detail: observe ICE & stream changes and react
	// accordingly.
	private class PCObserver implements PeerConnection.Observer {
		@Override
		public void onIceCandidate(final IceCandidate candidate) {
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					mEvents.onIceCandidate(mPeerId, candidate);
				}
			});
		}

		@Override
		public void onSignalingChange(PeerConnection.SignalingState newState) {
			Log.d(TAG, "SignalingState: " + newState);
		}

		@Override
		public void onIceConnectionChange(final IceConnectionState newState) {
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG, "IceConnectionState: " + newState);
					if (newState == IceConnectionState.CONNECTED) {
						mEvents.onIceConnected(mPeerId);
					} else if (newState == IceConnectionState.DISCONNECTED) {
						mEvents.onIceDisconnected();
					} else if (newState == IceConnectionState.FAILED) {
						Log.e(TAG, "ICE connection failed.");
					}
				}
			});
		}

		@Override
		public void onIceGatheringChange(PeerConnection.IceGatheringState newState) {
			Log.d(TAG, "IceGatheringState: " + newState);
		}

		@Override
		public void onAddStream(final MediaStream stream) {
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					if (mPeerConnection == null) {
						return;
					}
					mEvents.onAddStream(mPeerId, stream);
				}
			});
		}

		@Override
		public void onRemoveStream(final MediaStream stream) {
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					if (mPeerConnection == null) {
						return;
					}
					mEvents.onRemoveStream(mPeerId, stream);
					stream.videoTracks.get(0).dispose();
				}
			});
		}

		@Override
		public void onDataChannel(final DataChannel dc) {
			Log.e(TAG, "Data channel got: " + dc.label() + " anyway!");
			RtkUtils.assertIsTrue(mDataChannel == null);
			mDataChannel = dc;
			mDataChannel.registerObserver(dcObjserver);
		}

		@Override
		public void onRenegotiationNeeded() {
			// No need to do anything; AppRTC follows a pre-agreed-upon
			// signaling/negotiation protocol.
		}

		@Override
		public void onIceConnectionReceivingChange(boolean arg0) {
			// TODO Auto-generated method stub

		}
	}

	// Implementation detail: handle offer creation/signaling and answer
	// setting,
	// as well as adding remote ICE candidates once the answer SDP is set.
	private class SDPObserver implements SdpObserver {
		@Override
		public void onCreateSuccess(final SessionDescription origSdp) {
			if (mLocalSdp != null) {
				Log.e(TAG, "Multiple SDP create.");
				return;
			}
			String sdpDescription = origSdp.description;
			if (videoCallEnabled && preferH264) {
				sdpDescription = preferCodec(origSdp.description, VIDEO_CODEC_H264, false);
			}
			SessionDescription newSdp = new SessionDescription(origSdp.type, sdpDescription);
			final SessionDescription sdp = newSdp;//modifySdpMaxBW(newSdp);
			mLocalSdp = sdp;
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					if (mPeerConnection != null) {
						Log.d(TAG, "Set local SDP from " + sdp.type);
						mPeerConnection.setLocalDescription(sdpObserver, sdp);
					}
				}
			});
		}

		@Override
		public void onSetSuccess() {
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {
					if (mPeerConnection == null) {
						return;
					}
					if (mIsInitiator) {
						// For offering peer connection we first create offer
						// and set
						// local SDP, then after receiving answer set remote
						// SDP.
						if (mPeerConnection.getRemoteDescription() == null) {
							// We've just set our local SDP so time to send it.
							Log.d(TAG, "Local SDP set succesfully");
							mEvents.onLocalDescription(mPeerId, mLocalSdp);
						} else {
							// We've just set remote description, so drain
							// remote
							// and send local ICE candidates.
							Log.d(TAG, "Remote SDP set succesfully");
							drainCandidates();
						}
					} else {
						// For answering peer connection we set remote SDP and
						// then
						// create answer and set local SDP.
						if (mPeerConnection.getLocalDescription() != null) {
							// We've just set our local SDP so time to send it,
							// drain
							// remote and send local ICE candidates.
							Log.d(TAG, "Local SDP set succesfully");
							mEvents.onLocalDescription(mPeerId, mLocalSdp);
							drainCandidates();
						} else {
							// We've just set remote SDP - do nothing for now -
							// answer will be created soon.
							Log.d(TAG, "Remote SDP set succesfully");
						}
					}
				}
			});
		}

		@Override
		public void onCreateFailure(final String error) {
			Log.e(TAG, "createSDP error: " + error);
		}

		@Override
		public void onSetFailure(final String error) {
			Log.e(TAG, "setSDP error: " + error);
		}
	}

	// Implementation detail: data channel changes and react
	// accordingly.
	private class DCObserver implements DataChannel.Observer {

		@Override
		public void onMessage(Buffer msg) {
			// TODO Auto-generated method stub
			String message = RtkUtils.byteBufferToString(msg.data);
			Log.d(TAG, "Data channel recv: " + message);
			mEvents.onPeerDataChannelRecvMessage(mPeerId, message);
		}

		@Override
		public void onStateChange() {
			// TODO Auto-generated method stub
			if (mDataChannel.state() == DataChannel.State.OPEN) {
				mEvents.onPeerDataChannelOpened(mPeerId);
			} else if (mDataChannel.state() == DataChannel.State.CLOSED) {
				mEvents.onPeerDataChannelClosed(mPeerId);
			}
		}

		@Override
		public void onBufferedAmountChange(long arg0) {
			// TODO Auto-generated method stub

		}

	}
}
