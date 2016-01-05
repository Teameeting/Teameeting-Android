package org.dync.rtk.util;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * P2P呼叫连接管理
 * 
 * By Dync.inc - 2015/7/20
 * 
 * @author Maozongwu
 *
 */
public class RtkP2PClient extends RtkPPClient {

	private RtkPeerConnection mRtkPeer;

	public RtkP2PClient(RtkPPClientEvents events) {
		super(events);
		// TODO Auto-generated constructor stub
		mRtkPeer = null;
	}

	@Override
	protected void createPeerConnectionInternal(String peerId, int mediaMode) {
		// TODO Auto-generated method stub
		RtkUtils.assertIsTrue(null == mRtkPeer);
		mRtkPeer = new RtkPeerConnection(mExecutor, peerId, this);
		mRtkPeer.CreateConnection(mPeerConnectionParameters, mFactory, mLocalMediaStream, mIceServers, mediaMode);
		mRtkPeer.enableStatsEvents(true, 1000);
	}

	@Override
	protected void destroyPeerConnectionInternal(String peerId) {
		// TODO Auto-generated method stub
		if (mRtkPeer != null) {
			mRtkPeer.close();
			mRtkPeer = null;
		}
	}

	@Override
	protected void sendOfferInternal(String peerId) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void sendOffersInternal() {
		// TODO Auto-generated method stub
		if (mRtkPeer != null) {
			mRtkPeer.createOffer();
		}
	}

	@Override
	protected void recvOfferInternal(String peerId, SessionDescription sdp) {
		// TODO Auto-generated method stub
		if (mRtkPeer != null) {
			mRtkPeer.createAnswer(sdp);
		}
	}

	@Override
	protected void recvAnswerInternal(String peerId, SessionDescription sdp) {
		// TODO Auto-generated method stub
		if (mRtkPeer != null) {
			mRtkPeer.recvAnswer(sdp);
		}
	}

	@Override
	protected void recvCandidateInternal(String peerId, IceCandidate candidate) {
		// TODO Auto-generated method stub
		if (mRtkPeer != null) {
			mRtkPeer.addRemoteIceCandidate(candidate);
		}
	}

	@Override
	protected void closeInternal() {
		// TODO Auto-generated method stub
		if (mRtkPeer != null) {
			mRtkPeer.close();
			mRtkPeer = null;
		}
	}

	@Override
	public void sendDataCmd(String cmd) {
		if (null != mRtkPeer) {
			mRtkPeer.SendDataCMD(cmd);
		}
	}
}
