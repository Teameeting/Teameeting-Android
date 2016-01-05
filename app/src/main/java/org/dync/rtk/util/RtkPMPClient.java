/**
 * RtkPMPClient.class
 * 说明： 多人会议连接管理
 * 
 * By Dync.inc & Maozongwu
 * - 2015/7/20
 */
package org.dync.rtk.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public class RtkPMPClient extends RtkPPClient {
	private final static String TAG = "RtkPMPClient";

	private Map<String, RtkPeerConnection> mRtkPeers;

	public RtkPMPClient(RtkPPClientEvents events) {
		super(events);
		{// * New all value.
			mRtkPeers = new HashMap<String, RtkPeerConnection>();
		}
	}

	/**
	 * 
	 */
	@Override
	protected void createPeerConnectionInternal(String peerId, int mediaMode) {
		RtkPeerConnection peer = mRtkPeers.get(peerId);
		if (null == peer) {
			peer = new RtkPeerConnection(mExecutor, peerId, this);
			peer.CreateConnection(mPeerConnectionParameters, mFactory, mLocalMediaStream, mIceServers, mediaMode);
			mRtkPeers.put(peerId, peer);
			peer.enableStatsEvents(true, 1000);
		}
	}

	@Override
	protected void destroyPeerConnectionInternal(String peerId) {
		RtkPeerConnection peer = mRtkPeers.get(peerId);
		if (null != peer) {
			peer.close();
			mRtkPeers.remove(peerId);
		}
	}
	
	@Override
	protected void sendOfferInternal(String peerId) {
		// TODO Auto-generated method stub
		RtkPeerConnection peer = mRtkPeers.get(peerId);
		if (null != peer) {
			peer.createOffer();
		}
	}

	@Override
	protected void sendOffersInternal() {
		Iterator iter = mRtkPeers.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			RtkPeerConnection val = (RtkPeerConnection) entry.getValue();
			val.createOffer();
		}
	}

	@Override
	protected void recvOfferInternal(String peerId, SessionDescription sdp) {
		RtkPeerConnection peer = mRtkPeers.get(peerId);
		if (null != peer) {
			peer.createAnswer(sdp);
		}
	}

	@Override
	protected void recvAnswerInternal(String peerId, SessionDescription sdp) {
		RtkPeerConnection peer = mRtkPeers.get(peerId);
		if (null != peer) {
			peer.recvAnswer(sdp);
		}
	}

	@Override
	protected void recvCandidateInternal(String peerId, IceCandidate candidate) {
		RtkPeerConnection peer = mRtkPeers.get(peerId);
		if (null != peer) {
			peer.addRemoteIceCandidate(candidate);
		}
	}

	@Override
	protected void closeInternal() {
		Iterator iter = mRtkPeers.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			RtkPeerConnection val = (RtkPeerConnection) entry.getValue();
			val.close();
		}
		mRtkPeers.clear();
	}
	
	@Override
	public void sendDataCmd(String cmd) {
		Iterator iter = mRtkPeers.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			RtkPeerConnection val = (RtkPeerConnection) entry.getValue();
			val.SendDataCMD(cmd);
		}
	}

}
